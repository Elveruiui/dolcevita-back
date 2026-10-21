const { contenido, botonPrincipal } = montarPagina({
  activo: "productos", titulo: "Productos", subtitulo: "Administra el catálogo de productos.",
  boton: { texto: "Nuevo producto", icono: "fa-plus" },
});

let productos = [];
let categorias = [];

contenido.innerHTML =
  '<div class="filtros">' +
  '<div class="crece"><label for="f-texto">Buscar</label><input type="text" id="f-texto" placeholder="Nombre del producto..."></div>' +
  '<div><label for="f-categoria">Categoría</label><select id="f-categoria"><option value="">Todas</option></select></div>' +
  '<div><label for="f-estado">Estado</label><select id="f-estado"><option value="">Todos</option><option value="1">Activos</option><option value="0">Inactivos</option></select></div>' +
  "</div>" +
  '<div class="tabla-caja"><table class="tabla"><thead><tr><th>Producto</th><th>Categoría</th><th>Precio</th><th>Stock</th><th>Estado</th><th>Acciones</th></tr></thead>' +
  '<tbody id="filas"></tbody></table></div>';

const filas = document.getElementById("filas");
const filtroTexto = document.getElementById("f-texto");
const filtroCategoria = document.getElementById("f-categoria");
const filtroEstado = document.getElementById("f-estado");

function dibujar() {
  const texto = filtroTexto.value.trim().toLowerCase();
  const lista = productos.filter((p) =>
    (!texto || p.nombre.toLowerCase().includes(texto)) &&
    (!filtroCategoria.value || String(p.categoria && p.categoria.id_categoria) === filtroCategoria.value) &&
    (!filtroEstado.value || String(p.estado) === filtroEstado.value));

  if (lista.length === 0) {
    filas.innerHTML = '<tr><td colspan="6" class="mensaje-tabla">No hay productos para mostrar.</td></tr>';
    return;
  }
  filas.innerHTML = lista.map((p) =>
    "<tr>" +
    '<td><div class="persona">' + imagenProducto(p.imagen) + "<span>" + esc(p.nombre) + "</span></div></td>" +
    "<td>" + esc(p.categoria ? p.categoria.nombre : "—") + "</td>" +
    "<td>" + precio(p.precio) + "</td>" +
    '<td class="' + (p.stock <= 3 ? "stock-bajo" : "") + '">' + p.stock + "</td>" +
    "<td>" + insigniaActivo(p.estado) + "</td>" +
    '<td><div class="acciones">' +
    '<button class="ic-btn" data-editar="' + p.id_producto + '" title="Editar"><i class="fa-solid fa-pen"></i></button>' +
    '<button class="ic-btn rojo" data-eliminar="' + p.id_producto + '" title="Eliminar"><i class="fa-solid fa-trash"></i></button>' +
    "</div></td></tr>").join("");
}

async function cargar() {
  mostrarCargando(filas, 6);
  try {
    [productos, categorias] = await Promise.all([AdminApi.productos(), AdminApi.categorias()]);
    filtroCategoria.innerHTML = '<option value="">Todas</option>' +
      categorias.map((c) => '<option value="' + c.id_categoria + '">' + esc(c.nombre) + "</option>").join("");
    dibujar();
  } catch (error) {
    mostrarErrorTabla(filas, 6, error);
  }
}

