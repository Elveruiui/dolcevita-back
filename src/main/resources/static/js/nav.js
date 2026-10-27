const botonMenu = document.getElementById("navToggle");
const enlacesMenu = document.getElementById("navLinks");
if (botonMenu && enlacesMenu) {
  botonMenu.addEventListener("click", () => enlacesMenu.classList.toggle("abierto"));
}

async function actualizarBarraSesion() {
  const contenedorTop = document.querySelector(".top-bar .contenedor > div:last-child");
  const enlaceMisPedidos = document.querySelector('.nav-links a[href="mis-pedidos.html"]');

  const usuario = await Sesion.obtener();

  if (!usuario) {
    if (contenedorTop) {
      contenedorTop.innerHTML = '<a href="login.html"><i class="fa-solid fa-user"></i> Iniciar sesión / Registrarse</a>';
    }
    if (enlaceMisPedidos) {
      enlaceMisPedidos.style.display = "none";
    }
    return;
  }

  const rol = Sesion.rolDe(usuario);
  const nombre = esc((usuario.nombre || "") + " " + (usuario.apellido || "")).trim();

  if (enlaceMisPedidos) {
    enlaceMisPedidos.style.display = "";
  }

  let enlacePanel = "";
  if (rol.includes("ADMIN")) {
    enlacePanel = '<a href="admin/index.html" style="margin-right:1.2rem;font-weight:600;color:var(--vino)"><i class="fa-solid fa-gauge-high"></i> Panel Admin</a>';
  } else if (rol.includes("MOTOR")) {
    enlacePanel = '<a href="admin/entregas.html" style="margin-right:1.2rem;font-weight:600;color:var(--vino)"><i class="fa-solid fa-motorcycle"></i> Panel Entregas</a>';
  }

  if (contenedorTop) {
    contenedorTop.innerHTML =
      enlacePanel +
      '<span><i class="fa-solid fa-user"></i> Hola, <strong>' + nombre + "</strong></span>" +
      '<a href="#" id="btnCerrarSesion" style="font-weight:600;color:var(--rojo)"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</a>';

    const btnCerrar = document.getElementById("btnCerrarSesion");
    if (btnCerrar) {
      btnCerrar.addEventListener("click", (e) => {
        e.preventDefault();
        Sesion.cerrar();
      });
    }
  }
}

document.addEventListener("DOMContentLoaded", actualizarBarraSesion);