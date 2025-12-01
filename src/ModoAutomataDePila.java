import java.util.*;
import java.util.NoSuchElementException;

public class ModoAutomataDePila {
    private final Set<String> estados;
    private final Set<Character> alfabetoEntrada;
    private final Set<Character> alfabetoPila;
    private String estadoInicial;
    private final Set<String> estadosFinales;
    private final Map<TransicionClave, TransicionValor> transiciones;

    // Clase que representa la clave de una transición
    private static class TransicionClave {
        final String estado;
        final char simboloEntrada;
        final char cimaPila;

        TransicionClave(String estado, char simboloEntrada, char cimaPila) {
            this.estado = estado;
            this.simboloEntrada = simboloEntrada;
            this.cimaPila = cimaPila;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransicionClave)) return false;
            TransicionClave t = (TransicionClave) o;
            return simboloEntrada == t.simboloEntrada && cimaPila == t.cimaPila && estado.equals(t.estado);
        }

        @Override
        public int hashCode() {
            return Objects.hash(estado, simboloEntrada, cimaPila);
        }

        @Override
        public String toString() {
            return "(" + estado + ", " + simboloEntrada + ", " + cimaPila + ")";
        }
    }

    // Clase que representa el resultado de una transición
    private static class TransicionValor {
        final String nuevoEstado;
        final List<Character> pilaPush;

        TransicionValor(String nuevoEstado, List<Character> pilaPush) {
            this.nuevoEstado = nuevoEstado;
            this.pilaPush = pilaPush;
        }
    }

    public ModoAutomataDePila() {
        estados = new HashSet<>();
        alfabetoEntrada = new HashSet<>();
        alfabetoPila = new HashSet<>();
        estadosFinales = new HashSet<>();
        transiciones = new HashMap<>();
    }

    public void construirDesdeConsola() {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Alfabeto de Entrada
            String alphaInLine = scanner.nextLine();
            for (String s : alphaInLine.split(" ")) {
                if (!s.isEmpty()) alfabetoEntrada.add(s.charAt(0));
            }
            alfabetoEntrada.add('_');

            // 2. Alfabeto de Pila (Especial para AP)
            String alphaStackLine = scanner.nextLine();
            for (String s : alphaStackLine.split(" ")) {
                if (!s.isEmpty()) alfabetoPila.add(s.charAt(0));
            }
            alfabetoPila.add('_');

            // 3. Estados
            String statesLine = scanner.nextLine();
            if (!statesLine.trim().isEmpty()) {
                estados.addAll(Arrays.asList(statesLine.split(" ")));
            }

            // 4. Estado inicial
            String estadoInicialInput = scanner.nextLine().trim();
            if (estadoInicialInput.isEmpty() || !estados.contains(estadoInicialInput)) {
                throw new IllegalArgumentException("Estado inicial inválido o no definido.");
            }
            estadoInicial = estadoInicialInput;

            // 5. Estados finales
            String finalsLine = scanner.nextLine();
            if (!finalsLine.trim().isEmpty()) {
                estadosFinales.addAll(Arrays.asList(finalsLine.split(" ")));
            }

            // 6. Transiciones
            while (true) {
                String linea = scanner.nextLine();
                if (linea.equalsIgnoreCase("fin")) break;

                String[] partes = linea.split(" ");
                if (partes.length < 5) {
                    continue;
                }

                String estado = partes[0];
                char simboloEntrada = partes[1].charAt(0);
                char cimaPila = partes[2].charAt(0);
                String nuevoEstado = partes[3];

                if (!estados.contains(estado) || !estados.contains(nuevoEstado)) {
                    throw new IllegalArgumentException("Estado de origen o destino no definido: " + estado + " -> " + nuevoEstado);
                }

                List<Character> pilaPush = new ArrayList<>();
                for (int i = 4; i < partes.length; i++) {
                    char c = partes[i].charAt(0);
                    if (c != '_') pilaPush.add(c);
                }

                TransicionClave clave = new TransicionClave(estado, simboloEntrada, cimaPila);
                TransicionValor valor = new TransicionValor(nuevoEstado, pilaPush);
                transiciones.put(clave, valor);
            }
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Faltan líneas de entrada para completar la definición del AP.", e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error inesperado durante la lectura del AP: " + e.getMessage(), e);
        }
    }

    public boolean ejecutar(String entrada) {
        if (estadoInicial == null) {
            return false;
        }

        Stack<Character> pila = new Stack<>();
        pila.push('Z');
        String estadoActual = estadoInicial;
        int indice = 0;

        try {
            while (true) {
                // Determinar el símbolo de entrada
                char simbolo = (indice < entrada.length()) ? entrada.charAt(indice) : '_';

                // Determinar la cima de la pila (Peek, no Pop todavía)
                char cima = pila.isEmpty() ? '_' : pila.peek();

                // Si la cadena terminó y ya no hay transiciones épsilon (simbolo == '_')
                if (indice >= entrada.length() && simbolo == '_') {
                    // Si el estado actual es final, hemos terminado.
                    if (estadosFinales.contains(estadoActual)) break;
                    // Si ya no quedan más transiciones, salimos del bucle.
                    if (!transiciones.containsKey(new TransicionClave(estadoActual, '_', cima))) break;
                }


                TransicionClave clave = new TransicionClave(estadoActual, simbolo, cima);
                TransicionValor valor = transiciones.get(clave);

                if (valor == null) {
                    // System.out.println("No hay transición para " + clave);
                    return false;
                }

                // Aplicar Pop
                if (cima != '_') {
                    pila.pop();
                }

                estadoActual = valor.nuevoEstado;

                // Aplicar Push
                for (int i = valor.pilaPush.size() - 1; i >= 0; i--) {
                    pila.push(valor.pilaPush.get(i));
                }

                // Avanzar la entrada solo si NO fue una transición épsilon de entrada
                if (simbolo != '_') {
                    indice++;
                }

                // System.out.println("Estado: " + estadoActual + ", Pila: " + pila + ", Siguiente Símbolo: " + (indice < entrada.length() ? entrada.charAt(indice) : "EOF"));
            }
        } catch (EmptyStackException e) {
            // System.err.println("Error en la ejecución del AP: Se intentó sacar un elemento de una pila vacía (EmptyStackException).");
            return false;
        } catch (Exception e) {
            // System.err.println("Error inesperado durante la ejecución del AP: " + e.getMessage());
            return false;
        }

        return estadosFinales.contains(estadoActual);
    }
}