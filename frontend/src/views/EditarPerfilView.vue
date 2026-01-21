<template>
  <div class="editar-perfil-container">
    <div class="cerrar" @click="$emit('cerrar')">&#x2715;</div>
    <h2> Editar Perfil</h2>

    <form @submit.prevent="confirmarEdicion">
      <label>Nombre:</label>
      <input v-model="perfil.nombre" required />

      <label>Edad:</label>
      <input v-model.number="perfil.edad" type="number" min="0" required />

      <label>Contraseña:</label>
      <input v-model="perfil.contrasena" type="password" required />

      <button type="submit"> Confirmar</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'EditarPerfilView',
  props: {
    perfilOriginal: Object
  },
  data() {
    return {
      perfil: { ...this.perfilOriginal }
    }
  },
  methods: {
    confirmarEdicion() {
      axios.put(`http://localhost:8081/api/perfiles/actualizar/${this.perfil.id}`, this.perfil)
        .then(() => {
          alert("Perfil actualizado con éxito.");
          this.$emit('actualizado', this.perfil);
           this.$emit('cerrar');
        })
        .catch(error => {
          alert("Error al actualizar el perfil.");
          console.error(error);
        });
    }
  }
}
</script>

<style scoped>
.editar-perfil-container {
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
  max-height: 90vh;
  overflow-y: auto;
}
label {
  display: block;
  margin-top: 12px;
}
input {
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
.cerrar {
  position: absolute;
  top: 8px;
  right: 12px;
  font-size: 20px;
  color: #555;
  cursor: pointer;
}
</style>
