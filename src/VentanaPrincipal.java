import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        do {
            System.out.println("\n === Menú de Modos ===");
            System.out.println("1. ModoAFD");
            System.out.println("2. ModoTuring");
            System.out.println("3. ModoAutomataDePila");
            System.out.println("4. GramaticaRegular");
            System.out.println("5. GramaticaLibreDeContexto");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            try {
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); // Limpiar buffer
                } else {
                    System.out.println("⛔ Error de entrada: Por favor, introduce un número.");
                    scanner.nextLine();
                    opcion = -1;
                    continue;
                }
            } catch (Exception e) {
                System.out.println("⛔ Error inesperado al leer la opción: " + e.getMessage());
                scanner.nextLine();
                opcion = -1;
                continue;
            }

            try {
                switch (opcion) {
                    case 1:
                        ModoAFD modoAFD = new ModoAFD();
                        modoAFD.construirDesdeConsola();
                        while (true) {
                            System.out.print("Introduce una palabra (o 'salir'): ");
                            String palabra = scanner.nextLine();
                            if (palabra.equalsIgnoreCase("salir")) break;
                            if (modoAFD.acepta(palabra)) {
                                System.out.println("✅ La palabra es aceptada.");
                            } else {
                                System.out.println("❌ La palabra NO es aceptada.");
                            }
                        }
                        break;
                    case 2:
                        ModoTuring mt = new ModoTuring();
                        mt.construirDesdeConsola();
                        while (true) {
                            System.out.print("Introduce una cadena (o 'salir'): ");
                            String entrada = scanner.nextLine();
                            if (entrada.equalsIgnoreCase("salir")) break;

                            boolean aceptada = mt.ejecutar(entrada);
                            if (aceptada) {
                                System.out.println("✅ La máquina terminó en estado de aceptación.");
                            } else {
                                System.out.println("❌ La máquina no pudo continuar (o bucle infinito).");
                            }
                        }
                        break;
                    case 3:
                        ModoAutomataDePila ap = new ModoAutomataDePila();
                        ap.construirDesdeConsola();
                        while (true) {
                            System.out.print("Introduce una cadena (o 'salir'): ");
                            String entrada = scanner.nextLine();
                            if (entrada.equalsIgnoreCase("salir")) break;

                            boolean aceptada = ap.ejecutar(entrada);
                            if (aceptada) {
                                System.out.println("✅ La cadena fue aceptada.");
                            } else {
                                System.out.println("❌ La cadena fue rechazada.");
                            }
                        }
                        break;
                    case 4:
                        GramaticaRegular gr = new GramaticaRegular();
                        gr.construirDesdeConsola();
                        while (true) {
                            System.out.print("Introduce una cadena (o 'salir'): ");
                            String entrada = scanner.nextLine();
                            if (entrada.equalsIgnoreCase("salir")) break;

                            boolean aceptada = gr.acepta(entrada);
                            if (aceptada) {
                                System.out.println("✅ La cadena fue generada por la gramática.");
                            } else {
                                System.out.println("❌ La cadena NO fue generada por la gramática.");
                            }
                        }
                        break;
                    case 5:
                        GramaticaLibreDeContexto glc = new GramaticaLibreDeContexto();
                        glc.construirDesdeConsola();
                        while (true) {
                            System.out.print("Introduce una cadena (o 'salir'): ");
                            String entrada = scanner.nextLine();
                            if (entrada.equalsIgnoreCase("salir")) break;

                            boolean aceptada = glc.acepta(entrada);
                            if (aceptada) {
                                System.out.println("✅ La cadena fue generada por la gramática.");
                            } else {
                                System.out.println("❌ La cadena NO fue generada por la gramática.");
                            }
                        }
                        break;
                    case 0:
                        System.out.println("👋 Saliendo del programa...");
                        break;
                    default:
                        System.out.println("🚫 Opción inválida. Intenta de nuevo.");
                }
            } catch (Exception e) {
                System.err.println("💥 Un error crítico ocurrió durante la ejecución del modo seleccionado: " + e.getMessage());
            }
        } while (opcion != 0);

        scanner.close();
    }
}