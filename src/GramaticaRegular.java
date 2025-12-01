import java.util.*;
import java.util.NoSuchElementException;

public class GramaticaRegular {
    private final Set<String> variables;
    private final Set<Character> terminales;
    private String simboloInicial;
    private final Map<String, List<String>> producciones;

    public GramaticaRegular() {
        variables = new HashSet<>();
        terminales = new HashSet<>();
        producciones = new HashMap<>();
    }

    public void construirDesdeConsola() {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Terminales (Corrección de lectura robusta)
            String termsLine = scanner.nextLine().trim();
            if (!termsLine.isEmpty()) {
                for (String s : termsLine.split(" ")) {
                    if (!s.isEmpty()) {
                        terminales.add(s.charAt(0));
                    }
                }
            }

            // 2. Variables (Corrección de lectura robusta)
            String varsLine = scanner.nextLine().trim();
            if (!varsLine.isEmpty()) {
                variables.addAll(Arrays.asList(varsLine.split(" ")));
            }

            // 3. Símbolo Inicial
            simboloInicial = scanner.nextLine().trim();
            if (simboloInicial.isEmpty() || !variables.contains(simboloInicial)) {
                throw new IllegalArgumentException("El símbolo inicial es inválido o no está en el conjunto de variables.");
            }

            // 4. Variables Finales (Se lee la línea)
            scanner.nextLine();

            // 5. Producciones
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
            throw new RuntimeException("Faltan líneas de entrada para completar la definición de la GR.", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado durante la lectura de la GR: " + e.getMessage(), e);
        }
    }

    public boolean acepta(String cadena) {
        if (simboloInicial == null) {
            return false;
        }
        return aceptaDesde(simboloInicial, cadena);
    }

    // Método de derivación corregido para lógica de A -> a y A -> aB
    private boolean aceptaDesde(String variable, String cadena) {
        if (cadena.length() > 2000) return false;

        List<String> reglas = producciones.get(variable);
        if (reglas == null) return false;

        for (String regla : reglas) {

            // REGLA 1: Cadena Vacía (Epsilon)
            if (regla.equals("_") && cadena.isEmpty()) {
                return true;
            }

            // REGLA 2: Terminal Final (A -> a)
            if (regla.length() == 1 && terminales.contains(regla.charAt(0))) {
                if (cadena.equals(regla)) {
                    return true;
                }
            }

            // REGLA 3: Terminal y Variable (A -> aB)
            if (regla.length() == 2 && terminales.contains(regla.charAt(0)) && variables.contains("" + regla.charAt(1))) {
                char terminal = regla.charAt(0);
                String siguienteVar = "" + regla.charAt(1);

                if (!cadena.isEmpty() && cadena.charAt(0) == terminal) {
                    String resto = cadena.substring(1);

                    if (aceptaDesde(siguienteVar, resto)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}