const listaPedidos = document.getElementById("listaPedidos");
const pestanas = document.getElementById("pestanas");

const GRUPOS = [
  { id: "todos", texto: "Todos", estados: null },
  { id: "proceso", texto: "En proceso", estados: ["PENDIENTE", "CONFIRMADO"] },
  { id: "camino", texto: "En camino", estados: ["EN_CAMINO"] },
  { id: "entregados", texto: "Entregados", estados: ["ENTREGADO"] },
  { id: "cancelados", texto: "Cancelados", estados: ["CANCELADO"] },
];

const CLASES_ESTADO = {
  PENDIENTE: "e-pendiente", CONFIRMADO: "e-confirmado", EN_CAMINO: "e-camino",
  ENTREGADO: "e-entregado", CANCELADO: "e-cancelado",
};

let pedidos = [];
let grupoActivo = "todos";

function estadoDe(pedido) {
  return String(pedido.estado || "").toUpperCase();
}

function textoEstado(estado) {
  const t = estado.replace(/_/g, " ").toLowerCase();
  return t.charAt(0).toUpperCase() + t.slice(1);
}

function fechaCorta(valor) {
  if (!valor) return "—";
  const [a, m, d] = String(valor).split("T")[0].split("-");
  return d + "/" + m + "/" + a;
}

function codigo(id) {
  return "#DV-" + String(id).padStart(5, "0");
}

function dibujarPestanas() {
  pestanas.innerHTML = GRUPOS.map((g) =>
    '<button class="pestana' + (g.id === grupoActivo ? " activo" : "") + '" data-grupo="' + g.id + '">' + g.texto + "</button>").join("");
}

function dibujarLista() {
  const grupo = GRUPOS.find((g) => g.id === grupoActivo);
  const visibles = pedidos.filter((p) => !grupo.estados || grupo.estados.includes(estadoDe(p)));

  if (visibles.length === 0) {
    listaPedidos.innerHTML = pedidos.length === 0
      ? '<div class="carrito-vacio"><p>Todavía no tienes pedidos.</p><a class="btn btn-dorado" href="menu.html">Ver el menú</a></div>'
      : '<p class="mensaje-estado">No tienes pedidos en esta categoría.</p>';
    return;
  }

  listaPedidos.innerHTML = visibles.map((p) => {
    const detalles = p.detalles || [];
    const primero = detalles[0] && detalles[0].producto;
    const cantidad = detalles.length;
    const estado = estadoDe(p);
    return '<div class="pedido">' +
      "<div><strong>" + codigo(p.id_pedido) + "</strong><small>" + fechaCorta(p.fecha_pedido) + "</small></div>" +
      '<div class="prod"><img src="' + urlImagen(primero ? primero.imagen : null) + '" alt="">' +
      "<div>" + esc(primero ? primero.nombre : "Pedido") + "<small>" + cantidad + (cantidad === 1 ? " producto" : " productos") + "</small></div></div>" +
      '<div><span class="estado ' + (CLASES_ESTADO[estado] || "e-nuevo") + '">' + esc(textoEstado(estado)) + "</span></div>" +
      '<div><a class="btn btn-borde btn-sm" href="confirmacion.html?id=' + p.id_pedido + '">Ver detalle</a></div></div>';
  }).join("");
  listaPedidos.querySelectorAll("img").forEach(conRespaldo);
}

async function iniciar() {
  dibujarPestanas();
  try {
    const cliente = await Sesion.clienteActual();
    pedidos = (await pedir("/pedido/listarPorUsuario/" + cliente.id_usuario))
      .sort((a, b) => b.id_pedido - a.id_pedido);
    dibujarLista();
  } catch (error) {
    console.error(error);
    listaPedidos.innerHTML = '<p class="mensaje-estado error">No pudimos cargar tus pedidos. ' + esc(error.message) + "</p>";
  }
}

pestanas.addEventListener("click", (e) => {
  const boton = e.target.closest("[data-grupo]");
  if (!boton) return;
  grupoActivo = boton.dataset.grupo;
  dibujarPestanas();
  dibujarLista();
});

iniciar();
