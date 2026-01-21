<template>
  <div class="registrar-producto">
    <h2>Registrar Producto en el Inventario</h2>
    <form @submit.prevent="registrarProducto">
      <label for="nombre">Producto:</label>
      <input type="text" id="nombre" v-model="nombre" required />

      <label for="descripcion">Descripcion:</label>
      <input type="text" id="descripcion" v-model="descripcion" required />

      <label for="proveedor">Proveedor:</label>
      <input type="text" id="proveedor" v-model="proveedor" required />

      <label for="precio">Precio:</label>
      <input type="number" id="precio" v-model="precio" step="0.01" min="0" required />

      <label for="cantidad">Cantidad:</label>
      <input type="number" id="cantidad" v-model="cantidad" min="0" required />

      <label for="imagen">Imagen del producto:</label>
      <input
        type="file"
        id="imagen"
        @change="handleImagenSeleccionada"
        accept="image/*"
        required
      />

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="mensaje" class="success">{{ mensaje }}</p>

      <div class="botones">
        <button type="submit">Agregar producto</button>
        <button type="button" class="cancelar" @click="$emit('cancelar')">Cancelar</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'RegistrarProducto',
  data() {
    return {
      nombre: '',
      descripcion: '',
      proveedor: '',
      precio: '',
      cantidad: '',
      imagenFile: null,
      error: '',
      mensaje: ''
    }
  },
  methods: {
    handleImagenSeleccionada(event) {
      this.imagenFile = event.target.files[0]
    },
    async registrarProducto() {
      this.error = ''
      this.mensaje = ''

      try {
        // Validaciones de campos
        if (!this.nombre.trim()) {
          this.error = 'El nombre del producto es requerido'
          return
        }
        if (!this.descripcion.trim()) {
          this.error = 'La descripción del producto es requerida'
          return
        }
        if (!this.proveedor.trim()) {
          this.error = 'El proveedor es requerido'
          return
        }
        if (!this.precio || this.precio <= 0) {
          this.error = 'El precio debe ser mayor a 0'
          return
        }
        if (!this.cantidad || this.cantidad < 0) {
          this.error = 'La cantidad no puede ser negativa'
          return
        }
        if (!this.imagenFile) {
          this.error = 'La imagen del producto es requerida'
          return
        }

        // Validar tamaño de la imagen (máximo 15MB)
        const MAX_FILE_SIZE = 15 * 1024 * 1024 // 15MB
        if (this.imagenFile.size > MAX_FILE_SIZE) {
          this.error = 'La imagen es demasiado grande. El tamaño máximo es 15MB'
          return
        }

        // Validar tipo de archivo
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
        if (!allowedTypes.includes(this.imagenFile.type)) {
          this.error = 'Formato de imagen no válido. Use JPG, PNG, GIF o WEBP'
          return
        }

        try {
          // Obtener todos los productos
          const response = await axios.get('http://localhost:8081/api/productos/inventario')
          const productos = response.data

          // Validar que el nombre no esté repetido
          const existe = productos.some(p => p.nombre.toLowerCase() === this.nombre.toLowerCase())
          if (existe) {
            this.error = 'Ese producto ya está registrado.'
            return
          }

          // Crear FormData para enviar la imagen
          const formData = new FormData()
          formData.append('nombre', this.nombre.trim())
          formData.append('descripcion', this.descripcion.trim())
          formData.append('proveedor', this.proveedor.trim())
          formData.append('precio', parseFloat(this.precio))
          formData.append('cantidad', parseInt(this.cantidad))
          formData.append('imagen', this.imagenFile)

          // Enviar el producto con la imagen
          const resultado = await axios.post('http://localhost:8081/api/productos/agregar', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            maxContentLength: MAX_FILE_SIZE,
            maxBodyLength: MAX_FILE_SIZE
          })

          if (resultado.data && resultado.data.error) {
            this.error = resultado.data.error
            return
          }

          this.mensaje = 'Producto agregado exitosamente.'
          this.$emit('producto-agregado')
          
          // Limpiar el formulario
          this.nombre = ''
          this.descripcion = ''
          this.proveedor = ''
          this.precio = ''
          this.cantidad = ''
          this.imagenFile = null
          
          // Limpiar el input de archivo
          const fileInput = document.getElementById('imagen')
          if (fileInput) {
            fileInput.value = ''
          }
        } catch (error) {
          console.error('Error detallado:', error)
          if (error.response) {
            // El servidor respondió con un estado de error
            if (error.response.data && error.response.data.error) {
              this.error = error.response.data.error
            } else {
              switch (error.response.status) {
                case 400:
                  this.error = 'La imagen es demasiado grande o los datos son inválidos.'
                  break
                case 413:
                  this.error = 'La imagen es demasiado grande. El tamaño máximo es 15MB.'
                  break
                default:
                  this.error = `Error del servidor: ${error.response.status}`
              }
            }
          } else if (error.request) {
            this.error = 'No se pudo conectar con el servidor. Verifique su conexión a internet.'
          } else {
            this.error = 'Error al procesar la solicitud: ' + error.message
          }
        }
      } catch (err) {
        console.error('Error crítico:', err)
        this.error = 'Error inesperado al procesar la solicitud.'
      }
    }
  }
}
</script>

<style scoped>
.registrar-producto {
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

.botones {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;
}

button {
  padding: 10px 15px;
  background-color: #0b7d59;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button.cancelar {
  background-color: #dc3545;
}

.error {
  color: red;
  font-weight: bold;
  margin-top: 10px;
}

.success {
  color: green;
  font-weight: bold;
  margin-top: 10px;
}
</style>
