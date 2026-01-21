<template>
  <div class="login-container">
    <h2>Iniciar Sesión</h2>
    <form @submit.prevent="iniciarSesion">
      <label for="cedula">Cedula:</label>
      <input type="text" id="cedula" v-model="cedula" required />

      <label for="contrasena">Contraseña:</label>
      <input type="password" id="contrasena" v-model="contrasena" required />

      <button type="submit">Iniciar Sesión</button>
      <p v-if="error" class="error">Credenciales inválidas</p>
    </form>

    <div style="margin-top: 20px;">
      <button type="button" @click="$emit('crear-perfil')" class="crear">Crear Perfil</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'LoginView',
  data() {
    return {
      id: '',
      contrasena: '',
      error: false
    }
  },
  methods: {
    async iniciarSesion() {
    try {
      const response = await axios.post('http://localhost:8081/api/perfiles/login', {
        id: this.cedula,
        contrasena: this.contrasena
      });

      const esAdmin = response.data.esAdministrador;

      alert('Login exitoso');
      this.$emit('login-exitoso', esAdmin, response.data.usuario); // true o false
    } catch (err) {
      this.error = true;
    }
  }
}
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: #fdfdfd;
  text-align: center;
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
  margin-top: 10px;
}
button.crear {
  background-color: #0b7d59;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>
