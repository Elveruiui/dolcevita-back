const zona = document.getElementById("detalleProducto");
const idProducto = new URLSearchParams(location.search).get("id");

function dibujarDetalle(p) {
  const agotado = p.stock <= 0;
  const categoria = p.categoria ? p.categoria.nombre : "";
  document.title = p.nombre + " | DOLCEVITA";
  document.getElementById("migaProducto").textContent = p.nombre;
  document.getElementById("migaCategoria").textContent = categoria;
  zona.innerHTML = `
    <div class="detalle">
      <div><img class="principal" id="fotoProducto" src="${urlImagen(p.imagen)}" alt="${esc(p.nombre)}"></div>
      <div>
        <span class="ayuda" style="letter-spacing:.15em;text-transform:uppercase;font-weight:600;color:var(--dorado-oscuro)">${esc(categoria)}</span>
        <h1>${esc(p.nombre)}</h1>
        <div class="precio-g">${formatearPrecio(p.precio)}</div>
        <p class="ayuda" style="font-size:.92rem;margin:.8rem 0">${esc(p.descripcion)}</p>
        <p class="ayuda">${agotado ? '<strong style="color:var(--rojo)">Producto agotado</strong>' : "Disponibles: " + p.stock + " unidades"}</p>
        <label>Cantidad</label>
        <div class="cantidad">
          <button type="button" id="menos" aria-label="Quitar una unidad">−</button>
          <span id="cantidad">1</span>
          <button type="button" id="mas" aria-label="Agregar una unidad">+</button>
        </div>
        <button class="btn btn-dorado btn-bloque" id="agregar" style="margin-top:1.4rem" ${agotado ? "disabled" : ""}>
          <i class="fa-solid fa-bag-shopping"></i> Agregar al pedido
        </button>
        <p class="aviso-ok" id="avisoAgregado" hidden>Producto agregado. <a href="carrito.html" style="text-decoration:underline">Ver mi carrito</a></p>
      </div>
    </div>`;
  conRespaldo(document.getElementById("fotoProducto"));
  activarCantidad(p);
}

function activarCantidad(p) {
  let cantidad = 1;
  const etiqueta = document.getElementById("cantidad");
  document.getElementById("menos").addEventListener("click", () => {
    if (cantidad > 1) etiqueta.textContent = --cantidad;
  });
  document.getElementById("mas").addEventListener("click", () => {
    if (cantidad < p.stock) etiqueta.textContent = ++cantidad;
  });
  document.getElementById("agregar").addEventListener("click", () => {
    Carrito.agregar(p.id_producto, cantidad);
    document.getElementById("avisoAgregado").hidden = false;
  });
}

async function iniciar() {
  if (!idProducto) {
    zona.innerHTML = '<p class="mensaje-estado">No se indicó qué producto mostrar. <a href="menu.html" style="text-decoration:underline">Volver al menú</a></p>';
    return;
  }
  try {
    dibujarDetalle(await Api.producto(idProducto));
  } catch (error) {
    console.error(error);
    zona.innerHTML = '<p class="mensaje-estado error">No encontramos este producto. <a href="menu.html" style="text-decoration:underline">Volver al menú</a></p>';
  }
}
iniciar();
