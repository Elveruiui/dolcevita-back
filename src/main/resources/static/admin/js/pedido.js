const { contenido } = montarPagina({
  activo: "pedidos", titulo: "Detalle del pedido", subtitulo: "Consulta los datos y cambia el estado.",
<<<<<<< HEAD
  reporte: "pedidos",
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
});

const idPedido = new URLSearchParams(location.search).get("id");

function dibujar(p) {
  const estadoActual = String(p.estado || "").toUpperCase();
  const cancelado = estadoActual === "CANCELADO";
  const opciones = ESTADOS_PEDIDO.includes(estadoActual) ? ESTADOS_PEDIDO : [estadoActual].concat(ESTADOS_PEDIDO);
  const detalles = p.detalles || [];

  const filasProductos = detalles.map((d) =>
    "<tr><td>" + esc(d.producto ? d.producto.nombre : "—") + "</td><td>" + d.cantidad + "</td>" +
    "<td>" + precio(d.precio_unitario) + "</td><td>" + precio(d.subtotal) + "</td></tr>").join("");

  contenido.innerHTML =
    '<a class="enlace-volver" href="pedidos.html"><i class="fa-solid fa-arrow-left"></i> Volver a pedidos</a>' +
    '<div class="detalle-admin"><div>' +
    '<section class="panel"><h3>Pedido ' + codigoPedido(p.id_pedido) + "</h3>" +
    '<div class="fila-dato"><span>Estado</span><span>' + insigniaEstadoPedido(p.estado) + "</span></div>" +
    '<div class="fila-dato"><span>Cliente</span><span>' + esc(nombreCompleto(p.usuario)) + "</span></div>" +
    '<div class="fila-dato"><span>Correo</span><span>' + esc(p.usuario ? p.usuario.correo : "—") + "</span></div>" +
    '<div class="fila-dato"><span>Teléfono</span><span>' + esc(p.usuario && p.usuario.telefono ? p.usuario.telefono : "—") + "</span></div>" +
    '<div class="fila-dato"><span>Modalidad</span><span>' + esc(p.modalidad_entrega || "—") + "</span></div>" +
    '<div class="fila-dato"><span>Dirección</span><span>' + esc(p.direccion ? p.direccion.direccion : "—") + "</span></div>" +
    '<div class="fila-dato"><span>Fecha del pedido</span><span>' + fechaHora(p.fecha_pedido) + "</span></div>" +
    '<div class="fila-dato"><span>Fecha de entrega</span><span>' + fechaHora(p.fecha_entrega) + "</span></div>" +
    '<div class="fila-dato"><span>Observaciones</span><span>' + esc(p.observacion || "—") + "</span></div>" +
    "</section>" +
    '<section class="panel" style="margin-top:1.2rem"><h3>Actualizar estado</h3>' +
    '<label for="nuevo-estado">Estado del pedido</label>' +
    '<select id="nuevo-estado"' + (cancelado ? " disabled" : "") + ">" +
    opciones.map((e) => '<option value="' + e + '"' + (e === estadoActual ? " selected" : "") + ">" + etiquetaEstado(e) + "</option>").join("") +
    "</select>" +
    '<p class="ayuda-campo">' + (cancelado
      ? "Un pedido cancelado ya no se puede reactivar."
      : "Al cancelar un pedido, el stock de sus productos se devuelve automáticamente.") + "</p>" +
    '<div id="error-estado" class="error-form" hidden></div>' +
    '<button class="btn btn-dorado" id="guardar-estado" style="margin-top:1rem"' + (cancelado ? " disabled" : "") + '>Guardar estado</button>' +
    "</section></div>" +
    '<section class="panel"><h3>Productos del pedido</h3>' +
    '<div class="tabla-caja" style="box-shadow:none"><table class="tabla"><thead><tr><th>Producto</th><th>Cant.</th><th>Precio</th><th>Subtotal</th></tr></thead><tbody>' +
    (filasProductos || '<tr><td colspan="4" class="mensaje-tabla">Sin productos.</td></tr>') +
    '</tbody></table></div><div class="total-fila"><span>Total</span><span>' + precio(p.total) + "</span></div></section></div>";

  if (!cancelado) {
    document.getElementById("guardar-estado").addEventListener("click", () => guardarEstado(p, estadoActual));
  }
}

async function guardarEstado(pedido, estadoActual) {
  const nuevo = document.getElementById("nuevo-estado").value;
  const error = document.getElementById("error-estado");
  error.hidden = true;
  if (nuevo === estadoActual) {
    error.textContent = "Elige un estado distinto al actual.";
    error.hidden = false;
    return;
  }
  if (nuevo === "CANCELADO") {
    const seguro = await confirmar("Cancelar pedido",
      "¿Cancelar el pedido " + codigoPedido(pedido.id_pedido) + "? Se devolverá el stock de sus productos y no podrá reactivarse.", "Cancelar pedido");
    if (!seguro) return;
  }
  try {
    await AdminApi.cambiarEstadoPedido(pedido.id_pedido, nuevo);
    avisar("Estado actualizado a " + etiquetaEstado(nuevo) + ".");
    await cargar();
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  }
}

async function cargar() {
  if (!idPedido) {
    contenido.innerHTML = '<p class="mensaje-estado">No se indicó qué pedido mostrar. <a href="pedidos.html">Volver a pedidos</a></p>';
    return;
  }
  try {
    dibujar(await AdminApi.pedido(idPedido));
  } catch (error) {
    contenido.innerHTML = '<p class="mensaje-estado error">No se pudo cargar el pedido. ' + esc(error.message) + ' <a href="pedidos.html">Volver a pedidos</a></p>';
  }
}

cargar();
