const { contenido, botonPrincipal } = montarPagina({
  activo: "usuarios", titulo: "Usuarios", subtitulo: "Clientes, administradores y motorizados.",
<<<<<<< HEAD
  reporte: "usuarios",
=======
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
  boton: { texto: "Nuevo usuario", icono: "fa-plus" },
});

let usuarios = [];
let roles = [];

contenido.innerHTML =
  '<div class="filtros">' +
  '<div class="crece"><label for="f-texto">Buscar</label><input type="text" id="f-texto" placeholder="Nombre o correo..."></div>' +
  '<div><label for="f-rol">Rol</label><select id="f-rol"><option value="">Todos</option></select></div>' +
  "</div>" +
  '<div class="tabla-caja"><table class="tabla"><thead><tr><th>Usuario</th><th>Correo</th><th>Teléfono</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead>' +
  '<tbody id="filas"></tbody></table></div>';

const filas = document.getElementById("filas");
const filtroTexto = document.getElementById("f-texto");
const filtroRol = document.getElementById("f-rol");

function dibujar() {
  const texto = filtroTexto.value.trim().toLowerCase();
  const lista = usuarios.filter((u) =>
    (!texto || (nombreCompleto(u) + " " + (u.correo || "")).toLowerCase().includes(texto)) &&
    (!filtroRol.value || String(u.rol && u.rol.id_rol) === filtroRol.value));
  if (lista.length === 0) {
    filas.innerHTML = '<tr><td colspan="6" class="mensaje-tabla">No hay usuarios para mostrar.</td></tr>';
    return;
  }
  filas.innerHTML = lista.map((u) =>
    "<tr><td><strong>" + esc(nombreCompleto(u)) + "</strong></td>" +
    "<td>" + esc(u.correo || "—") + "</td>" +
    "<td>" + esc(u.telefono || "—") + "</td>" +
    "<td>" + esc(u.rol ? u.rol.nombre_rol : "—") + "</td>" +
    "<td>" + insigniaActivo(u.estado) + "</td>" +
    '<td><div class="acciones">' +
    '<button class="ic-btn" data-editar="' + u.id_usuario + '" title="Editar"><i class="fa-solid fa-pen"></i></button>' +
    '<button class="ic-btn rojo" data-eliminar="' + u.id_usuario + '" title="Eliminar"><i class="fa-solid fa-trash"></i></button>' +
    "</div></td></tr>").join("");
}

async function cargar() {
  mostrarCargando(filas, 6);
  try {
    [usuarios, roles] = await Promise.all([AdminApi.usuarios(), AdminApi.roles()]);
    filtroRol.innerHTML = '<option value="">Todos</option>' +
      roles.map((r) => '<option value="' + r.id_rol + '">' + esc(r.nombre_rol) + "</option>").join("");
    dibujar();
  } catch (error) {
    mostrarErrorTabla(filas, 6, error);
  }
}

function abrirFormulario(usuario) {
  const esNuevo = !usuario;
  const u = usuario || { nombre: "", apellido: "", correo: "", telefono: "", estado: 1, rol: null };
  const opcionesRol = roles.map((r) =>
    '<option value="' + r.id_rol + '"' + (u.rol && u.rol.id_rol === r.id_rol ? " selected" : "") + ">" + esc(r.nombre_rol) + "</option>").join("");

  abrirModal(esNuevo ? "Nuevo usuario" : "Editar usuario",
    '<div class="dos-campos"><div><label for="m-nombre">Nombre</label><input type="text" id="m-nombre" maxlength="60" value="' + esc(u.nombre) + '"></div>' +
    '<div><label for="m-apellido">Apellido</label><input type="text" id="m-apellido" maxlength="60" value="' + esc(u.apellido) + '"></div></div>' +
    '<label for="m-correo">Correo</label><input type="email" id="m-correo" maxlength="100" value="' + esc(u.correo) + '">' +
    '<div class="dos-campos"><div><label for="m-telefono">Teléfono</label><input type="tel" id="m-telefono" maxlength="9" value="' + esc(u.telefono) + '"></div>' +
    '<div><label for="m-rol">Rol</label><select id="m-rol"><option value="">Elige un rol</option>' + opcionesRol + "</select></div></div>" +
    '<label for="m-clave">Contraseña</label><input type="password" id="m-clave" autocomplete="new-password">' +
    '<div class="ayuda-campo">' + (esNuevo ? "Mínimo 6 caracteres." : "Déjala vacía para conservar la actual.") + "</div>" +
    '<label for="m-estado">Estado</label><select id="m-estado"><option value="1"' + (Number(u.estado) === 1 ? " selected" : "") + '>Activo</option><option value="0"' + (Number(u.estado) === 0 ? " selected" : "") + ">Inactivo</option></select>",
    async (f) => {
      const nombre = f.querySelector("#m-nombre").value.trim();
      const apellido = f.querySelector("#m-apellido").value.trim();
      const correo = f.querySelector("#m-correo").value.trim();
      const telefono = f.querySelector("#m-telefono").value.trim();
      const idRol = f.querySelector("#m-rol").value;
      const clave = f.querySelector("#m-clave").value;
      if (!nombre || !apellido) throw new Error("Escribe el nombre y el apellido.");
      if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(correo)) throw new Error("Escribe un correo válido.");
      if (telefono && !/^\d{9}$/.test(telefono)) throw new Error("El teléfono debe tener 9 dígitos.");
      if (!idRol) throw new Error("Elige un rol.");
      if (esNuevo && clave.length < 6) throw new Error("La contraseña debe tener al menos 6 caracteres.");
      if (!esNuevo && clave && clave.length < 6) throw new Error("La contraseña debe tener al menos 6 caracteres.");
      const repetido = usuarios.some((x) => x.correo && x.correo.toLowerCase() === correo.toLowerCase() &&
        (esNuevo || x.id_usuario !== u.id_usuario));
      if (repetido) throw new Error("Ya existe un usuario con ese correo.");

      const datos = {
        nombre, apellido, correo, telefono,
        contrasena: clave || u.contrasena,
        estado: Number(f.querySelector("#m-estado").value),
        rol: { id_rol: Number(idRol) },
      };
      if (esNuevo) await AdminApi.crearUsuario(datos);
      else await AdminApi.actualizarUsuario(u.id_usuario, datos);
      avisar(esNuevo ? "Usuario creado." : "Usuario actualizado.");
      await cargar();
    });
}

async function eliminar(id) {
  const usuario = usuarios.find((u) => u.id_usuario === id);
  const seguro = await confirmar("Eliminar usuario",
    "¿Eliminar a <strong>" + esc(nombreCompleto(usuario)) + "</strong>? Esta acción no se puede deshacer.", "Eliminar");
  if (!seguro) return;
  try {
    await AdminApi.eliminarUsuario(id);
    avisar("Usuario eliminado.");
    await cargar();
  } catch (error) {
    avisar("No se pudo eliminar. Si tiene pedidos o entregas, márcalo como Inactivo en lugar de eliminarlo.", true);
  }
}

filas.addEventListener("click", (e) => {
  const editar = e.target.closest("[data-editar]");
  const borrar = e.target.closest("[data-eliminar]");
  if (editar) abrirFormulario(usuarios.find((u) => u.id_usuario === Number(editar.dataset.editar)));
  if (borrar) eliminar(Number(borrar.dataset.eliminar));
});
[filtroTexto, filtroRol].forEach((c) => c.addEventListener("input", dibujar));
botonPrincipal.addEventListener("click", () => abrirFormulario(null));

cargar();
