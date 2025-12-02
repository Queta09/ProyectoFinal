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

    private boolean aceptaDesde(String variable, String cadena) {
        if (cadena.length() > 2000) return false;

        List<String> reglas = producciones.get(variable);
        if (reglas == null) return false;

        for (String regla : reglas) {

            if (regla.equals("_") && cadena.isEmpty()) {
                return true;
            }

            if (regla.length() == 1 && terminales.contains(regla.charAt(0))) {
                if (cadena.equals(regla)) {
                    return true;
                }
            }

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