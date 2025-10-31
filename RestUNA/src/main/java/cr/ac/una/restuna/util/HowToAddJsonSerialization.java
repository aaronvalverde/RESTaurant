/**
 * GUÍA: Cómo agregar soporte para un nuevo DTO
 * ===============================================
 * 
 * ANTES (anti-patrón - modificar Request.java cada vez):
 * -------------------------------------------------------
 * 1. Abrir Request.java
 * 2. Agregar nuevo bloque if instanceof dentro de convertirObjetoAJson()
 * 3. Copiar/pegar código de serialización
 * 4. Request.java crece cada vez más
 * 5. Violación del principio Open/Closed
 * 
 * 
 * AHORA (patrón Strategy - más limpio):
 * --------------------------------------
 * Opción 1: Implementar JsonSerializable en el DTO (RECOMENDADO)
 * 
 *    public class ProductoDto extends RecursiveTreeObject<ProductoDto> implements JsonSerializable {
 *        // ... campos y métodos normales ...
 *        
 *        @Override
 *        public String toJson() {
 *            StringBuilder json = new StringBuilder("{");
 *            
 *            if (idProducto != null && idProducto.get() > 0) {
 *                json.append("\"idProducto\":").append(idProducto.get()).append(',');
 *            }
 *            if (nombre != null && nombre.get() != null) {
 *                json.append("\"nombre\":\"").append(escaparJson(nombre.get())).append("\",");
 *            }
 *            // ... más campos ...
 *            
 *            // Eliminar última coma
 *            if (json.charAt(json.length() - 1) == ',') {
 *                json.setLength(json.length() - 1);
 *            }
 *            
 *            json.append("}");
 *            return json.toString();
 *        }
 *        
 *        private String escaparJson(String texto) {
 *            if (texto == null) return "";
 *            return texto.replace("\\", "\\\\")
 *                        .replace("\"", "\\\"")
 *                        .replace("\n", "\\n");
 *        }
 *    }
 * 
 * 
 * Opción 2: Registrar serializador externo (si no puedes modificar el DTO)
 * 
 *    // En cualquier lugar antes de usar (ej: en Application startup)
 *    JsonSerializer.register(ProductoDto.class, obj -> {
 *        ProductoDto dto = (ProductoDto) obj;
 *        StringBuilder json = new StringBuilder("{");
 *        
 *        if (dto.getIdProducto() != null && dto.getIdProducto() > 0) {
 *            json.append("\"idProducto\":").append(dto.getIdProducto()).append(',');
 *        }
 *        // ... serialización ...
 *        
 *        json.append("}");
 *        return json.toString();
 *    });
 * 
 * 
 * EJEMPLO DE USO:
 * ---------------
 * 
 *    // En cualquier servicio:
 *    ProductoDto producto = new ProductoDto();
 *    producto.setNombre("Café");
 *    producto.setPrecio(2500.0);
 *    
 *    Request request = new Request("ProductoController/producto");
 *    request.post(producto);  // ¡Automáticamente serializado!
 * 
 * 
 * VENTAJAS:
 * ---------
 * ✓ Request.java NO necesita modificarse nunca más
 * ✓ Cada DTO maneja su propia serialización (Single Responsibility)
 * ✓ Fácil testear serialización individual
 * ✓ Extensible sin modificar código existente (Open/Closed)
 * ✓ Código más limpio y mantenible
 * ✓ Mensaje de error claro si falta serializador
 * 
 * 
 * MIGRACIÓN GRADUAL:
 * ------------------
 * Los DTOs existentes (Usuario, Archivo, Mesa, etc.) ya están registrados
 * en JsonSerializer. Puedes:
 * 
 * 1. Seguir usando todo como antes (funciona igual)
 * 2. Migrar DTOs gradualmente a JsonSerializable
 * 3. Agregar nuevos DTOs con JsonSerializable desde el inicio
 * 
 * El código legacy en Request.java está marcado como @Deprecated
 * pero seguirá funcionando.
 */
package cr.ac.una.restuna.util;

// Esta clase solo contiene documentación
public final class HowToAddJsonSerialization {
    private HowToAddJsonSerialization() {
        throw new UnsupportedOperationException("Esta clase solo contiene documentación");
    }
}
