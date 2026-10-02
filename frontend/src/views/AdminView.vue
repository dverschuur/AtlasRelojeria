<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">Inventario de Relojes</h2>
        <p class="page-subtitle">Gestiona los productos disponibles</p>
      </div>
      <button
        @click="mostrarFormulario = true"
        v-if="!mostrarFormulario"
        class="btn-agregar"
      >
        + Agregar producto
      </button>
    </div>

    <!-- Alerta de productos con bajo stock -->
    <div v-if="productosConBajaDisponibilidad.length > 0" class="alerta-stock">
      <strong>{{ productosConBajaDisponibilidad.length }} producto(s) con stock bajo</strong>
      <span class="alerta-nombres">
        — {{ productosConBajaDisponibilidad.map(p => p.nombre).join(', ') }}
      </span>
    </div>

    <!-- Formulario de registro -->
    <transition name="slide-down">
      <RegistrarProducto
        v-if="mostrarFormulario"
        @producto-agregado="onProductoAgregado"
        @cancelar="mostrarFormulario = false"
      />
    </transition>

    <!-- Grid de inventario -->
    <div class="inventario-grid">
      <div
        v-for="reloj in relojes"
        :key="reloj.nombre"
        class="inv-card"
        :class="{ 'bajo-stock': reloj.cantidad < 5 }"
      >
        <!-- Badge bajo stock -->
        <div v-if="reloj.cantidad < 5" class="badge-stock">
          Stock bajo
        </div>

        <div class="inv-img-wrapper">
          <img :src="'http://localhost:8081/img/' + reloj.imagen" :alt="reloj.nombre" />
        </div>

        <div class="inv-body">
          <div class="inv-id">ID: {{ reloj.id }}</div>
          <h3 class="inv-nombre">{{ reloj.nombre }}</h3>
          <p class="inv-desc">{{ reloj.descripcion }}</p>

          <div class="inv-stats">
            <div class="stat">
              <span class="stat-label">Precio</span>
              <span class="stat-value price">${{ reloj.precio }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">Cantidad</span>
              <span class="stat-value" :class="{ 'qty-low': reloj.cantidad < 5 }">
                {{ reloj.cantidad }}
              </span>
            </div>
          </div>

          <div class="inv-actions">
            <button class="btn-editar" @click="productoAEditar = reloj">Modificar</button>
            <button class="btn-eliminar" @click="confirmarEliminacion(reloj)">Eliminar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Estado vacío -->
    <div v-if="relojes.length === 0" class="empty-state">
      <div class="empty-icon"></div>
      <p>No hay productos en el inventario.</p>
    </div>

    <EditarProductoView
      v-if="productoAEditar"
      :producto-original="productoAEditar"
      @actualizado="cerrarYActualizar"
      @cerrar="productoAEditar = null"
    />
  </div>
</template>

<script>
import axios from 'axios'
import RegistrarProducto from './RegistrarProducto.vue'
import EditarProductoView from './EditarProductoView.vue'

export default {
  name: 'InventarioView',
  components: {
    RegistrarProducto,
    EditarProductoView
  },
  data() {
    return {
      relojes: [],
      mostrarFormulario: false,
      productoAEditar: null
    }
  },
  computed: {
    productosConBajaDisponibilidad() {
      return this.relojes.filter(p => p.cantidad < 5)
    }
  },
  mounted() {
    this.cargarInventario()
  },
  methods: {
    cargarInventario() {
      axios.get('http://localhost:8081/api/productos/inventario')
        .then(response => { this.relojes = response.data })
        .catch(error => { console.error('Error al cargar el inventario:', error) })
    },
    cerrarYActualizar() {
      this.productoAEditar = null
      this.cargarInventario()
    },
    confirmarEliminacion(reloj) {
      if (confirm(`¿Estás segura de que deseas eliminar el producto "${reloj.nombre}"?`)) {
        axios.delete(`http://localhost:8081/api/productos/eliminar-id/${encodeURIComponent(reloj.id)}`)
          .then(() => {
            alert("Producto eliminado correctamente.");
            this.cargarInventario();
          })
          .catch(error => {
            alert("Error al eliminar el producto.");
            console.error("Error al eliminar:", error);
          });
      }
    },
    onProductoAgregado() {
      this.mostrarFormulario = false
      this.cargarInventario()
    }
  },
  props: {
    logueado: Boolean
  }
}
</script>

<style scoped>
.admin-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 24px 48px;
}

