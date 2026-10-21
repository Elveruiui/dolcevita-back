const CLAVE_CARRITO = "dolcevita_carrito";

const Carrito = {
  leer() {
    try {
      return JSON.parse(localStorage.getItem(CLAVE_CARRITO)) || [];
    } catch (e) {
      return [];
    }
  },
  guardar(lista) {
    try {
      localStorage.setItem(CLAVE_CARRITO, JSON.stringify(lista));
    } catch (e) {

    }
  },
  agregar(idProducto, cantidad) {
    const lista = this.leer();
    const existente = lista.find((i) => i.id === idProducto);
    if (existente) existente.cantidad += cantidad;
    else lista.push({ id: idProducto, cantidad: cantidad });
    this.guardar(lista);
    this.actualizarInsignia();
  },
  cambiarCantidad(idProducto, cantidad) {
    const lista = this.leer();
    const item = lista.find((i) => i.id === idProducto);
    if (item) item.cantidad = cantidad;
    this.guardar(lista);
    this.actualizarInsignia();
  },
  quitar(idProducto) {
    this.guardar(this.leer().filter((i) => i.id !== idProducto));
    this.actualizarInsignia();
  },
  vaciar() {
    this.guardar([]);
    this.actualizarInsignia();
  },
  totalUnidades() {
    return this.leer().reduce((suma, i) => suma + i.cantidad, 0);
  },
  actualizarInsignia() {
    const insignia = document.querySelector(".nav-carrito .n");
    if (!insignia) return;
    const total = this.totalUnidades();
    insignia.textContent = total;
    insignia.style.display = total > 0 ? "grid" : "none";
  },
};

document.addEventListener("DOMContentLoaded", () => Carrito.actualizarInsignia());
