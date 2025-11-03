package cr.ac.una.restuna.util;

import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.ParametroDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;


public class BillingCalculator {

    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    
    public static class BillingResult {
        private BigDecimal subtotal;
        private BigDecimal iva;
        private BigDecimal serviceTax;
        private BigDecimal total;
        private String currency;
        
        public BillingResult(BigDecimal subtotal, BigDecimal iva, BigDecimal serviceTax, BigDecimal total, String currency) {
            this.subtotal = subtotal.setScale(DECIMAL_PLACES, ROUNDING_MODE);
            this.iva = iva.setScale(DECIMAL_PLACES, ROUNDING_MODE);
            this.serviceTax = serviceTax.setScale(DECIMAL_PLACES, ROUNDING_MODE);
            this.total = total.setScale(DECIMAL_PLACES, ROUNDING_MODE);
            this.currency = currency;
        }
        
        public BigDecimal getSubtotal() {
            return subtotal;
        }
        
        public BigDecimal getIva() {
            return iva;
        }
        
        public BigDecimal getServiceTax() {
            return serviceTax;
        }
        
        public BigDecimal getTotal() {
            return total;
        }
        
        public String getCurrency() {
            return currency;
        }
        
        public String getFormattedSubtotal() {
            return formatCurrency(subtotal, currency);
        }
        
        public String getFormattedIva() {
            return formatCurrency(iva, currency);
        }
        
        public String getFormattedServiceTax() {
            return formatCurrency(serviceTax, currency);
        }
        
        public String getFormattedTotal() {
            return formatCurrency(total, currency);
        }
    }
    
    
    public static BillingResult calculateBilling(List<DetalleOrdenDto> orderItems, boolean sectionHasTax, Map<String, ParametroDto> parametros) {
        
        BigDecimal subtotalCRC = BigDecimal.ZERO;
        for (DetalleOrdenDto item : orderItems) {
            if (item.getPrecioUnitario() != null && item.getCantidad() != null) {
                BigDecimal price = BigDecimal.valueOf(item.getPrecioUnitario());
                BigDecimal quantity = BigDecimal.valueOf(item.getCantidad());
                subtotalCRC = subtotalCRC.add(price.multiply(quantity));
            }
        }
        
        
        BigDecimal ivaRate = getIvaRate(parametros);
        BigDecimal serviceRate = getServiceRate(parametros);
        String currency = getCurrency(parametros);
        BigDecimal exchangeRate = getExchangeRate(parametros, currency);
        
        
        BigDecimal subtotal = convertCurrency(subtotalCRC, exchangeRate);
        
        
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal serviceTax = BigDecimal.ZERO;
        
        if (sectionHasTax) {
            iva = subtotal.multiply(ivaRate);
            serviceTax = subtotal.multiply(serviceRate);
        }
        
        
        BigDecimal total = subtotal.add(iva).add(serviceTax);
        
        return new BillingResult(subtotal, iva, serviceTax, total, currency);
    }
    
    
    private static BigDecimal getIvaRate(Map<String, ParametroDto> parametros) {
        if (parametros == null) {
            return BigDecimal.valueOf(0.13); 
        }
        
        ParametroDto ivaParam = parametros.get("IMPUESTO_VENTA");
        if (ivaParam != null && ivaParam.getValorComoDecimal() != null) {
            return BigDecimal.valueOf(ivaParam.getValorComoDecimal() / 100.0);
        }
        
        return BigDecimal.valueOf(0.13); 
    }
    
    
    private static BigDecimal getServiceRate(Map<String, ParametroDto> parametros) {
        if (parametros == null) {
            return BigDecimal.valueOf(0.10); 
        }
        
        ParametroDto serviceParam = parametros.get("IMPUESTO_SERVICIO");
        if (serviceParam != null && serviceParam.getValorComoDecimal() != null) {
            return BigDecimal.valueOf(serviceParam.getValorComoDecimal() / 100.0);
        }
        
        return BigDecimal.valueOf(0.10); 
    }
    
    
    private static String getCurrency(Map<String, ParametroDto> parametros) {
        if (parametros == null) {
            return "CRC - Colón";
        }
        
        ParametroDto currencyParam = parametros.get("MONEDA");
        if (currencyParam != null && currencyParam.getValor() != null) {
            return currencyParam.getValor();
        }
        
        return "CRC - Colón";
    }
    
    
    private static BigDecimal getExchangeRate(Map<String, ParametroDto> parametros, String currency) {
        if (parametros == null || currency == null) {
            return BigDecimal.ONE; 
        }
        
        
        if (currency.startsWith("CRC")) {
            return BigDecimal.ONE;
        }
        
        
        if (currency.startsWith("USD")) {
            ParametroDto usdParam = parametros.get("TIPO_CAMBIO_USD");
            if (usdParam != null && usdParam.getValorComoDecimal() != null) {
                BigDecimal rate = BigDecimal.valueOf(usdParam.getValorComoDecimal());
                
                
                if (rate.compareTo(BigDecimal.ONE) > 0) {
                    return BigDecimal.ONE.divide(rate, 6, ROUNDING_MODE);
                }
                return rate;
            }
            
            return BigDecimal.ONE.divide(BigDecimal.valueOf(520), 6, ROUNDING_MODE);
        }
        
        
        if (currency.startsWith("EUR")) {
            ParametroDto eurParam = parametros.get("TIPO_CAMBIO_EUR");
            if (eurParam != null && eurParam.getValorComoDecimal() != null) {
                BigDecimal rate = BigDecimal.valueOf(eurParam.getValorComoDecimal());
                
                
                if (rate.compareTo(BigDecimal.ONE) > 0) {
                    return BigDecimal.ONE.divide(rate, 6, ROUNDING_MODE);
                }
                return rate;
            }
            
            return BigDecimal.ONE.divide(BigDecimal.valueOf(570), 6, ROUNDING_MODE);
        }
        
        
        return BigDecimal.ONE;
    }
    
    
    private static BigDecimal convertCurrency(BigDecimal amountCRC, BigDecimal exchangeRate) {
        if (amountCRC == null) {
            return BigDecimal.ZERO;
        }
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            return amountCRC; 
        }
        
        return amountCRC.multiply(exchangeRate).setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
    
    
    public static String formatCurrency(BigDecimal amount, String currency) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        
        amount = amount.setScale(DECIMAL_PLACES, ROUNDING_MODE);
        
        if (currency == null) {
            currency = "CRC - Colón";
        }
        
        String symbol = getCurrencySymbol(currency);
        return symbol + " " + String.format("%,.2f", amount);
    }
    
    
    private static String getCurrencySymbol(String currency) {
        if (currency == null) {
            return "₡";
        }
        
        if (currency.startsWith("USD")) {
            return "$";
        } else if (currency.startsWith("EUR")) {
            return "€";
        } else if (currency.startsWith("CRC")) {
            return "₡";
        }
        
        return "₡"; 
    }
    
    
    public static BigDecimal calculateChange(BigDecimal totalDue, BigDecimal amountPaid) {
        if (totalDue == null) totalDue = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        
        BigDecimal change = amountPaid.subtract(totalDue);
        return change.max(BigDecimal.ZERO).setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
    
    
    public static BigDecimal calculateAmountDue(BigDecimal totalDue, BigDecimal amountPaid) {
        if (totalDue == null) totalDue = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        
        BigDecimal due = totalDue.subtract(amountPaid);
        return due.max(BigDecimal.ZERO).setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
}
