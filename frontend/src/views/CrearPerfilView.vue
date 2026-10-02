<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-icon"></div>
        <h2>Crear Perfil</h2>
        <p class="auth-subtitle">Únete a Atlas Relojería</p>
      </div>

      <form @submit.prevent="crearPerfil" class="auth-form">
        <div class="field-group">
          <label for="nombre">Nombre</label>
          <input type="text" id="nombre" v-model="nombre" placeholder="Tu nombre completo" required />
        </div>

        <div class="field-group">
          <label for="id">Cédula (ID)</label>
          <input type="text" id="id" v-model="id" placeholder="Solo números" required pattern="[0-9]+" />
        </div>

        <div class="field-group">
          <label for="edad">Edad</label>
          <input type="number" id="edad" v-model="edad" placeholder="Tu edad" required min="1" />
        </div>

        <div class="field-group">
          <label for="contrasena">Contraseña</label>
          <input type="password" id="contrasena" v-model="contrasena" placeholder="••••••••" required />
        </div>

        <transition name="fade-slide">
          <div v-if="error" class="alert alert-error">
            {{ error }}
          </div>
        </transition>

        <transition name="fade-slide">
          <div v-if="mensaje" class="alert alert-success">
            {{ mensaje }}
          </div>
        </transition>

        <button type="submit" class="btn-primary">Crear cuenta</button>
      </form>
    </div>
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

      if (!/^\d+$/.test(this.id)) {
        this.error = 'La cédula debe contener solo números.'
        return
      }

      try {
        const response = await axios.get('http://localhost:8081/api/perfiles')
        const perfiles = response.data
        const existe = perfiles.some(p => p.id === this.id)
        if (existe) {
          this.error = 'Esta cédula (ID) ya está registrada.'
          return
        }
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
.auth-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  background: linear-gradient(160deg, var(--green-50) 0%, var(--surface-1) 100%);
}

.auth-card {
  width: 100%;
  max-width: 440px;
  background: var(--surface-0);
  border-radius: var(--radius-xl);
  padding: 40px 36px;
  box-shadow: var(--shadow-lg), 0 0 0 1px var(--border);
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(24px); }
  to   { opacity: 1; transform: translateY(0); }
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, var(--green-100), var(--green-50));
  border: 2px solid var(--green-600);
  border-radius: 50%;
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.auth-icon::before {
  content: '';
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--green-700);
  border-top-color: transparent;
  display: block;
}

.auth-header h2 {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 0.9rem;
  color: var(--text-muted);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-group label {
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.field-group input {
  width: 100%;
  padding: 12px 14px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.95rem;
  color: var(--text-primary);
  background: var(--surface-1);
  transition: border-color var(--transition), box-shadow var(--transition), background var(--transition);
  outline: none;
}

.field-group input:focus {
  border-color: var(--green-700);
  background: var(--surface-0);
  box-shadow: 0 0 0 3px rgba(11, 125, 89, 0.12);
}

.alert {
  padding: 12px 14px;
  border-radius: var(--radius-md);
  font-size: 0.88rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.alert-error {
  background: #fff0f0;
  color: #c0392b;
  border: 1px solid #fcc;
}

.alert-success {
  background: var(--green-100);
  color: var(--green-800);
  border: 1px solid #b2dfcf;
}

.btn-primary {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, var(--green-700) 0%, var(--green-800) 100%);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition), filter var(--transition);
  letter-spacing: 0.02em;
  margin-top: 4px;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(11, 125, 89, 0.35);
  filter: brightness(1.06);
}

.btn-primary:active { transform: translateY(0); }

.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.25s ease;
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
