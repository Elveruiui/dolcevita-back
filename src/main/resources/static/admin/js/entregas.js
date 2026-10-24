const { contenido, botonPrincipal } = montarPagina({
  activo: "entregas", titulo: "Entregas", subtitulo: "Asigna pedidos a los motorizados y sigue su avance.",
  boton: { texto: "Asignar entrega", icono: "fa-plus" },
  enlace: { texto: "Descargar reporte", icono: "fa-file-excel", url: "/reporte/entregas" },
});

let entregas = [];
let pedidos = [];
let usuarios = [];

contenido.innerHTML =
  '<div class="tabla-caja"><table class="tabla"><thead><tr><th>Pedido</th><th>Cliente</th><th>Dirección</th><th>Motorizado</th><th>Asignada</th><th>Costo</th><th>Estado</th><th>Acciones</th></tr></thead>' +
  '<tbody id="filas"></tbody></table></div>';
const filas = document.getElementById("filas");

const CLASES_ENTREGA = ["e-pendiente", "e-camino", "e-entregado", "e-cancelado"];

function motorizados() {
  return usuarios.filter((u) => u.rol && /motor/i.test(u.rol.nombre_rol) && Number(u.estado) === 1);
}

function dibujar() {
  if (entregas.length === 0) {
    filas.innerHTML = '<tr><td colspan="8" class="mensaje-tabla">Todavía no hay entregas asignadas.</td></tr>';
    return;
  }
  filas.innerHTML = entregas.slice().sort((a, b) => b.id_entrega - a.id_entrega).map((e) => {
    const pedido = e.pedido || {};
    return "<tr><td><strong>" + codigoPedido(pedido.id_pedido) + "</strong></td>" +
      "<td>" + esc(nombreCompleto(pedido.usuario)) + "</td>" +
      "<td>" + esc(pedido.direccion ? pedido.direccion.direccion : "—") + "</td>" +
      "<td>" + esc(nombreCompleto(e.motorizado)) + "</td>" +
      "<td>" + fechaHora(e.fecha_asignacion) + "</td>" +
      "<td>" + precio(e.costo_delivery) + "</td>" +
      '<td><span class="estado ' + (CLASES_ENTREGA[e.estado] || "e-nuevo") + '">' + (ESTADOS_ENTREGA[e.estado] || "—") + "</span></td>" +
      '<td><div class="acciones">' +
      '<button class="ic-btn" data-editar="' + e.id_entrega + '" title="Editar"><i class="fa-solid fa-pen"></i></button>' +
      '<button class="ic-btn rojo" data-eliminar="' + e.id_entrega + '" title="Eliminar"><i class="fa-solid fa-trash"></i></button>' +
      "</div></td></tr>";
  }).join("");
}

async function cargar() {
  mostrarCargando(filas, 8);
  try {
    [entregas, pedidos, usuarios] = await Promise.all([AdminApi.entregas(), AdminApi.pedidos(), AdminApi.usuarios()]);
    dibujar();
  } catch (error) {
    mostrarErrorTabla(filas, 8, error);
  }
}

/* Cuando la entrega avanza, el pedido avanza con ella. */
async function sincronizarPedido(idPedido, estadoEntrega) {
  const nuevo = { 1: "EN_CAMINO", 2: "ENTREGADO" }[estadoEntrega];
  const pedido = pedidos.find((p) => p.id_pedido === idPedido);
  if (!nuevo || !pedido || String(pedido.estado).toUpperCase() === "CANCELADO" || String(pedido.estado).toUpperCase() === nuevo) return;
  try {
    await AdminApi.cambiarEstadoPedido(idPedido, nuevo);
  } catch (error) {
    avisar("La entrega se guardó, pero no se pudo actualizar el estado del pedido.", true);
  }
}

