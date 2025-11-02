/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.FacturaDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fonse
 */
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
}
