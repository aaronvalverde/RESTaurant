package cr.ac.una.restuna.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsonParser {
    
    
    public static String extraerValorString(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    
    public static String extraerValorNumerico(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*([^,\\}]+)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            String valor = matcher.group(1).trim();
            
            try {
                Double.parseDouble(valor);
                return valor;
            } catch (NumberFormatException e) {
                
            }
        }
        
        return null;
    }
    
    
    public static Boolean extraerValorBooleano(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*(true|false)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        
        return null;
    }
    
    
    public static String extraerValor(String json, String campo) {
        
        String valor = extraerValorString(json, campo);
        if (valor != null) {
            return valor;
        }
        
        
        return extraerValorNumerico(json, campo);
    }
    
    
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
    
    
    public static String extraerArray(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*(\\[.*?\\])";
        Pattern pattern = Pattern.compile(patron, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        
        int startIndex = json.indexOf("\"" + campo + "\"");
        if (startIndex == -1) {
            return null;
        }
        
        
        int arrayStart = json.indexOf('[', startIndex);
        if (arrayStart == -1) {
            return null;
        }
        
        
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
