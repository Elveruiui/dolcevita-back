async function pedir(ruta) {
  const respuesta = await fetch(ruta);
  if (!respuesta.ok) {
    throw new Error("Error " + respuesta.status + " al consultar " + ruta);
  }
  return respuesta.json();
}

async function enviar(ruta, metodo, cuerpo) {
  const respuesta = await fetch(ruta, {
    method: metodo,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(cuerpo),
  });
  const texto = await respuesta.text();
  if (!respuesta.ok) {
    let mensaje = texto || "Error " + respuesta.status;
    try {
      const interpretado = JSON.parse(texto); 
      if (typeof interpretado === "string") mensaje = interpretado;
    } catch (e) {  }
    throw new Error(mensaje);
  }
  return texto ? JSON.parse(texto) : null;
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
