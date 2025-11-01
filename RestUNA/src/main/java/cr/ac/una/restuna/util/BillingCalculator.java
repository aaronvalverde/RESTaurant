package cr.ac.una.restuna.util;

import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.ParametroDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Utility class for billing calculations including taxes, service charges, and currency conversion
 * 
 * @author gambo
 */
public class BillingCalculator {

    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    /**
     * Result of billing calculation
     */
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
    
    /**
     * Calculate billing totals including taxes and currency conversion
     * 
     * @param orderItems List of order items
     * @param sectionHasTax Whether the section charges tax (S/N)
     * @param parametros Map of system parameters
     * @return BillingResult with all calculations
     */
    public static BillingResult calculateBilling(List<DetalleOrdenDto> orderItems, boolean sectionHasTax, Map<String, ParametroDto> parametros) {
        // Calculate subtotal in base currency (CRC)
        BigDecimal subtotalCRC = BigDecimal.ZERO;
        for (DetalleOrdenDto item : orderItems) {
            if (item.getPrecioUnitario() != null && item.getCantidad() != null) {
                BigDecimal price = BigDecimal.valueOf(item.getPrecioUnitario());
                BigDecimal quantity = BigDecimal.valueOf(item.getCantidad());
                subtotalCRC = subtotalCRC.add(price.multiply(quantity));
            }
        }
        
        // Get configuration from parameters
        BigDecimal ivaRate = getIvaRate(parametros);
        BigDecimal serviceRate = getServiceRate(parametros);
        String currency = getCurrency(parametros);
        BigDecimal exchangeRate = getExchangeRate(parametros, currency);
        
        // Convert subtotal to target currency
        BigDecimal subtotal = convertCurrency(subtotalCRC, exchangeRate);
        
        // Calculate taxes only if section has tax
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal serviceTax = BigDecimal.ZERO;
        
        if (sectionHasTax) {
            iva = subtotal.multiply(ivaRate);
            serviceTax = subtotal.multiply(serviceRate);
        }
        
        // Calculate total
        BigDecimal total = subtotal.add(iva).add(serviceTax);
        
        return new BillingResult(subtotal, iva, serviceTax, total, currency);
    }
    
    /**
     * Get IVA rate from parameters (default 13%)
     */
    private static BigDecimal getIvaRate(Map<String, ParametroDto> parametros) {
        if (parametros == null) {
            return BigDecimal.valueOf(0.13); // Default 13%
        }
        
        ParametroDto ivaParam = parametros.get("IMPUESTO_VENTA");
        if (ivaParam != null && ivaParam.getValorComoDecimal() != null) {
            return BigDecimal.valueOf(ivaParam.getValorComoDecimal() / 100.0);
        }
        
        return BigDecimal.valueOf(0.13); // Default 13%
    }
    
    /**
     * Get service tax rate from parameters (default 10%)
     */
    private static BigDecimal getServiceRate(Map<String, ParametroDto> parametros) {
        if (parametros == null) {
            return BigDecimal.valueOf(0.10); // Default 10%
        }
        
        ParametroDto serviceParam = parametros.get("IMPUESTO_SERVICIO");
        if (serviceParam != null && serviceParam.getValorComoDecimal() != null) {
            return BigDecimal.valueOf(serviceParam.getValorComoDecimal() / 100.0);
        }
        
        return BigDecimal.valueOf(0.10); // Default 10%
    }
    
    /**
     * Get currency from parameters (default CRC - Colón)
     */
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
    
    /**
     * Get exchange rate from parameters based on currency
     * CRC is the base currency (rate = 1.0)
     * 
     * @param parametros Map of system parameters
     * @param currency Target currency string
     * @return Exchange rate to convert from CRC to target currency
     */
    private static BigDecimal getExchangeRate(Map<String, ParametroDto> parametros, String currency) {
        if (parametros == null || currency == null) {
            return BigDecimal.ONE; // No conversion
        }
        
        // CRC is base currency, no conversion needed
        if (currency.startsWith("CRC")) {
            return BigDecimal.ONE;
        }
        
        // USD conversion
        if (currency.startsWith("USD")) {
            ParametroDto usdParam = parametros.get("TIPO_CAMBIO_USD");
            if (usdParam != null && usdParam.getValorComoDecimal() != null) {
                BigDecimal rate = BigDecimal.valueOf(usdParam.getValorComoDecimal());
                // If rate is > 1, it means CRC per USD (e.g., 520.50 CRC = 1 USD)
                // To convert CRC to USD: divide by rate
                if (rate.compareTo(BigDecimal.ONE) > 0) {
                    return BigDecimal.ONE.divide(rate, 6, ROUNDING_MODE);
                }
                return rate;
            }
            // Default: 1 USD = 520 CRC → to convert CRC to USD divide by 520
            return BigDecimal.ONE.divide(BigDecimal.valueOf(520), 6, ROUNDING_MODE);
        }
        
        // EUR conversion
        if (currency.startsWith("EUR")) {
            ParametroDto eurParam = parametros.get("TIPO_CAMBIO_EUR");
            if (eurParam != null && eurParam.getValorComoDecimal() != null) {
                BigDecimal rate = BigDecimal.valueOf(eurParam.getValorComoDecimal());
                // If rate is > 1, it means CRC per EUR (e.g., 570.00 CRC = 1 EUR)
                // To convert CRC to EUR: divide by rate
                if (rate.compareTo(BigDecimal.ONE) > 0) {
                    return BigDecimal.ONE.divide(rate, 6, ROUNDING_MODE);
                }
                return rate;
            }
            // Default: 1 EUR = 570 CRC → to convert CRC to EUR divide by 570
            return BigDecimal.ONE.divide(BigDecimal.valueOf(570), 6, ROUNDING_MODE);
        }
        
        // Unknown currency, no conversion
        return BigDecimal.ONE;
    }
    
    /**
     * Convert amount from base currency (CRC) to target currency
     * 
     * @param amountCRC Amount in colones
     * @param exchangeRate Exchange rate (CRC to target)
     * @return Converted amount
     */
    private static BigDecimal convertCurrency(BigDecimal amountCRC, BigDecimal exchangeRate) {
        if (amountCRC == null) {
            return BigDecimal.ZERO;
        }
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            return amountCRC; // No conversion if rate is invalid
        }
        
        return amountCRC.multiply(exchangeRate).setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
    
    /**
     * Format currency amount based on currency type
     */
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
    
    /**
     * Get currency symbol from currency string
     */
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
        
        return "₡"; // Default to colones
    }
    
    /**
     * Calculate change for a payment
     */
    public static BigDecimal calculateChange(BigDecimal totalDue, BigDecimal amountPaid) {
        if (totalDue == null) totalDue = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        
        BigDecimal change = amountPaid.subtract(totalDue);
        return change.max(BigDecimal.ZERO).setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
    
    /**
     * Calculate amount due (remaining to pay)
     */
    public static BigDecimal calculateAmountDue(BigDecimal totalDue, BigDecimal amountPaid) {
        if (totalDue == null) totalDue = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        
        BigDecimal due = totalDue.subtract(amountPaid);
        return due.max(BigDecimal.ZERO).setScale(DECIMAL_PLACES, ROUNDING_MODE);
    }
}
