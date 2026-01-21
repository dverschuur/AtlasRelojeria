<template>
  <div class="editar-container">
  <button class="cerrar-edicion" type="button" @click="$emit('cerrar')">Salir</button>

    <h2>Editar Producto</h2>

    <form @submit.prevent="confirmarEdicion">
      <label>Nombre:</label>
      <input v-model="producto.nombre" required />

      <label>Descripción:</label>
      <textarea v-model="producto.descripcion" required />

      <label>Precio:</label>
      <input v-model.number="producto.precio" type="number" min="0" required />

      <label>Cantidad:</label>
      <input v-model.number="producto.cantidad" type="number" min="0" required />

      <label>Proveedor:</label>
      <input v-model="producto.proveedor" required />

      <label>Imagen:</label>
      <input type="file" @change="handleImageUpload" />
      <div v-if="producto.imagen">
        <p style="margin-top: 3px;">Imagen actual:</p>
        <img :src="'http://localhost:8081/img/' + producto.imagen" style="width: 100%; max-height: 100px; object-fit: contain;" />
      </div>

      <button type="submit"> Confirmar</button>
    </form>
  </div>
</template>


<script>
import axios from 'axios'

export default {
  name: 'EditarProductoView',
  props: {
    productoOriginal: Object
  },
  data() {
    return {
      producto: { ...this.productoOriginal },
      nuevaImagen: null
    }
  },
  methods: {
    handleImageUpload(event) {
      this.nuevaImagen = event.target.files[0];
    },
    confirmarEdicion() {
      const formData = new FormData();
      formData.append("nombre", this.producto.nombre);
      formData.append("descripcion", this.producto.descripcion);
      formData.append("precio", this.producto.precio);
      formData.append("cantidad", this.producto.cantidad);
      formData.append("proveedor", this.producto.proveedor);
      formData.append("id", this.producto.id);

      if (this.nuevaImagen) {
        formData.append("imagen", this.nuevaImagen);
      }

      axios.put(`http://localhost:8081/api/productos/actualizar/${this.producto.id}`, formData)
        .then(() => {
          alert(" Producto actualizado con éxito.");
          this.$emit('actualizado');
        })
        .catch(error => {
          alert(" Error al actualizar el producto.");
          console.error(error);
        });
    }
  }
}
</script>
<style scoped>
.editar-container {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
  background-color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.3);
  width: 400px;
}
label {
  display: block;
  margin-top: 12px;
}
input, textarea {
  width: 100%;
  padding: 6px;
  margin-top: 4px;
  border-radius: 6px;
  border: 1px solid #aaa;
}
button {
  margin-top: 20px;
  padding: 8px 12px;
  background-color:rgb(107, 154, 195);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.cerrar-edicion {
  position: absolute;
  top: 12px;
  right: 16px;
  background-color:rgb(200, 85, 83);
  margin-top: 20px;
  padding: 8px 12px;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}



</style>