package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class SubMenuProfesor extends JFrame {

    private JPanel contenedorDinamico;
    private JPanel panelRegistro, panelBorrar;
    private JButton btnIrReg, btnIrBorrar;
    private JTextField txtNombreReg, txtApellidoReg, txtCedulaReg, txtCedulaBuscar;
    private JLabel lblInfoProfesor;
    private JComboBox<String> comboResultados;
    private final Color COLOR_ACTIVO = new Color(59, 130, 246);
    private final Color COLOR_INACTIVO = new Color(45, 45, 45);

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
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 195)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    class PanelTranslucido extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(40, 40, 40, 220));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2d.setColor(new Color(100, 100, 100));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2d.dispose();
            super.paintComponent(g);
        }
        public PanelTranslucido() {
            setOpaque(false);
            setLayout(new BorderLayout());
        }
    }

    public SubMenuProfesor() {
        setTitle("Meowlnanzas - Gestión de Profesores");
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        PanelFondo fondo = new PanelFondo();
        setContentPane(fondo);

        JPanel panelNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        panelNav.setOpaque(false);

        JButton btnVolver = new JButton("<-");
        btnVolver.setPreferredSize(new Dimension(50, 30));
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.addActionListener(e -> {
            new MenuAcademico().setVisible(true);
            this.dispose();
        });
        panelNav.add(btnVolver);

        btnIrReg = new JButton("MODO REGISTRO");
        btnIrBorrar = new JButton("MODO ELIMINACIÓN");
        
        estilizarBotonNav(btnIrReg, true);
        estilizarBotonNav(btnIrBorrar, false);
        
        panelNav.add(btnIrReg);
        panelNav.add(btnIrBorrar);
        fondo.add(panelNav, BorderLayout.NORTH);

        contenedorDinamico = new JPanel(new CardLayout());
        contenedorDinamico.setOpaque(false);

        crearPanelRegistro();
        crearPanelBorrar();

        contenedorDinamico.add(panelRegistro, "REGISTRO");
        contenedorDinamico.add(panelBorrar, "BORRAR");

        fondo.add(contenedorDinamico, BorderLayout.CENTER);

        btnIrReg.addActionListener(e -> {
            actualizarBotones(true);
            ((CardLayout)contenedorDinamico.getLayout()).show(contenedorDinamico, "REGISTRO");
        });

        btnIrBorrar.addActionListener(e -> {
            actualizarBotones(false);
            ((CardLayout)contenedorDinamico.getLayout()).show(contenedorDinamico, "BORRAR");
        });
    }

    private void estilizarBotonNav(JButton btn, boolean activo) {
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(activo ? COLOR_ACTIVO : COLOR_INACTIVO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void actualizarBotones(boolean registro) {
        btnIrReg.setBackground(registro ? COLOR_ACTIVO : COLOR_INACTIVO);
        btnIrBorrar.setBackground(registro ? COLOR_INACTIVO : COLOR_ACTIVO);
    }

    private void crearPanelRegistro() {
        panelRegistro = new JPanel(null);
        panelRegistro.setOpaque(false);
        
        JLabel titulo = new JLabel("REGISTRAR PROFESOR", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setForeground(new Color(0, 255, 127));
        titulo.setBounds(0, 30, 750, 40);
        panelRegistro.add(titulo);

        txtNombreReg = crearCampoConLabel("Nombre:", 140, panelRegistro);
        txtApellidoReg = crearCampoConLabel("Apellido:", 230, panelRegistro);
        txtCedulaReg = crearCampoConLabel("Cédula de Identidad:", 320, panelRegistro);

        JButton btnGuardar = new JButton("GUARDAR PROFESOR");
        btnGuardar.setBounds(225, 420, 300, 50);
        btnGuardar.setBackground(new Color(34, 139, 34));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.addActionListener(e -> guardarAccion());
        panelRegistro.add(btnGuardar);
    }

    private void crearPanelBorrar() {
        panelBorrar = new JPanel(null);
        panelBorrar.setOpaque(false);

        JLabel titulo = new JLabel("ELIMINAR PROFESOR", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setForeground(new Color(255, 69, 0));
        titulo.setBounds(0, 30, 750, 40);
        panelBorrar.add(titulo);

        JLabel lblBusca = new JLabel("Escriba cédula (coincidencias):");
        lblBusca.setForeground(Color.LIGHT_GRAY);
        lblBusca.setBounds(175, 110, 250, 20);
        panelBorrar.add(lblBusca);

        txtCedulaBuscar = new JTextField();
        txtCedulaBuscar.setBounds(175, 135, 280, 40);
        txtCedulaBuscar.setBackground(new Color(35, 35, 35));
        txtCedulaBuscar.setForeground(Color.WHITE);
        txtCedulaBuscar.setBorder(BorderFactory.createLineBorder(COLOR_ACTIVO));
        panelBorrar.add(txtCedulaBuscar);

        JButton btnBusca = new JButton("BUSCAR");
        btnBusca.setBounds(465, 135, 110, 40);
        btnBusca.setBackground(COLOR_ACTIVO);
        btnBusca.setForeground(Color.WHITE);
        btnBusca.addActionListener(e -> buscarAccion());
        panelBorrar.add(btnBusca);

        comboResultados = new JComboBox<>();
        comboResultados.setBounds(175, 210, 400, 40);
        comboResultados.setBackground(new Color(45, 45, 45));
        comboResultados.setForeground(Color.WHITE);
        panelBorrar.add(comboResultados);

        PanelTranslucido recuadroInfo = new PanelTranslucido();
        recuadroInfo.setBounds(175, 270, 400, 80);
        
        lblInfoProfesor = new JLabel("<html><center>Seleccione un profesor de la lista</center></html>", SwingConstants.CENTER);
        lblInfoProfesor.setForeground(Color.WHITE);
        lblInfoProfesor.setFont(new Font("Arial", Font.PLAIN, 15));
        recuadroInfo.add(lblInfoProfesor, BorderLayout.CENTER);
        panelBorrar.add(recuadroInfo);

        JButton btnBorrar = new JButton("BORRAR DEFINITIVAMENTE");
        btnBorrar.setBounds(225, 420, 300, 50);
        btnBorrar.setBackground(new Color(178, 34, 34));
        btnBorrar.setForeground(Color.WHITE);
        btnBorrar.setEnabled(false);
        btnBorrar.addActionListener(e -> borrarAccion());
        panelBorrar.add(btnBorrar);
        panelBorrar.putClientProperty("btnB", btnBorrar);
    }

    private JTextField crearCampoConLabel(String t, int y, JPanel p) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.LIGHT_GRAY);
        l.setBounds(225, y - 25, 300, 20);
        p.add(l);
        JTextField tf = new JTextField();
        tf.setBounds(225, y, 300, 40);
        tf.setBackground(new Color(35, 35, 35));
        tf.setForeground(Color.WHITE);
        tf.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        p.add(tf);
        return tf;
    }

    private void buscarAccion() {
        Conectar conecta = new Conectar();
        comboResultados.removeAllItems();
        String busqueda = txtCedulaBuscar.getText().trim();
        
        if(busqueda.isEmpty()){
            lblInfoProfesor.setText("Ingrese un valor para buscar.");
            return;
        }

        try (Connection con = conecta.getConexion(); 
             PreparedStatement pst = con.prepareStatement("SELECT nombre, apellido, cedula FROM profesores WHERE cedula LIKE ?")) {
            pst.setString(1, "%" + busqueda + "%");
            ResultSet rs = pst.executeQuery();
            
            boolean hayDatos = false;
            while (rs.next()) {
                String item = rs.getString("cedula") + " - " + rs.getString("nombre") + " " + rs.getString("apellido");
                comboResultados.addItem(item);
                hayDatos = true;
            }

            if (hayDatos) {
                lblInfoProfesor.setText("<html><center>Coincidencias encontradas</center></html>");
                ((JButton)panelBorrar.getClientProperty("btnB")).setEnabled(true);
            } else {
                lblInfoProfesor.setText("No se encontraron coincidencias.");
                ((JButton)panelBorrar.getClientProperty("btnB")).setEnabled(false);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void borrarAccion() {
        String seleccion = (String) comboResultados.getSelectedItem();
        if (seleccion == null) return;
        
        String cedulaAEliminar = seleccion.split(" - ")[0];

        int opt = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el profesor con C.I: " + cedulaAEliminar + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;

        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion(); 
             PreparedStatement pst = con.prepareStatement("DELETE FROM profesores WHERE cedula = ?")) {
            pst.setString(1, cedulaAEliminar);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Eliminado exitosamente.");
            txtCedulaBuscar.setText("");
            comboResultados.removeAllItems();
            lblInfoProfesor.setText("Búsqueda finalizada.");
            ((JButton)panelBorrar.getClientProperty("btnB")).setEnabled(false);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23503")) JOptionPane.showMessageDialog(this, "No se puede borrar: Relacionado a un curso.");
        }
    }

    private void guardarAccion() {
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion(); 
             PreparedStatement pst = con.prepareStatement("INSERT INTO profesores (nombre, apellido, cedula) VALUES (?,?,?)")) {
            pst.setString(1, txtNombreReg.getText().trim());
            pst.setString(2, txtApellidoReg.getText().trim());
            pst.setString(3, txtCedulaReg.getText().trim());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registro guardado.");
            txtNombreReg.setText(""); txtApellidoReg.setText(""); txtCedulaReg.setText("");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error al guardar."); }
    }
}