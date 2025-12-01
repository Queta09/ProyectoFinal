import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class VentanaPrincipal extends JFrame {

    private JComboBox<String> selectorModo;
    private JTextArea areaResultados;
    private JTextField campoEntrada;
    private JButton botonConstruir;
    private JButton botonProbar;

    private final Map<String, Object> modelosConstruidos;

    public VentanaPrincipal() {
        super("Proyecto POO: Autómatas y Gramáticas");
        modelosConstruidos = new HashMap<>();

        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inicializarComponentes();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarComponentes() {
        // --- Panel Norte: Selector y Construcción ---
        JPanel panelNorte = new JPanel(new FlowLayout());

        selectorModo = new JComboBox<>(new String[]{
                "Selecciona un Modo",
                "1. ModoAFD",
                "2. ModoTuring",
                "3. ModoAutomataDePila",
                "4. GramaticaRegular",
                "5. GramaticaLibreDeContexto"
        });
        panelNorte.add(selectorModo);

        botonConstruir = new JButton("Construir Modelo");
        botonConstruir.addActionListener(this::accionConstruir);
        panelNorte.add(botonConstruir);

        add(panelNorte, BorderLayout.NORTH);

        // --- Panel Central: Resultados ---
        areaResultados = new JTextArea("Selecciona un modo y presiona 'Construir Modelo' para comenzar la definición secuencial.");
        areaResultados.setEditable(false);
        areaResultados.setPreferredSize(new Dimension(780, 400));
        JScrollPane scrollPane = new JScrollPane(areaResultados);
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel Sur: Entrada y Prueba ---
        JPanel panelSur = new JPanel(new BorderLayout());

        campoEntrada = new JTextField();
        campoEntrada.setToolTipText("Introduce la cadena a probar aquí.");
        panelSur.add(campoEntrada, BorderLayout.CENTER);

        botonProbar = new JButton("Probar Cadena");
        botonProbar.setEnabled(false);
        botonProbar.addActionListener(this::accionProbar);
        panelSur.add(botonProbar, BorderLayout.EAST);

        add(panelSur, BorderLayout.SOUTH);
    }

    private void accionConstruir(ActionEvent event) {
        String modoSeleccionado = (String) selectorModo.getSelectedItem();
        if (modoSeleccionado == null || modoSeleccionado.equals("Selecciona un Modo")) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un modo válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        areaResultados.setText("Iniciando la construcción secuencial del modelo " + modoSeleccionado + "...\n");
        String nombreClase = modoSeleccionado.split("\\. ")[1];

        StringBuilder entradaSimulada = new StringBuilder();

        // --- 1. CAPTURA SECUENCIAL DE DATOS ---
        try {
            // Paso 1: Alfabeto/Terminales
            String input = JOptionPane.showInputDialog(this,
                    nombreClase + ": Introduce los símbolos del ALFABETO/TERMINALES (separados por espacio. Ej: a b).", "Paso 1/N", JOptionPane.PLAIN_MESSAGE);
            if (input == null) throw new NoSuchElementException("Construcción cancelada");
            entradaSimulada.append(input).append('\n');

            // --- Lógica especial para ModoAutomataDePila (Alfabeto de Pila) ---
            if (nombreClase.equals("ModoAutomataDePila")) {
                String inputPila = JOptionPane.showInputDialog(this,
                        "ModoAutomataDePila: Introduce el ALFABETO DE PILA (separados por espacio. Ej: Z A).", "Paso 2/N", JOptionPane.PLAIN_MESSAGE);
                if (inputPila == null) throw new NoSuchElementException("Construcción cancelada");
                entradaSimulada.append(inputPila).append('\n');
            }
            // --- Fin de Lógica especial para AP ---

            // Paso 2 (o 3): Estados/Variables
            input = JOptionPane.showInputDialog(this,
                    nombreClase + ": Introduce los ESTADOS/VARIABLES (separados por espacio. Ej: q0 q1).", "Paso " + (nombreClase.equals("ModoAutomataDePila") ? "3/N" : "2/N"), JOptionPane.PLAIN_MESSAGE);
            if (input == null) throw new NoSuchElementException("Construcción cancelada");
            entradaSimulada.append(input).append('\n');

            // Paso 3 (o 4): Estado/Símbolo Inicial
            input = JOptionPane.showInputDialog(this,
                    nombreClase + ": Introduce el ESTADO/SÍMBOLO INICIAL (solo uno).", "Paso " + (nombreClase.equals("ModoAutomataDePila") ? "4/N" : "3/N"), JOptionPane.PLAIN_MESSAGE);
            if (input == null) throw new NoSuchElementException("Construcción cancelada");
            entradaSimulada.append(input).append('\n');

            // Paso 4 (o 5): Estados/Variables Finales
            input = JOptionPane.showInputDialog(this,
                    nombreClase + ": Introduce los ESTADOS/VARIABLES FINALES (separados por espacio. Ej: qf).", "Paso " + (nombreClase.equals("ModoAutomataDePila") ? "5/N" : "4/N"), JOptionPane.PLAIN_MESSAGE);
            if (input == null) throw new NoSuchElementException("Construcción cancelada");
            entradaSimulada.append(input).append('\n');

            // Paso 5 (o 6): Transiciones/Producciones (Bucle)
            String ejemploTransiciones = "";
            if (nombreClase.equals("ModoAFD")) {
                ejemploTransiciones = "Ej: q0 a q1\\q1 b q0";
            } else if (nombreClase.equals("ModoTuring")) {
                ejemploTransiciones = "Ej: q0 a q1 X R\\q1 _ qf _ L";
            } else if (nombreClase.equals("ModoAutomataDePila")) {
                ejemploTransiciones = "Ej: q0 a Z q1 A Z\\q1 b A qf _";
            } else if (nombreClase.contains("Gramatica")) {
                ejemploTransiciones = "Ej: S -> aS\\S -> b";
            }

            String transiciones = JOptionPane.showInputDialog(this,
                    nombreClase + ": Introduce las TRANSICIONES/PRODUCCIONES. Usa la barra invertida '\\' para separar cada una.\n"
                            + ejemploTransiciones, "Paso " + (nombreClase.equals("ModoAutomataDePila") ? "6/N" : "5/N") + ": Transiciones", JOptionPane.PLAIN_MESSAGE);
            if (transiciones == null) throw new NoSuchElementException("Construcción cancelada");

            // Reemplazar el separador de la GUI por un salto de línea
            entradaSimulada.append(transiciones.replace("\\", "\n")).append('\n');

            // Línea final para terminar la entrada
            entradaSimulada.append("fin").append('\n');

        } catch (NoSuchElementException e) {
            areaResultados.setText("Construcción cancelada por el usuario.");
            botonProbar.setEnabled(false);
            return;
        }

        // --- 2. SIMULACIÓN DE LA CONSOLA ---
        InputStream inputStreamOriginal = System.in;
        try {
            InputStream inputStreamNuevo = new ByteArrayInputStream(entradaSimulada.toString().getBytes());
            System.setIn(inputStreamNuevo);

            Object modelo = null;

            switch (nombreClase) {
                case "ModoAFD":
                    modelo = new ModoAFD();
                    ((ModoAFD) modelo).construirDesdeConsola();
                    break;
                case "ModoTuring":
                    modelo = new ModoTuring();
                    ((ModoTuring) modelo).construirDesdeConsola();
                    break;
                case "ModoAutomataDePila":
                    modelo = new ModoAutomataDePila();
                    ((ModoAutomataDePila) modelo).construirDesdeConsola();
                    break;
                case "GramaticaRegular":
                    modelo = new GramaticaRegular();
                    ((GramaticaRegular) modelo).construirDesdeConsola();
                    break;
                case "GramaticaLibreDeContexto":
                    modelo = new GramaticaLibreDeContexto();
                    ((GramaticaLibreDeContexto) modelo).construirDesdeConsola();
                    break;
            }

            modelosConstruidos.put(modoSeleccionado, modelo);
            areaResultados.append("\n✅ Modelo '" + modoSeleccionado + "' construido con éxito. Listo para probar cadenas.");
            botonProbar.setEnabled(true);

        } catch (Exception e) {
            areaResultados.append("\n❌ ERROR durante la construcción del modelo: " + e.getMessage());
            e.printStackTrace();
            modelosConstruidos.remove(modoSeleccionado);
            botonProbar.setEnabled(false);
        } finally {
            System.setIn(inputStreamOriginal);
        }
    }

    private void accionProbar(ActionEvent event) {
        String modoSeleccionado = (String) selectorModo.getSelectedItem();
        Object modelo = modelosConstruidos.get(modoSeleccionado);
        String cadena = campoEntrada.getText().trim();

        if (modelo == null) {
            areaResultados.append("\nError: Primero debes construir el modelo.");
            return;
        }
        if (cadena.isEmpty()) {
            areaResultados.append("\nError: Introduce una cadena para probar.");
            return;
        }

        boolean aceptada = false;
        long startTime = System.currentTimeMillis();

        try {
            areaResultados.setText("Probando cadena: '" + cadena + "'...\n");

            if (modelo instanceof ModoAFD) {
                aceptada = ((ModoAFD) modelo).acepta(cadena);
            } else if (modelo instanceof ModoTuring) {
                aceptada = ((ModoTuring) modelo).ejecutar(cadena);
            } else if (modelo instanceof ModoAutomataDePila) {
                aceptada = ((ModoAutomataDePila) modelo).ejecutar(cadena);
            } else if (modelo instanceof GramaticaRegular) {
                aceptada = ((GramaticaRegular) modelo).acepta(cadena);
            } else if (modelo instanceof GramaticaLibreDeContexto) {
                aceptada = ((GramaticaLibreDeContexto) modelo).acepta(cadena);
            }

            long endTime = System.currentTimeMillis();
            String tiempo = String.format("%.3f segundos", (endTime - startTime) / 1000.0);

            if (aceptada) {
                areaResultados.append("\n✅ RESULTADO: Cadena ACEPTADA/GENERADA.");
            } else {
                areaResultados.append("\n❌ RESULTADO: Cadena NO aceptada/generada.");
            }
            areaResultados.append("\nTiempo de ejecución: " + tiempo);

        } catch (Exception e) {
            areaResultados.append("\n❌ ERROR CRÍTICO durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}