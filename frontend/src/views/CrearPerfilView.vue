<template>
  <div class="crear-container">
    <h2>Crear Perfil</h2>
    <form @submit.prevent="crearPerfil">
      <label for="nombre">Nombre:</label>
      <input type="text" v-model="nombre" required />

      <label for="edad">Edad:</label>
      <input type="number" v-model="edad" required min="1" />

      <label for="contrasena">Contraseña:</label>
      <input type="password" v-model="contrasena" required />

      <label for="id">Cédula (ID):</label>
      <input type="text" v-model="id" required pattern="[0-9]+" />

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="mensaje" class="success">{{ mensaje }}</p>

      <button type="submit">Crear</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CrearPerfilView',
  data() {
    return {
      nombre: '',
      edad: '',
      contrasena: '',
      id: '',
      error: '',
      mensaje: ''
    }
  },
  methods: {
    async crearPerfil() {
      this.error = ''
      this.mensaje = ''

      // Validar que ID tenga solo números
      if (!/^\d+$/.test(this.id)) {
        this.error = 'La cédula debe contener solo números.'
        return
      }

      try {
        // Obtener todos los perfiles
        const response = await axios.get('http://localhost:8081/api/perfiles')
        const perfiles = response.data

        // Validar que el ID no esté repetido
        const existe = perfiles.some(p => p.id === this.id)
        if (existe) {
          this.error = 'Esta cédula (ID) ya está registrada.'
          return
        }

        // Si todo está bien, crear el nuevo perfil
        const nuevo = {
          nombre: this.nombre,
          id: this.id,
          edad: parseInt(this.edad),
          contrasena: this.contrasena,
          esAdministrador: false
        }

        await axios.post('http://localhost:8081/api/perfiles/crear', nuevo)
        this.mensaje = 'Perfil creado exitosamente.'
        this.nombre = this.edad = this.contrasena = this.id = ''
      } catch (err) {
        this.error = 'Hubo un error al crear el perfil.'
      }
    }
  }
}
</script>

<style scoped>
.crear-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: #fdfdfd;
}
label {
  display: block;
  margin-top: 10px;
  font-weight: bold;
}
input {
  width: 90%;
  padding: 8px;
  margin-top: 5px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #aaa;
}
button {
  padding: 10px 15px;
  background-color: #0b7d59;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.error {
  color: red;
  font-weight: bold;
}
.success {
  color: green;
  font-weight: bold;
}
</style>
