import java.util.*;
import java.util.NoSuchElementException;

public class ModoAutomataDePila {
    private final Set<String> estados;
    private final Set<Character> alfabetoEntrada;
    private final Set<Character> alfabetoPila;
    private String estadoInicial;
    private final Set<String> estadosFinales;
    private final Map<TransicionClave, TransicionValor> transiciones;

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
            String alphaInLine = scanner.nextLine();
            for (String s : alphaInLine.split(" ")) {
                if (!s.isEmpty()) alfabetoEntrada.add(s.charAt(0));
            }
            alfabetoEntrada.add('_');

            String alphaStackLine = scanner.nextLine();
            for (String s : alphaStackLine.split(" ")) {
                if (!s.isEmpty()) alfabetoPila.add(s.charAt(0));
            }
            alfabetoPila.add('_');

            String statesLine = scanner.nextLine();
            if (!statesLine.trim().isEmpty()) {
                estados.addAll(Arrays.asList(statesLine.split(" ")));
            }

            String estadoInicialInput = scanner.nextLine().trim();
            if (estadoInicialInput.isEmpty() || !estados.contains(estadoInicialInput)) {
                throw new IllegalArgumentException("Estado inicial inválido o no definido.");
            }
            estadoInicial = estadoInicialInput;

            String finalsLine = scanner.nextLine();
            if (!finalsLine.trim().isEmpty()) {
                estadosFinales.addAll(Arrays.asList(finalsLine.split(" ")));
            }

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
                char simbolo = (indice < entrada.length()) ? entrada.charAt(indice) : '_';

                char cima = pila.isEmpty() ? '_' : pila.peek();

                if (indice >= entrada.length() && simbolo == '_') {
                    if (estadosFinales.contains(estadoActual)) break;
                    if (!transiciones.containsKey(new TransicionClave(estadoActual, '_', cima))) break;
                }


                TransicionClave clave = new TransicionClave(estadoActual, simbolo, cima);
                TransicionValor valor = transiciones.get(clave);

                if (valor == null) {
                    return false;
                }

                if (cima != '_') {
                    pila.pop();
                }

                estadoActual = valor.nuevoEstado;

                for (int i = valor.pilaPush.size() - 1; i >= 0; i--) {
                    pila.push(valor.pilaPush.get(i));
                }

                if (simbolo != '_') {
                    indice++;
                }

            }
        } catch (EmptyStackException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

        return estadosFinales.contains(estadoActual);
    }
}