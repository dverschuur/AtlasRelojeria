<template>
  <div class="orden-page">
    <div class="orden-card">
      <div class="orden-header">
        <div class="orden-icon"></div>
        <h2>Registrar Orden de Compra</h2>
        <p class="orden-subtitle">Selecciona proveedor, producto y cantidad</p>
      </div>

      <form @submit.prevent="registrarOrdenDeCompra" class="orden-form">

        <!-- Proveedor -->
        <div class="field-group">
          <label for="proveedor">Proveedor</label>
          <select id="proveedor" v-model="proveedorSeleccionado" required @change="actualizarProductos">
            <option value="">Seleccione un proveedor</option>
            <option v-for="prov in proveedoresUnicos" :key="prov" :value="prov">
              {{ prov || 'Sin proveedor asignado' }}
            </option>
          </select>
        </div>

        <!-- Producto -->
        <div class="field-group">
          <label for="producto">Producto</label>
          <select
            id="producto"
            v-model="producto"
            required
            @change="actualizarPrecioUnitario"
            :disabled="!proveedorSeleccionado"
          >
            <option value="">Seleccione un producto</option>
            <option v-for="prod in productosFiltrados" :key="prod.id" :value="prod.id">
              {{ prod.nombre }} — Stock: {{ prod.cantidad }} — ${{ prod.precio }}
            </option>
          </select>
          <span v-if="!proveedorSeleccionado" class="field-hint">
            Primero selecciona un proveedor
          </span>
        </div>

        <!-- Cantidad -->
        <div class="field-group">
          <label for="cantidad">Cantidad</label>
          <input
            type="number"
            id="cantidad"
            v-model="cantidadDeProducto"
            min="1"
            required
            :disabled="!producto"
            placeholder="Unidades a ordenar"
          />
        </div>

        <!-- Resumen -->
        <transition name="fade-slide">
          <div v-if="producto && cantidadDeProducto" class="resumen-orden">
            <h3 class="resumen-title">Resumen de la orden</h3>
            <div class="resumen-fila">
              <span class="res-label">Proveedor</span>
              <span class="res-val">{{ proveedorSeleccionado || 'Sin proveedor' }}</span>
            </div>
            <div class="resumen-fila">
              <span class="res-label">Producto</span>
              <span class="res-val">{{ obtenerNombreProducto }}</span>
            </div>
            <div class="resumen-fila">
              <span class="res-label">Precio unitario</span>
              <span class="res-val">${{ precioUnitario }}</span>
            </div>
            <div class="resumen-fila">
              <span class="res-label">Cantidad</span>
              <span class="res-val">{{ cantidadDeProducto }}</span>
            </div>
            <div class="resumen-total">
              <span>Total</span>
              <span class="total-val">${{ calcularTotal }}</span>
            </div>
          </div>
        </transition>

        <!-- Mensaje -->
        <transition name="fade-slide">
          <div v-if="mensaje" class="alert" :class="{ 'alert-error': error, 'alert-success': !error }">
            {{ mensaje }}
          </div>
        </transition>

        <!-- Botones -->
        <div class="botones-orden">
          <button type="submit" :disabled="!producto || !cantidadDeProducto" class="btn-primary">
            Registrar Orden
          </button>
          <button type="button" class="btn-cancel" @click="limpiarFormulario">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'OrdenDeCompraView',
  data() {
    return {
      productos: [],
      proveedorSeleccionado: '',
      producto: '',
      cantidadDeProducto: '',
      precioUnitario: 0,
      mensaje: '',
      error: false
    }
  },
  computed: {
    proveedoresUnicos() {
      return [...new Set(this.productos.map(p => p.proveedor))].sort()
    },
    productosFiltrados() {
      return this.productos.filter(p => p.proveedor === this.proveedorSeleccionado)
    },
    obtenerNombreProducto() {
      const prod = this.productos.find(p => p.id === this.producto)
      return prod ? prod.nombre : ''
    },
    calcularTotal() {
      return this.precioUnitario * this.cantidadDeProducto || 0
    }
  },
  mounted() {
    this.cargarProductos()
  },
  methods: {
    async cargarProductos() {
      try {
        const response = await axios.get('http://localhost:8081/api/productos/inventario')
        this.productos = response.data
      } catch (error) {
        this.mensaje = 'Error al cargar la lista de productos'
        this.error = true
      }
    },
    actualizarProductos() {
      this.producto = ''
      this.cantidadDeProducto = ''
      this.precioUnitario = 0
    },
    actualizarPrecioUnitario() {
      const prod = this.productos.find(p => p.id === this.producto)
      this.precioUnitario = prod ? prod.precio : 0
      this.cantidadDeProducto = ''
    },
    async registrarOrdenDeCompra() {
      try {
        const ordenData = {
          producto: this.producto,
          cantidadDeProducto: parseInt(this.cantidadDeProducto),
          proveedor: this.proveedorSeleccionado
        }
        const response = await axios.post('http://localhost:8081/api/ordenes', ordenData)
        this.mensaje = response.data
        this.error = false
        this.limpiarFormulario()
        await this.cargarProductos()
      } catch (error) {
        this.mensaje = error.response?.data || 'Error al registrar la orden de compra'
        this.error = true
      }
    },
    limpiarFormulario() {
      this.proveedorSeleccionado = ''
      this.producto = ''
      this.cantidadDeProducto = ''
      this.precioUnitario = 0
    }
  }
}
</script>

