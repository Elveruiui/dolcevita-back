const zonaCheckout = document.getElementById("zonaCheckout");
const avisoCheckout = document.getElementById("avisoCheckout");

let productosPedido = [];  
let cliente = null;

function hoyISO() {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().slice(0, 10);
}

function dibujarResumen() {
  let total = 0;
  const filas = productosPedido.map(({ producto, cantidad }) => {
    const linea = producto.precio * cantidad;
    total += linea;
    return `
      <div class="item">
        <img src="${urlImagen(producto.imagen)}" alt="${esc(producto.nombre)}">
        <div class="info"><h3>${esc(producto.nombre)}</h3><span class="ayuda">${cantidad} und. × ${formatearPrecio(producto.precio)}</span></div>
        <span class="precio">${formatearPrecio(linea)}</span>
      </div>`;
  }).join("");
  return `<div class="panel"><h3>Resumen del pedido</h3>${filas}
    <div class="fila-total"><span>Total</span><span class="monto">${formatearPrecio(total)}</span></div>
    <p class="ayuda" style="margin-top:.8rem">El precio final lo confirma el sistema al registrar el pedido.</p>
    <a class="ayuda" href="carrito.html" style="text-decoration:underline">Volver al carrito</a></div>`;
}

function dibujarFormulario() {
  return `
    <form class="panel" id="formReserva" novalidate>
      <h3>Datos de entrega</h3>
      <p class="ayuda">Pedido a nombre de: <strong>${esc(cliente.nombre)} ${esc(cliente.apellido)}</strong> (cliente de prueba)</p>
      <label for="fecha">Fecha de entrega</label>
      <input type="date" id="fecha" min="${hoyISO()}" required>
      <label for="hora">Hora aproximada</label>
      <input type="time" id="hora" value="10:00" required>
      <label>Modalidad de entrega</label>
      <div class="fila-radio">
        <label><input type="radio" name="modalidad" value="Recojo" checked> Recojo en el local</label>
        <label><input type="radio" name="modalidad" value="Delivery"> Delivery a domicilio</label>
      </div>
      <div id="bloqueDireccion" hidden>
        <label for="direccion">Dirección de entrega</label>
        <input type="text" id="direccion" placeholder="Ingresa tu dirección" maxlength="200">
        <label for="referencia">Referencia (opcional)</label>
        <input type="text" id="referencia" placeholder="Ej. Frente al parque" maxlength="150">
      </div>
      <label for="obs">Observaciones (opcional)</label>
      <textarea id="obs" maxlength="200" placeholder="Mensaje para la torta, alergias, etc."></textarea>
      <p class="aviso-carrito" id="errorForm" hidden></p>
      <button class="btn btn-dorado btn-bloque" id="confirmar" type="submit" style="margin-top:1.4rem">Confirmar pedido</button>
    </form>`;
}

function mostrarError(mensaje) {
  const caja = document.getElementById("errorForm");
  caja.textContent = mensaje;
  caja.hidden = !mensaje;
}

async function obtenerIdDireccion(modalidad, textoDireccion) {
  if (modalidad === "Delivery") {
    const nueva = await Api.crearDireccion({ usuario: { id_usuario: cliente.id_usuario }, direccion: textoDireccion });
    return nueva.id_direccion;
  }
  const guardadas = await Api.direccionesDe(cliente.id_usuario);
  if (guardadas.length > 0) return guardadas[0].id_direccion;
  const nueva = await Api.crearDireccion({ usuario: { id_usuario: cliente.id_usuario }, direccion: "Recojo en el local" });
  return nueva.id_direccion;
}

async function registrarPedido(evento) {
  evento.preventDefault();
  mostrarError("");
  const fecha = document.getElementById("fecha").value;
  const hora = document.getElementById("hora").value || "10:00";
  const modalidad = document.querySelector("input[name=modalidad]:checked").value;
  const direccion = document.getElementById("direccion").value.trim();

  if (!fecha) return mostrarError("Elige la fecha de entrega.");
  if (fecha < hoyISO()) return mostrarError("La fecha de entrega no puede ser anterior a hoy.");
  if (modalidad === "Delivery" && direccion.length < 5) return mostrarError("Escribe la dirección de entrega.");

  const boton = document.getElementById("confirmar");
  boton.disabled = true;
  boton.textContent = "Registrando...";
  try {
    const idDireccion = await obtenerIdDireccion(modalidad, direccion);
    const referencia = document.getElementById("referencia").value.trim();
    const notas = document.getElementById("obs").value.trim();
    const observacion = [referencia && "Referencia: " + referencia, notas].filter(Boolean).join(" · ");

    const pedido = await Api.crearPedido({
      usuario: { id_usuario: cliente.id_usuario },
      direccion: { id_direccion: idDireccion },
      fecha_entrega: fecha + "T" + hora + ":00",
      modalidad_entrega: modalidad,
      observacion: observacion || null,
      detalles: productosPedido.map(({ producto, cantidad }) => ({
        producto: { id_producto: producto.id_producto },
        cantidad: cantidad,
      })),
    });
    Carrito.vaciar();
    location.href = "confirmacion.html?nuevo=1&id=" + pedido.id_pedido;
  } catch (error) {
    console.error(error);
    mostrarError(error.message.replace(/^Error al registrar el pedido:\s*/, "No se pudo registrar el pedido: "));
    boton.disabled = false;
    boton.textContent = "Confirmar pedido";
  }
}

async function iniciar() {
  const items = Carrito.leer();
  if (items.length === 0) {
    zonaCheckout.innerHTML = '<div class="panel carrito-vacio"><p>Tu carrito está vacío.</p><a class="btn btn-dorado" href="menu.html">Ver el menú</a></div>';
    return;
  }
  try {
    const resultados = await Promise.allSettled(items.map((i) => Api.producto(i.id)));
    resultados.forEach((r, n) => {
      if (r.status === "fulfilled" && r.value.estado === 1 && r.value.stock > 0) {
        productosPedido.push({ producto: r.value, cantidad: Math.min(items[n].cantidad, r.value.stock) });
      }
    });
    if (productosPedido.length === 0) {
      zonaCheckout.innerHTML = '<div class="panel carrito-vacio"><p>Los productos de tu carrito ya no están disponibles.</p><a class="btn btn-dorado" href="menu.html">Ver el menú</a></div>';
      return;
    }
    cliente = await Sesion.clienteActual();
    zonaCheckout.innerHTML = dibujarFormulario() + dibujarResumen();
    zonaCheckout.querySelectorAll(".item img").forEach(conRespaldo);
    const formulario = document.getElementById("formReserva");
    formulario.addEventListener("submit", registrarPedido);
    formulario.addEventListener("input", () => mostrarError(""));
    document.querySelectorAll("input[name=modalidad]").forEach((radio) =>
      radio.addEventListener("change", () => {
        document.getElementById("bloqueDireccion").hidden =
          document.querySelector("input[name=modalidad]:checked").value !== "Delivery";
      })
    );
  } catch (error) {
    console.error(error);
    zonaCheckout.innerHTML = '<p class="mensaje-estado error">No pudimos preparar tu reserva. ' + esc(error.message) + "</p>";
  }
}
iniciar();