function abrirFormulario(entrega) {
  const esNueva = !entrega;
  const ocupados = entregas.map((e) => e.pedido && e.pedido.id_pedido);
  const disponibles = pedidos.filter((p) =>
    /delivery/i.test(p.modalidad_entrega || "") && !ocupados.includes(p.id_pedido) &&
    !["CANCELADO", "ENTREGADO"].includes(String(p.estado).toUpperCase()));

  if (esNueva && disponibles.length === 0) {
    avisar("No hay pedidos de Delivery pendientes de asignar.", true);
    return;
  }
  const e = entrega || { motorizado: null, estado: 0, costo_delivery: "", observacion: "" };
  const campoPedido = esNueva
    ? '<label for="m-pedido">Pedido</label><select id="m-pedido"><option value="">Elige un pedido</option>' +
      disponibles.map((p) => '<option value="' + p.id_pedido + '">' + codigoPedido(p.id_pedido) + " · " + esc(nombreCompleto(p.usuario)) + "</option>").join("") + "</select>"
    : '<label>Pedido</label><input type="text" value="' + codigoPedido(e.pedido.id_pedido) + '" disabled>';

  abrirModal(esNueva ? "Asignar entrega" : "Editar entrega",
    campoPedido +
    '<label for="m-motorizado">Motorizado</label><select id="m-motorizado"><option value="">Elige un motorizado</option>' +
    motorizados().map((m) => '<option value="' + m.id_usuario + '"' + (e.motorizado && e.motorizado.id_usuario === m.id_usuario ? " selected" : "") + ">" + esc(nombreCompleto(m)) + "</option>").join("") + "</select>" +
    '<div class="dos-campos"><div><label for="m-costo">Costo de delivery (S/)</label><input type="number" id="m-costo" step="0.01" min="0" value="' + esc(e.costo_delivery) + '"></div>' +
    '<div><label for="m-estado">Estado</label><select id="m-estado">' +
    ESTADOS_ENTREGA.map((t, i) => '<option value="' + i + '"' + (e.estado === i ? " selected" : "") + ">" + t + "</option>").join("") + "</select></div></div>" +
    '<label for="m-obs">Observación</label><textarea id="m-obs" maxlength="300">' + esc(e.observacion) + "</textarea>" +
    '<p class="ayuda-campo">Al marcar la entrega como "En camino" o "Entregada", el pedido cambia de estado automáticamente.</p>',
    async (f) => {
      const idPedido = esNueva ? Number(f.querySelector("#m-pedido").value) : e.pedido.id_pedido;
      const idMotorizado = f.querySelector("#m-motorizado").value;
      const costo = f.querySelector("#m-costo").value;
      const estado = Number(f.querySelector("#m-estado").value);
      if (!idPedido) throw new Error("Elige un pedido.");
      if (!idMotorizado) throw new Error("Elige un motorizado.");
      if (costo === "" || Number(costo) < 0) throw new Error("Escribe el costo de delivery (puede ser 0).");

      const datos = {
        pedido: { id_pedido: idPedido },
        motorizado: { id_usuario: Number(idMotorizado) },
        estado,
        costo_delivery: Number(costo),
        observacion: f.querySelector("#m-obs").value.trim(),
        fecha_entrega: estado === 2 ? fechaParaBackend(new Date()) : (e.fecha_entrega || null),
      };
      if (esNueva) await AdminApi.crearEntrega(datos);
      else await AdminApi.actualizarEntrega(e.id_entrega, datos);
      await sincronizarPedido(idPedido, estado);
      avisar(esNueva ? "Entrega asignada." : "Entrega actualizada.");
      await cargar();
    });
}

async function eliminar(id) {
  const entrega = entregas.find((x) => x.id_entrega === id);
  const seguro = await confirmar("Eliminar entrega",
    "¿Quitar la entrega del pedido " + codigoPedido(entrega.pedido.id_pedido) + "? El pedido no se elimina.", "Eliminar");
  if (!seguro) return;
  try {
    await AdminApi.eliminarEntrega(id);
    avisar("Entrega eliminada.");
    await cargar();
  } catch (error) {
    avisar("No se pudo eliminar la entrega. " + error.message, true);
  }
}

filas.addEventListener("click", (ev) => {
  const editar = ev.target.closest("[data-editar]");
  const borrar = ev.target.closest("[data-eliminar]");
  if (editar) abrirFormulario(entregas.find((x) => x.id_entrega === Number(editar.dataset.editar)));
  if (borrar) eliminar(Number(borrar.dataset.eliminar));
});
botonPrincipal.addEventListener("click", () => abrirFormulario(null));

cargar();
