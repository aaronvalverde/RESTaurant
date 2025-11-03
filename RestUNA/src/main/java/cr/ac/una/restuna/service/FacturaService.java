
package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.FacturaDto;
import cr.ac.una.restuna.model.ResumenCierreCajaDto;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;


public class FacturaService {
     private static final Logger LOG = Logger.getLogger(FacturaService.class.getName());
     
     
    public Respuesta getFactura(Long id) {
        try {
            Request request = new Request("FacturaController/factura","/{id}", null);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            FacturaDto factura = (FacturaDto) request.readEntity(FacturaDto.class);
            return new Respuesta(true, "", "", "Factura", factura);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo la factura [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo la factura.", "getFactura " + ex.getMessage());
        }
    }

    public Respuesta getFacturas() {
        try {
            Request request = new Request("FacturaController/facturas");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            FacturaDto[] facturas = (FacturaDto[]) request.readEntity(FacturaDto[].class);
            return new Respuesta(true, "", "", "Facturas", facturas);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo las facturas.", ex);
            return new Respuesta(false, "Error obteniendo las facturas.", "getFacturas " + ex.getMessage());
        }
    }

    public Respuesta getFacturasPorCajero(Long idCajero) {
        try {
            Request request = new Request("FacturaController/facturas/cajero/","/{idCajero}", null);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            FacturaDto[] facturas = (FacturaDto[]) request.readEntity(FacturaDto[].class);
            return new Respuesta(true, "", "", "Facturas", facturas);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo las facturas del cajero [" + idCajero + "].", ex);
            return new Respuesta(false, "Error obteniendo las facturas del cajero.", "getFacturasPorCajero " + ex.getMessage());
        }
    }


    public Respuesta guardarFactura(FacturaDto facturaDto) {
        try {
            if (facturaDto == null) {
                return new Respuesta(false, "Debe indicar la información de la factura.", "guardarFactura");
            }

            Request request = new Request("FacturaController/factura");
            request.post(facturaDto);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            FacturaDto factura = (FacturaDto) request.readEntity(FacturaDto.class);
            return new Respuesta(true, "", "", "Factura", factura);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando la factura.", ex);
            return new Respuesta(false, "Error guardando la factura.", "guardarFactura " + ex.getMessage());
        }
    }

    public Respuesta eliminarFactura(Long id) {
        try {
            Request request = new Request("FacturaController/factura/","/{id}", null);
            request.delete();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            return new Respuesta(true, "", "Factura eliminada correctamente.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando la factura.", ex);
            return new Respuesta(false, "Error eliminando la factura.", "eliminarFactura " + ex.getMessage());
        }
    }
    
    public Respuesta obtenerResumenCierreCaja(Long idCajero, Date fechaInicio, Date fechaFin) {
        try {
            if (idCajero == null) {
                return new Respuesta(false, "Debe indicar el cajero.", "obtenerResumenCierreCaja idCajero nulo");
            }
            if (fechaInicio == null || fechaFin == null) {
                return new Respuesta(false, "Debe indicar el rango de fechas.", "obtenerResumenCierreCaja fechas nulas");
            }

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String fechaInicioStr = URLEncoder.encode(formato.format(fechaInicio), StandardCharsets.UTF_8);
            String fechaFinStr = URLEncoder.encode(formato.format(fechaFin), StandardCharsets.UTF_8);

            String endpoint = String.format("FacturaController/facturas/resumen?idCajero=%d&fechaInicio=%s&fechaFin=%s",
                    idCajero, fechaInicioStr, fechaFinStr);

            Request request = new Request(endpoint);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.getResponseBody();
            ResumenCierreCajaDto resumen = parsearResumen(json);
            return new Respuesta(true, "", "", "ResumenCierreCaja", resumen);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo resumen de facturas para cierre de caja.", ex);
            return new Respuesta(false, "Error obteniendo el resumen de facturas.",
                    "obtenerResumenCierreCaja " + ex.getMessage());
        }
    }

    private ResumenCierreCajaDto parsearResumen(String json) {
        ResumenCierreCajaDto resumen = new ResumenCierreCajaDto();

        if (json == null || json.trim().isEmpty()) {
            return resumen;
        }

        Long totalFacturas = JsonParser.extraerValorLong(json, "totalFacturas");
        if (totalFacturas != null) {
            resumen.setTotalFacturas(totalFacturas);
        }

        String efectivoStr = JsonParser.extraerValorNumerico(json, "efectivoSistema");
        if (efectivoStr != null) {
            try {
                resumen.setEfectivoSistema(new BigDecimal(efectivoStr));
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "No se pudo parsear efectivoSistema: {0}", efectivoStr);
            }
        }

        String tarjetaStr = JsonParser.extraerValorNumerico(json, "tarjetaSistema");
        if (tarjetaStr != null) {
            try {
                resumen.setTarjetaSistema(new BigDecimal(tarjetaStr));
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "No se pudo parsear tarjetaSistema: {0}", tarjetaStr);
            }
        }

        return resumen;
    }

    public Respuesta obtenerFacturasPorPeriodo(Date fechaInicio, Date fechaFin) {
        try {
            
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaInicio", formato.format(fechaInicio));
            parametros.put("fechaFin", formato.format(fechaFin));
            
            
            Request request = new Request("FacturaController/facturas/fecha", "/{fechaInicio}/{fechaFin}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            
            String facturasJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Facturas", facturasJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo facturas por período.", ex);
            return new Respuesta(false, "Error obteniendo facturas por período.", "obtenerFacturasPorPeriodo " + ex.getMessage());
        }
    }
}
