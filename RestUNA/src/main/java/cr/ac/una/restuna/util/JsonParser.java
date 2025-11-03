package cr.ac.una.restuna.util;

import java.util.ArrayList;
import java.util.List;
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
    
    /**
     * Extrae los objetos JSON de primer nivel contenidos en un arreglo JSON.
     * Se utiliza cuando la respuesta es un array de objetos y se necesita procesarlos manualmente.
     * @param jsonArray Cadena con el arreglo JSON
     * @return Lista de objetos JSON como cadenas individuales
     */
    public static List<String> extraerObjetosDelArray(String jsonArray) {
        List<String> objetos = new ArrayList<>();

        if (jsonArray == null || !jsonArray.trim().startsWith("[")) {
            return objetos;
        }

        int nivel = 0;
        int inicioObjeto = -1;

        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);

            if (c == '{') {
                if (nivel == 0) {
                    inicioObjeto = i;
                }
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0 && inicioObjeto != -1) {
                    objetos.add(jsonArray.substring(inicioObjeto, i + 1));
                    inicioObjeto = -1;
                }
            }
        }

        return objetos;
    }
    
    /**
     * Extrae un array anidado de un objeto JSON
     * @param json El objeto JSON como String
     * @param campo El nombre del campo que contiene el array
     * @return El array JSON como String o null si no se encuentra
     */
    public static String extraerArray(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*(\\[.*?\\])";
        Pattern pattern = Pattern.compile(patron, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Si el patrón simple no funciona, buscar con balanceo de corchetes
        int startIndex = json.indexOf("\"" + campo + "\"");
        if (startIndex == -1) {
            return null;
        }
        
        // Buscar el inicio del array después del campo
        int arrayStart = json.indexOf('[', startIndex);
        if (arrayStart == -1) {
            return null;
        }
        
        // Balancear corchetes para encontrar el final del array
        int nivel = 0;
        int arrayEnd = -1;
        
        for (int i = arrayStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') {
                nivel++;
            } else if (c == ']') {
                nivel--;
                if (nivel == 0) {
                    arrayEnd = i;
                    break;
                }
            }
        }
        
        if (arrayEnd != -1) {
            return json.substring(arrayStart, arrayEnd + 1);
        }
        
        return null;
    }
}
