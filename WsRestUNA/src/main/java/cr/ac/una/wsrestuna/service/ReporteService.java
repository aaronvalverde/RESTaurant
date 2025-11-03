/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Reporte;
import cr.ac.una.wsrestuna.model.ReporteDto;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author fonse
 */
@Stateless
@LocalBean
public class ReporteService {

    private static final Logger LOG = Logger.getLogger(ReporteService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String USUARIO = "RestUNA";
    private static final String CONTRASENA = "una";
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";

    public Respuesta reporteProductosVendidos(Date fechaInicio, Date fechaFin) {

        Connection connection = null;
        try {
            LOG.log(Level.INFO, "Generando reporte productos vendidos",
                    new Object[]{fechaInicio, fechaFin});

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("/Reportes/SalesReport.jrxml");

            if (reportStream == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró SalesReport.jrxml", "reporteProductosVendidos");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHAI", fechaInicio);
            parameters.put("FECHAF", fechaFin);

            connection = obtenerConexionJDBC();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            ReporteDto reporteDto = new ReporteDto();
            reporteDto.setTipoReporte("PRODUCTOS_VENDIDOS");
            reporteDto.setNombreReporte("reporte_productos_" + System.currentTimeMillis() + ".pdf");
            reporteDto.setPdf(pdfBytes);
            reporteDto.setParametros("fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin);

            return guardarReporte(reporteDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando reporte productos vendidos", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error generando reporte: " + e.getMessage(),
                    "reporteProductosVendidos " + e.getMessage());
        } finally {

            cerrarConexion(connection);
        }
    }

    public Respuesta reporteCierreCaja(Long idCierreCaja) {

        Connection connection = null;
        try {
            LOG.log(Level.INFO, "Generando reporte cierre caja ID: {0}", idCierreCaja);

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("/Reportes/CashClosingrReport.jrxml");

            if (reportStream == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró CashClosingrReport.jrxml", "reporteCierreCaja");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CAJERO", idCierreCaja);

            connection = obtenerConexionJDBC();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            ReporteDto reporteDto = new ReporteDto();
            reporteDto.setTipoReporte("CIERRE_CAJA");
            reporteDto.setNombreReporte("reporte_cierre_caja_" + System.currentTimeMillis() + ".pdf");
            reporteDto.setPdf(pdfBytes);
            reporteDto.setParametros("idCierreCaja=" + idCierreCaja);

            return guardarReporte(reporteDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando reporte cierre caja", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error generando reporte: " + e.getMessage(),
                    "reporteCierreCaja " + e.getMessage());
        } finally {

            cerrarConexion(connection);
        }
    }

    public Respuesta reporteFacturas(Date fechaInicio,Date fechaFin) {

        Connection connection = null;
        try {
            LOG.log(Level.INFO, "Generando reporte facturas: {0} a {1}",
                    new Object[]{fechaInicio, fechaFin});

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("/Reportes/BillingReport.jrxml");

            if (reportStream == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró BillingReport.jrxml", "reporteFacturas");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHAI", fechaInicio);
            parameters.put("FECHAF", fechaFin);

            connection = obtenerConexionJDBC();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            ReporteDto reporteDto = new ReporteDto();
            reporteDto.setTipoReporte("FACTURAS");
            reporteDto.setNombreReporte("reporte_facturas_" + System.currentTimeMillis() + ".pdf");
            reporteDto.setPdf(pdfBytes);
            reporteDto.setParametros("fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin);

            return guardarReporte(reporteDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando reporte facturas", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error generando reporte: " + e.getMessage(),
                    "reporteFacturas " + e.getMessage());
        } finally {

            cerrarConexion(connection);
        }
    }

    public Respuesta guardarReporte(ReporteDto reporteDto) {
        try {
            Reporte reporte = new Reporte(reporteDto);
            em.persist(reporte);
            em.flush();

            ReporteDto resultado = new ReporteDto(reporte);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "Reporte generado correctamente", "", "Reporte", resultado);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error guardando reporte.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error guardando el reporte: " + e.getMessage(), "guardarReporte " + e.getMessage());
        }
    }

    public Respuesta pdfFactura(Long idFactura) {

        Connection connection = null;
        try {
            LOG.log(Level.INFO, "Generando PDF de factura ID: {0}", idFactura);

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("/Reportes/BillingReport.jrxml");

            if (reportStream == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró BillingReport.jrxml", "pdfFactura");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FACTURA", idFactura);

            connection = obtenerConexionJDBC();

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            if (pdfBytes == null || pdfBytes.length == 0) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró la factura con ID: " + idFactura,
                        "pdfFactura");
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "PDF de factura generado correctamente", "pdfFactura",
                    "PDF", pdfBytes);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error generando PDF de factura individual ID: " + idFactura, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error generando PDF de factura: " + e.getMessage(),
                    "pdfFactura " + e.getMessage());
        } finally {
            cerrarConexion(connection);
        }
    }

    private Connection obtenerConexionJDBC() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    private void cerrarConexion(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LOG.log(Level.FINE, "Conexión JDBC cerrada correctamente");
                }
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Error al cerrar conexión JDBC", e);
            }
        }
    }

}
