const zonaCarrito = document.getElementById("zonaCarrito");
const avisoCarrito = document.getElementById("avisoCarrito");
let productosCarrito = {}; 
const avisos = [];

function dibujarCarrito() {
  const items = Carrito.leer();
  avisoCarrito.innerHTML = avisos.map((a) => `<div class="aviso-carrito">${esc(a)}</div>`).join("");

  if (items.length === 0) {
    zonaCarrito.innerHTML = `
      <div class="panel carrito-vacio">
        <p>Tu carrito está vacío.</p>
        <a class="btn btn-dorado" href="menu.html">Ver el menú</a>
      </div>`;
    return;
  }

  let subtotal = 0;
  const filas = items.map((item) => {
    const p = productosCarrito[item.id];
    const linea = p.precio * item.cantidad;
    subtotal += linea;
    return `
      <div class="item" data-id="${item.id}">
        <img src="${urlImagen(p.imagen)}" alt="${esc(p.nombre)}">
        <div class="info">
          <h3>${esc(p.nombre)}</h3>
          <span class="ayuda">${formatearPrecio(p.precio)} c/u · Disponibles: ${p.stock}</span>
          <div class="cantidad cantidad-chica">
            <button type="button" data-accion="menos" aria-label="Quitar una unidad">−</button>
            <span>${item.cantidad}</span>
            <button type="button" data-accion="mas" aria-label="Agregar una unidad">+</button>
          </div>
        </div>
        <span class="precio">${formatearPrecio(linea)}</span>
        <button type="button" class="ic-btn rojo" data-accion="quitar" aria-label="Quitar del carrito"><i class="fa-solid fa-trash"></i></button>
      </div>`;
  }).join("");

  zonaCarrito.innerHTML = `
    <div class="panel"><h3>Productos seleccionados</h3>${filas}</div>
    <div class="panel"><h3>Resumen</h3>
      <div class="fila-dato"><span>Productos</span><span>${Carrito.totalUnidades()} unidades</span></div>
      <div class="fila-total"><span>Total</span><span class="monto">${formatearPrecio(subtotal)}</span></div>
      <p class="ayuda" style="margin-top:.8rem">El precio final lo confirma el sistema al registrar el pedido.</p>
      <a class="btn btn-dorado btn-bloque" style="margin-top:1.2rem" href="checkout.html">Continuar con la reserva</a>
      <a class="btn btn-borde btn-bloque" style="margin-top:.6rem" href="menu.html">Seguir comprando</a>
    </div>`;
  zonaCarrito.querySelectorAll(".item img").forEach(conRespaldo);
}

zonaCarrito.addEventListener("click", (e) => {
  const boton = e.target.closest("[data-accion]");
  if (!boton) return;
  const id = Number(boton.closest(".item").dataset.id);
  const item = Carrito.leer().find((i) => i.id === id);
  const producto = productosCarrito[id];
  if (!item || !producto) return;

  if (boton.dataset.accion === "quitar") Carrito.quitar(id);
  if (boton.dataset.accion === "menos" && item.cantidad > 1) Carrito.cambiarCantidad(id, item.cantidad - 1);
  if (boton.dataset.accion === "mas" && item.cantidad < producto.stock) Carrito.cambiarCantidad(id, item.cantidad + 1);
  dibujarCarrito();
});

async function iniciar() {
  const items = Carrito.leer();
  if (items.length === 0) return dibujarCarrito();
  try {
    const resultados = await Promise.allSettled(items.map((i) => Api.producto(i.id)));
    resultados.forEach((r, n) => {
      const item = items[n];
      if (r.status !== "fulfilled" || r.value.estado !== 1 || r.value.stock <= 0) {
        avisos.push("Un producto de tu carrito ya no está disponible y se quitó.");
        Carrito.quitar(item.id);
        return;
      }
      productosCarrito[item.id] = r.value;
      if (item.cantidad > r.value.stock) {
        Carrito.cambiarCantidad(item.id, r.value.stock);
        avisos.push("Ajustamos la cantidad de " + r.value.nombre + " al stock disponible.");
      }
    });
    dibujarCarrito();
  } catch (error) {
    console.error(error);
    zonaCarrito.innerHTML = '<p class="mensaje-estado error">No pudimos cargar tu carrito. Revisa que el servidor esté encendido.</p>';
  }
}
iniciar();
