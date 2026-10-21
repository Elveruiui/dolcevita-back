const botonMenu = document.getElementById("navToggle");
const enlacesMenu = document.getElementById("navLinks");
if (botonMenu && enlacesMenu) {
  botonMenu.addEventListener("click", () => enlacesMenu.classList.toggle("abierto"));
}
