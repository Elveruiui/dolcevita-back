const { contenido, botonPrincipal } = montarPagina({
<<<<<<< HEAD
  activo: "entregas",
  titulo: "Entregas",
  subtitulo: "Asigna pedidos a los motorizados y sigue su avance.",
  reporte: "entregas",
  boton: { texto: "Asignar entrega", icono: "fa-plus" },
=======
  activo: "entregas", titulo: "Entregas", subtitulo: "Asigna pedidos a los motorizados y sigue su avance.",
  boton: { texto: "Asignar entrega", icono: "fa-plus" },
  enlace: { texto: "Descargar reporte", icono: "fa-file-excel", url: "/reporte/entregas" },
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
});

let entregas = [];
let pedidos = [];
let usuarios = [];
<<<<<<< HEAD
let usuarioActual = null;
let esMotorizado = false;
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e

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
<<<<<<< HEAD
    const mensajeVacio = esMotorizado
      ? "No tienes entregas asignadas por el momento."
      : "Todavía no hay entregas asignadas.";
    filas.innerHTML = '<tr><td colspan="8" class="mensaje-tabla">' + mensajeVacio + "</td></tr>";
=======
    filas.innerHTML = '<tr><td colspan="8" class="mensaje-tabla">Todavía no hay entregas asignadas.</td></tr>';
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
    return;
  }
  filas.innerHTML = entregas.slice().sort((a, b) => b.id_entrega - a.id_entrega).map((e) => {
    const pedido = e.pedido || {};
<<<<<<< HEAD
    const botonEliminar = esMotorizado
      ? ""
      : '<button class="ic-btn rojo" data-eliminar="' + e.id_entrega + '" title="Eliminar"><i class="fa-solid fa-trash"></i></button>';

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
    return "<tr><td><strong>" + codigoPedido(pedido.id_pedido) + "</strong></td>" +
      "<td>" + esc(nombreCompleto(pedido.usuario)) + "</td>" +
      "<td>" + esc(pedido.direccion ? pedido.direccion.direccion : "—") + "</td>" +
      "<td>" + esc(nombreCompleto(e.motorizado)) + "</td>" +
      "<td>" + fechaHora(e.fecha_asignacion) + "</td>" +
      "<td>" + precio(e.costo_delivery) + "</td>" +
      '<td><span class="estado ' + (CLASES_ENTREGA[e.estado] || "e-nuevo") + '">' + (ESTADOS_ENTREGA[e.estado] || "—") + "</span></td>" +
      '<td><div class="acciones">' +
<<<<<<< HEAD
      '<button class="ic-btn" data-editar="' + e.id_entrega + '" title="Actualizar entrega"><i class="fa-solid fa-pen"></i></button>' +
      botonEliminar +
=======
      '<button class="ic-btn" data-editar="' + e.id_entrega + '" title="Editar"><i class="fa-solid fa-pen"></i></button>' +
      '<button class="ic-btn rojo" data-eliminar="' + e.id_entrega + '" title="Eliminar"><i class="fa-solid fa-trash"></i></button>' +
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
      "</div></td></tr>";
  }).join("");
}

async function cargar() {
  mostrarCargando(filas, 8);
  try {
<<<<<<< HEAD
    if (!usuarioActual) {
      usuarioActual = await llamar("/auth/me", "GET");
      const nombreRol = usuarioActual && usuarioActual.rol ? usuarioActual.rol.nombre_rol : "";
      esMotorizado = /motor/i.test(nombreRol);

      if (esMotorizado) {
        if (botonPrincipal) botonPrincipal.style.display = "none";
        const subtitulo = document.querySelector(".cab-pag p");
        if (subtitulo) subtitulo.textContent = "Consulta tus entregas asignadas y actualiza su estado.";
      }
    }

    if (esMotorizado) {
      entregas = await llamar("/entrega/listarPorMotorizado/" + usuarioActual.id_usuario, "GET");
    } else {
      [entregas, pedidos, usuarios] = await Promise.all([
        AdminApi.entregas(),
        AdminApi.pedidos(),
        AdminApi.usuarios(),
      ]);
    }
=======
    [entregas, pedidos, usuarios] = await Promise.all([AdminApi.entregas(), AdminApi.pedidos(), AdminApi.usuarios()]);
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
    dibujar();
  } catch (error) {
    mostrarErrorTabla(filas, 8, error);
  }
}

