const { contenido } = montarPagina({
  activo: "pedidos", titulo: "Pedidos registrados", subtitulo: "Revisa los pedidos y actualiza su estado.",
  reporte: "pedidos",
});

let pedidos = [];
let estadoActivo = "TODOS";

contenido.innerHTML =
  '<div class="pestanas" id="pestanas"></div>' +
  '<div class="filtros">' +
  '<div class="crece"><label for="f-texto">Buscar</label><input type="text" id="f-texto" placeholder="Código, cliente o producto..."></div>' +
  '<div><label for="f-fecha">Fecha de entrega</label><input type="date" id="f-fecha"></div>' +
  "</div>" +
  '<div class="tabla-caja"><table class="tabla"><thead><tr><th>Código</th><th>Cliente</th><th>Entrega</th><th>Modalidad</th><th>Total</th><th>Estado</th><th>Acciones</th></tr></thead>' +
  '<tbody id="filas"></tbody></table></div>';

const filas = document.getElementById("filas");
const pestanas = document.getElementById("pestanas");
const filtroTexto = document.getElementById("f-texto");
const filtroFecha = document.getElementById("f-fecha");

function estadoDe(p) { return String(p.estado || "").toUpperCase(); }

function dibujarPestanas() {
  const opciones = ["TODOS"].concat(ESTADOS_PEDIDO);
  pestanas.innerHTML = opciones.map((e) => {
    const cantidad = e === "TODOS" ? pedidos.length : pedidos.filter((p) => estadoDe(p) === e).length;
    return '<button class="pestana' + (e === estadoActivo ? " activo" : "") + '" data-estado="' + e + '">' +
      (e === "TODOS" ? "Todos" : etiquetaEstado(e)) + "<small>" + cantidad + "</small></button>";
  }).join("");
}

function textoDePedido(p) {
  const productos = (p.detalles || []).map((d) => d.producto ? d.producto.nombre : "").join(" ");
  return (codigoPedido(p.id_pedido) + " " + nombreCompleto(p.usuario) + " " + productos).toLowerCase();
}

function dibujar() {
  const texto = filtroTexto.value.trim().toLowerCase();
  const fecha = filtroFecha.value;
  const lista = pedidos
    .filter((p) => estadoActivo === "TODOS" || estadoDe(p) === estadoActivo)
    .filter((p) => !texto || textoDePedido(p).includes(texto))
    .filter((p) => !fecha || String(p.fecha_entrega || "").slice(0, 10) === fecha ||
      (Array.isArray(p.fecha_entrega) && fechaParaBackend(aFecha(p.fecha_entrega)).slice(0, 10) === fecha))
    .sort((a, b) => b.id_pedido - a.id_pedido);

  if (lista.length === 0) {
    filas.innerHTML = '<tr><td colspan="7" class="mensaje-tabla">No hay pedidos para mostrar.</td></tr>';
    return;
  }
  filas.innerHTML = lista.map((p) =>
    "<tr><td><strong>" + codigoPedido(p.id_pedido) + "</strong></td>" +
    "<td>" + esc(nombreCompleto(p.usuario)) + "</td>" +
    "<td>" + fechaHora(p.fecha_entrega) + "</td>" +
    "<td>" + esc(p.modalidad_entrega || "—") + "</td>" +
    "<td>" + precio(p.total) + "</td>" +
    "<td>" + insigniaEstadoPedido(p.estado) + "</td>" +
    '<td><div class="acciones"><a class="ic-btn" href="pedido.html?id=' + p.id_pedido + '" title="Ver y actualizar"><i class="fa-solid fa-eye"></i></a></div></td></tr>').join("");
}

async function cargar() {
  mostrarCargando(filas, 7);
  try {
    pedidos = await AdminApi.pedidos();
    dibujarPestanas();
    dibujar();
  } catch (error) {
    mostrarErrorTabla(filas, 7, error);
  }
}

pestanas.addEventListener("click", (e) => {
  const boton = e.target.closest("[data-estado]");
  if (!boton) return;
  estadoActivo = boton.dataset.estado;
  dibujarPestanas();
  dibujar();
});
filtroTexto.addEventListener("input", dibujar);
filtroFecha.addEventListener("input", dibujar);

cargar();
