package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.*;

public class SubMenuCursos extends JFrame {

    private JPanel panelPrincipal, panelRegistrar, panelEliminar;
    private JButton btnTabRegistrar, btnTabEliminar;
    private JTextField txtNombre, txtDesc, txtCosto, txtDuracion;
    private JComboBox<String> comboProfesor, comboEliminar;
    private ImageIcon iconoSucc = cargarIcono("meowl_icon_aprobado.png", 50, 50);
    private ImageIcon iconoWarning = cargarIcono("meowl_icon_warning.png", 50, 50);
    private ImageIcon iconoError = cargarIcono("meowl_icon_error.png", 50, 50);

    class PanelFondo extends JPanel {
        private Image imagen;
        public PanelFondo() {
            try {
                imagen = ImageIO.read(new File("resources/meowl_login.jpg"));
            } catch (Exception e) {
                setBackground(new Color(30, 30, 30));
            }
            setLayout(new BorderLayout());
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public SubMenuCursos() {
        setTitle("Pynanzas - Gestión de Cursos");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Contenedor principal con fondo
        PanelFondo contenedorFondo = new PanelFondo();
        setContentPane(contenedorFondo);

        // Botón Volver (Estilo minimalista arriba a la izquierda)
        JButton btnVolver = new JButton("←");
        btnVolver.setBounds(15, 15, 50, 30);
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(null);
        btnVolver.addActionListener(e -> {
            new MenuAcademico().setVisible(true);
            this.dispose();
        });
        contenedorFondo.add(btnVolver);

        panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelPrincipal.setLayout(null);
        panelPrincipal.setPreferredSize(new Dimension(550, 580));
        panelPrincipal.setBackground(new Color(35, 35, 35, 220));
        panelPrincipal.setOpaque(false);

        // Centrar el panel principal usando GridBagLayout
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.add(panelPrincipal);
        contenedorFondo.add(contenedorCentrado, BorderLayout.CENTER);

        // Botones de Navegación (Tabs)
        btnTabRegistrar = new JButton("Registrar Curso");
        btnTabRegistrar.setBounds(125, 20, 150, 35);
        btnTabRegistrar.setFocusPainted(false);
        btnTabRegistrar.setBorder(null);

        btnTabEliminar = new JButton("Eliminar Curso");
        btnTabEliminar.setBounds(275, 20, 150, 35);
        btnTabEliminar.setFocusPainted(false);
        btnTabEliminar.setBorder(null);

        panelPrincipal.add(btnTabRegistrar);
        panelPrincipal.add(btnTabEliminar);

        // Crear sub-paneles
        crearPanelRegistrar();
        crearPanelEliminar();

        // Eventos de Tabs
        btnTabRegistrar.addActionListener(e -> mostrarPanel("registrar"));
        btnTabEliminar.addActionListener(e -> mostrarPanel("eliminar"));

        // Mostrar por defecto
        mostrarPanel("registrar");
    }

    private void mostrarPanel(String nombre) {
        boolean esRegistrar = nombre.equals("registrar");
        panelRegistrar.setVisible(esRegistrar);
        panelEliminar.setVisible(!esRegistrar);

        if (esRegistrar) {
            btnTabRegistrar.setBackground(new Color(59, 130, 246));
            btnTabRegistrar.setForeground(Color.WHITE);
            btnTabEliminar.setBackground(new Color(50, 50, 50));
            btnTabEliminar.setForeground(Color.GRAY);
        } else {
            btnTabEliminar.setBackground(new Color(185, 28, 28));
            btnTabEliminar.setForeground(Color.WHITE);
            btnTabRegistrar.setBackground(new Color(50, 50, 50));
            btnTabRegistrar.setForeground(Color.GRAY);
        }

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void crearPanelRegistrar() {
        panelRegistrar = new JPanel();
        panelRegistrar.setBounds(25, 70, 500, 500);
        panelRegistrar.setOpaque(false);
        panelRegistrar.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("Formulario de Registro", SwingConstants.CENTER);
        lblHeader.setForeground(new Color(34, 197, 94)); // Verde suave
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblHeader.setBounds(0, y, 500, 25);
        panelRegistrar.add(lblHeader);

        y += 40;
        agregarCampo(panelRegistrar, "Nombre del Curso", txtNombre = new JTextField(), y);
        y += 65;
        agregarCampo(panelRegistrar, "Descripción (Opcional)", txtDesc = new JTextField(), y);
        y += 65;
        agregarCampo(panelRegistrar, "Costo del Curso (Bs.)", txtCosto = new JTextField(), y);
        y += 65;
        agregarCampo(panelRegistrar, "Duración (Semanas)", txtDuracion = new JTextField(), y);
        
        y += 65;
        JLabel lblProf = new JLabel("Asignar Profesor", SwingConstants.CENTER);
        lblProf.setForeground(Color.GRAY);
        lblProf.setBounds(0, y, 500, 20);
        panelRegistrar.add(lblProf);

        comboProfesor = new JComboBox<>();
        comboProfesor.setBounds(100, y + 20, 300, 30);
        comboProfesor.setBackground(new Color(45, 45, 45));
        comboProfesor.setForeground(Color.WHITE);
        cargarProfesores();
        panelRegistrar.add(comboProfesor);

        JButton btnGuardar = new JButton("Guardar Curso");
        btnGuardar.setBounds(150, 430, 200, 40);
        btnGuardar.setBackground(new Color(22, 101, 52));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarCurso());
        panelRegistrar.add(btnGuardar);

        panelPrincipal.add(panelRegistrar);
    }

    private void crearPanelEliminar() {
        panelEliminar = new JPanel();
        panelEliminar.setBounds(25, 70, 500, 500);
        panelEliminar.setOpaque(false);
        panelEliminar.setLayout(null);

        JLabel lblTitulo = new JLabel("Eliminar Curso Existente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(249, 115, 22)); // Naranja
        lblTitulo.setBounds(0, 40, 500, 30);
        panelEliminar.add(lblTitulo);

        comboEliminar = new JComboBox<>();
        comboEliminar.setBounds(100, 100, 300, 40);
        comboEliminar.setBackground(new Color(45, 45, 45));
        comboEliminar.setForeground(Color.WHITE);
        cargarCursosParaEliminar();
        panelEliminar.add(comboEliminar);

        JLabel lblWarn = new JLabel("<html><center>Advertencia: Solo se pueden eliminar cursos<br>que no tengan alumnos inscritos actualmente.</center></html>", SwingConstants.CENTER);
        lblWarn.setForeground(new Color(239, 68, 68));
        lblWarn.setBounds(50, 180, 400, 60);
        panelEliminar.add(lblWarn);

        JButton btnConfirmar = new JButton("Eliminar de la Base de Datos");
        btnConfirmar.setBounds(125, 260, 250, 40);
        btnConfirmar.setBackground(new Color(153, 27, 27));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.addActionListener(e -> eliminarCurso());
        panelEliminar.add(btnConfirmar);

        panelPrincipal.add(panelEliminar);
    }

    private void agregarCampo(JPanel panel, String titulo, JTextField campo, int y) {
        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setForeground(Color.GRAY);
        label.setBounds(0, y, 500, 20);
        panel.add(label);

        campo.setBounds(100, y + 20, 300, 30);
        campo.setBackground(new Color(45, 45, 45));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        panel.add(campo);
    }


    private void cargarProfesores() {
        Conectar conecta = new Conectar();
        String sql = "SELECT id_profesor, nombre, apellido, cedula FROM profesores ORDER BY id_profesor ASC";
        try (Connection con = conecta.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            comboProfesor.removeAllItems();
            comboProfesor.addItem("---- Seleccionar ----");
            while (rs.next()) {
                comboProfesor.addItem(rs.getInt("id_profesor") + "-" + rs.getString("nombre") + " " + rs.getString("apellido"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void guardarCurso() {
        String nombre = txtNombre.getText().trim();
        String desc = txtDesc.getText().trim();
        String costo = txtCosto.getText().trim();
        String duracion = txtDuracion.getText().trim();
        String prof = (String) comboProfesor.getSelectedItem();

        if (nombre.isEmpty() || costo.isEmpty() || prof.equals("---- Seleccionar ----")) {
            JOptionPane.showMessageDialog(this, "Por favor rellena los campos obligatorios.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            return;
        }

        try {
            int idProf = Integer.parseInt(prof.split("-")[0]);
            Conectar conecta = new Conectar();
            String sql = "INSERT INTO cursos (nombre, descripcion, costo_curso, duracion_semanas, id_profesor) VALUES (?,?,?,?,?)";
            
            try (Connection con = conecta.getConexion();
                 PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, nombre);
                pst.setString(2, desc);
                pst.setDouble(3, Double.parseDouble(costo));
                pst.setInt(4, Integer.parseInt(duracion));
                pst.setInt(5, idProf);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Curso guardado correctamente.", "NICE", JOptionPane.PLAIN_MESSAGE, iconoSucc);
                cargarCursosParaEliminar();
                // Limpiar campos
                txtNombre.setText(""); txtDesc.setText(""); txtCosto.setText(""); txtDuracion.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
        }
    }

    private void cargarCursosParaEliminar() {
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_curso, nombre FROM cursos")) {
            comboEliminar.removeAllItems();
            while (rs.next()) {
                comboEliminar.addItem(rs.getInt("id_curso") + " - " + rs.getString("nombre"));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void eliminarCurso() {
        String seleccionado = (String) comboEliminar.getSelectedItem();
        if (seleccionado == null) return;

        int id = Integer.parseInt(seleccionado.split(" - ")[0]);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este curso permanentemente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Conectar conecta = new Conectar();
            try (Connection con = conecta.getConexion();
                 PreparedStatement pst = con.prepareStatement("DELETE FROM cursos WHERE id_curso = ?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Curso eliminado.", "NICE", JOptionPane.PLAIN_MESSAGE, iconoSucc);
                cargarCursosParaEliminar();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: tiene alumnos registrados.", "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
            }
        }
    }
    
    private ImageIcon cargarIcono(String nombreArchivo, int ancho, int alto) {
        try {
            ImageIcon iconoOriginal = new ImageIcon("resources/" + nombreArchivo);
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imgEscalada);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + nombreArchivo);
            return null;
        }
    }
}