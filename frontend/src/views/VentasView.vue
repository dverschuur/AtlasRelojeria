<template>
  <div class="venta-container">
    <div class="producto-info">
      <h2>{{ producto.nombre }}</h2>
      <img :src="'http://localhost:8081/img/' + producto.imagen" alt="Imagen del reloj" />
      <p>{{ producto.descripcion }}</p>
      <strong>${{ producto.precio }}</strong>
    </div>

    <div class="formulario-compra">
      <h3>Información del comprador</h3>
      <p><strong>Nombre:</strong> {{ perfil.nombre }}</p>
      <p><strong>ID:</strong> {{ perfil.id }}</p>

      <form @submit.prevent="procesarCompra">
        <div>
          <label for="direccion">Dirección de envío:</label>
          <input type="text" id="direccion" v-model="direccion" required />
        </div>

        <div>
          <label for="tarjeta">Número de tarjeta:</label>
          <input type="text" v-model="tarjeta" inputmode="numeric" maxlength="16" required>
        </div>

        <div>
          <label for="codigo">Código de seguridad:</label>
          <input type="text" id="codigo" v-model="codigo" maxlength="3" required />
        </div>

        <div>
          <label for="vencimiento">Fecha de vencimiento (MM/YY):</label>
          <input type="text" id="vencimiento" v-model="vencimiento" required />
        </div>

        <button type="submit">Confirmar compra</button>
      </form>

      <p v-if="mensaje" class="mensaje">{{ mensaje }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VentasView',
  props: {
    producto: Object,
    perfil: Object
  },
  data() {
    return {
       
      tarjeta: '',
      codigo: '',
      vencimiento: '',
      mensaje: ''
    };
  },
  methods: {
    async procesarCompra() {
  this.mensaje = '';

  // Validar dirección (solo letras y espacios)
  if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(this.direccion.trim())) {
    this.mensaje = 'La dirección solo debe contener letras y espacios.';
    return;
  }

console.log('TARJETA INTRODUCIDA:', this.tarjeta);
console.log('LIMPIA:', this.tarjeta.replace(/\s/g, ''));
console.log('LONGITUD:', this.tarjeta.replace(/\s/g, '').length);

  // Validar número de tarjeta: 16 dígitos
  const tarjetaLimpia = this.tarjeta.replace(/\s/g, '');

if (!/^\d{16}$/.test(tarjetaLimpia)) {
  this.mensaje = 'El número de tarjeta debe tener exactamente 16 dígitos.';
  return;
}

  // Validar código de seguridad: 3 dígitos
  if (!/^\d{3}$/.test(this.codigo)) {
    this.mensaje = 'El código de seguridad debe tener exactamente 3 dígitos.';
    return;
  }

  // Validar vencimiento: formato MM/YY
  const match = this.vencimiento.match(/^(\d{2})\/(\d{2})$/);
  if (!match) {
    this.mensaje = 'La fecha de vencimiento debe tener el formato MM/YY.';
    return;
  }
  const mes = parseInt(match[1]);
  const año = parseInt(match[2]);

  if (mes < 1 || mes > 12) {
    this.mensaje = 'El mes de la fecha de vencimiento debe estar entre 01 y 12.';
    return;
  }
 
// Validar que la tarjeta no esté vencida
const fechaActual = new Date();
const mesActual = fechaActual.getMonth() + 1;
const añoActual = parseInt(fechaActual.getFullYear().toString().slice(-2));

if (año < añoActual || (año === añoActual && mes < mesActual)) {
  this.mensaje = 'La tarjeta está vencida.';
  return;
}

  // Si pasa todo, registrar la venta (sin guardar datos de tarjeta)
  const nuevaVenta = {
    nombreCliente: this.perfil.nombre,
    idUsuario: this.perfil.id,
    idProducto: this.producto.id,
    direccion: this.direccion,
    fecha: new Date().toISOString(),
    monto: this.producto.precio
  };

  try {
    await axios.post('http://localhost:8081/api/ventas', nuevaVenta);
    this.$emit('compra-exitosa', '¡Compra realizada con éxito!');
    // this.mensaje = '¡Compra realizada con éxito!';
    // Limpiar
    this.direccion = this.tarjeta = this.codigo = this.vencimiento = '';
  } catch (error) {
    console.error('Error al registrar la venta:', error);
    this.mensaje = 'Hubo un error al procesar la compra.';
  }
}

  }
};
</script>

<style scoped>
.venta-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 40px;
  padding: 30px;
  flex-wrap: wrap;
}

.producto-info {
  flex: 1;
  min-width: 250px;
  max-width: 400px;
  background: #f4f4f4;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.producto-info img {
  width: 100%;
  border-radius: 8px;
  margin-bottom: 15px;
}

.formulario-compra {
  flex: 1;
  min-width: 280px;
  max-width: 450px;
  background: #ffffff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.formulario-compra h3 {
  margin-bottom: 15px;
  color: #0b7d59;
}

label {
  display: block;
  margin-top: 10px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  margin-bottom: 15px;
  border-radius: 6px;
  border: 1px solid #ccc;
  transition: border-color 0.3s;
}

input:focus {
  border-color: #0b7d59;
  outline: none;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #0b7d59;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 15px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #0b7d59;
}

.mensaje-error {
  color: red;
  margin-top: 10px;
  text-align: center;
}

</style>
