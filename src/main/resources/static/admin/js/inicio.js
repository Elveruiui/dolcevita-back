const { contenido } = montarPagina({
  activo: "inicio", titulo: "Panel de administración", subtitulo: "Resumen general del sistema.",
<<<<<<< HEAD
  reporte: "pedidos",
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
});

contenido.innerHTML = '<p class="mensaje-estado">Cargando resumen...</p>';

const DIAS = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];

function contar(pedidos, estado) {
  return pedidos.filter((p) => String(p.estado).toUpperCase() === estado).length;
}

function tarjeta(clase, icono, cifra, texto) {
  return '<div class="kpi"><span class="ic ' + clase + '"><i class="fa-solid ' + icono + '"></i></span><div><div class="num">' + cifra + "</div><small>" + texto + "</small></div></div>";
}

function pedidosPorDia(pedidos) {
  const dias = [];
  for (let i = 6; i >= 0; i--) {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    d.setDate(d.getDate() - i);
    dias.push({ fecha: d, cantidad: 0 });
  }
  pedidos.forEach((p) => {
    const f = aFecha(p.fecha_pedido);
    if (!f) return;
    const dia = dias.find((x) => x.fecha.toDateString() === f.toDateString());
    if (dia) dia.cantidad++;
  });
  return dias;
}

function dibujar(pedidos, productos) {
  const vigentes = pedidos.filter((p) => String(p.estado).toUpperCase() !== "CANCELADO");
  const ventas = vigentes.reduce((suma, p) => suma + Number(p.total || 0), 0);

  const dias = pedidosPorDia(pedidos);
  const maximo = Math.max(1, ...dias.map((d) => d.cantidad));
  const barras = dias.map((d) =>
    '<div style="height:' + Math.max(4, Math.round(d.cantidad / maximo * 100)) + '%" title="' + d.cantidad + ' pedidos"></div>').join("");
  const etiquetas = dias.map((d) => "<span>" + DIAS[d.fecha.getDay()] + "<br><b>" + d.cantidad + "</b></span>").join("");

  const delivery = pedidos.filter((p) => /delivery/i.test(p.modalidad_entrega || "")).length;
  const porcentaje = pedidos.length ? Math.round(delivery / pedidos.length * 100) : 0;

  const recientes = pedidos.slice().sort((a, b) => b.id_pedido - a.id_pedido).slice(0, 5);
  const filasRecientes = recientes.map((p) =>
    '<tr><td><a href="pedido.html?id=' + p.id_pedido + '"><strong>' + codigoPedido(p.id_pedido) + "</strong></a></td><td>" +
    esc(nombreCompleto(p.usuario)) + "</td><td>" + precio(p.total) + "</td><td>" + insigniaEstadoPedido(p.estado) + "</td></tr>").join("");

  const pocos = productos.filter((p) => Number(p.estado) === 1 && p.stock <= 5).sort((a, b) => a.stock - b.stock).slice(0, 5);
  const filasStock = pocos.map((p) =>
    '<tr><td><div class="persona">' + imagenProducto(p.imagen) + "<span>" + esc(p.nombre) + '</span></div></td><td class="stock-bajo">' + p.stock + "</td></tr>").join("");

  contenido.innerHTML =
    '<div class="kpis cinco">' +
    tarjeta("ic-ambar", "fa-cart-shopping", contar(pedidos, "PENDIENTE"), "Pendientes") +
    tarjeta("ic-azul", "fa-clipboard-check", contar(pedidos, "CONFIRMADO"), "Confirmados") +
    tarjeta("ic-rosa", "fa-truck", contar(pedidos, "EN_CAMINO"), "En camino") +
    tarjeta("ic-verde", "fa-circle-check", contar(pedidos, "ENTREGADO"), "Entregados") +
    tarjeta("ic-rojo", "fa-circle-xmark", contar(pedidos, "CANCELADO"), "Cancelados") +
    "</div>" +
    '<div class="resumen-ventas"><span>Ventas acumuladas (sin pedidos cancelados)</span><strong>' + precio(ventas) + "</strong></div>" +
    '<div class="graficos">' +
    '<section class="panel"><h3>Pedidos de los últimos 7 días</h3><div class="barras">' + barras + '</div><div class="barras-etq">' + etiquetas + "</div></section>" +
    '<section class="panel"><h3>Modalidad de entrega</h3><div class="dona" style="background:conic-gradient(var(--dorado) 0 ' + porcentaje + '%, var(--rosa-claro) ' + porcentaje + '% 100%)"><span>' + porcentaje + '%<small>Delivery</small></span></div>' +
    '<div class="leyenda">Delivery ' + porcentaje + "% · Recojo en el local " + (pedidos.length ? 100 - porcentaje : 0) + "%</div></section></div>" +
    '<div class="rejilla-2">' +
    '<section class="panel"><h3>Últimos pedidos</h3><div class="tabla-caja" style="box-shadow:none"><table class="tabla"><thead><tr><th>Código</th><th>Cliente</th><th>Total</th><th>Estado</th></tr></thead><tbody>' +
    (filasRecientes || '<tr><td colspan="4" class="mensaje-tabla">Aún no hay pedidos.</td></tr>') + "</tbody></table></div></section>" +
    '<section class="panel"><h3>Productos con poco stock</h3><div class="tabla-caja" style="box-shadow:none"><table class="tabla"><thead><tr><th>Producto</th><th>Stock</th></tr></thead><tbody>' +
    (filasStock || '<tr><td colspan="2" class="mensaje-tabla">Todos los productos tienen stock suficiente.</td></tr>') + "</tbody></table></div></section></div>";
}

Promise.all([AdminApi.pedidos(), AdminApi.productos()])
  .then(([pedidos, productos]) => dibujar(pedidos, productos))
  .catch((error) => {
    contenido.innerHTML = '<p class="mensaje-estado error">No se pudo cargar el resumen. ' + esc(error.message) + "</p>";
  });
