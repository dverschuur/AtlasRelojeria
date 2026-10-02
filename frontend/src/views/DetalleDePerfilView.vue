<template>
  <div class="perfil-page">
    <div class="perfil-inner">

      <!-- Tarjeta de perfil -->
      <div class="perfil-card">
        <div class="perfil-avatar">
          <div class="avatar-icon"></div>
        </div>

        <div class="perfil-info" v-if="perfilEditable">
          <h2 class="perfil-nombre">{{ perfilEditable.nombre }}</h2>
          <span class="perfil-rol">{{ esAdmin ? 'Administrador' : 'Cliente' }}</span>

          <div class="info-lista">
            <div class="info-item">
              <span class="info-label">Cédula (ID)</span>
              <span class="info-val">{{ perfilEditable.id }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Edad</span>
              <span class="info-val">{{ perfilEditable.edad }} años</span>
            </div>
          </div>
        </div>

        <!-- Acciones -->
        <div class="perfil-actions">
          <button v-if="!esAdmin" @click="mostrarEditor = true" class="btn-action btn-edit">Editar Perfil</button>
          <button v-if="!esAdmin" @click="toggleListaDeseos" class="btn-action btn-wish">
            {{ mostrarListaDeseos ? 'Ocultar deseos' : 'Lista de deseos' }}
          </button>
          <button @click="$emit('cerrar-sesion')" class="btn-action btn-logout">Cerrar Sesión</button>
          <button v-if="!esAdmin" @click="eliminarPerfil" class="btn-action btn-delete">Eliminar Perfil</button>
        </div>

        <!-- Mensaje de estado -->
        <transition name="fade-slide">
          <div v-if="mensaje" class="status-msg" :class="{ error: mensajeEsError }">
            {{ mensaje }}
          </div>
        </transition>
      </div>

      <!-- Lista de deseos -->
      <transition name="slide-down">
        <div v-if="mostrarListaDeseos" class="deseos-section">
          <h3 class="section-title">Mi Lista de Deseos</h3>

          <div v-if="productosDeseados.length === 0" class="empty-wish">
            <p>No tienes productos en tu lista de deseos.</p>
          </div>

          <div v-else class="deseos-grid">
            <div v-for="producto in productosDeseados" :key="producto.id" class="deseo-card">
              <img
                :src="'http://localhost:8081/api/productos/img/' + producto.imagen"
                :alt="producto.nombre"
              />
              <div class="deseo-info">
                <h4>{{ producto.nombre }}</h4>
                <p>{{ producto.descripcion }}</p>
                <strong class="deseo-precio">${{ producto.precio }}</strong>
                <div class="deseo-actions">
                  <button @click="quitarDeListaDeseos(producto)" class="btn-quitar">
                    ✕ Quitar
                  </button>
                  <button @click="$emit('comprar', producto)" class="btn-comprar">
                    Comprar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- Editor de perfil -->
    <EditarPerfilView
      v-if="mostrarEditor"
      :perfil-original="perfilEditable"
      @actualizado="actualizarPerfil"
      @cerrar="mostrarEditor = false"
    />
  </div>
</template>

<script>
import axios from 'axios'
import EditarPerfilView from './EditarPerfilView.vue'

export default {
  name: 'DetalleDePerfilView',
  components: { EditarPerfilView },
  props: {
    perfil: Object,
    esAdmin: { type: Boolean, default: false }
  },
  data() {
    return {
      perfilEditable: null,
      mensaje: '',
      mensajeEsError: false,
      mostrarEditor: false,
      mostrarListaDeseos: false,
      productosDeseados: []
    }
  },
  mounted() {
    this.perfilEditable = { ...this.perfil };
  },
  methods: {
    async eliminarPerfil() {
      if (!confirm("¿Estás seguro de que deseas eliminar este perfil?")) return;
      try {
        await axios.delete(`http://localhost:8081/api/perfiles/eliminar/${this.perfil.nombre}`);
        this.mensaje = "Perfil eliminado exitosamente.";
        this.mensajeEsError = false;
        setTimeout(() => this.$emit('perfil-eliminado'), 1000);
      } catch (err) {
        this.mensaje = "Error al eliminar el perfil.";
        this.mensajeEsError = true;
      }
    },
    actualizarPerfil(perfilEditado) {
      this.perfilEditable = { ...perfilEditado };
      this.$emit('perfil-actualizado', perfilEditado);
      this.mensaje = "Perfil actualizado correctamente.";
      this.mensajeEsError = false;
    },
    async toggleListaDeseos() {
      this.mostrarListaDeseos = !this.mostrarListaDeseos;
      if (this.mostrarListaDeseos) {
        await this.cargarListaDeseos();
      }
    },
    async cargarListaDeseos() {
      try {
        const response = await axios.get(`http://localhost:8081/api/perfiles/lista-deseos/${this.perfil.id}`);
        this.productosDeseados = response.data;
      } catch (error) {
        console.error('Error al cargar la lista de deseos:', error);
        this.mensaje = 'Error al cargar la lista de deseos.';
        this.mensajeEsError = true;
      }
    },
    async quitarDeListaDeseos(producto) {
      try {
        const response = await axios.post(
          `http://localhost:8081/api/perfiles/toggle-lista-deseos/${this.perfil.id}`,
          { productoId: producto.id }
        );
        this.productosDeseados = response.data;
        this.mensaje = 'Producto eliminado de la lista de deseos.';
        this.mensajeEsError = false;
      } catch (error) {
        this.mensaje = 'Error al quitar el producto.';
        this.mensajeEsError = true;
      }
    }
  }
}
</script>

<style scoped>
.perfil-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 24px 60px;
}

