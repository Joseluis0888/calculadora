import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class Calculadora extends JFrame implements ActionListener {

    // Componentes de UI
    private JTextField pantalla;
    private JLabel resultadoLabel;

    // Estado interno de la calculadora
    private double num1 = 0, num2 = 0, resultado = 0;
    private char operador;
    private boolean nuevaOperacion = true; // Flag para saber si debemos limpiar la pantalla al teclear

    // Formateador para evitar notación científica indeseada y limitar decimales
    private DecimalFormat df = new DecimalFormat("#.########");

    // Constantes de diseño (Design Tokens)
    // Extraer estos colores a variables 'final' facilita mantener la paleta y crear temas (ej. Dark/Light mode)
    private final Color COLOR_FONDO = new Color(40, 45, 50);
    private final Color COLOR_PANTALLA = new Color(60, 65, 70);
    private final Color COLOR_BOTON_NUMERO = new Color(75, 80, 85);
    private final Color COLOR_BOTON_OPERADOR = new Color(255, 165, 0);
    private final Color COLOR_BOTON_IGUAL = new Color(0, 150, 200);
    private final Color COLOR_BOTON_BORRAR = new Color(200, 50, 50);
    private final Color COLOR_BOTON_ADVANCED = new Color(150, 50, 200);
    private final Color COLOR_TEXTO = Color.WHITE;

    /**
     * Constructor: Inicializa la interfaz gráfica de usuario.
     */
    public Calculadora() {
        // Configuración base de la ventana
        setTitle("Calculadora Avanzada");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Evita fugas de memoria al cerrar
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_FONDO);

        // --- PANEL SUPERIOR (PANTALLA) ---
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.setBackground(COLOR_FONDO);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pantalla = new JTextField();
        pantalla.setFont(new Font("Segoe UI", Font.BOLD, 30));
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setEditable(false); // Previene que el usuario inyecte texto no numérico desde el teclado
        pantalla.setBackground(COLOR_PANTALLA);
        pantalla.setForeground(COLOR_TEXTO);
        pantalla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSuperior.add(pantalla, BorderLayout.CENTER);

        resultadoLabel = new JLabel(" ");
        resultadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultadoLabel.setHorizontalAlignment(JTextField.RIGHT);
        resultadoLabel.setForeground(new Color(180, 180, 180));
        resultadoLabel.setBackground(COLOR_PANTALLA);
        resultadoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        panelSuperior.add(resultadoLabel, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL INFERIOR (BOTONES) ---
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(6, 5, 8, 8));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Matriz de distribución de botones
        String[] botones = {
                "C", "⌫", "(", ")", "√",
                "x²", "x³", "xⁿ", "1/x", "x!",
                "7", "8", "9", "/", "sin",
                "4", "5", "6", "*", "cos",
                "1", "2", "3", "-", "tan",
                "0", ".", "=", "+", "log"
        };

        // Renderizado dinámico de botones según su tipo
        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setOpaque(true);

            // Asignación de estilos mediante Regex (Expresiones Regulares)
            // TODO: Podríamos extraer esta lógica de colores a una factoría de UI
            if (texto.matches("[0-9]") || texto.equals(".")) {
                boton.setBackground(COLOR_BOTON_NUMERO);
                boton.setForeground(COLOR_TEXTO);
            } else if (texto.equals("=")) {
                boton.setBackground(COLOR_BOTON_IGUAL);
                boton.setForeground(COLOR_TEXTO);
            } else if (texto.equals("C") || texto.equals("⌫")) {
                boton.setBackground(COLOR_BOTON_BORRAR);
                boton.setForeground(COLOR_TEXTO);
            } else if (texto.equals("(") || texto.equals(")")) {
                boton.setBackground(new Color(100, 100, 110));
                boton.setForeground(COLOR_TEXTO);
            } else if (texto.matches("√|x²|x³|xⁿ|1/x|x!|sin|cos|tan|log")) {
                boton.setBackground(COLOR_BOTON_ADVANCED);
                boton.setForeground(COLOR_TEXTO);
            } else {
                boton.setBackground(COLOR_BOTON_OPERADOR);
                boton.setForeground(COLOR_TEXTO);
            }

            // Gestión de eventos de ratón (Hover effects)
            boton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    boton.setBackground(boton.getBackground().darker());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    // Restaura el color original. Es un poco repetitivo,
                    // sería mejor guardar el color original en una variable dentro del listener.
                    if (texto.matches("[0-9]") || texto.equals(".")) {
                        boton.setBackground(COLOR_BOTON_NUMERO);
                    } else if (texto.equals("=")) {
                        boton.setBackground(COLOR_BOTON_IGUAL);
                    } else if (texto.equals("C") || texto.equals("⌫")) {
                        boton.setBackground(COLOR_BOTON_BORRAR);
                    } else if (texto.equals("(") || texto.equals(")")) {
                        boton.setBackground(new Color(100, 100, 110));
                    } else if (texto.matches("√|x²|x³|xⁿ|1/x|x!|sin|cos|tan|log")) {
                        boton.setBackground(COLOR_BOTON_ADVANCED);
                    } else {
                        boton.setBackground(COLOR_BOTON_OPERADOR);
                    }
                }
            });

            // Se asigna la propia clase como listener (Patrón Observer)
            boton.addActionListener(this);
            panelBotones.add(boton);
        }

        add(panelBotones, BorderLayout.CENTER);

        setLocationRelativeTo(null); // Centra la ventana en la pantalla primaria
        setResizable(false);
        setVisible(true);
    }

    /**
     * Formatea el número para omitir decimales si es un entero exacto.
     * Maneja de forma segura los valores matemáticos indefinidos.
     *
     * @param numero El valor double a formatear.
     * @return String con el formato amigable o "Error".
     */
    private String formatearNumero(double numero) {
        // Prevención de crasheos por indeterminaciones matemáticas (ej. 0/0 o infinito)
        if (Double.isNaN(numero) || Double.isInfinite(numero)) {
            return "Error";
        }
        // Si el número es igual a su versión long (ej. 5.0 == 5), mostramos el entero puro
        if (numero == (long) numero) {
            return String.valueOf((long) numero);
        } else {
            return df.format(numero);
        }
    }

    /**
     * Comprueba si la interfaz se encuentra en estado de error para bloquear operaciones.
     */
    private boolean hayErrorEnPantalla() {
        String texto = pantalla.getText();
        return texto.equals("Error") || texto.startsWith("Error");
    }

    /**
     * Transforma el texto de la vista al modelo de datos numérico.
     *
     * @return El valor actual de la pantalla en formato numérico.
     * @throws NumberFormatException Si el texto no es un número válido.
     */
    private double obtenerNumeroPantalla() throws NumberFormatException {
        String texto = pantalla.getText();
        if (texto.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(texto);
    }

    /**
     * Centraliza el comportamiento de la UI cuando ocurre un error de negocio.
     */
    private void mostrarError() {
        pantalla.setText("Error");
        resultadoLabel.setText(" ");
        operador = 0;
        nuevaOperacion = true;
    }

    /**
     * Actualiza la pantalla y el estado tras finalizar un cálculo exitoso.
     */
    private void mostrarResultado(double resultado) {
        String resultadoStr = formatearNumero(resultado);
        if (resultadoStr.equals("Error")) {
            mostrarError();
        } else {
            pantalla.setText(resultadoStr);
            nuevaOperacion = true;
        }
    }

    /**
     * Controlador central de eventos.
     * * REFACTOR PENDIENTE (Code Smell): Este método actúa como un "God Method" con alta
     * complejidad ciclomática. Reemplazar esta estructura 'if-else' gigante con el
     * Patrón Command o un Enum polimórfico facilitará el mantenimiento en el futuro.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        try {
            // --- MANEJO DE ENTRADA BÁSICA ---
            if (comando.matches("[0-9]")) {
                if (hayErrorEnPantalla()) {
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                }
                if (nuevaOperacion) {
                    pantalla.setText("");
                    nuevaOperacion = false;
                }
                pantalla.setText(pantalla.getText() + comando);
            }
            else if (comando.equals(".")) {
                if (hayErrorEnPantalla()) {
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                }
                if (!pantalla.getText().contains(".")) {
                    if (pantalla.getText().isEmpty()) {
                        pantalla.setText("0.");
                    } else {
                        pantalla.setText(pantalla.getText() + ".");
                    }
                }
                nuevaOperacion = false;
            }

            // --- MANEJO DE LIMPIEZA Y EDICIÓN ---
            else if (comando.equals("C")) {
                pantalla.setText("");
                resultadoLabel.setText(" ");
                num1 = num2 = resultado = 0;
                operador = 0;
                nuevaOperacion = true;
            }
            else if (comando.equals("⌫")) {
                String texto = pantalla.getText();
                if (hayErrorEnPantalla()) {
                    // Reutilizar el comportamiento de 'C' aquí evitaría código duplicado
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                    num1 = num2 = resultado = 0;
                    operador = 0;
                    nuevaOperacion = true;
                } else if (!texto.isEmpty()) {
                    pantalla.setText(texto.substring(0, texto.length() - 1));
                }
            }

            // --- OPERACIONES BÁSICAS ---
            else if (comando.matches("[+\\-*/]")) {
                if (hayErrorEnPantalla()) {
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                }
                if (!pantalla.getText().isEmpty()) {
                    try {
                        num1 = obtenerNumeroPantalla();
                        operador = comando.charAt(0);
                        resultadoLabel.setText(formatearNumero(num1) + " " + operador);
                        nuevaOperacion = true;
                    } catch (NumberFormatException ex) {
                        mostrarError();
                    }
                }
            }

            // --- CÁLCULO FINAL (IGUAL) ---
            else if (comando.equals("=")) {
                if (hayErrorEnPantalla()) {
                    return; // Fail-fast: salimos temprano si hay error
                }
                if (!pantalla.getText().isEmpty() && operador != 0) {
                    try {
                        num2 = obtenerNumeroPantalla();

                        switch (operador) {
                            case '+': resultado = num1 + num2; break;
                            case '-': resultado = num1 - num2; break;
                            case '*': resultado = num1 * num2; break;
                            case '/':
                                if (num2 != 0) {
                                    resultado = num1 / num2;
                                } else {
                                    mostrarError(); // Manejo explícito de división por cero
                                    return;
                                }
                                break;
                        }

                        String num1Str = formatearNumero(num1);
                        String num2Str = formatearNumero(num2);
                        String resultadoStr = formatearNumero(resultado);

                        if (resultadoStr.equals("Error")) {
                            mostrarError();
                        } else {
                            pantalla.setText(resultadoStr);
                            resultadoLabel.setText(num1Str + " " + operador + " " + num2Str + " =");
                            // Permitimos encadenar operaciones usando el resultado anterior
                            num1 = resultado;
                            operador = 0;
                            nuevaOperacion = true;
                        }

                    } catch (NumberFormatException ex) {
                        mostrarError();
                    }
                } else if (pantalla.getText().isEmpty()) {
                    mostrarError();
                }
            }

            // --- OPERACIONES CIENTÍFICAS (Unarias y Especiales) ---
            else if (comando.equals("√")) {
                try {
                    double num = obtenerNumeroPantalla();
                    if (num < 0) {
                        mostrarError(); // Números imaginarios no soportados
                    } else {
                        resultado = Math.sqrt(num);
                        resultadoLabel.setText("√(" + formatearNumero(num) + ") =");
                        mostrarResultado(resultado);
                    }
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("x²")) {
                try {
                    double num = obtenerNumeroPantalla();
                    resultado = Math.pow(num, 2);
                    resultadoLabel.setText("(" + formatearNumero(num) + ")² =");
                    mostrarResultado(resultado);
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("x³")) {
                try {
                    double num = obtenerNumeroPantalla();
                    resultado = Math.pow(num, 3);
                    resultadoLabel.setText("(" + formatearNumero(num) + ")³ =");
                    mostrarResultado(resultado);
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("xⁿ")) {
                if (hayErrorEnPantalla()) {
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                }
                if (!pantalla.getText().isEmpty()) {
                    try {
                        num1 = obtenerNumeroPantalla();
                        operador = '^'; // Operador virtual para potencias
                        resultadoLabel.setText(formatearNumero(num1) + " ^");
                        nuevaOperacion = true;
                    } catch (NumberFormatException ex) {
                        mostrarError();
                    }
                }
            }
            else if (comando.equals("1/x")) {
                try {
                    double num = obtenerNumeroPantalla();
                    if (num == 0) {
                        mostrarError();
                    } else {
                        resultado = 1 / num;
                        resultadoLabel.setText("1/(" + formatearNumero(num) + ") =");
                        mostrarResultado(resultado);
                    }
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("x!")) {
                try {
                    double num = obtenerNumeroPantalla();
                    // El factorial solo aplica para enteros no negativos
                    if (num < 0 || num != Math.floor(num)) {
                        mostrarError();
                    } else {
                        int n = (int) num;
                        long factorial = 1;
                        for (int i = 2; i <= n; i++) {
                            factorial *= i;
                        }
                        resultado = factorial;
                        resultadoLabel.setText("(" + formatearNumero(num) + ")! =");
                        mostrarResultado(resultado);
                    }
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("sin")) {
                try {
                    double num = obtenerNumeroPantalla();
                    resultado = Math.sin(Math.toRadians(num)); // Convertimos a radianes por defecto
                    resultadoLabel.setText("sin(" + formatearNumero(num) + ") =");
                    mostrarResultado(resultado);
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("cos")) {
                try {
                    double num = obtenerNumeroPantalla();
                    resultado = Math.cos(Math.toRadians(num));
                    resultadoLabel.setText("cos(" + formatearNumero(num) + ") =");
                    mostrarResultado(resultado);
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("tan")) {
                try {
                    double num = obtenerNumeroPantalla();
                    // Tangente de 90, 270... tiende a infinito
                    if (num % 90 == 0 && num % 180 != 0) {
                        mostrarError();
                    } else {
                        resultado = Math.tan(Math.toRadians(num));
                        resultadoLabel.setText("tan(" + formatearNumero(num) + ") =");
                        mostrarResultado(resultado);
                    }
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }
            else if (comando.equals("log")) {
                try {
                    double num = obtenerNumeroPantalla();
                    if (num <= 0) {
                        mostrarError(); // El logaritmo base 10 solo admite números positivos
                    } else {
                        resultado = Math.log10(num);
                        resultadoLabel.setText("log(" + formatearNumero(num) + ") =");
                        mostrarResultado(resultado);
                    }
                } catch (NumberFormatException ex) {
                    mostrarError();
                }
            }

            // --- CONTINUACIÓN DEL FLUJO DE POTENCIA (xⁿ) ---
            else if (operador == '^' && comando.matches("[0-9.]")) {
                if (hayErrorEnPantalla()) {
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                }
                if (nuevaOperacion) {
                    pantalla.setText("");
                    nuevaOperacion = false;
                }
                pantalla.setText(pantalla.getText() + comando);
            }
            else if (operador == '^' && comando.equals("=")) {
                if (!pantalla.getText().isEmpty()) {
                    try {
                        num2 = obtenerNumeroPantalla();
                        resultado = Math.pow(num1, num2);
                        String num1Str = formatearNumero(num1);
                        String num2Str = formatearNumero(num2);
                        String resultadoStr = formatearNumero(resultado);

                        if (resultadoStr.equals("Error")) {
                            mostrarError();
                        } else {
                            pantalla.setText(resultadoStr);
                            resultadoLabel.setText(num1Str + " ^ " + num2Str + " =");
                            num1 = resultado;
                            operador = 0;
                            nuevaOperacion = true;
                        }
                    } catch (NumberFormatException ex) {
                        mostrarError();
                    }
                }
            }

            // Paréntesis no implementados en la lógica actual (queda como To-Do)
            else if (comando.equals("(") || comando.equals(")")) {
                if (hayErrorEnPantalla()) {
                    pantalla.setText("");
                    resultadoLabel.setText(" ");
                }
                pantalla.setText(pantalla.getText() + comando);
            }

        } catch (Exception ex) {
            // Capturamos cualquier otra excepción no contemplada para evitar cierres abruptos
            mostrarError();
            // Evitar System.err en producción; idealmente usar un Logger (ej. SLF4J, Log4j)
            System.err.println("Error no controlado en la calculadora: " + ex.getMessage());
        }
    }

    /**
     * Punto de entrada de la aplicación.
     */
    public static void main(String[] args) {
        // Establecer el aspecto visual nativo del sistema operativo (Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // Falla silenciosamente a nivel de UI si no encuentra el tema
        }

        // BUENA PRÁCTICA: Toda actualización de Swing debe ocurrir en el Event Dispatch Thread (EDT).
        // Evita condiciones de carrera y cuelgues de la interfaz.
        SwingUtilities.invokeLater(() -> new Calculadora());
    }
}