import java.util.*;
import java.util.NoSuchElementException;

public class GramaticaLibreDeContexto {
    private final Set<String> variables;
    private final Set<Character> terminales;
    private String simboloInicial;
    private final Map<String, List<String>> producciones;

    public GramaticaLibreDeContexto() {
        variables = new HashSet<>();
        terminales = new HashSet<>();
        producciones = new HashMap<>();
    }

    public void construirDesdeConsola() {
        Scanner scanner = new Scanner(System.in);

        try {
            String termsLine = scanner.nextLine().trim();
            if (!termsLine.isEmpty()) {
                for (String s : termsLine.split(" ")) {
                    if (!s.isEmpty()) {
                        terminales.add(s.charAt(0));
                    }
                }
            }

            String varsLine = scanner.nextLine().trim();
            if (!varsLine.isEmpty()) {
                variables.addAll(Arrays.asList(varsLine.split(" ")));
            }

            simboloInicial = scanner.nextLine().trim();
            if (simboloInicial.isEmpty() || !variables.contains(simboloInicial)) {
                throw new IllegalArgumentException("El símbolo inicial es inválido o no está en el conjunto de variables.");
            }

            scanner.nextLine();

            while (true) {
                String linea = scanner.nextLine();
                if (linea.equalsIgnoreCase("fin")) break;

                String[] partes = linea.split("->");
                if (partes.length != 2) {
                    continue;
                }

                String izquierda = partes[0].trim();
                String derecha = partes[1].trim();

                if (izquierda.isEmpty()) {
                    continue;
                }

                String[] derechas = derecha.split("\\|");

                if (!variables.contains(izquierda)) {
                    continue;
                }

                producciones.putIfAbsent(izquierda, new ArrayList<>());
                for (String d : derechas) {
                    producciones.get(izquierda).add(d.trim());
                }
            }
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Faltan líneas de entrada para completar la definición de la GLC.", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado durante la lectura de la GLC: " + e.getMessage(), e);
        }
    }

    public boolean acepta(String cadena) {
        if (simboloInicial == null) {
            return false;
        }
        return deriva(simboloInicial, cadena, 0);
    }

    private boolean deriva(String actual, String cadena, int profundidad) {
        if (profundidad > 2000) return false; // Límite de seguridad

        if (actual.equals("_")) return cadena.isEmpty();

        if (actual.equals(cadena)) return true;

        for (int i = 0; i < actual.length(); i++) {
            char c = actual.charAt(i);
            String simbolo = String.valueOf(c);

            if (variables.contains(simbolo)) {
                List<String> reglas = producciones.get(simbolo);
                if (reglas == null) continue;

                for (String regla : reglas) {

                    String reglaLimpia = regla.equals("_") ? "" : regla;
                    String nueva = actual.substring(0, i) + reglaLimpia + actual.substring(i + 1);

                    if (deriva(nueva, cadena, profundidad + 1)) {
                        return true;
                    }
                }

                return false;
            }

            if (terminales.contains(c) && i < cadena.length() && c != cadena.charAt(i)) {
                return false;
            }
        }

        return false;
    }
}