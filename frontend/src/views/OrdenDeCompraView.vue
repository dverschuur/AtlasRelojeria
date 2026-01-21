<template>
  <div class="orden-compra-container">
    <h2>Registrar Orden de Compra</h2>
    
    <form @submit.prevent="registrarOrdenDeCompra" class="formulario-orden">
      <div class="campo">
        <label for="proveedor">Proveedor:</label>
        <select id="proveedor" v-model="proveedorSeleccionado" required @change="actualizarProductos">
          <option value="">Seleccione un proveedor</option>
          <option v-for="prov in proveedoresUnicos" :key="prov" :value="prov">
            {{ prov || 'Sin proveedor asignado' }}
          </option>
        </select>
      </div>

      <div class="campo">
        <label for="producto">Producto:</label>
        <select 
          id="producto" 
          v-model="producto" 
          required 
          @change="actualizarPrecioUnitario"
          :disabled="!proveedorSeleccionado"
        >
          <option value="">Seleccione un producto</option>
          <option v-for="prod in productosFiltrados" :key="prod.id" :value="prod.id">
            {{ prod.nombre }} - Stock actual: {{ prod.cantidad }} - Precio: ${{ prod.precio }}
          </option>
        </select>
      </div>

      <div class="campo">
        <label for="cantidad">Cantidad:</label>
        <input 
          type="number" 
          id="cantidad" 
          v-model="cantidadDeProducto" 
          min="1" 
          required
          :disabled="!producto"
        />
      </div>

      <div class="resumen" v-if="producto && cantidadDeProducto">
        <h3>Resumen de la orden</h3>
        <p><strong>Proveedor:</strong> {{ proveedorSeleccionado || 'Sin proveedor asignado' }}</p>
        <p><strong>Producto seleccionado:</strong> {{ obtenerNombreProducto }}</p>
        <p><strong>Precio unitario:</strong> ${{ precioUnitario }}</p>
        <p><strong>Cantidad:</strong> {{ cantidadDeProducto }}</p>
        <p><strong>Total:</strong> ${{ calcularTotal }}</p>
      </div>

      <div class="botones">
        <button type="submit" :disabled="!producto || !cantidadDeProducto">Registrar Orden</button>
        <button type="button" class="cancelar" @click="limpiarFormulario">Cancelar</button>
      </div>

      <p v-if="mensaje" :class="['mensaje', { error: error }]">{{ mensaje }}</p>
    </form>
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
      const proveedores = [...new Set(this.productos.map(p => p.proveedor))]
      return proveedores.sort()
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
        console.error('Error al cargar productos:', error)
        this.mensaje = 'Error al cargar la lista de productos'
        this.error = true
      }
    },
    actualizarProductos() {
      // Resetear la selección de producto cuando cambia el proveedor
      this.producto = ''
      this.cantidadDeProducto = ''
      this.precioUnitario = 0
    },
    actualizarPrecioUnitario() {
      const productoSeleccionado = this.productos.find(p => p.id === this.producto)
      this.precioUnitario = productoSeleccionado ? productoSeleccionado.precio : 0
      // Resetear la cantidad cuando cambia el producto
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
        await this.cargarProductos() // Recargar productos para actualizar el stock
      } catch (error) {
        console.error('Error al registrar la orden:', error)
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
.orden-compra-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
}

.formulario-orden {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.campo {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

select, input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

select:disabled, input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.resumen {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.resumen h3 {
  margin-top: 0;
  color: #0b7d59;
}

.botones {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  flex: 1;
}

button[type="submit"] {
  background-color: #0b7d59;
  color: white;
}

button[type="submit"]:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

button.cancelar {
  background-color: #dc3545;
  color: white;
}

.mensaje {
  margin-top: 15px;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
  background-color: #d4edda;
  color: #155724;
}

.mensaje.error {
  background-color: #f8d7da;
  color: #721c24;
}
</style>