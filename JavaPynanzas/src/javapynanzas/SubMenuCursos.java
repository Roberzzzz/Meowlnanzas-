package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class SubMenuCursos extends JFrame {

    private JPanel panelRegistrar, panelEliminar;
    private JButton btnTabRegistrar, btnTabEliminar;
    private JTextField txtNombre, txtDesc, txtCosto, txtDuracion;
    private JComboBox<String> comboProfesor, comboEliminar;

    public SubMenuCursos() {
        setTitle("Pynanzas - Gestión de Cursos");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        
        btnTabRegistrar = new JButton("Registrar");
        btnTabRegistrar.setBounds(100, 30, 140, 35);
        btnTabRegistrar.setFocusPainted(false);
        
        btnTabEliminar = new JButton("Eliminar");
        btnTabEliminar.setBounds(250, 30, 140, 35);
        btnTabEliminar.setFocusPainted(false);
        
     
        crearPanelRegistrar();
        crearPanelEliminar();

        btnTabRegistrar.addActionListener(e -> mostrarPanel("registrar"));
        btnTabEliminar.addActionListener(e -> mostrarPanel("eliminar"));

       
        mostrarPanel("registrar");

        add(btnTabRegistrar);
        add(btnTabEliminar);
        
        // Botón Volver General
        JButton btnVolver = new JButton("←");
        btnVolver.setBounds(15, 15, 45, 30);
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> {
            new MenuRegistro().setVisible(true);
            this.dispose();
        });
        add(btnVolver);
        
        
        getContentPane().setBackground(new Color(30, 30, 30));
    }

    private void mostrarPanel(String nombre) {
        if (nombre.equals("registrar")) {
            panelRegistrar.setVisible(true);
            panelEliminar.setVisible(false);
            btnTabRegistrar.setBackground(new Color(59, 130, 246));
            btnTabEliminar.setBackground(new Color(185, 28, 28, 100));
        } else {
            panelRegistrar.setVisible(false);
            panelEliminar.setVisible(true);
            btnTabEliminar.setBackground(new Color(185, 28, 28));
            btnTabRegistrar.setBackground(new Color(59, 130, 246, 100));
        }
    }

    private void crearPanelRegistrar() {
        panelRegistrar = new JPanel();
        panelRegistrar.setBounds(0, 80, 500, 600);
        panelRegistrar.setOpaque(false);
        panelRegistrar.setLayout(null);
        int yValue = 35;

        JLabel lblHeader= new JLabel("Registrar nuevo curso", SwingConstants.CENTER);
        lblHeader.setForeground(Color.GREEN);
        lblHeader.setBounds(0, yValue, 500, 20);
        panelRegistrar.add(lblHeader);
        
        // PARA EL NOMBRE
        yValue = yValue+20;
        JLabel lblNombreCurso = new JLabel("Nombre del Curso", SwingConstants.CENTER);
        lblNombreCurso.setForeground(Color.GRAY);
        lblNombreCurso.setBounds(0, yValue, 500, 20);
        panelRegistrar.add(lblNombreCurso);
        
        yValue = yValue+20;
        txtNombre = new JTextField();
        txtNombre.setBounds(80, yValue, 340, 35);
        txtNombre.setBackground(new Color(45, 45, 45));
        txtNombre.setForeground(Color.WHITE);
        panelRegistrar.add(txtNombre);
        
        yValue = yValue+40;
        
        //PARA LA DESCRIPCION
        
        JLabel lblDescription = new JLabel("Descripción (Opcional)", SwingConstants.CENTER);
        lblDescription.setForeground(Color.GRAY);
        lblDescription.setBounds(0, yValue, 500, 20);
        panelRegistrar.add(lblDescription);
        
        yValue = yValue+20;
        txtDesc = new JTextField();
        txtDesc.setBounds(80, yValue, 340, 35);
        txtDesc.setBackground(new Color(45, 45, 45));
        txtDesc.setForeground(Color.WHITE);
        panelRegistrar.add(txtDesc);
        
        yValue = yValue+40;
        
        // PARA EL COSTO
        
        JLabel lblCosto = new JLabel("Costo", SwingConstants.CENTER);
        lblCosto.setForeground(Color.GRAY);
        lblCosto.setBounds(0, yValue, 500, 20);
        panelRegistrar.add(lblCosto);
        
        yValue = yValue+20;
        txtCosto = new JTextField();
        txtCosto.setBounds(80, yValue, 340, 35);
        txtCosto.setBackground(new Color(45, 45, 45));
        txtCosto.setForeground(Color.WHITE);
        panelRegistrar.add(txtCosto);
        
        yValue = yValue+40;
        
        // PARA LA DURACION
        
        JLabel lblDuracion = new JLabel("Duración (Semanas)", SwingConstants.CENTER);
        lblDuracion.setForeground(Color.GRAY);
        lblDuracion.setBounds(0, yValue, 500, 20);
        panelRegistrar.add(lblDuracion);
        
        yValue = yValue+20;
        txtDuracion = new JTextField();
        txtDuracion.setBounds(80, yValue, 340, 35);
        txtDuracion.setBackground(new Color(45, 45, 45));
        txtDuracion.setForeground(Color.WHITE);
        panelRegistrar.add(txtDuracion);
        
        yValue = yValue+40;

        //PARA LOS PROFESORES
        JLabel lblProfesor = new JLabel("Profesores", SwingConstants.CENTER);
        lblProfesor.setForeground(Color.GRAY);
        lblProfesor.setBounds(0, yValue, 500, 20);
        panelRegistrar.add(lblProfesor);
        
        yValue = yValue+20;
        
        comboProfesor = new JComboBox<>();
        comboProfesor.setBounds(80, yValue, 340, 20);
        comboProfesor.setBackground(new Color(59, 130, 246));
        comboProfesor.setForeground(Color.WHITE);
        panelRegistrar.add(comboProfesor);
        cargarProfesores(); 

        panelRegistrar.add(comboProfesor);
        
        //
        

        JButton btnGuardar = new JButton("Registrar Curso");
        btnGuardar.setBounds(150, 500, 200, 45);
        btnGuardar.setBackground(new Color(34, 100, 50));
        btnGuardar.setForeground(Color.WHITE);
        panelRegistrar.add(btnGuardar);
        btnGuardar.addActionListener(e-> guardarCurso());

        add(panelRegistrar);
    }
    

    private void crearPanelEliminar() {
        panelEliminar = new JPanel();
        panelEliminar.setBounds(0, 80, 500, 600);
        panelEliminar.setOpaque(false);
        panelEliminar.setLayout(null);

        JLabel lblTitulo = new JLabel("Eliminar curso existente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(255, 165, 0)); 
        lblTitulo.setBounds(0, 40, 500, 30);
        panelEliminar.add(lblTitulo);

        comboEliminar = new JComboBox<>();
        comboEliminar.setBounds(80, 85, 340, 40);
        comboEliminar.setBackground(new Color(59, 130, 246));
        comboEliminar.setForeground(Color.WHITE);
        cargarCursosParaEliminar();
        panelEliminar.add(comboEliminar);
   
    
        
        JLabel lblWarn = new JLabel("<html><center>Esta acción eliminará el curso permanentemente.<br>Solo se permite si no hay alumnos inscritos.</center></html>", SwingConstants.CENTER);
        lblWarn.setForeground(new Color(255, 165, 0));
        lblWarn.setBounds(0, 160, 400, 50);
        panelEliminar.add(lblWarn);

     
        JButton btnConfirmar = new JButton("Confirmar eliminación");
        btnConfirmar.setBounds(150, 230, 200, 40);
        btnConfirmar.setBackground(new Color(185, 28, 28));
        btnConfirmar.setForeground(Color.WHITE);
        panelEliminar.add(btnConfirmar);
        btnConfirmar.addActionListener(e -> eliminarCurso());

        add(panelEliminar);
    }
    private void cargarProfesores() {
        Conectar conecta = new Conectar();
        String sql = "SELECT  id_profesor, nombre, apellido, cedula FROM profesores ORDER BY id_profesor ASC";

        try (java.sql.Connection con = conecta.getConexion();
             java.sql.Statement st = con.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {

            comboProfesor.removeAllItems();
            comboProfesor.addItem("----"); 

            while (rs.next()) {
                String item =rs.getString("id_profesor")+"-"+
                        rs.getString("nombre") + " " + 
                              rs.getString("apellido")+ "-"+
                                rs.getString("cedula");

                comboProfesor.addItem(item);
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error al cargar profesores: " + e.getMessage());
        }
    }
    private void guardarCurso() {
   
    String nombre = txtNombre.getText().trim();
    String descripcion = txtDesc.getText().trim();
    String costoStr = txtCosto.getText().trim();
    String duracionStr = txtDuracion.getText().trim();
    
    String profesorSeleccionado = (String) comboProfesor.getSelectedItem();
    int idProfesor = 0;
    
    if (profesorSeleccionado != null && !profesorSeleccionado.equals("----")) {
        try {
            // Dividimos el string para sacar solo el número del ID
            idProfesor = Integer.parseInt(profesorSeleccionado.split("-")[0]);
        } catch (Exception e) {
            System.err.println("Error al procesar ID de profesor: " + e.getMessage());
        }
    }

    // Validación rápida
    if (nombre.isEmpty() || costoStr.isEmpty() || idProfesor == 0) {
        JOptionPane.showMessageDialog(this, "Por favor, completa los campos obligatorios.");
        return;
    }

    // 2. Proceso de inserción en la base de datos
    Conectar conecta = new Conectar();
    String sql = "INSERT INTO cursos (nombre, descripcion, costo_curso, duracion_semanas, id_profesor) VALUES (?, ?, ?, ?, ?)";

    // Usamos try-with-resources para cerrar la conexión automáticamente
    try (java.sql.Connection con = conecta.getConexion();
         java.sql.PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, nombre);
        pst.setString(2, descripcion);
        pst.setDouble(3, Double.parseDouble(costoStr));   
        pst.setInt(4, Integer.parseInt(duracionStr));     
        pst.setInt(5, idProfesor);

        int resultado = pst.executeUpdate();

        if (resultado > 0) {
            JOptionPane.showMessageDialog(this, "¡Curso '" + nombre + "' registrado con éxito!");
            cargarCursosParaEliminar();
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error: El costo y la duración deben ser números válidos.");
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de SQL: " + e.getMessage());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + e.getMessage());
    }
}
    
    private void cargarCursosParaEliminar() {
    Conectar conecta = new Conectar();
    String sql = "SELECT id_curso, nombre FROM cursos";
    
    try (java.sql.Connection con = conecta.getConexion();
         java.sql.Statement st = con.createStatement();
         java.sql.ResultSet rs = st.executeQuery(sql)) {
        
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

   
    if (seleccionado == null || seleccionado.equals("----")) {
        JOptionPane.showMessageDialog(this, "Por favor, selecciona un curso para eliminar.");
        return;
    }

   
    String[] partes = seleccionado.split(" - ");
    int idCurso = Integer.parseInt(partes[0]);

   
    int respuesta = JOptionPane.showConfirmDialog(this, 
        "¿Estás seguro de que deseas eliminar el curso: " + partes[1] + "?\nEsta acción no se puede deshacer.",
        "Confirmar Eliminación", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.WARNING_MESSAGE);

    if (respuesta == JOptionPane.YES_OPTION) {
        Conectar conecta = new Conectar();
        String sql = "DELETE FROM cursos WHERE id_curso = ?";

        try (java.sql.Connection con = conecta.getConexion();
             java.sql.PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idCurso);

            int resultado = pst.executeUpdate();

            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Curso eliminado exitosamente.");
                cargarCursosParaEliminar(); 
            }

        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "No se puede eliminar el curso: Posiblemente tiene alumnos inscritos.\nError: " + e.getMessage(),
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
}

