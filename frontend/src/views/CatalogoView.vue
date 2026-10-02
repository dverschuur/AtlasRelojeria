<template>
  <div class="catalogo-page">
    <!-- Banner de sesión -->
    <transition name="fade-slide">
      <div v-if="!logueado" class="banner-info">
        <strong>Inicia sesión</strong> para poder realizar compras y guardar favoritos.
      </div>
    </transition>

    <!-- Toast de compra exitosa -->
    <transition name="toast">
      <div v-if="mensajeCompra" class="toast-success">
        {{ mensajeCompra }}
      </div>
    </transition>

    <!-- Encabezado del catálogo -->
    <div class="catalogo-header">
      <h2 class="catalogo-title">Nuestra Colección</h2>
      <p class="catalogo-subtitle">Relojes de alta gama, seleccionados para ti</p>
    </div>

    <!-- Grid de productos -->
    <div class="catalogo-grid">
      <div
        v-for="reloj in relojes"
        :key="reloj.nombre"
        class="producto-card"
      >
        <!-- Botón favorito -->
        <button
          v-if="logueado"
          @click="toggleListaDeseos(reloj)"
          class="btn-favorito"
          :class="{ activo: estaEnListaDeseos(reloj) }"
          :title="estaEnListaDeseos(reloj) ? 'Quitar de favoritos' : 'Añadir a favoritos'"
        >
          {{ estaEnListaDeseos(reloj) ? '♥' : '♡' }}
        </button>

        <!-- Imagen -->
        <div class="card-img-wrapper">
          <img :src="'http://localhost:8081/img/' + reloj.imagen" :alt="reloj.nombre" />
        </div>

        <!-- Info -->
        <div class="card-body">
          <h3 class="card-nombre">{{ reloj.nombre }}</h3>
          <p class="card-desc">{{ reloj.descripcion }}</p>
          <div class="card-footer">
            <span class="card-precio">${{ reloj.precio }}</span>
            <button
              v-if="logueado"
              @click="$emit('comprar', reloj)"
              class="btn-comprar"
            >
              Comprar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Estado vacío -->
    <div v-if="relojes.length === 0" class="empty-state">
      <div class="empty-icon"></div>
      <p>No hay productos disponibles en este momento.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CatalogoView',
  data() {
    return {
      relojes: [],
      listaDeseos: [],
      perfilId: null
    }
  },
  props: {
    logueado: Boolean,
    mensajeCompra: String,
    perfilActual: {
      type: Object,
      default: null
    }
  },
  watch: {
    perfilActual: {
      immediate: true,
      handler(nuevoPerfil) {
        if (nuevoPerfil) {
          this.perfilId = nuevoPerfil.id;
          if (this.logueado) {
            this.cargarListaDeseos();
          }
        }
      }
    },
    logueado: {
      immediate: true,
      handler(estaLogueado) {
        if (estaLogueado && this.perfilId) {
          this.cargarListaDeseos();
        }
      }
    }
  },
  mounted() {
    this.cargarProductos();
  },
  methods: {
    async cargarProductos() {
      try {
        const response = await axios.get('http://localhost:8081/api/productos/vista-cliente');
        this.relojes = response.data.filter(p => p.cantidad > 0);
      } catch (error) {
        console.error("Error al cargar productos:", error);
      }
    },
    async cargarListaDeseos() {
      if (!this.perfilId) return;
      try {
        const response = await axios.get(`http://localhost:8081/api/perfiles/lista-deseos/${this.perfilId}`);
        this.listaDeseos = response.data || [];
      } catch (error) {
        console.error("Error al cargar lista de deseos:", error);
        this.listaDeseos = [];
      }
    },
    async toggleListaDeseos(reloj) {
      if (!this.perfilId) return;
      try {
        const response = await axios.post(
          `http://localhost:8081/api/perfiles/toggle-lista-deseos/${this.perfilId}`,
          { productoId: reloj.id }
        );
        this.listaDeseos = response.data || [];
      } catch (error) {
        console.error("Error al actualizar lista de deseos:", error);
      }
    },
    estaEnListaDeseos(reloj) {
      return this.listaDeseos.some(item => item.id === reloj.id);
    }
  }
}
</script>

<style scoped>
.catalogo-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 24px 48px;
  position: relative;
}

/* ── Banner info ── */
.banner-info {
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #fffbea 0%, #fff8e1 100%);
  color: #7a5a00;
  border: 1px solid #f5e090;
  border-left: 4px solid var(--gold);
  border-radius: var(--radius-md);
  padding: 12px 18px;
  font-size: 0.9rem;
  margin-bottom: 24px;
}

.banner-icon { font-size: 1.1rem; }

/* ── Toast ── */
.toast-success {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 999;
  background: var(--green-700);
  color: white;
  padding: 14px 20px;
  border-radius: var(--radius-lg);
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: var(--shadow-lg);
}

/* ── Header ── */
.catalogo-header {
  text-align: center;
  margin-bottom: 36px;
}

.catalogo-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 2rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 6px;
}

.catalogo-subtitle {
  font-size: 0.95rem;
  color: var(--text-muted);
  font-style: italic;
}

/* ── Grid ── */
.catalogo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

/* ── Card ── */
.producto-card {
  background: var(--surface-0);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  overflow: hidden;
  position: relative;
  transition: transform var(--transition), box-shadow var(--transition);
  display: flex;
  flex-direction: column;
}

.producto-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
  border-color: var(--green-600);
}

/* ── Favorito ── */
.btn-favorito {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255,255,255,0.92);
  border: 1px solid var(--border);
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #bbb;
  transition: background var(--transition), color var(--transition), transform var(--transition), border-color var(--transition);
  box-shadow: var(--shadow-sm);
  padding: 0;
  margin: 0;
}

.btn-favorito:hover {
  transform: scale(1.15);
  border-color: #ff4081;
}

.btn-favorito.activo {
  background: #ff4081;
  color: white;
  border-color: #ff4081;
  animation: heartPop 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes heartPop {
  0%   { transform: scale(1); }
  50%  { transform: scale(1.3); }
  100% { transform: scale(1); }
}

/* ── Imagen ── */
.card-img-wrapper {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: var(--surface-2);
}

.card-img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.producto-card:hover .card-img-wrapper img {
  transform: scale(1.05);
}

/* ── Body ── */
.card-body {
  padding: 16px 18px 18px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-nombre {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--green-900);
  margin-bottom: 6px;
}

.card-desc {
  font-size: 0.83rem;
  color: var(--text-muted);
  line-height: 1.5;
  flex: 1;
  margin-bottom: 14px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.card-precio {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--green-700);
  font-variant-numeric: tabular-nums;
}

.btn-comprar {
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--green-700) 0%, var(--green-800) 100%);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.83rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition);
  margin: 0;
}

.btn-comprar:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(11, 125, 89, 0.3);
}

/* ── Empty state ── */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 12px;
  opacity: 0.4;
}

/* ── Transitions ── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(-8px); }

.toast-enter-active, .toast-leave-active { transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(24px); }
</style>
