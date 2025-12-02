import java.util.*;
import java.util.NoSuchElementException;

public class ModoTuring {
    private final Set<String> estados;
    private final Set<Character> alfabeto;
    private String estadoInicial;
    private String estadoFinal;
    private final Map<String, Map<Character, Transicion>> transiciones;

    private static class Transicion {
        final String siguienteEstado;
        final char escribir;
        final char direccion;

        Transicion(String siguienteEstado, char escribir, char direccion) {
            this.siguienteEstado = siguienteEstado;
            this.escribir = escribir;
            this.direccion = direccion;
        }
    }

    public ModoTuring() {
        estados = new HashSet<>();
        alfabeto = new HashSet<>();
        transiciones = new HashMap<>();
    }

    public void construirDesdeConsola() {
        Scanner scanner = new Scanner(System.in);

        try {
            String alphaLine = scanner.nextLine();
            for (String s : alphaLine.split(" ")) {
                if (!s.isEmpty()) alfabeto.add(s.charAt(0));
            }
            if (!alfabeto.contains('_')) alfabeto.add('_');

            String statesLine = scanner.nextLine();
            if (!statesLine.trim().isEmpty()) {
                estados.addAll(Arrays.asList(statesLine.split(" ")));
            }

            estadoInicial = scanner.nextLine().trim();
            if (estadoInicial.isEmpty() || !estados.contains(estadoInicial)) {
                throw new IllegalArgumentException("Estado inicial inválido o no definido.");
            }

            estadoFinal = scanner.nextLine().trim();
            if (estadoFinal.isEmpty() || !estados.contains(estadoFinal)) {
                throw new IllegalArgumentException("Estado final inválido o no definido.");
            }

            while (true) {
                String linea = scanner.nextLine();
                if (linea.equalsIgnoreCase("fin")) break;

                String[] partes = linea.split(" ");
                if (partes.length != 5) {
                    continue;
                }

                String estado = partes[0];
                char leido = partes[1].charAt(0);
                String nuevoEstado = partes[2];
                char escribir = partes[3].charAt(0);
                char direccion = partes[4].toUpperCase().charAt(0);

                if (!estados.contains(estado) || !estados.contains(nuevoEstado)) {
                    throw new IllegalArgumentException("Estado de origen o destino no definido.");
                }
                if (!alfabeto.contains(leido) || !alfabeto.contains(escribir)) {
                    throw new IllegalArgumentException("Símbolo leído o escrito no definido en el alfabeto.");
                }
                if (direccion != 'L' && direccion != 'R') {
                    throw new IllegalArgumentException("Dirección inválida. Debe ser 'L' o 'R'.");
                }

                transiciones.putIfAbsent(estado, new HashMap<>());
                transiciones.get(estado).put(leido, new Transicion(nuevoEstado, escribir, direccion));
            }
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Faltan líneas de entrada para completar la definición de la MT.", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado durante la lectura de la MT: " + e.getMessage(), e);
        }
    }

    public boolean ejecutar(String entrada) {
        if (estadoInicial == null || estadoFinal == null) {
            return false;
        }

        List<Character> cinta = new ArrayList<>();
        for (char c : entrada.toCharArray()) cinta.add(c);
        cinta.add('_');

        int cabeza = 0;
        String estadoActual = estadoInicial;
        int maxPasos = 10000;
        int pasos = 0;

        try {
            while (!estadoActual.equals(estadoFinal)) {
                if (pasos++ > maxPasos) {
                    return false;
                }

                char simbolo;
                if (cabeza >= 0 && cabeza < cinta.size()) {
                    simbolo = cinta.get(cabeza);
                } else {
                    simbolo = '_';
                }

                Map<Character, Transicion> mapa = transiciones.get(estadoActual);
                if (mapa == null || !mapa.containsKey(simbolo)) {
                    return false;
                }

                Transicion t = mapa.get(simbolo);

                if (cabeza >= 0 && cabeza < cinta.size()) {
                    cinta.set(cabeza, t.escribir);
                }

                estadoActual = t.siguienteEstado;


                if (t.direccion == 'R') {
                    cabeza++;
                    if (cabeza >= cinta.size()) cinta.add('_');
                } else if (t.direccion == 'L') {
                    cabeza--;

                    if (cabeza < 0) {
                        cinta.add(0, '_');
                        cabeza = 0;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}