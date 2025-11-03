package cr.ac.una.restuna.util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase utilitaria para realizar peticiones HTTP siguiendo patrón UNA Planilla
 * Versión simplificada sin Jackson para compatibilidad
 */
public class Request {

    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    private static final String BASE_URL = ApplicationProperties.getRestBaseUrl();
    private static final String CONTENT_TYPE = "application/json";

    private String endpoint;
    private String pathTemplate;
    private Map<String, Object> parametros;
    private String error;
    private boolean isError;
    private String responseBody;

    public Request(String endpoint) {
        this.endpoint = endpoint;
        this.isError = false;
    }

    public Request(String endpoint, String pathTemplate, Map<String, Object> parametros) {
        this(endpoint);
        this.pathTemplate = pathTemplate;
        this.parametros = parametros;
    }

    /**
     * Realiza una petición GET
     */
    public void get() {
        HttpURLConnection connection = null;
        try {
            String url = buildUrl();
            System.out.println("Realizando GET a: " + url);
            connection = createConnection(url, "GET");
            connection.setConnectTimeout(10000); // 10 segundos
            connection.setReadTimeout(15000);    // 15 segundos
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición GET", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public byte[] getBytes() {
        HttpURLConnection connection = null;
        try {
            String fullUrl = buildUrl();

            URL url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/pdf");

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                this.error = "Error HTTP " + status + " al obtener datos binarios desde: " + fullUrl;
                this.isError = true;
                return null;
            }

            try (InputStream inputStream = connection.getInputStream(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

                return buffer.toByteArray();
            }

        } catch (Exception ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, "Error en getBytes()", ex);
            this.error = "Excepción al obtener bytes: " + ex.getMessage();
            this.isError = true;
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public byte[] getResponseBytes() {
        HttpURLConnection conn = null;
        try {
            String urlStr = buildUrl();
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/pdf, application/octet-stream");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            int statusCode = conn.getResponseCode();
            System.out.println("HTTP Status Code en getResponseBytes(): " + statusCode);

            InputStream inputStream;
            if (statusCode >= 200 && statusCode < 300) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
                this.error = "Error HTTP " + statusCode + " al obtener PDF";
                this.isError = true;
                return null;
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            byte[] result = buffer.toByteArray();
            System.out.println("PDF recibido, tamaño: " + result.length + " bytes");
            return result;

        } catch (Exception ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, "Error obteniendo respuesta binaria", ex);
            this.error = "Error obteniendo PDF: " + ex.getMessage();
            this.isError = true;
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Realiza una petición POST
     */
    public void post(Object body) {
        HttpURLConnection connection = null;
        try {
            String url = BASE_URL + endpoint;
            System.out.println("Realizando POST a: " + url);
            connection = createConnection(url, "POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000); // 10 segundos
            connection.setReadTimeout(15000);    // 15 segundos

            if (body != null) {
                // Convertir el objeto a JSON manualmente
                String jsonBody = convertirObjetoAJson(body);
                System.out.println("Enviando JSON: " + jsonBody);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición POST", e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Convierte un objeto a formato JSON de manera simple Para objetos
     * complejos, se debería usar una biblioteca como Jackson
     */
    private String convertirObjetoAJson(Object objeto) {
        if (objeto == null) {
            return "{}";
        }

        // Si es un DTO, construimos un JSON básico con sus propiedades
        if (objeto instanceof cr.ac.una.restuna.model.UsuarioDto) {
            cr.ac.una.restuna.model.UsuarioDto usuarioDto = (cr.ac.una.restuna.model.UsuarioDto) objeto;
            StringBuilder jsonBuilder = new StringBuilder("{");

            // Añadir ID si existe
            if (usuarioDto.getIdUsuario() != null) {
                jsonBuilder.append("\"idUsuario\":").append(usuarioDto.getIdUsuario()).append(",");
            }

            // Añadir propiedades obligatorias
            jsonBuilder.append("\"usuario\":\"").append(escaparJson(usuarioDto.getUsuario())).append("\"");

            // Añadir nombre si existe - asegurarse de que siempre se envía
            if (usuarioDto.getNombre() != null && !usuarioDto.getNombre().isEmpty()) {
                jsonBuilder.append(",\"nombre\":\"").append(escaparJson(usuarioDto.getNombre())).append("\"");
            } else {
                jsonBuilder.append(",\"nombre\":\"").append(escaparJson(usuarioDto.getUsuario())).append("\"");
            }

            // Añadir resto de propiedades
            jsonBuilder.append(",\"rol\":\"").append(escaparJson(usuarioDto.getRol())).append("\"");
            jsonBuilder.append(",\"estado\":\"").append(escaparJson(usuarioDto.getEstado())).append("\"");

            // Añadir contraseña si existe
            if (usuarioDto.getNuevaContrasena() != null && !usuarioDto.getNuevaContrasena().isEmpty()) {
                jsonBuilder.append(",\"nuevaContrasena\":\"").append(escaparJson(usuarioDto.getNuevaContrasena())).append("\"");
            }

            jsonBuilder.append("}");
            return jsonBuilder.toString();
        }

        // Soportar ArchivoDto (serialización manual sencilla)
        if (objeto instanceof cr.ac.una.restuna.model.ArchivoDto) {
            cr.ac.una.restuna.model.ArchivoDto archivo = (cr.ac.una.restuna.model.ArchivoDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');
            if (archivo.getIdArchivo() != null) {
                json.append("\"idArchivo\":").append(archivo.getIdArchivo()).append(',');
            }
            json.append("\"nombreArchivo\":\"").append(escaparJson(archivo.getNombreArchivo())).append("\"");
            if (archivo.getTipoMime() != null) {
                json.append(',').append("\"tipoMime\":\"").append(escaparJson(archivo.getTipoMime())).append("\"");
            }
            if (archivo.getTamanio() != null) {
                json.append(',').append("\"tamanio\":").append(archivo.getTamanio());
            }
            if (archivo.getContenidoBase64() != null && !archivo.getContenidoBase64().isEmpty()) {
                json.append(',').append("\"contenidoBase64\":\"")
                        .append(escaparJson(archivo.getContenidoBase64())).append("\"");
            }
            json.append('}');
            return json.toString();
        }

        // Soportar SeccionDto
        if (objeto instanceof cr.ac.una.restuna.model.SeccionDto) {
            cr.ac.una.restuna.model.SeccionDto seccion = (cr.ac.una.restuna.model.SeccionDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (seccion.getIdSeccion() != null && seccion.getIdSeccion() > 0) {
                json.append("\"idSeccion\":").append(seccion.getIdSeccion()).append(',');
            }

            // Nombre (obligatorio)
            json.append("\"nombre\":\"").append(escaparJson(seccion.getNombre())).append("\"");

            // Tipo
            if (seccion.getTipo() != null) {
                json.append(',').append("\"tipo\":\"").append(escaparJson(seccion.getTipo())).append("\"");
            }

            // Estado
            if (seccion.getEstado() != null) {
                json.append(',').append("\"estado\":\"").append(escaparJson(seccion.getEstado())).append("\"");
            }

            // Cobra Impuesto
            if (seccion.getCobraImpuesto() != null) {
                json.append(',').append("\"cobraImpuesto\":\"").append(escaparJson(seccion.getCobraImpuesto())).append("\"");
            }

            // *** IMPORTANTE: ID del archivo de imagen ***
            if (seccion.getIdArchivoImagen() != null && seccion.getIdArchivoImagen() > 0) {
                json.append(',').append("\"idArchivoImagen\":").append(seccion.getIdArchivoImagen());
            }

            // Archivo Imagen completo (objeto anidado) - Solo si necesitas enviarlo
            if (seccion.getImagen() != null) {
                json.append(',').append("\"imagen\":");
                // Recursión para convertir el ArchivoDto anidado
                json.append(convertirObjetoAJson(seccion.getImagen()));
            }

            json.append('}');
            return json.toString();
        }

        // Soportar MesaDto
        if (objeto instanceof cr.ac.una.restuna.model.MesaDto) {
            cr.ac.una.restuna.model.MesaDto mesa = (cr.ac.una.restuna.model.MesaDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (mesa.getIdMesa() != null && mesa.getIdMesa() > 0) {
                json.append("\"idMesa\":").append(mesa.getIdMesa()).append(',');
            }

            // ID Sección (obligatorio)
            if (mesa.getIdSeccion() != null) {
                json.append("\"idSeccion\":").append(mesa.getIdSeccion()).append(',');
            }

            // Número de Mesa (obligatorio)
            if (mesa.getNumeroMesa() != null) {
                json.append("\"numeroMesa\":\"").append(escaparJson(mesa.getNumeroMesa())).append("\",");
            }

            // Capacidad
            if (mesa.getCapacidad() != null) {
                json.append("\"capacidad\":").append(mesa.getCapacidad()).append(',');
            }

            // Posición X
            if (mesa.getPosicionX() != null) {
                json.append("\"posicionX\":").append(mesa.getPosicionX()).append(',');
            }

            // Posición Y
            if (mesa.getPosicionY() != null) {
                json.append("\"posicionY\":").append(mesa.getPosicionY()).append(',');
            }

            // Estado
            if (mesa.getEstado() != null) {
                json.append("\"estado\":\"").append(escaparJson(mesa.getEstado())).append("\",");
            }

            // Eliminar la última coma si existe
            if (json.charAt(json.length() - 1) == ',') {
                json.setLength(json.length() - 1);
            }

            json.append('}');
            return json.toString();
        }

        // Soportar ClienteDto
        if (objeto instanceof cr.ac.una.restuna.model.ClienteDto) {
            cr.ac.una.restuna.model.ClienteDto cliente = (cr.ac.una.restuna.model.ClienteDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (cliente.getIdCliente() != null && cliente.getIdCliente() > 0) {
                json.append("\"idCliente\":").append(cliente.getIdCliente()).append(',');
            }

            // Nombre (requerido)
            if (cliente.getNombre() != null && !cliente.getNombre().isEmpty()) {
                json.append("\"nombre\":\"").append(escaparJson(cliente.getNombre())).append("\"");
            } else {
                json.append("\"nombre\":null");
            }

            // Correo (opcional)
            if (cliente.getCorreo() != null && !cliente.getCorreo().isEmpty()) {
                json.append(",\"correo\":\"").append(escaparJson(cliente.getCorreo())).append("\"");
            }

            json.append('}');
            return json.toString();
        }

        // Soportar GrupoProductoDto
        if (objeto instanceof cr.ac.una.restuna.model.GrupoProductoDto) {
            cr.ac.una.restuna.model.GrupoProductoDto grupo = (cr.ac.una.restuna.model.GrupoProductoDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (grupo.getIdGrupoProducto() != null && grupo.getIdGrupoProducto() > 0) {
                json.append("\"idGrupoProducto\":").append(grupo.getIdGrupoProducto()).append(',');
            }

            // Nombre (obligatorio)
            if (grupo.getNombre() != null) {
                json.append("\"nombre\":\"").append(escaparJson(grupo.getNombre())).append("\",");
            }

            // Descripción
            if (grupo.getDescripcion() != null && !grupo.getDescripcion().isEmpty()) {
                json.append("\"descripcion\":\"").append(escaparJson(grupo.getDescripcion())).append("\",");
            }

            // Acceso Rápido
            if (grupo.getAccesoRapido() != null) {
                json.append("\"accesoRapido\":\"").append(escaparJson(grupo.getAccesoRapido())).append("\",");
            }

            // Orden de Visualización
            if (grupo.getOrdenVisualizacion() != null) {
                json.append("\"ordenVisualizacion\":").append(grupo.getOrdenVisualizacion()).append(',');
            }

            // Estado
            if (grupo.getEstado() != null) {
                json.append("\"estado\":\"").append(escaparJson(grupo.getEstado())).append("\",");
            }

            // Eliminar la última coma si existe
            if (json.charAt(json.length() - 1) == ',') {
                json.setLength(json.length() - 1);
            }

            json.append('}');
            return json.toString();
        }

        // Soportar ProductoDto
        if (objeto instanceof cr.ac.una.restuna.model.ProductoDto) {
            cr.ac.una.restuna.model.ProductoDto producto = (cr.ac.una.restuna.model.ProductoDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (producto.getIdProducto() != null && producto.getIdProducto() > 0) {
                json.append("\"idProducto\":").append(producto.getIdProducto()).append(',');
            }

            // ID Grupo Producto (obligatorio)
            if (producto.getIdGrupoProducto() != null) {
                json.append("\"idGrupoProducto\":").append(producto.getIdGrupoProducto()).append(',');
            }

            // Nombre (obligatorio)
            if (producto.getNombre() != null) {
                json.append("\"nombre\":\"").append(escaparJson(producto.getNombre())).append("\",");
            }

            // Nombre Corto (obligatorio)
            if (producto.getNombreCorto() != null) {
                json.append("\"nombreCorto\":\"").append(escaparJson(producto.getNombreCorto())).append("\",");
            }

            // Descripción
            if (producto.getDescripcion() != null && !producto.getDescripcion().isEmpty()) {
                json.append("\"descripcion\":\"").append(escaparJson(producto.getDescripcion())).append("\",");
            }

            // Precio (obligatorio)
            if (producto.getPrecio() != null) {
                json.append("\"precio\":").append(producto.getPrecio()).append(',');
            }

            // Acceso Rápido
            if (producto.getAccesoRapido() != null) {
                json.append("\"accesoRapido\":\"").append(escaparJson(producto.getAccesoRapido())).append("\",");
            }

            // Estado
            if (producto.getEstado() != null) {
                json.append("\"estado\":\"").append(escaparJson(producto.getEstado())).append("\",");
            }

            // Eliminar la última coma si existe
            if (json.charAt(json.length() - 1) == ',') {
                json.setLength(json.length() - 1);
            }

            json.append('}');
            return json.toString();
        }

        // Soportar OrdenDto
        if (objeto instanceof cr.ac.una.restuna.model.OrdenDto) {
            cr.ac.una.restuna.model.OrdenDto orden = (cr.ac.una.restuna.model.OrdenDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (orden.getIdOrden() != null && orden.getIdOrden() > 0) {
                json.append("\"idOrden\":").append(orden.getIdOrden()).append(',');
            }

            // ID Mesa
            if (orden.getIdMesa() != null) {
                json.append("\"idMesa\":").append(orden.getIdMesa()).append(',');
            }

            // ID Sección
            if (orden.getIdSeccion() != null) {
                json.append("\"idSeccion\":").append(orden.getIdSeccion()).append(',');
            }

            // ID Cliente
            if (orden.getIdCliente() != null) {
                json.append("\"idCliente\":").append(orden.getIdCliente()).append(',');
            }

            // ID Salonero
            if (orden.getIdSalonero() != null) {
                json.append("\"idSalonero\":").append(orden.getIdSalonero()).append(',');
            }

            // Número Orden
            if (orden.getNumeroOrden() != null && !orden.getNumeroOrden().isEmpty()) {
                json.append("\"numeroOrden\":\"").append(escaparJson(orden.getNumeroOrden())).append("\",");
            }

            // Estado
            if (orden.getEstado() != null) {
                json.append("\"estado\":\"").append(escaparJson(orden.getEstado())).append("\",");
            }

            // Observaciones
            if (orden.getObservaciones() != null && !orden.getObservaciones().isEmpty()) {
                json.append("\"observaciones\":\"").append(escaparJson(orden.getObservaciones())).append("\",");
            }

            // Fecha Hora (formato ISO LocalDateTime sin nanosegundos)
            if (orden.getFechaHora() != null) {
                // Truncar a segundos para coincidir con formato backend: yyyy-MM-dd'T'HH:mm:ss
                java.time.LocalDateTime fechaTruncada = orden.getFechaHora().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
                json.append("\"fechaHora\":\"").append(fechaTruncada.toString()).append("\",");
            }

            // Subtotal
            if (orden.getSubtotal() != null) {
                json.append("\"subtotal\":").append(orden.getSubtotal()).append(',');
            }

            // Detalles (lista de DetalleOrdenDto)
            if (orden.getDetalles() != null && !orden.getDetalles().isEmpty()) {
                json.append("\"detalles\":[");
                for (int i = 0; i < orden.getDetalles().size(); i++) {
                    if (i > 0) {
                        json.append(',');
                    }
                    json.append(convertirDetalleOrdenDtoAJson(orden.getDetalles().get(i)));
                }
                json.append("],");
            }

            // Eliminar la última coma si existe
            if (json.charAt(json.length() - 1) == ',') {
                json.setLength(json.length() - 1);
            }

            json.append('}');
            return json.toString();
        }

        // Soportar List (para listas de DTOs como List<ParametroDto>)
        if (objeto instanceof java.util.List) {
            java.util.List<?> lista = (java.util.List<?>) objeto;
            StringBuilder json = new StringBuilder();
            json.append('[');

            for (int i = 0; i < lista.size(); i++) {
                if (i > 0) {
                    json.append(',');
                }
                Object item = lista.get(i);

                // Recursión para convertir cada elemento
                if (item instanceof cr.ac.una.restuna.model.ParametroDto) {
                    json.append(convertirParametroDtoAJson((cr.ac.una.restuna.model.ParametroDto) item));
                } else if (item instanceof cr.ac.una.restuna.model.MesaDto) {
                    json.append(convertirObjetoAJson(item));
                } else {
                    // Para otros tipos, usar recursión general
                    json.append(convertirObjetoAJson(item));
                }
            }

            json.append(']');
            return json.toString();
        }

        // Soportar ParametroDto individual
        if (objeto instanceof cr.ac.una.restuna.model.ParametroDto) {
            return convertirParametroDtoAJson((cr.ac.una.restuna.model.ParametroDto) objeto);
        }

        // Soportar FacturaDto
        if (objeto instanceof cr.ac.una.restuna.model.FacturaDto) {
            cr.ac.una.restuna.model.FacturaDto factura = (cr.ac.una.restuna.model.FacturaDto) objeto;
            StringBuilder json = new StringBuilder();
            json.append('{');

            // ID (si existe)
            if (factura.getIdFactura() != null && factura.getIdFactura() > 0) {
                json.append("\"idFactura\":").append(factura.getIdFactura()).append(',');
            }

            // ID Orden
            if (factura.getIdOrden() != null) {
                json.append("\"idOrden\":").append(factura.getIdOrden()).append(',');
            }

            // ID Cliente
            if (factura.getIdCliente() != null) {
                json.append("\"idCliente\":").append(factura.getIdCliente()).append(',');
            }

            // ID Usuario Cajero (obligatorio)
            if (factura.getIdUsuarioCajero() != null) {
                json.append("\"idCajero\":").append(factura.getIdUsuarioCajero()).append(',');
            }

            // Subtotal (obligatorio)
            if (factura.getSubtotal() != null) {
                json.append("\"subtotal\":").append(factura.getSubtotal()).append(',');
            }

            // Impuesto Venta (obligatorio)
            if (factura.getImpuestoVenta() != null) {
                json.append("\"impuestoVenta\":").append(factura.getImpuestoVenta()).append(',');
            }

            // Impuesto Servicio (obligatorio)
            if (factura.getImpuestoServicio() != null) {
                json.append("\"impuestoServicio\":").append(factura.getImpuestoServicio()).append(',');
            }

            // Total
            if (factura.getTotal() != null) {
                json.append("\"total\":").append(factura.getTotal()).append(',');
            }

            // Efectivo Recibido
            if (factura.getEfectivoRecibido() != null) {
                json.append("\"efectivoRecibido\":").append(factura.getEfectivoRecibido()).append(',');
            }

            // Tarjeta Recibida
            if (factura.getTarjetaRecibida() != null) {
                json.append("\"tarjetaRecibido\":").append(factura.getTarjetaRecibida()).append(',');
            }

            // Vuelto
            if (factura.getVuelto() != null) {
                json.append("\"vuelto\":").append(factura.getVuelto()).append(',');
            }

            // Fecha Factura
            if (factura.getFechaFactura() != null) {
                json.append("\"fechaFactura\":\"").append(factura.getFechaFactura().getTime()).append("\",");
            }

            // Eliminar la última coma si existe
            if (json.charAt(json.length() - 1) == ',') {
                json.setLength(json.length() - 1);
            }

            json.append('}');
            return json.toString();
        }

        // Para otros tipos de objetos, usar toString como fallback (no recomendado)
        return objeto.toString();
    }

    /**
     * Convierte un ParametroDto a JSON
     */
    private String convertirParametroDtoAJson(cr.ac.una.restuna.model.ParametroDto parametro) {
        StringBuilder json = new StringBuilder();
        json.append('{');

        // ID (si existe)
        if (parametro.getIdParametro() != null && parametro.getIdParametro() > 0) {
            json.append("\"idParametro\":").append(parametro.getIdParametro()).append(',');
        }

        // ID Usuario (obligatorio)
        if (parametro.getIdUsuario() != null) {
            json.append("\"idUsuario\":").append(parametro.getIdUsuario()).append(',');
        }

        // Clave (obligatoria)
        if (parametro.getClave() != null) {
            json.append("\"clave\":\"").append(escaparJson(parametro.getClave())).append("\",");
        }

        // Valor (obligatorio)
        if (parametro.getValor() != null) {
            json.append("\"valor\":\"").append(escaparJson(parametro.getValor())).append("\",");
        }

        // Descripción
        if (parametro.getDescripcion() != null) {
            json.append("\"descripcion\":\"").append(escaparJson(parametro.getDescripcion())).append("\",");
        }

        // Tipo de dato
        if (parametro.getTipoDato() != null) {
            json.append("\"tipoDato\":\"").append(escaparJson(parametro.getTipoDato())).append("\",");
        }

        // Modificado
        if (parametro.getModificado() != null) {
            json.append("\"modificado\":").append(parametro.getModificado()).append(',');
        }

        // Eliminar la última coma si existe
        if (json.charAt(json.length() - 1) == ',') {
            json.setLength(json.length() - 1);
        }

        json.append('}');
        return json.toString();
    }

    /**
     * Convierte un DetalleOrdenDto a JSON
     */
    private String convertirDetalleOrdenDtoAJson(cr.ac.una.restuna.model.DetalleOrdenDto detalle) {
        StringBuilder json = new StringBuilder();
        json.append('{');

        // ID (si existe)
        if (detalle.getIdDetalleOrden() != null && detalle.getIdDetalleOrden() > 0) {
            json.append("\"idDetalleOrden\":").append(detalle.getIdDetalleOrden()).append(',');
        }

        // ID Orden
        if (detalle.getIdOrden() != null) {
            json.append("\"idOrden\":").append(detalle.getIdOrden()).append(',');
        }

        // ID Producto (obligatorio)
        if (detalle.getIdProducto() != null) {
            json.append("\"idProducto\":").append(detalle.getIdProducto()).append(',');
        }

        // Cantidad (obligatoria)
        if (detalle.getCantidad() != null) {
            json.append("\"cantidad\":").append(detalle.getCantidad()).append(',');
        }

        // Precio Unitario (obligatorio)
        if (detalle.getPrecioUnitario() != null) {
            json.append("\"precioUnitario\":").append(detalle.getPrecioUnitario()).append(',');
        }

        // Subtotal (obligatorio)
        if (detalle.getSubtotal() != null) {
            json.append("\"subtotal\":").append(detalle.getSubtotal()).append(',');
        }

        // Observaciones
        if (detalle.getObservaciones() != null && !detalle.getObservaciones().isEmpty()) {
            json.append("\"observaciones\":\"").append(escaparJson(detalle.getObservaciones())).append("\",");
        }

        // Eliminar la última coma si existe
        if (json.charAt(json.length() - 1) == ',') {
            json.setLength(json.length() - 1);
        }

        json.append('}');
        return json.toString();
    }

    /**
     * Escapa caracteres especiales en strings para JSON
     */
    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Realiza una petición DELETE
     */
    public void delete() {
        try {
            String url = buildUrl();
            HttpURLConnection connection = createConnection(url, "DELETE");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición DELETE", e);
        }
    }

    /**
     * Método para autenticación con token
     */
    public void getToken() {
        try {
            String url = buildUrl();
            HttpURLConnection connection = createConnection(url, "GET");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error obteniendo token", e);
        }
    }

    /**
     * Método para renovar token
     */
    public void getRenewal() {
        try {
            String url = BASE_URL + endpoint;
            HttpURLConnection connection = createConnection(url, "GET");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error renovando token", e);
        }
    }

    /**
     * Lee la respuesta como una entidad específica Versión simplificada que
     * retorna el JSON como string
     */
    public <T> T readEntity(Class<T> entityClass) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }
            // Por simplicidad, retornamos el JSON como String si es String.class
            if (entityClass == String.class) {
                @SuppressWarnings("unchecked")
                T result = (T) responseBody;
                return result;
            }
            // Para otros tipos necesitarías deserialización manual o Jackson
            return null;
        } catch (Exception e) {
            handleError("Error leyendo respuesta", e);
            return null;
        }
    }

    /**
     * Lee la respuesta como una lista usando GenericType simulado
     */
    public <T> T readEntity(GenericType<T> genericType) {
        // Por simplicidad, retornamos null
        // En implementación real necesitarías deserialización apropiada
        return null;
    }

    private String buildUrl() {
        String url = BASE_URL + endpoint;
        if (pathTemplate != null && parametros != null) {
            String path = pathTemplate;
            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                path = path.replace("{" + entry.getKey() + "}",
                        entry.getValue() != null ? entry.getValue().toString() : "");
            }
            url += path;
        }
        return url;
    }

    private HttpURLConnection createConnection(String urlString, String method) throws Exception {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setRequestProperty("Accept", CONTENT_TYPE);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        return connection;
    }

    private void processResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode + ", URL: " + connection.getURL());

        InputStream inputStream = null;
        try {
            // Intentar obtener el stream adecuado según el código de respuesta
            inputStream = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            // Si ambos son nulos (raro pero posible), manejar el caso
            if (inputStream == null) {
                isError = true;
                error = "Error: No se pudo leer la respuesta del servidor";
                return;
            }

            // Leer la respuesta
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            responseBody = response.toString();
            reader.close();

            // Manejar códigos de error HTTP
            if (responseCode < 200 || responseCode >= 300) {
                isError = true;

                // Filtrar contenido HTML para que no se muestre al usuario final
                String errorBody = responseBody;

                // Verificar si la respuesta contiene HTML y eliminarla
                if (errorBody != null && (errorBody.contains("<html") || errorBody.contains("<!DOCTYPE"))) {
                    errorBody = "Respuesta con formato HTML no mostrable";
                }

                // Guardar error completo en logs pero no mostrarlo al usuario
                System.err.println("Error HTTP: HTTP " + responseCode + ": " + errorBody);

                // Generar mensaje amigable según el código de error
                switch (responseCode) {
                    case 400:
                        error = "Error 400: Los datos enviados son incorrectos o incompletos";
                        break;
                    case 401:
                        error = "Error 401: No está autorizado para esta operación";
                        break;
                    case 403:
                        error = "Error 403: No tiene permisos para acceder a este recurso";
                        break;
                    case 404:
                        error = "Error 404: El recurso solicitado no existe";
                        break;
                    case 500:
                        error = "Error 500: Error interno del servidor";
                        break;
                    default:
                        error = "Error " + responseCode + ": No se pudo completar la solicitud";
                }
            } else {
                System.out.println("Respuesta recibida correctamente, longitud: "
                        + (responseBody != null ? responseBody.length() : 0));
            }
        } catch (Exception e) {
            isError = true;
            error = "Error de comunicación con el servidor";
            System.err.println("Error técnico completo: " + e.getMessage());
            e.printStackTrace(); // Solo para log, no para usuario
            throw e;
        } finally {
            // Cerrar el stream si existe
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("Error cerrando stream: " + e.getMessage());
                }
            }
        }
    }

    private void handleError(String message, Exception e) {
        isError = true;
        error = message + ": " + e.getMessage();
        LOGGER.log(Level.SEVERE, message, e);
    }

    public boolean isError() {
        return isError;
    }

    public String getError() {
        return error;
    }

    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Clase interna para simular GenericType
     */
    public static class GenericType<T> {
        // Implementación básica para compatibilidad
    }
}
