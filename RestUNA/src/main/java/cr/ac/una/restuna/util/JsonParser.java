package cr.ac.una.restuna.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidad para parsear JSON manualmente sin dependencias externas.
 * Útil cuando no se pueden usar librerías como Jackson debido a problemas de módulos.
 */
public class JsonParser {
    
    /**
     * Extrae el valor de un campo String de un objeto JSON
     * @param json El objeto JSON como String
     * @param campo El nombre del campo a extraer
     * @return El valor del campo o null si no se encuentra
     */
    public static String extraerValorString(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extrae el valor de un campo numérico de un objeto JSON
     * @param json El objeto JSON como String
     * @param campo El nombre del campo a extraer
     * @return El valor del campo como String o null si no se encuentra
     */
    public static String extraerValorNumerico(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*([^,\\}]+)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            String valor = matcher.group(1).trim();
            // Verificar que sea un número válido
            try {
                Double.parseDouble(valor);
                return valor;
            } catch (NumberFormatException e) {
                // No es numérico
            }
        }
        
        return null;
    }
    
    /**
     * Extrae el valor de un campo booleano de un objeto JSON
     * @param json El objeto JSON como String
     * @param campo El nombre del campo a extraer
     * @return El valor del campo como Boolean o null si no se encuentra
     */
    public static Boolean extraerValorBooleano(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*(true|false)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        
        return null;
    }
    
    /**
     * Extrae el valor de un campo que puede ser String o numérico
     * @param json El objeto JSON como String
     * @param campo El nombre del campo a extraer
     * @return El valor del campo o null si no se encuentra
     */
    public static String extraerValor(String json, String campo) {
        // Primero intentar como String
        String valor = extraerValorString(json, campo);
        if (valor != null) {
            return valor;
        }
        
        // Si no, intentar como numérico
        return extraerValorNumerico(json, campo);
    }
    
    /**
     * Extrae el valor de un campo Long de un objeto JSON
     * @param json El objeto JSON como String
     * @param campo El nombre del campo a extraer
     * @return El valor del campo como Long o null si no se encuentra o no es válido
     */
    public static Long extraerValorLong(String json, String campo) {
        String valor = extraerValorNumerico(json, campo);
        if (valor != null) {
            try {
                return Long.parseLong(valor);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Extrae el valor de un campo Integer de un objeto JSON
     * @param json El objeto JSON como String
     * @param campo El nombre del campo a extraer
     * @return El valor del campo como Integer o null si no se encuentra o no es válido
     */
    public static Integer extraerValorInteger(String json, String campo) {
        String valor = extraerValorNumerico(json, campo);
        if (valor != null) {
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
