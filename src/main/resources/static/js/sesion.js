<<<<<<< HEAD
/*const CORREO_CLIENTE_PRUEBA = "lucia.paredes@correo.com";
=======
const CORREO_CLIENTE_PRUEBA = "lucia.paredes@correo.com";
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e

const Sesion = {
  async clienteActual() {
    const usuarios = await Api.usuarios();
    const cliente = usuarios.find((u) => u.correo === CORREO_CLIENTE_PRUEBA);
    if (!cliente) {
      throw new Error("No existe el cliente de prueba (" + CORREO_CLIENTE_PRUEBA + ") en la base de datos.");
    }
    return cliente;
  },
<<<<<<< HEAD
};*/
=======
};
>>>>>>> 7a8f0bed507ec3a77a98c71f15337719b7fed49e
