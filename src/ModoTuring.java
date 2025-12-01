import java.util.*;
import java.util.NoSuchElementException;

public class ModoTuring {
    private final Set<String> estados;
    private final Set<Character> alfabeto;
    private String estadoInicial;
    private String estadoFinal;
    private final Map<String, Map<Character, Transicion>> transiciones;

    // Clase interna que representa una transición de la máquina de Turing
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
            // 1. Alfabeto
            String alphaLine = scanner.nextLine();
            for (String s : alphaLine.split(" ")) {
                if (!s.isEmpty()) alfabeto.add(s.charAt(0));
            }
            if (!alfabeto.contains('_')) alfabeto.add('_');

            // 2. Estados
            String statesLine = scanner.nextLine();
            if (!statesLine.trim().isEmpty()) {
                estados.addAll(Arrays.asList(statesLine.split(" ")));
            }

            // 3. Estado inicial
            estadoInicial = scanner.nextLine().trim();
            if (estadoInicial.isEmpty() || !estados.contains(estadoInicial)) {
                throw new IllegalArgumentException("Estado inicial inválido o no definido.");
            }

            // 4. Estado final
            estadoFinal = scanner.nextLine().trim();
            if (estadoFinal.isEmpty() || !estados.contains(estadoFinal)) {
                throw new IllegalArgumentException("Estado final inválido o no definido.");
            }

            // 5. Transiciones
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

    // Ejecuta la máquina de Turing sobre una cadena de entrada
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

                // 2. Leer símbolo actual
                char simbolo;
                if (cabeza >= 0 && cabeza < cinta.size()) {
                    simbolo = cinta.get(cabeza);
                } else {
                    // Si la cabeza se mueve fuera del rango derecho, se lee '_'.
                    // Si la cabeza se mueve fuera del rango izquierdo (< 0), se lee '_'.
                    simbolo = '_';
                }

                // 3. Buscar Transición
                Map<Character, Transicion> mapa = transiciones.get(estadoActual);
                if (mapa == null || !mapa.containsKey(simbolo)) {
                    return false; // Se detiene sin una regla
                }

                Transicion t = mapa.get(simbolo);

                // 4. Escribir: Solo se escribe si la cabeza está en el área actual de la cinta.
                if (cabeza >= 0 && cabeza < cinta.size()) {
                    cinta.set(cabeza, t.escribir);
                }

                estadoActual = t.siguienteEstado;

                // 5. Mover Cabeza
                if (t.direccion == 'R') {
                    cabeza++;
                    // Expansión a la derecha si es necesario
                    if (cabeza >= cinta.size()) cinta.add('_');
                } else if (t.direccion == 'L') {
                    cabeza--;

                    // --- CORRECCIÓN CRUCIAL PARA EL LÍMITE IZQUIERDO ---
                    // Si la cabeza se mueve a la izquierda más allá del inicio de la lista (cabeza < 0),
                    // en una implementación real de MT, se debería insertar un blanco al inicio.
                    // Aquí, si la cabeza es menor que 0, insertamos un blanco y ajustamos la cabeza a 0.
                    if (cabeza < 0) {
                        cinta.add(0, '_'); // Inserta un blanco al inicio
                        cabeza = 0; // La cabeza se queda en el nuevo índice 0.
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true; // Aceptación
    }
}