.perfil-inner {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* ── Card principal ── */
.perfil-card {
  background: var(--surface-0);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 36px 40px;
  box-shadow: var(--shadow-md);
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ── Avatar ── */
.perfil-avatar {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, var(--green-100), var(--green-50));
  border: 2px solid var(--green-600);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar-icon {
  width: 32px;
  height: 32px;
  border: 2px solid var(--green-600);
  border-radius: 50%;
  position: relative;
}

.avatar-icon::before {
  content: '';
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 12px;
  height: 12px;
  background: var(--green-600);
  border-radius: 50%;
}

.avatar-icon::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 10px;
  border: 2px solid var(--green-600);
  border-bottom: none;
  border-radius: 10px 10px 0 0;
}

/* ── Info ── */
.perfil-nombre {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.7rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 4px;
}

.perfil-rol {
  display: inline-block;
  background: var(--green-100);
  color: var(--green-800);
  border: 1px solid #b2dfcf;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 20px;
}

.info-lista {
  border-top: 1px solid var(--border);
  padding-top: 16px;
  margin-bottom: 28px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--text-muted);
  font-weight: 500;
}

.info-val {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 0.95rem;
}

/* ── Acciones ── */
.perfil-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.btn-action {
  padding: 9px 18px;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.83rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: transform var(--transition), box-shadow var(--transition), opacity var(--transition);
}

.btn-action:hover { transform: translateY(-1px); opacity: 0.9; }

.btn-edit    { background: rgba(107,154,195,0.12); color: #3a6fa0; border: 1px solid rgba(107,154,195,0.35); }
.btn-wish    { background: rgba(255,64,129,0.08); color: #c0286a; border: 1px solid rgba(255,64,129,0.25); }
.btn-logout  { background: rgba(11,125,89,0.1); color: var(--green-800); border: 1px solid rgba(11,125,89,0.25); }
.btn-delete  { background: rgba(229,57,53,0.07); color: #c0392b; border: 1px solid rgba(229,57,53,0.2); }

/* ── Status msg ── */
.status-msg {
  margin-top: 20px;
  padding: 11px 14px;
  border-radius: var(--radius-md);
  font-size: 0.88rem;
  background: var(--green-100);
  color: var(--green-800);
  border: 1px solid #b2dfcf;
}

.status-msg.error {
  background: #fff0f0;
  color: #c0392b;
  border-color: #fcc;
}

/* ── Lista deseos ── */
.deseos-section {
  background: var(--surface-0);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 32px 36px;
  box-shadow: var(--shadow-sm);
}

.section-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--green-900);
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.empty-wish {
  text-align: center;
  color: var(--text-muted);
  padding: 32px;
}

.deseos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.deseo-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--surface-0);
  transition: transform var(--transition), box-shadow var(--transition);
  display: flex;
  flex-direction: column;
}

.deseo-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.deseo-card img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.deseo-info {
  padding: 14px 16px 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.deseo-info h4 {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 0.95rem;
  color: var(--green-900);
  margin-bottom: 4px;
}

.deseo-info p {
  font-size: 0.82rem;
  color: var(--text-muted);
  margin-bottom: 8px;
  flex: 1;
}

.deseo-precio {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--green-700);
  margin-bottom: 12px;
}

.deseo-actions {
  display: flex;
  gap: 8px;
}

.btn-quitar, .btn-comprar {
  flex: 1;
  padding: 8px 10px;
  border: none;
  border-radius: var(--radius-sm);
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity var(--transition), transform var(--transition);
}

.btn-quitar  { background: rgba(255,64,129,0.1); color: #c0286a; border: 1px solid rgba(255,64,129,0.25); }
.btn-comprar { background: linear-gradient(135deg, var(--green-700), var(--green-800)); color: white; }

.btn-quitar:hover, .btn-comprar:hover { transform: translateY(-1px); opacity: 0.88; }

/* ── Transitions ── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.25s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(-6px); }

.slide-down-enter-active, .slide-down-leave-active { transition: all 0.35s cubic-bezier(0.16,1,0.3,1); }
.slide-down-enter-from, .slide-down-leave-to { opacity: 0; transform: translateY(-16px); }
</style>
