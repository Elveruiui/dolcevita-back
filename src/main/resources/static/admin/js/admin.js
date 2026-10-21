const SECCIONES = [
  { id: "inicio", url: "index.html", icono: "fa-house", texto: "Inicio" },
  { id: "pedidos", url: "pedidos.html", icono: "fa-clipboard-list", texto: "Pedidos" },
  { id: "productos", url: "productos.html", icono: "fa-cake-candles", texto: "Productos" },
  { id: "categorias", url: "categorias.html", icono: "fa-tags", texto: "Categorías" },
  { id: "entregas", url: "entregas.html", icono: "fa-motorcycle", texto: "Entregas" },
  { id: "usuarios", url: "usuarios.html", icono: "fa-users", texto: "Usuarios" },
];

const ESTADOS_PEDIDO = ["PENDIENTE", "CONFIRMADO", "EN_CAMINO", "ENTREGADO", "CANCELADO"];
const ESTADOS_ENTREGA = ["Asignada", "En camino", "Entregada", "Con incidencia"];

async function llamar(ruta, metodo, cuerpo) {
  const opciones = { method: metodo };
  if (cuerpo instanceof FormData) {
    opciones.body = cuerpo;
  } else if (cuerpo !== undefined) {
    opciones.headers = { "Content-Type": "application/json" };
    opciones.body = JSON.stringify(cuerpo);
  }
  const respuesta = await fetch(ruta, opciones);
  const texto = await respuesta.text();
  let datos = texto;
  try { datos = JSON.parse(texto); } catch (e) {  }
  if (!respuesta.ok) {
    throw new Error(typeof datos === "string" && datos ? datos : "Error " + respuesta.status);
  }
  return datos;
}

function formularioProducto(producto, archivo) {
  const datos = new FormData();
  datos.append("producto", new Blob([JSON.stringify(producto)], { type: "application/json" }));
  if (archivo) datos.append("file", archivo);
  return datos;
}

const AdminApi = {
  productos: () => llamar("/producto/listarProductos", "GET"),
  crearProducto: (p, archivo) => llamar("/producto/crear", "POST", formularioProducto(p, archivo)),
  actualizarProducto: (id, p, archivo) => llamar("/producto/actualizar/" + id, "PUT", formularioProducto(p, archivo)),
  eliminarProducto: (id) => llamar("/producto/eliminar/" + id, "DELETE"),

  categorias: () => llamar("/categoria/listarCategorias", "GET"),
  crearCategoria: (c) => llamar("/categoria/crear", "POST", c),
  actualizarCategoria: (id, c) => llamar("/categoria/actualizar/" + id, "PUT", c),
  eliminarCategoria: (id) => llamar("/categoria/eliminar/" + id, "DELETE"),

  pedidos: () => llamar("/pedido/listarPedidos", "GET"),
  pedido: (id) => llamar("/pedido/listarId/" + encodeURIComponent(id), "GET"),
  cambiarEstadoPedido: (id, estado) =>
    llamar("/pedido/actualizarEstado/" + id + "?estado=" + encodeURIComponent(estado), "PUT"),

  entregas: () => llamar("/entrega/listarEntregas", "GET"),
  crearEntrega: (e) => llamar("/entrega/crear", "POST", e),
  actualizarEntrega: (id, e) => llamar("/entrega/actualizar/" + id, "PUT", e),
  eliminarEntrega: (id) => llamar("/entrega/eliminar/" + id, "DELETE"),

  usuarios: () => llamar("/usuario/listarUsuarios", "GET"),
  roles: () => llamar("/rol/listarrol", "GET"),
  crearUsuario: (u) => llamar("/usuario/crear", "POST", u),
  actualizarUsuario: (id, u) => llamar("/usuario/actualizar/" + id, "PUT", u),
  eliminarUsuario: (id) => llamar("/usuario/eliminar/" + id, "DELETE"),
};

function esc(texto) {
  const div = document.createElement("div");
  div.textContent = texto == null ? "" : String(texto);
  return div.innerHTML;
}

function precio(valor) {
  return "S/ " + Number(valor || 0).toFixed(2);
}

function codigoPedido(id) {
  return "#DV-" + String(id).padStart(5, "0");
}

function aFecha(valor) {
  if (!valor) return null;
  const f = Array.isArray(valor)
    ? new Date(valor[0], valor[1] - 1, valor[2], valor[3] || 0, valor[4] || 0)
    : new Date(valor);
  return isNaN(f) ? null : f;
}

function fechaCorta(valor) {
  const f = aFecha(valor);
  return f ? f.toLocaleDateString("es-PE", { day: "2-digit", month: "2-digit", year: "numeric" }) : "—";
}

function fechaHora(valor) {
  const f = aFecha(valor);
  if (!f) return "—";
  return fechaCorta(valor) + " · " + f.toLocaleTimeString("es-PE", { hour: "2-digit", minute: "2-digit" });
}

function fechaParaBackend(f) {
  const dos = (n) => String(n).padStart(2, "0");
  return f.getFullYear() + "-" + dos(f.getMonth() + 1) + "-" + dos(f.getDate()) +
    "T" + dos(f.getHours()) + ":" + dos(f.getMinutes()) + ":" + dos(f.getSeconds());
}

function etiquetaEstado(estado) {
  const t = String(estado || "").replace(/_/g, " ").toLowerCase();
  return t.charAt(0).toUpperCase() + t.slice(1);
}

function claseEstado(estado) {
  const clases = {
    PENDIENTE: "e-pendiente", CONFIRMADO: "e-confirmado", EN_CAMINO: "e-camino",
    ENTREGADO: "e-entregado", CANCELADO: "e-cancelado",
  };
  return clases[String(estado).toUpperCase()] || "e-nuevo";
}

