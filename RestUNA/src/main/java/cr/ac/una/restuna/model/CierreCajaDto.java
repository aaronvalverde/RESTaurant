/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author fonse
 */
public class CierreCajaDto extends RecursiveTreeObject<CierreCajaDto> implements Serializable {

    private LongProperty idCierreCaja;
    private LongProperty idUsuarioCajero;
    private ObjectProperty<Date> fechaApertura;
    private ObjectProperty<Date> fechaCierre;
    private LongProperty efectivoInicial;
    private LongProperty efectivoSistema;
    private LongProperty efectivoDeclarado;
    private LongProperty tarjetaSistema;
    private LongProperty tarjetaDeclarado;
    private LongProperty diferenciaEfectivo;
    private LongProperty diferenciaTarjeta;
    private IntegerProperty totalFacturas;
    private StringProperty estado;
    private StringProperty observaciones;

    public CierreCajaDto() {
        this.idCierreCaja = new SimpleLongProperty();
        this.idUsuarioCajero = new SimpleLongProperty();
        this.fechaApertura = new SimpleObjectProperty<>();
        this.fechaCierre = new SimpleObjectProperty<>();
        this.efectivoInicial = new SimpleLongProperty();
        this.efectivoSistema = new SimpleLongProperty();
        this.efectivoDeclarado = new SimpleLongProperty();
        this.tarjetaSistema = new SimpleLongProperty();
        this.tarjetaDeclarado = new SimpleLongProperty();
        this.diferenciaEfectivo = new SimpleLongProperty();
        this.diferenciaTarjeta = new SimpleLongProperty();
        this.totalFacturas = new SimpleIntegerProperty();
        this.estado = new SimpleStringProperty();
        this.observaciones = new SimpleStringProperty();
    }

    public Long getIdCierreCaja() {
        return idCierreCaja.get();
    }

    public Long getIdUsuarioCajero() {
        return idUsuarioCajero.get();
    }

    public Date getFechaApertura() {
        return fechaApertura.get();
    }

    public Date getFechaCierre() {
        return fechaCierre.get();
    }

    public Long getEfectivoInicial() {
        return efectivoInicial.get();
    }

    public Long getEfectivoSistema() {
        return efectivoSistema.get();
    }

    public Long getEfectivoDeclarado() {
        return efectivoDeclarado.get();
    }

    public Long getTarjetaSistema() {
        return tarjetaSistema.get();
    }

    public Long getTarjetaDeclarado() {
        return tarjetaDeclarado.get();
    }

    public Long getDiferenciaEfectivo() {
        return diferenciaEfectivo.get();
    }

    public Long getDiferenciaTarjeta() {
        return diferenciaTarjeta.get();
    }

    public Integer getTotalFacturas() {
        return totalFacturas.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public String getObservaciones() {
        return observaciones.get();
    }

    public void setIdCierreCaja(Long idCierreCaja) {
        this.idCierreCaja.set(idCierreCaja);
    }

    public void setIdUsuarioCajero(Long idUsuarioCajero) {
        this.idUsuarioCajero.set(idUsuarioCajero);
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura.set(fechaApertura);
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre.set(fechaCierre);
    }

    public void setEfectivoInicial(Long efectivoInicial) {
        this.efectivoInicial.set(efectivoInicial);
    }

    public void setEfectivoSistema(Long efectivoSistema) {
        this.efectivoSistema.set(efectivoSistema);
    }

    public void setEfectivoDeclarado(Long efectivoDeclarado) {
        this.efectivoDeclarado.set(efectivoDeclarado);
    }

    public void setTarjetaSistema(Long tarjetaSistema) {
        this.tarjetaSistema.set(tarjetaSistema);
    }

    public void setTarjetaDeclarado(Long tarjetaDeclarado) {
        this.tarjetaDeclarado.set(tarjetaDeclarado);
    }

    public void setDiferenciaEfectivo(Long diferenciaEfectivo) {
        this.diferenciaEfectivo.set(diferenciaEfectivo);
    }

    public void setDiferenciaTarjeta(Long diferenciaTarjeta) {
        this.diferenciaTarjeta.set(diferenciaTarjeta);
    }

    public void setTotalFacturas(Integer totalFacturas) {
        this.totalFacturas.set(totalFacturas);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public void setObservaciones(String observaciones) {
        this.observaciones.set(observaciones);
    }

    // Property methods for JavaFX binding
    public LongProperty idCierreCajaProperty() {
        return idCierreCaja;
    }

    public LongProperty idUsuarioCajeroProperty() {
        return idUsuarioCajero;
    }

    public ObjectProperty<Date> fechaAperturaProperty() {
        return fechaApertura;
    }

    public ObjectProperty<Date> fechaCierreProperty() {
        return fechaCierre;
    }

    public LongProperty efectivoInicialProperty() {
        return efectivoInicial;
    }

    public LongProperty efectivoSistemaProperty() {
        return efectivoSistema;
    }

    public LongProperty efectivoDeclaradoProperty() {
        return efectivoDeclarado;
    }

    public LongProperty tarjetaSistemaProperty() {
        return tarjetaSistema;
    }

    public LongProperty tarjetaDeclaradoProperty() {
        return tarjetaDeclarado;
    }

    public LongProperty diferenciaEfectivoProperty() {
        return diferenciaEfectivo;
    }

    public LongProperty diferenciaTarjetaProperty() {
        return diferenciaTarjeta;
    }

    public IntegerProperty totalFacturasProperty() {
        return totalFacturas;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public StringProperty observacionesProperty() {
        return observaciones;
    }

}
