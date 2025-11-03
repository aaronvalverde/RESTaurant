package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO simple para exponer los totales necesarios al realizar un cierre de caja.
 * Agrupa la cantidad de facturas y los montos cobrados en efectivo y tarjeta
 * dentro de un periodo determinado para un cajero.
 *
 */
@Schema(description = "Resumen de facturas durante el periodo de caja abierta")
public class ResumenCierreCajaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Cantidad de facturas registradas en el periodo", example = "12")
    private Long totalFacturas;

    @Schema(description = "Total cobrado en efectivo seg\u00fan el sistema", example = "32500.00")
    private BigDecimal efectivoSistema;

    @Schema(description = "Total cobrado con tarjeta seg\u00fan el sistema", example = "15800.00")
    private BigDecimal tarjetaSistema;

    public ResumenCierreCajaDto() {
        this.totalFacturas = 0L;
        this.efectivoSistema = BigDecimal.ZERO;
        this.tarjetaSistema = BigDecimal.ZERO;
    }

    public ResumenCierreCajaDto(Long totalFacturas, BigDecimal efectivoSistema, BigDecimal tarjetaSistema) {
        this.totalFacturas = totalFacturas != null ? totalFacturas : 0L;
        this.efectivoSistema = efectivoSistema != null ? efectivoSistema : BigDecimal.ZERO;
        this.tarjetaSistema = tarjetaSistema != null ? tarjetaSistema : BigDecimal.ZERO;
    }

    public Long getTotalFacturas() {
        return totalFacturas;
    }

    public void setTotalFacturas(Long totalFacturas) {
        this.totalFacturas = totalFacturas;
    }

    public BigDecimal getEfectivoSistema() {
        return efectivoSistema.setScale(2, RoundingMode.HALF_UP);
    }

    public void setEfectivoSistema(BigDecimal efectivoSistema) {
        this.efectivoSistema = efectivoSistema != null ? efectivoSistema : BigDecimal.ZERO;
    }

    public BigDecimal getTarjetaSistema() {
        return tarjetaSistema.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTarjetaSistema(BigDecimal tarjetaSistema) {
        this.tarjetaSistema = tarjetaSistema != null ? tarjetaSistema : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ResumenCierreCajaDto{" +
                "totalFacturas=" + totalFacturas +
                ", efectivoSistema=" + efectivoSistema +
                ", tarjetaSistema=" + tarjetaSistema +
                '}';
    }
}
