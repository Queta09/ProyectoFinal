import java.util.*;
import java.util.NoSuchElementException;

public class ModoAFD {
    private final Set<String> estados;
    private final Set<Character> alfabeto;
    private String estadoInicial;
    private final Set<String> estadosFinales;
    private final Map<String, Map<Character, String>> transiciones;

    public ModoAFD() {
        estados = new HashSet<>();
        alfabeto = new HashSet<>();
        estadosFinales = new HashSet<>();
        transiciones = new HashMap<>();
    }

    public void construirDesdeConsola() {
        Scanner scanner = new Scanner(System.in);

        try {
            String alphaLine = scanner.nextLine();
            for (String s : alphaLine.split(" ")) {
                if (!s.isEmpty()) alfabeto.add(s.charAt(0));
            }

            String statesLine = scanner.nextLine();
            if (!statesLine.trim().isEmpty()) {
                estados.addAll(Arrays.asList(statesLine.split(" ")));
            }

            estadoInicial = scanner.nextLine().trim();
            if (estadoInicial.isEmpty() || !estados.contains(estadoInicial)) {
                throw new IllegalArgumentException("Estado inicial inválido o no definido.");
            }

            String finalsLine = scanner.nextLine();
            if (!finalsLine.trim().isEmpty()) {
                estadosFinales.addAll(Arrays.asList(finalsLine.split(" ")));
            }

            while (true) {
                String linea = scanner.nextLine();
                if (linea.equalsIgnoreCase("fin")) break;

                String[] partes = linea.split(" ");

                if (partes.length != 3) {
                    continue;
                }

                String origen = partes[0].trim();
                String simboloStr = partes[1].trim();
                String destino = partes[2].trim();

                if (!estados.contains(origen) || !estados.contains(destino)) {
                    throw new IllegalArgumentException("Estado de origen o destino no definido: " + origen + " o " + destino);
                }
                if (simboloStr.length() != 1 || !alfabeto.contains(simboloStr.charAt(0))) {
                    throw new IllegalArgumentException("Símbolo de transición inválido o no definido en el alfabeto: " + simboloStr);
                }

                char simbolo = simboloStr.charAt(0);

                transiciones.putIfAbsent(origen, new HashMap<>());
                transiciones.get(origen).put(simbolo, destino);
            }
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Faltan líneas de entrada para completar la definición del AFD. Asegúrate de incluir la línea 'fin'.", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado durante la lectura del AFD: " + e.getMessage(), e);
        }
    }

    public boolean acepta(String palabra) {
        if (estadoInicial == null) {
            return false;
        }

        String estadoActual = estadoInicial;
        try {
            for (char simbolo : palabra.toCharArray()) {
                if (!alfabeto.contains(simbolo)) {
                    return false;
                }
                Map<Character, String> trans = transiciones.get(estadoActual);
                if (trans == null || !trans.containsKey(simbolo)) return false;
                estadoActual = trans.get(simbolo);
            }
            return estadosFinales.contains(estadoActual);
        } catch (Exception e) {
            return false;
        }
    }
}