<style scoped>
.orden-page {
  min-height: 80vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 40px 24px;
  background: linear-gradient(160deg, var(--green-50) 0%, var(--surface-1) 100%);
}

.orden-card {
  width: 100%;
  max-width: 560px;
  background: var(--surface-0);
  border-radius: var(--radius-xl);
  padding: 40px 36px;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-lg);
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.orden-header {
  text-align: center;
  margin-bottom: 32px;
}

.orden-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 14px;
  background: linear-gradient(135deg, var(--green-100), var(--green-50));
  border: 2px solid var(--green-600);
  border-radius: var(--radius-md);
  position: relative;
}

.orden-icon::before {
  content: '';
  position: absolute;
  inset: 10px;
  border: 2px solid var(--green-700);
  border-radius: 2px;
}

.orden-header h2 {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--green-900);
  margin-bottom: 6px;
}

.orden-subtitle {
  font-size: 0.88rem;
  color: var(--text-muted);
}

.orden-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* ── Fields ── */
.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-group label {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.field-group select,
.field-group input {
  width: 100%;
  padding: 11px 13px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.92rem;
  color: var(--text-primary);
  background: var(--surface-1);
  outline: none;
  transition: border-color var(--transition), box-shadow var(--transition);
}

.field-group select:focus,
.field-group input:focus {
  border-color: var(--green-700);
  background: var(--surface-0);
  box-shadow: 0 0 0 3px rgba(11, 125, 89, 0.12);
}

.field-group select:disabled,
.field-group input:disabled {
  background: var(--surface-2);
  color: var(--text-muted);
  cursor: not-allowed;
}

.field-hint {
  font-size: 0.75rem;
  color: var(--text-muted);
  font-style: italic;
}

/* ── Resumen ── */
.resumen-orden {
  background: var(--surface-1);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px;
}

.resumen-title {
  font-family: 'Playfair Display', Georgia, serif;
  font-size: 1rem;
  font-weight: 600;
  color: var(--green-900);
  margin-bottom: 14px;
}

.resumen-fila {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.resumen-fila:last-of-type { border-bottom: none; }

.res-label {
  font-size: 0.78rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.res-val {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 0.9rem;
}

.resumen-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
  padding: 12px 16px;
  background: linear-gradient(135deg, var(--green-100), var(--green-50));
  border-radius: var(--radius-md);
  font-weight: 700;
  color: var(--green-900);
}

.total-val {
  font-size: 1.2rem;
  color: var(--green-700);
}

/* ── Alerts ── */
.alert {
  padding: 11px 14px;
  border-radius: var(--radius-md);
  font-size: 0.87rem;
}

.alert-success {
  background: var(--green-100);
  color: var(--green-800);
  border: 1px solid #b2dfcf;
}

.alert-error {
  background: #fff0f0;
  color: #c0392b;
  border: 1px solid #fcc;
}

/* ── Botones ── */
.botones-orden {
  display: flex;
  gap: 10px;
}

.btn-primary {
  flex: 2;
  padding: 13px;
  background: linear-gradient(135deg, var(--green-700), var(--green-800));
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.93rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition), box-shadow var(--transition);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(11, 125, 89, 0.3);
}

.btn-primary:disabled {
  background: #d0d0d0;
  cursor: not-allowed;
}

.btn-cancel {
  flex: 1;
  padding: 13px;
  background: transparent;
  color: #c0392b;
  border: 1.5px solid rgba(229,57,53,0.3);
  border-radius: var(--radius-md);
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition);
}

.btn-cancel:hover {
  background: rgba(229,57,53,0.06);
}

/* ── Transitions ── */
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(-8px); }
</style>