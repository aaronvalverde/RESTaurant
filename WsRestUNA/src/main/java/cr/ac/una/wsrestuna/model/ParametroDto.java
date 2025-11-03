package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO para transferencia de datos de Parametro
 * Representa parámetros de configuración del sistema por usuario
 */
@Schema(description = "Parámetro de configuración asociado a un usuario")
public class ParametroDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del parámetro", example = "10")
    @JsonbProperty("idParametro")
    private Long idParametro;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "Identificador del usuario propietario", example = "3")
    @JsonbProperty("idUsuario")
    private Long idUsuario;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 50, message = "La clave no puede exceder 50 caracteres")
    @Schema(description = "Clave del parámetro", example = "IMPUESTO_VENTA")
    @JsonbProperty("clave")
    private String clave;

    @NotBlank(message = "El valor es obligatorio")
    @Size(max = 200, message = "El valor no puede exceder 200 caracteres")
    @Schema(description = "Valor del parámetro", example = "13")
    @JsonbProperty("valor")
    private String valor;

    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    @Schema(description = "Descripción del parámetro", example = "Porcentaje de impuesto de ventas")
    @JsonbProperty("descripcion")
    private String descripcion;

    @Pattern(regexp = "^(STRING|NUMBER|BOOLEAN|DATE)$", message = "El tipo de dato debe ser STRING, NUMBER, BOOLEAN o DATE")
    @Schema(description = "Tipo de dato almacenado", allowableValues = {"STRING", "NUMBER", "BOOLEAN", "DATE"}, example = "NUMBER")
    @JsonbProperty("tipoDato")
    private String tipoDato;

    @Schema(description = "Fecha de última modificación", example = "2024-06-01")
    @JsonbProperty("fechaModificacion")
    private LocalDate fechaModificacion;

    // Campos de control de modificación
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    private Boolean modificado;

    // Constructores
    public ParametroDto() {
        this.modificado = false;
        this.tipoDato = "STRING"; // Por defecto
    }

    public ParametroDto(Parametro parametro) {
        this();
        if (parametro != null) {
            this.idParametro = parametro.getIdParametro();
            this.idUsuario = parametro.getUsuario() != null ? parametro.getUsuario().getIdUsuario() : null;
            this.clave = parametro.getClave();
            this.valor = parametro.getValor();
            this.descripcion = parametro.getDescripcion();
            this.tipoDato = parametro.getTipoDato();
            this.fechaModificacion = parametro.getFechaModificacion();
        }
    }

    // Métodos de utilidad para conversión de tipos
    public Integer getValorComoEntero() {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getValorComoDecimal() {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getValorComoBoolean() {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        return "S".equalsIgnoreCase(valor) || "true".equalsIgnoreCase(valor) || "1".equals(valor);
    }

    // Getters y Setters
    public Long getIdParametro() {
        return idParametro;
    }

    public void setIdParametro(Long idParametro) {
        this.idParametro = idParametro;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDate fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    @Override
    public String toString() {
        return "ParametroDto{" +
                "idParametro=" + idParametro +
                ", idUsuario=" + idUsuario +
                ", clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                ", tipoDato='" + tipoDato + '\'' +
                '}';
    }
}