function abrirFormulario(producto) {
  const esNuevo = !producto;
  const p = producto || { nombre: "", descripcion: "", precio: "", stock: "", estado: 1, categoria: null, imagen: null };
  const opcionesCategoria = categorias.map((c) =>
    '<option value="' + c.id_categoria + '"' + (p.categoria && p.categoria.id_categoria === c.id_categoria ? " selected" : "") + ">" + esc(c.nombre) + "</option>").join("");

  const formulario = abrirModal(esNuevo ? "Nuevo producto" : "Editar producto",
    '<label for="m-nombre">Nombre</label><input type="text" id="m-nombre" maxlength="100" value="' + esc(p.nombre) + '">' +
    '<label for="m-categoria">Categoría</label><select id="m-categoria"><option value="">Elige una categoría</option>' + opcionesCategoria + "</select>" +
    '<label for="m-descripcion">Descripción</label><textarea id="m-descripcion" maxlength="500">' + esc(p.descripcion) + "</textarea>" +
    '<div class="dos-campos"><div><label for="m-precio">Precio (S/)</label><input type="number" id="m-precio" step="0.01" min="0" value="' + esc(p.precio) + '"></div>' +
    '<div><label for="m-stock">Stock</label><input type="number" id="m-stock" step="1" min="0" value="' + esc(p.stock) + '"></div></div>' +
    '<label for="m-estado">Estado</label><select id="m-estado"><option value="1"' + (Number(p.estado) === 1 ? " selected" : "") + '>Activo (se ve en la tienda)</option><option value="0"' + (Number(p.estado) === 0 ? " selected" : "") + ">Inactivo (oculto)</option></select>" +
    '<label for="m-foto">Foto</label><input type="file" id="m-foto" accept="image/*">' +
    '<div class="ayuda-campo">' + (esNuevo ? "Opcional." : "Déjala vacía para conservar la foto actual.") + "</div>" +
    '<div id="m-vista">' + (p.imagen ? imagenProducto(p.imagen).replace("<img ", '<img class="vista-previa" ') : "") + "</div>",
    async (f) => {
      const nombre = f.querySelector("#m-nombre").value.trim();
      const idCategoria = f.querySelector("#m-categoria").value;
      const valorPrecio = f.querySelector("#m-precio").value;
      const valorStock = f.querySelector("#m-stock").value;
      if (!nombre) throw new Error("Escribe el nombre del producto.");
      if (!idCategoria) throw new Error("Elige una categoría.");
      if (valorPrecio === "" || Number(valorPrecio) <= 0) throw new Error("El precio debe ser mayor que 0.");
      if (valorStock === "" || !Number.isInteger(Number(valorStock)) || Number(valorStock) < 0) {
        throw new Error("El stock debe ser un número entero, 0 o mayor.");
      }
      const datos = {
        nombre,
        descripcion: f.querySelector("#m-descripcion").value.trim(),
        precio: Number(valorPrecio),
        stock: Number(valorStock),
        estado: Number(f.querySelector("#m-estado").value),
        categoria: { id_categoria: Number(idCategoria) },
      };
      const archivo = f.querySelector("#m-foto").files[0] || null;
      if (esNuevo) await AdminApi.crearProducto(datos, archivo);
      else await AdminApi.actualizarProducto(p.id_producto, datos, archivo);
      avisar(esNuevo ? "Producto creado." : "Producto actualizado.");
      await cargar();
    });

  formulario.querySelector("#m-foto").addEventListener("change", (e) => {
    const archivo = e.target.files[0];
    const vista = formulario.querySelector("#m-vista");
    if (!archivo) return;
    vista.innerHTML = '<img class="vista-previa" alt="" src="' + URL.createObjectURL(archivo) + '">';
  });
}

async function eliminar(id) {
  const producto = productos.find((p) => p.id_producto === id);
  const seguro = await confirmar("Eliminar producto",
    "¿Eliminar <strong>" + esc(producto.nombre) + "</strong>? Esta acción no se puede deshacer.", "Eliminar");
  if (!seguro) return;
  try {
    await AdminApi.eliminarProducto(id);
    avisar("Producto eliminado.");
    await cargar();
  } catch (error) {
    avisar("No se pudo eliminar. Si el producto ya está en pedidos, márcalo como Inactivo en lugar de eliminarlo.", true);
  }
}

filas.addEventListener("click", (e) => {
  const editar = e.target.closest("[data-editar]");
  const borrar = e.target.closest("[data-eliminar]");
  if (editar) abrirFormulario(productos.find((p) => p.id_producto === Number(editar.dataset.editar)));
  if (borrar) eliminar(Number(borrar.dataset.eliminar));
});
[filtroTexto, filtroCategoria, filtroEstado].forEach((c) => c.addEventListener("input", dibujar));
botonPrincipal.addEventListener("click", () => abrirFormulario(null));

cargar();
