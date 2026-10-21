const contenedor = document.getElementById("listaProductos");
const chips = document.getElementById("chipsCategorias");
let todosLosProductos = [];
let categoriaActiva = new URLSearchParams(location.search).get("categoria") || "todas";

function tarjetaProducto(p) {
  const agotado = p.stock <= 0;
  const categoria = p.categoria ? p.categoria.nombre : "";
  return `
    <article class="card-prod">
      <img src="${urlImagen(p.imagen)}" alt="${esc(p.nombre)}" loading="lazy">
      <div class="cuerpo">
        <h3>${esc(p.nombre)}</h3>
        <div class="sub">${esc(categoria)}${agotado ? ' · <span style="color:var(--rojo)">Agotado</span>' : ""}</div>
        <p class="desc">${esc(p.descripcion)}</p>
        <div class="pie">
          <span class="precio">${formatearPrecio(p.precio)}</span>
          <a href="detalle-producto.html?id=${p.id_producto}">Ver detalles →</a>
        </div>
      </div>
    </article>`;
}

function mostrarProductos() {
  const lista = todosLosProductos.filter(
    (p) => categoriaActiva === "todas" || String(p.categoria && p.categoria.id_categoria) === categoriaActiva
  );
  if (lista.length === 0) {
    contenedor.innerHTML = '<p class="mensaje-estado">No hay productos en esta categoría por ahora.</p>';
    return;
  }
  contenedor.innerHTML = lista.map(tarjetaProducto).join("");
  contenedor.querySelectorAll("img").forEach(conRespaldo);
}

function mostrarChips(categorias) {
  const activas = categorias.filter((c) => c.estado === 1);
  chips.innerHTML =
    `<button class="chip" data-id="todas">Todos</button>` +
    activas.map((c) => `<button class="chip" data-id="${c.id_categoria}">${esc(c.nombre)}</button>`).join("");
  marcarChipActivo();
  chips.addEventListener("click", (e) => {
    const chip = e.target.closest(".chip");
    if (!chip) return;
    categoriaActiva = chip.dataset.id;
    marcarChipActivo();
    mostrarProductos();
  });
}

function marcarChipActivo() {
  chips.querySelectorAll(".chip").forEach((c) => c.classList.toggle("activo", c.dataset.id === categoriaActiva));
}

async function iniciar() {
  try {
    const [categorias, productos] = await Promise.all([Api.categorias(), Api.productos()]);
    todosLosProductos = productos.filter((p) => p.estado === 1);
    mostrarChips(categorias);
    mostrarProductos();
  } catch (error) {
    console.error(error);
    contenedor.innerHTML =
      '<p class="mensaje-estado error">No pudimos cargar los productos. Revisa que el servidor y la base de datos estén encendidos.</p>';
  }
}
iniciar();
