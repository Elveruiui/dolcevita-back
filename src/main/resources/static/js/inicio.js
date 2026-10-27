const zonaDestacados = document.getElementById("destacados");

async function iniciar() {
  try {
    const productos = (await Api.productos()).filter((p) => p.estado === 1 && p.stock > 0).slice(0, 3);
    if (productos.length === 0) {
      zonaDestacados.innerHTML = '<p class="mensaje-estado">Pronto tendremos productos para mostrar.</p>';
      return;
    }
    zonaDestacados.innerHTML = productos
      .map((p) => `
        <article class="card-prod">
          <img src="${urlImagen(p.imagen)}" alt="${esc(p.nombre)}" loading="lazy">
          <div class="cuerpo">
            <h3>${esc(p.nombre)}</h3>
            <div class="sub">${esc(p.categoria ? p.categoria.nombre : "")}</div>
            <p class="desc">${esc(p.descripcion)}</p>
            <div class="pie"><span class="precio">${formatearPrecio(p.precio)}</span><a href="detalle-producto.html?id=${p.id_producto}">Ver detalles →</a></div>
          </div>
        </article>`)
      .join("");
    zonaDestacados.querySelectorAll("img").forEach(conRespaldo);
  } catch (error) {
    console.error(error);
    zonaDestacados.innerHTML = '<p class="mensaje-estado error">No pudimos cargar los productos destacados.</p>';
  }
}
iniciar();