function insigniaEstadoPedido(estado) {
  return '<span class="estado ' + claseEstado(estado) + '">' + esc(etiquetaEstado(estado)) + "</span>";
}

function insigniaActivo(estado) {
  return Number(estado) === 1
    ? '<span class="estado e-activo">Activo</span>'
    : '<span class="estado e-cancelado">Inactivo</span>';
}

function nombreCompleto(usuario) {
  return usuario ? ((usuario.nombre || "") + " " + (usuario.apellido || "")).trim() : "—";
}

function imagenProducto(nombre) {
  const ruta = nombre ? "/producto/imagenProducto?nombreImagen=" + encodeURIComponent(nombre) : "/img/placeholder.jpg";
  return '<img src="' + ruta + '" alt="" onerror="this.onerror=null;this.src=\'/img/placeholder.jpg\'">';
}

function montarPagina(opciones) {
  const menu = SECCIONES.map((s) =>
    '<a href="' + s.url + '"' + (s.id === opciones.activo ? ' class="activo"' : "") + '>' +
    '<i class="fa-solid ' + s.icono + '"></i>' + s.texto + "</a>").join("");

  const boton = opciones.boton
    ? '<button class="btn btn-dorado" id="btn-principal"><i class="fa-solid ' + opciones.boton.icono + '"></i> ' + opciones.boton.texto + "</button>"
    : "";

  document.getElementById("raiz").innerHTML =
    '<div class="admin">' +
    '<aside class="lateral"><a href="../index.html" class="marca"><b>DOLCEVITA</b><small>Panel de administración</small></a>' +
    menu +
    '<a href="../index.html" class="salir"><i class="fa-solid fa-store"></i>Ver tienda</a></aside>' +
    '<div class="admin-main">' +
    '<div class="topbar"><span class="saludo">Hola, <strong>Administrador</strong></span>' +
    '<div class="usuario"><span><span class="avatar">A</span>admin@dolcevita.com</span></div></div>' +
    '<main class="cuerpo"><div class="cab-pag"><div><h1>' + opciones.titulo + "</h1><p>" + (opciones.subtitulo || "") + "</p></div>" + boton + "</div>" +
    '<div id="contenido"></div></main></div></div>';

  return {
    contenido: document.getElementById("contenido"),
    botonPrincipal: document.getElementById("btn-principal"),
  };
}

function avisar(mensaje, esError) {
  const aviso = document.createElement("div");
  aviso.className = "toast" + (esError ? " error" : "");
  aviso.textContent = mensaje;
  document.body.appendChild(aviso);
  setTimeout(() => aviso.remove(), esError ? 6000 : 3000);
}

function abrirModal(titulo, cuerpoHtml, alGuardar, textoGuardar) {
  const fondo = document.createElement("div");
  fondo.className = "modal-fondo";
  fondo.innerHTML =
    '<div class="modal"><form novalidate>' +
    '<div class="modal-cab"><h3>' + titulo + '</h3><button type="button" class="ic-btn" data-cerrar aria-label="Cerrar"><i class="fa-solid fa-xmark"></i></button></div>' +
    '<div class="modal-cuerpo">' + cuerpoHtml + '<div class="error-form" hidden></div></div>' +
    '<div class="modal-pie"><button type="button" class="btn btn-borde" data-cerrar>Cancelar</button>' +
    '<button type="submit" class="btn btn-dorado">' + (textoGuardar || "Guardar") + "</button></div>" +
    "</form></div>";
  document.body.appendChild(fondo);

  const formulario = fondo.querySelector("form");
  const error = fondo.querySelector(".error-form");
  const cerrar = () => fondo.remove();

  fondo.querySelectorAll("[data-cerrar]").forEach((b) => b.addEventListener("click", cerrar));
  fondo.addEventListener("mousedown", (e) => { if (e.target === fondo) cerrar(); });

  formulario.addEventListener("submit", async (e) => {
    e.preventDefault();
    const guardar = formulario.querySelector('[type="submit"]');
    error.hidden = true;
    guardar.disabled = true;
    try {
      await alGuardar(formulario);
      cerrar();
    } catch (err) {
      error.textContent = err.message;
      error.hidden = false;
      guardar.disabled = false;
    }
  });

  const primero = formulario.querySelector("input, select, textarea");
  if (primero) primero.focus();
  return formulario;
}

function confirmar(titulo, texto, textoBoton) {
  return new Promise((resolver) => {
    const fondo = document.createElement("div");
    fondo.className = "modal-fondo";
    fondo.innerHTML =
      '<div class="modal" style="max-width:440px"><div class="modal-cab"><h3>' + titulo + "</h3></div>" +
      '<div class="modal-cuerpo"><p style="padding-top:1rem">' + texto + "</p></div>" +
      '<div class="modal-pie"><button class="btn btn-borde" data-no>Cancelar</button>' +
      '<button class="btn btn-peligro" data-si>' + (textoBoton || "Aceptar") + "</button></div></div>";
    document.body.appendChild(fondo);
    const terminar = (valor) => { fondo.remove(); resolver(valor); };
    fondo.querySelector("[data-no]").addEventListener("click", () => terminar(false));
    fondo.querySelector("[data-si]").addEventListener("click", () => terminar(true));
  });
}

function mostrarCargando(elemento, columnas) {
  elemento.innerHTML = '<tr><td colspan="' + columnas + '" class="mensaje-tabla">Cargando...</td></tr>';
}

function mostrarErrorTabla(elemento, columnas, error) {
  elemento.innerHTML = '<tr><td colspan="' + columnas + '" class="mensaje-tabla error">No se pudo cargar la información. ' + esc(error.message) + "</td></tr>";
}
