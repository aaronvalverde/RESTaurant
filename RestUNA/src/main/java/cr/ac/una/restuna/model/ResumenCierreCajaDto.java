package cr.ac.una.restuna.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO local para representar el resumen retornado por el backend al calcular
 * los totales de facturas durante un periodo de caja abierta.
 *
 * @author Codex
 */
public class ResumenCierreCajaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalFacturas;
    private BigDecimal efectivoSistema;
    private BigDecimal tarjetaSistema;

    public ResumenCierreCajaDto() {
        this.totalFacturas = 0L;
        this.efectivoSistema = BigDecimal.ZERO;
        this.tarjetaSistema = BigDecimal.ZERO;
    }

    public Long getTotalFacturas() {
        return totalFacturas;
    }

    public void setTotalFacturas(Long totalFacturas) {
        this.totalFacturas = totalFacturas != null ? totalFacturas : 0L;
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

    public long getEfectivoSistemaAsLong() {
        return getEfectivoSistema().setScale(0, RoundingMode.HALF_UP).longValue();
    }

    public long getTarjetaSistemaAsLong() {
        return getTarjetaSistema().setScale(0, RoundingMode.HALF_UP).longValue();
    }
}
