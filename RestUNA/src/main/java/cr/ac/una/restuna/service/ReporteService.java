/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.service;

import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fonse
 */
public class ReporteService {

    private static final Logger LOG = Logger.getLogger(ReporteService.class.getName());

    public Respuesta reporteProductoVendido(Date fechaInicio, Date fechaFin) {

        try {
            if (fechaInicio == null || fechaFin == null) {

                return new Respuesta(false, "Debe colocar las fechas", "Fechas invalidas");

            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaInicio", fechaInicio);
            parametros.put("fechaFin", fechaFin);

            Request request = new Request("ReporteController/reporte/productos-vendidos", "", parametros);
            request.post(parametros);

            if (request.isError()) {

                return new Respuesta(false, "Error en el Reporte", request.getError());

            }

            String json = request.getResponseBody();
            return new Respuesta(true, "", "", "Reporte", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando reporte de productos", e);
            return new Respuesta(false, "Error generando reporte de productos.", "reporteProductoVendido " + e.getMessage());
        }

    }

    public Respuesta reporteFactura(Date fechaInicio, Date fechaFin) {

        try {
            if (fechaInicio == null || fechaFin == null) {

                return new Respuesta(false, "Debe colocar las fechas", "Fechas invalidas");

            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaInicio", fechaInicio);
            parametros.put("fechaFin", fechaFin);

            Request request = new Request("ReporteController/reporte/facturas", "", parametros);
            request.post(parametros);

            if (request.isError()) {

                return new Respuesta(false, "Error en el Reporte", request.getError());

            }

            String json = request.getResponseBody();
            return new Respuesta(true, "", "", "Reporte", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando reporte de facturas", e);
            return new Respuesta(false, "Error generando reporte de facturas.", "reporteFactura " + e.getMessage());
        }
    }

    public Respuesta reporteCierreCaja(Long idCierreCaja) {

        try {

            if (idCierreCaja == null) {

                return new Respuesta(false, "ID obligatorio", "ID vacio o incorrcto");

            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idCierreCja", idCierreCaja);

            Request request = new Request("ReporteController/reporte/cierre-caja", "", parametros);
            request.post(parametros);

            if (request.isError()) {

                return new Respuesta(false, "Error en el Reporte", request.getError());

            }

            String json = request.getResponseBody();
            return new Respuesta(true, "", "", "Reporte", json);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando reporte de cierre", e);
            return new Respuesta(false, "Error generando reporte de cierre.", "reporteCierreCaja " + e.getMessage());
        }
    }

    public Respuesta pdfFactura(Long idFactura) {
        try {

            if (idFactura == null) {

                return new Respuesta(false, "ID obligatorio", "ID vacio o incorrcto");

            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", idFactura);

            Request request = new Request("ReporteController/factura", "/{id}/pdf", parametros);
            byte[] pdf = request.getResponseBytes();

            if (request.isError()) {

                return new Respuesta(false, "Error en el pdf", request.getError());

            }

            if (pdf == null || pdf.length == 0) {
                return new Respuesta(false, "error en el pdf ", "pdf vacio");
            }

            Respuesta respuesta = new Respuesta(true, "pdf correcto", "");
            respuesta.setResultado("PDF", pdf);
            return respuesta;

        } catch (Exception e) {
            Logger.getLogger(ReporteService.class.getName()).log(Level.SEVERE, "Error generando PDF de factura", e);
            return new Respuesta(false, "Error generando PDF de factura", "pdfFactura " + e.getMessage());
        }
    }
}
