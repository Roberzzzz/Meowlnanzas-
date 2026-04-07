package javapynanzas;

import javax.swing.*;
import java.awt.*;

public class SubMenuPersonas extends JFrame {

    private JPanel panelPrincipal;
    private JTextField txtNombre, txtSegundoNombre, txtApellido, txtSegundoApellido;
    private JTextField txtCedula, txtDireccion, txtTelefono, txtEmail;
    private JComboBox<String> comboGenero;
    
    private JLabel errNombre, errSegundoNombre, errApellido, errSegundoApellido, errCedula, errDireccion, errTelefono, errEmail;

    public SubMenuPersonas() {
        setTitle("Pynanzas - Gestión de Personas");
        setSize(500, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        panelPrincipal.setBackground(new Color(30, 30, 30));
        add(panelPrincipal);

        inicializarCampos();
        crearFormulario();
    }

    private void inicializarCampos() {
        txtNombre = new JTextField(); txtSegundoNombre = new JTextField();
        txtApellido = new JTextField(); txtSegundoApellido = new JTextField();
        txtCedula = new JTextField(); txtDireccion = new JTextField();
        txtTelefono = new JTextField(); txtEmail = new JTextField();
        comboGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
    }

    private void crearFormulario() {
        panelPrincipal.removeAll(); 
        int y = 40;
        int x = 80;

    
        JButton btnVolver = new JButton("<-");
        btnVolver.setBounds(15, 15, 45, 30);
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> {
            new MenuRegistro().setVisible(true);
            this.dispose();
        });
        panelPrincipal.add(btnVolver);

        JLabel lblHeader = new JLabel("Registrar Nueva Persona", SwingConstants.CENTER);
        lblHeader.setForeground(Color.GREEN);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setBounds(0, y, 500, 25);
        panelPrincipal.add(lblHeader);

        y += 60;

        AggCAP(txtNombre, "Primer Nombre (Ej: Ana)", y); y += 35;
        errNombre = crearEtiquetaError(y); y += 50;

        AggCAP(txtSegundoNombre, "Segundo Nombre (Ej: María)", y); y += 35;
        errSegundoNombre = crearEtiquetaError(y); y += 50;

        AggCAP(txtApellido, "Primer Apellido (Ej: Gómez)", y); y += 35;
        errApellido = crearEtiquetaError(y); y += 50;

        AggCAP(txtSegundoApellido, "Segundo Apellido (Ej: Martínez)", y); y += 35;
        errSegundoApellido = crearEtiquetaError(y); y += 50;

        AggCAP(txtCedula, "Cédula (Ej: 31145234)", y); y += 35;
        errCedula = crearEtiquetaError(y); y += 50;

        AggCAP(txtDireccion, "Dirección (Ej: Av. Principal #12)", y); y += 35;
        errDireccion = crearEtiquetaError(y); y += 50;

        AggCAP(txtTelefono, "Teléfono (Ej: 04123426565)", y); y += 35;
        errTelefono = crearEtiquetaError(y); y += 50;

        AggCAP(txtEmail, "Correo Electrónico (Ej: anitalahuerfanita@gmail.com)", y); y += 35;
        errEmail = crearEtiquetaError(y); y += 20;

        JLabel lblGen = new JLabel("Género:", SwingConstants.LEFT);
        lblGen.setForeground(new Color(150, 150, 150));
        lblGen.setBounds(x, y, 100, 25);
        panelPrincipal.add(lblGen);

        comboGenero.setBounds(x + 60, y, 70, 25);
        comboGenero.setBackground(new Color(59, 130, 246));
        comboGenero.setForeground(Color.WHITE);
        panelPrincipal.add(comboGenero);

        y += 40;

        JButton btnRegistrar = new JButton("Registrar Persona");
        btnRegistrar.setBounds(140, y, 220, 45);
        btnRegistrar.setBackground(new Color(34, 139, 34));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegistrar.addActionListener(e -> validarYRegistrar());
        panelPrincipal.add(btnRegistrar);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void mostrarConfirmacion() {
        panelPrincipal.removeAll();
        int y = 50;

        JLabel lblHeader = new JLabel("Confirmar Datos", SwingConstants.CENTER);
        lblHeader.setForeground(Color.YELLOW);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeader.setBounds(0, y, 500, 30);
        panelPrincipal.add(lblHeader);

        y += 70;
        String[] nombres = {"Nombre:", "2do Nombre:", "Apellido:", "2do Apellido:", "Cédula:", "Dirección:", "Teléfono:", "Correo:", "Género:"};
        String[] valores = {
            txtNombre.getText(), txtSegundoNombre.getText(), txtApellido.getText(), 
            txtSegundoApellido.getText(), txtCedula.getText(), txtDireccion.getText(), 
            txtTelefono.getText(), txtEmail.getText(), (String) comboGenero.getSelectedItem()
        };

        for (int i = 0; i < nombres.length; i++) {
            JLabel lblTit = new JLabel(nombres[i]);
            lblTit.setForeground(new Color(150, 150, 150));
            lblTit.setBounds(80, y, 150, 20);
            panelPrincipal.add(lblTit);

            JLabel lblVal = new JLabel(valores[i]);
            lblVal.setForeground(Color.WHITE);
            lblVal.setFont(new Font("Arial", Font.BOLD, 14));
            lblVal.setBounds(80, y + 20, 340, 25);
            panelPrincipal.add(lblVal);
            y += 60;
        }

        y += 20;
        JButton btnVolver = new JButton("Volver a editar");
        btnVolver.setBounds(50, y, 180, 45);
        btnVolver.setBackground(new Color(80, 80, 80));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> crearFormulario());
        panelPrincipal.add(btnVolver);

        JButton btnConfirmar = new JButton("Confirmar Registro");
        btnConfirmar.setBounds(250, y, 200, 45);
        btnConfirmar.setBackground(new Color(0, 102, 204));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.addActionListener(e -> guardarPersona());
        panelPrincipal.add(btnConfirmar);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void AggCAP(JTextField txt, String hint, int y) {
        JLabel lbl = new JLabel(hint);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setBounds(80, y - 25, 340, 20);
        panelPrincipal.add(lbl);

        txt.setBounds(80, y, 340, 35);
        txt.setBackground(new Color(45, 45, 45));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        panelPrincipal.add(txt);
    }

    private JLabel crearEtiquetaError(int y) {
        JLabel lbl = new JLabel("");
        lbl.setForeground(new Color(255, 100, 100));
        lbl.setFont(new Font("Arial", Font.ITALIC, 11));
        lbl.setBounds(80, y - 5, 340, 20);
        panelPrincipal.add(lbl);
        return lbl;
    }

    private void validarYRegistrar() {
        errNombre.setText(""); errSegundoNombre.setText(""); errApellido.setText("");
        errSegundoApellido.setText(""); errCedula.setText(""); errDireccion.setText("");
        errTelefono.setText(""); errEmail.setText("");

        boolean ok = true;
        if (txtNombre.getText().trim().length() < 2 || !txtNombre.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errNombre.setText("Mínimo 2 letras alfabéticas."); ok = false; }
        if (txtSegundoNombre.getText().trim().length() < 2 || !txtSegundoNombre.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errSegundoNombre.setText("Mínimo 2 letras alfabéticas."); ok = false; }
        if (txtApellido.getText().trim().length() < 2 || !txtApellido.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errApellido.setText("Mínimo 2 letras alfabéticas."); ok = false; }
        if (txtSegundoApellido.getText().trim().length() < 2 || !txtSegundoApellido.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errSegundoApellido.setText("Mínimo 2 letras alfabéticas."); ok = false; }
        if (!txtCedula.getText().matches("\\d{5,10}")) { errCedula.setText("Cédula inválida (5-10 dígitos)."); ok = false; }
        if (txtDireccion.getText().trim().isEmpty()) { errDireccion.setText("La dirección no puede estar vacía."); ok = false; }
        if (!txtTelefono.getText().matches("(\\+?\\d{10,15}|0\\d{9,14})")) { errTelefono.setText("Formato inválido."); ok = false; }
        if (!txtEmail.getText().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) { errEmail.setText("Email inválido."); ok = false; }

        if (ok) {
            mostrarConfirmacion();
        }
    }

    private void guardarPersona() {
        Conectar conecta = new Conectar();
        String sql = "INSERT INTO personas (nombre, segundo_nombre, apellido, segundo_apellido, cedula, direccion, genero, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection con = conecta.getConexion();
             java.sql.PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtNombre.getText().trim());
            pst.setString(2, txtSegundoNombre.getText().trim());
            pst.setString(3, txtApellido.getText().trim());
            pst.setString(4, txtSegundoApellido.getText().trim());
            pst.setString(5, txtCedula.getText().trim());
            pst.setString(6, txtDireccion.getText().trim());
            pst.setString(7, (String) comboGenero.getSelectedItem());
            pst.setString(8, txtTelefono.getText().trim());
            pst.setString(9, txtEmail.getText().trim());

            int res = pst.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(this, "Persona registrada exitosamente");
                this.dispose();
                new MenuRegistro().setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de Base de Datos: " + e.getMessage());
        }
    }
}