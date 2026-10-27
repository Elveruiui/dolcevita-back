async function pedir(ruta) {
  const respuesta = await fetch(ruta);
  if (!respuesta.ok) {
    const texto = await respuesta.text();
    throw new Error(texto || "Error " + respuesta.status + " al consultar " + ruta);
  }
  if (respuesta.status === 204) return null;
  return respuesta.json();
}

async function enviar(ruta, metodo, cuerpo) {
  const opciones = {
    method: metodo,
    headers: { "Content-Type": "application/json" },
  };
  if (cuerpo !== undefined) {
    opciones.body = JSON.stringify(cuerpo);
  }
  const respuesta = await fetch(ruta, opciones);
  const texto = await respuesta.text();
  if (!respuesta.ok) {
    let mensaje = texto || "Error " + respuesta.status;
    try {
      const interpretado = JSON.parse(texto);
      if (typeof interpretado === "string") mensaje = interpretado;
      else if (interpretado && interpretado.message) mensaje = interpretado.message;
    } catch (e) {}
    throw new Error(mensaje);
  }
  if (!texto) return null;
  try {
    return JSON.parse(texto);
  } catch (e) {
    return texto;
  }
}

const Api = {
  productos: () => pedir("/producto/listarProductos"),
  producto: (id) => pedir("/producto/listarId/" + encodeURIComponent(id)),
  categorias: () => pedir("/categoria/listarCategorias"),
  usuarios: () => pedir("/usuario/listarUsuarios"),
  direccionesDe: (idUsuario) => pedir("/direccion/listarPorUsuario/" + idUsuario),
  crearDireccion: (direccion) => enviar("/direccion/crear", "POST", direccion),
  crearPedido: (pedido) => enviar("/pedido/crear", "POST", pedido),
  pedido: (id) => pedir("/pedido/listarId/" + encodeURIComponent(id)),
  login: (correo, contrasena) => enviar("/auth/login", "POST", { correo, contrasena }),
  registro: (datos) => enviar("/auth/registro", "POST", datos),
  usuarioActual: () => pedir("/auth/me"),
  logout: () => enviar("/auth/logout", "POST"),
};

const CLAVE_SESION_CACHE = "dolcevita_usuario";

const Sesion = {
  leerCache() {
    try {
      return JSON.parse(localStorage.getItem(CLAVE_SESION_CACHE));
    } catch (e) {
      return null;
    }
  },
  guardarCache(usuario) {
    try {
      if (usuario) localStorage.setItem(CLAVE_SESION_CACHE, JSON.stringify(usuario));
      else localStorage.removeItem(CLAVE_SESION_CACHE);
    } catch (e) {}
  },
  async obtener() {
    try {
      const usuario = await Api.usuarioActual();
      this.guardarCache(usuario);
      return usuario;
    } catch (e) {
      this.guardarCache(null);
      return null;
    }
  },
  async clienteActual() {
    const usuario = await this.obtener();
    if (!usuario) {
      const destino = encodeURIComponent(location.pathname.split("/").pop() + location.search);
      location.href = "login.html?redirect=" + destino;
      throw new Error("Redirigiendo al inicio de sesión...");
    }
    return usuario;
  },
  async cerrar() {
    try {
      await Api.logout();
    } catch (e) {}
    this.guardarCache(null);
    location.href = "index.html";
  },
  rolDe(usuario) {
    return usuario && usuario.rol && usuario.rol.nombre_rol
      ? usuario.rol.nombre_rol.toUpperCase()
      : "";
  },
};

function urlImagen(nombreImagen) {
  if (!nombreImagen) return "img/placeholder.jpg";
  return "/producto/imagenProducto?nombreImagen=" + encodeURIComponent(nombreImagen);
}

function conRespaldo(img) {
  img.addEventListener("error", () => {
    if (!img.dataset.respaldo) {
      img.dataset.respaldo = "1";
      img.src = "img/placeholder.jpg";
    }
  });
}

function formatearPrecio(valor) {
  return "S/ " + Number(valor).toFixed(2);
}

function esc(texto) {
  const div = document.createElement("div");
  div.textContent = texto == null ? "" : String(texto);
  return div.innerHTML;
}