/* ── Header ── */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.page-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 4px;
}

.page-subtitle {
  font-size: 0.88rem;
  color: var(--text-muted);
}

.btn-agregar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 11px 22px;
  background: linear-gradient(135deg, var(--green-700), var(--green-800));
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition);
}

.btn-agregar:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(11, 125, 89, 0.3);
}

/* ── Alerta stock global ── */
.alerta-stock {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff5f5;
  border: 1px solid #fcc;
  border-left: 4px solid #e53935;
  border-radius: var(--radius-md);
  padding: 12px 18px;
  margin-bottom: 24px;
  font-size: 0.88rem;
  color: #c0392b;
  flex-wrap: wrap;
}

.alerta-nombres {
  color: var(--text-secondary);
}

/* ── Grid ── */
.inventario-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

/* ── Card ── */
.inv-card {
  background: var(--surface-0);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
  transition: transform var(--transition), box-shadow var(--transition);
}

.inv-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.inv-card.bajo-stock {
  border-color: #e53935;
  box-shadow: 0 0 0 2px rgba(229, 57, 53, 0.15);
}

/* ── Badge ── */
.badge-stock {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
  background: #e53935;
  color: white;
  font-size: 0.72rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 4px;
  letter-spacing: 0.02em;
}

/* ── Imagen ── */
.inv-img-wrapper {
  width: 100%;
  height: 190px;
  overflow: hidden;
  background: var(--surface-2);
}

.inv-img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.inv-card:hover .inv-img-wrapper img {
  transform: scale(1.04);
}

/* ── Body ── */
.inv-body {
  padding: 16px 18px 18px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.inv-id {
  font-size: 0.72rem;
  color: var(--text-muted);
  font-weight: 500;
  letter-spacing: 0.04em;
  margin-bottom: 4px;
}

.inv-nombre {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1rem;
  font-weight: 600;
  color: var(--green-900);
  margin-bottom: 6px;
}

.inv-desc {
  font-size: 0.82rem;
  color: var(--text-muted);
  line-height: 1.4;
  flex: 1;
  margin-bottom: 14px;
}

/* ── Stats ── */
.inv-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 14px;
  padding: 10px 12px;
  background: var(--surface-1);
  border-radius: var(--radius-sm);
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 0.7rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.stat-value {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-value.price { color: var(--green-700); }
.stat-value.qty-low { color: #e53935; }

/* ── Acciones ── */
.inv-actions {
  display: flex;
  gap: 8px;
}

.btn-editar, .btn-eliminar {
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

.btn-editar {
  background: rgba(107, 154, 195, 0.12);
  color: #3a6fa0;
  border: 1px solid rgba(107, 154, 195, 0.3);
}

.btn-eliminar {
  background: rgba(229, 57, 53, 0.08);
  color: #c0392b;
  border: 1px solid rgba(229, 57, 53, 0.2);
}

.btn-editar:hover { background: rgba(107, 154, 195, 0.22); transform: translateY(-1px); }
.btn-eliminar:hover { background: rgba(229, 57, 53, 0.15); transform: translateY(-1px); }

/* ── Empty ── */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.empty-icon { font-size: 3rem; margin-bottom: 12px; opacity: 0.4; }

/* ── Transition ── */
.slide-down-enter-active, .slide-down-leave-active {
  transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-down-enter-from, .slide-down-leave-to {
  opacity: 0;
  transform: translateY(-16px);
}
</style>