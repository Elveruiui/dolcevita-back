const zonaConfirmacion = document.getElementById("zonaConfirmacion");
const parametros = new URLSearchParams(location.search);
const idPedido = parametros.get("id");
const esNuevo = parametros.get("nuevo") === "1";

const CLASES_ESTADO = {
  PENDIENTE: "e-pendiente", CONFIRMADO: "e-confirmado", EN_CAMINO: "e-camino",
  ENTREGADO: "e-entregado", CANCELADO: "e-cancelado",
};

function formatearFecha(texto) {
  if (!texto) return "—";
  const [fecha, hora] = String(texto).split("T");
  const [a, m, d] = fecha.split("-");
  return d + "/" + m + "/" + a + (hora ? " · " + hora.slice(0, 5) : "");
}

function textoEstado(estado) {
  const t = String(estado || "").replace(/_/g, " ").toLowerCase();
  return t.charAt(0).toUpperCase() + t.slice(1);
}

async function iniciar() {
  if (!idPedido) {
    zonaConfirmacion.innerHTML = '<p class="mensaje-estado">No se indicó qué pedido mostrar. <a href="mis-pedidos.html" style="text-decoration:underline">Ir a mis pedidos</a></p>';
    return;
  }
  try {
    const p = await Api.pedido(idPedido);
    const productos = (p.detalles || []).map((d) => esc(d.producto.nombre) + " × " + d.cantidad).join(", ");
    const estado = String(p.estado || "").toUpperCase();
    const cabecera = esNuevo
      ? '<div class="check"><i class="fa-solid fa-check"></i></div><h1>¡Pedido registrado con éxito!</h1><p class="ayuda">Tu pedido ha sido registrado correctamente.</p>'
      : "<h1>Detalle de tu pedido</h1>";
    zonaConfirmacion.innerHTML = `
      ${cabecera}
      <p style="margin-top:.6rem"><strong>Número de pedido: #DV-${String(p.id_pedido).padStart(5, "0")}</strong></p>
      <div class="panel"><h3>Detalles del pedido</h3>
        <div class="fila-dato"><span>Productos</span><span>${productos}</span></div>
        <div class="fila-dato"><span>Fecha de entrega</span><span>${formatearFecha(p.fecha_entrega)}</span></div>
        <div class="fila-dato"><span>Modalidad</span><span>${esc(p.modalidad_entrega)}</span></div>
        <div class="fila-dato"><span>Dirección</span><span>${esc(p.direccion ? p.direccion.direccion : "—")}</span></div>
        <div class="fila-dato"><span>Estado</span><span class="estado ${CLASES_ESTADO[estado] || "e-nuevo"}">${esc(textoEstado(estado))}</span></div>
        <div class="fila-total"><span>Total</span><span class="monto">${formatearPrecio(p.total)}</span></div>
      </div>
      <a class="btn btn-dorado btn-bloque" href="mis-pedidos.html">Ir a mis pedidos</a>`;
  } catch (error) {
    console.error(error);
    zonaConfirmacion.innerHTML = '<p class="mensaje-estado error">No encontramos este pedido. <a href="mis-pedidos.html" style="text-decoration:underline">Ir a mis pedidos</a></p>';
  }
}
iniciar();