/* Cuando la entrega avanza, el pedido avanza con ella. */
<<<<<<< HEAD
async function sincronizarPedido(pedidoObj, estadoEntrega) {
  const nuevo = { 1: "EN_CAMINO", 2: "ENTREGADO" }[estadoEntrega];
  if (!nuevo || !pedidoObj || !pedidoObj.id_pedido) return;
  const estadoActual = String(pedidoObj.estado || "").toUpperCase();
  if (estadoActual === "CANCELADO" || estadoActual === nuevo) return;
  try {
    await AdminApi.cambiarEstadoPedido(pedidoObj.id_pedido, nuevo);
=======
async function sincronizarPedido(idPedido, estadoEntrega) {
  const nuevo = { 1: "EN_CAMINO", 2: "ENTREGADO" }[estadoEntrega];
  const pedido = pedidos.find((p) => p.id_pedido === idPedido);
  if (!nuevo || !pedido || String(pedido.estado).toUpperCase() === "CANCELADO" || String(pedido.estado).toUpperCase() === nuevo) return;
  try {
    await AdminApi.cambiarEstadoPedido(idPedido, nuevo);
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
  } catch (error) {
    avisar("La entrega se guardó, pero no se pudo actualizar el estado del pedido.", true);
  }
}

function abrirFormulario(entrega) {
  const esNueva = !entrega;
<<<<<<< HEAD
  if (esNueva && esMotorizado) return;

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
  const ocupados = entregas.map((e) => e.pedido && e.pedido.id_pedido);
  const disponibles = pedidos.filter((p) =>
    /delivery/i.test(p.modalidad_entrega || "") && !ocupados.includes(p.id_pedido) &&
    !["CANCELADO", "ENTREGADO"].includes(String(p.estado).toUpperCase()));

  if (esNueva && disponibles.length === 0) {
    avisar("No hay pedidos de Delivery pendientes de asignar.", true);
    return;
  }
<<<<<<< HEAD

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
  const e = entrega || { motorizado: null, estado: 0, costo_delivery: "", observacion: "" };
  const campoPedido = esNueva
    ? '<label for="m-pedido">Pedido</label><select id="m-pedido"><option value="">Elige un pedido</option>' +
      disponibles.map((p) => '<option value="' + p.id_pedido + '">' + codigoPedido(p.id_pedido) + " · " + esc(nombreCompleto(p.usuario)) + "</option>").join("") + "</select>"
    : '<label>Pedido</label><input type="text" value="' + codigoPedido(e.pedido.id_pedido) + '" disabled>';

<<<<<<< HEAD
  const campoMotorizado = esMotorizado
    ? '<label>Motorizado</label><input type="text" value="' + esc(nombreCompleto(e.motorizado)) + '" disabled>'
    : '<label for="m-motorizado">Motorizado</label><select id="m-motorizado"><option value="">Elige un motorizado</option>' +
      motorizados().map((m) => '<option value="' + m.id_usuario + '"' + (e.motorizado && e.motorizado.id_usuario === m.id_usuario ? " selected" : "") + ">" + esc(nombreCompleto(m)) + "</option>").join("") + "</select>";

  const atributoCosto = esMotorizado ? " disabled" : "";

  abrirModal(esNueva ? "Asignar entrega" : (esMotorizado ? "Actualizar estado de entrega" : "Editar entrega"),
    campoPedido +
    campoMotorizado +
    '<div class="dos-campos"><div><label for="m-costo">Costo de delivery (S/)</label><input type="number" id="m-costo" step="0.01" min="0" value="' + esc(e.costo_delivery) + '"' + atributoCosto + '></div>' +
=======
  abrirModal(esNueva ? "Asignar entrega" : "Editar entrega",
    campoPedido +
    '<label for="m-motorizado">Motorizado</label><select id="m-motorizado"><option value="">Elige un motorizado</option>' +
    motorizados().map((m) => '<option value="' + m.id_usuario + '"' + (e.motorizado && e.motorizado.id_usuario === m.id_usuario ? " selected" : "") + ">" + esc(nombreCompleto(m)) + "</option>").join("") + "</select>" +
    '<div class="dos-campos"><div><label for="m-costo">Costo de delivery (S/)</label><input type="number" id="m-costo" step="0.01" min="0" value="' + esc(e.costo_delivery) + '"></div>' +
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
    '<div><label for="m-estado">Estado</label><select id="m-estado">' +
    ESTADOS_ENTREGA.map((t, i) => '<option value="' + i + '"' + (e.estado === i ? " selected" : "") + ">" + t + "</option>").join("") + "</select></div></div>" +
    '<label for="m-obs">Observación</label><textarea id="m-obs" maxlength="300">' + esc(e.observacion) + "</textarea>" +
    '<p class="ayuda-campo">Al marcar la entrega como "En camino" o "Entregada", el pedido cambia de estado automáticamente.</p>',
    async (f) => {
      const idPedido = esNueva ? Number(f.querySelector("#m-pedido").value) : e.pedido.id_pedido;
<<<<<<< HEAD
      const idMotorizado = esMotorizado
        ? (e.motorizado && e.motorizado.id_usuario)
        : f.querySelector("#m-motorizado").value;
      const costo = esMotorizado ? e.costo_delivery : f.querySelector("#m-costo").value;
      const estado = Number(f.querySelector("#m-estado").value);

=======
      const idMotorizado = f.querySelector("#m-motorizado").value;
      const costo = f.querySelector("#m-costo").value;
      const estado = Number(f.querySelector("#m-estado").value);
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
      if (!idPedido) throw new Error("Elige un pedido.");
      if (!idMotorizado) throw new Error("Elige un motorizado.");
      if (costo === "" || Number(costo) < 0) throw new Error("Escribe el costo de delivery (puede ser 0).");

<<<<<<< HEAD
      const pedidoActual = esNueva
        ? pedidos.find((p) => p.id_pedido === idPedido)
        : e.pedido;

=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
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
<<<<<<< HEAD
      await sincronizarPedido(pedidoActual, estado);
=======
      await sincronizarPedido(idPedido, estado);
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
      avisar(esNueva ? "Entrega asignada." : "Entrega actualizada.");
      await cargar();
    });
}

async function eliminar(id) {
<<<<<<< HEAD
  if (esMotorizado) return;
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
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
<<<<<<< HEAD
  if (borrar && !esMotorizado) eliminar(Number(borrar.dataset.eliminar));
});
if (botonPrincipal) {
  botonPrincipal.addEventListener("click", () => abrirFormulario(null));
}

cargar();
=======
  if (borrar) eliminar(Number(borrar.dataset.eliminar));
});
botonPrincipal.addEventListener("click", () => abrirFormulario(null));

cargar();
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
