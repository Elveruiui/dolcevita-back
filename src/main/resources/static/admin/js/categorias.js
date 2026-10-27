const { contenido, botonPrincipal } = montarPagina({
  activo: "categorias", titulo: "Categorías", subtitulo: "Organiza los tipos de producto de la tienda.",
  reporte: "categorias",
  boton: { texto: "Nueva categoría", icono: "fa-plus" },
});

let categorias = [];
let productos = [];

contenido.innerHTML =
  '<div class="tabla-caja"><table class="tabla"><thead><tr><th>Categoría</th><th>Descripción</th><th>Productos</th><th>Estado</th><th>Acciones</th></tr></thead>' +
  '<tbody id="filas"></tbody></table></div>';
const filas = document.getElementById("filas");

function dibujar() {
  if (categorias.length === 0) {
    filas.innerHTML = '<tr><td colspan="5" class="mensaje-tabla">Todavía no hay categorías.</td></tr>';
    return;
  }
  filas.innerHTML = categorias.map((c) => {
    const cantidad = productos.filter((p) => p.categoria && p.categoria.id_categoria === c.id_categoria).length;
    return "<tr><td><strong>" + esc(c.nombre) + "</strong></td>" +
      "<td>" + esc(c.descripcion || "—") + "</td>" +
      "<td>" + cantidad + "</td>" +
      "<td>" + insigniaActivo(c.estado) + "</td>" +
      '<td><div class="acciones">' +
      '<button class="ic-btn" data-editar="' + c.id_categoria + '" title="Editar"><i class="fa-solid fa-pen"></i></button>' +
      '<button class="ic-btn rojo" data-eliminar="' + c.id_categoria + '" title="Eliminar"><i class="fa-solid fa-trash"></i></button>' +
      "</div></td></tr>";
  }).join("");
}

async function cargar() {
  mostrarCargando(filas, 5);
  try {
    [categorias, productos] = await Promise.all([AdminApi.categorias(), AdminApi.productos()]);
    dibujar();
  } catch (error) {
    mostrarErrorTabla(filas, 5, error);
  }
}

function abrirFormulario(categoria) {
  const esNueva = !categoria;
  const c = categoria || { nombre: "", descripcion: "", estado: 1 };
  abrirModal(esNueva ? "Nueva categoría" : "Editar categoría",
    '<label for="m-nombre">Nombre</label><input type="text" id="m-nombre" maxlength="80" value="' + esc(c.nombre) + '">' +
    '<label for="m-descripcion">Descripción</label><textarea id="m-descripcion" maxlength="300">' + esc(c.descripcion) + "</textarea>" +
    '<label for="m-estado">Estado</label><select id="m-estado"><option value="1"' + (Number(c.estado) === 1 ? " selected" : "") + '>Activa</option><option value="0"' + (Number(c.estado) === 0 ? " selected" : "") + ">Inactiva</option></select>",
    async (f) => {
      const nombre = f.querySelector("#m-nombre").value.trim();
      if (!nombre) throw new Error("Escribe el nombre de la categoría.");
      const datos = {
        nombre,
        descripcion: f.querySelector("#m-descripcion").value.trim(),
        estado: Number(f.querySelector("#m-estado").value),
      };
      if (esNueva) await AdminApi.crearCategoria(datos);
      else await AdminApi.actualizarCategoria(c.id_categoria, datos);
      avisar(esNueva ? "Categoría creada." : "Categoría actualizada.");
      await cargar();
    });
}

async function eliminar(id) {
  const categoria = categorias.find((c) => c.id_categoria === id);
  const seguro = await confirmar("Eliminar categoría",
    "¿Eliminar <strong>" + esc(categoria.nombre) + "</strong>? Esta acción no se puede deshacer.", "Eliminar");
  if (!seguro) return;
  try {
    await AdminApi.eliminarCategoria(id);
    avisar("Categoría eliminada.");
    await cargar();
  } catch (error) {
    avisar("No se pudo eliminar. Si tiene productos, cámbiala a Inactiva en lugar de eliminarla.", true);
  }
}

filas.addEventListener("click", (e) => {
  const editar = e.target.closest("[data-editar]");
  const borrar = e.target.closest("[data-eliminar]");
  if (editar) abrirFormulario(categorias.find((c) => c.id_categoria === Number(editar.dataset.editar)));
  if (borrar) eliminar(Number(borrar.dataset.eliminar));
});
botonPrincipal.addEventListener("click", () => abrirFormulario(null));

cargar();
