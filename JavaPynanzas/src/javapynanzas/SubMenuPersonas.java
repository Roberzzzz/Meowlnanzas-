package javapynanzas;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class SubMenuPersonas extends JFrame {

    private JPanel panelPrincipal; 
    private JTextField txtNombre, txtSegundoNombre, txtApellido, txtSegundoApellido;
    private JTextField txtCedula, txtDireccion, txtTelefono, txtEmail;
    private JComboBox<String> comboGenero;
    
    private JLabel errNombre, errSegundoNombre, errApellido, errSegundoApellido, errCedula, errDireccion, errTelefono, errEmail;

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
                g2d.setColor(new Color(0, 0, 0, 170)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public SubMenuPersonas() {
        setTitle("Meowlnanzas - Gestión de Personas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedorFondo = new PanelFondo();
        setContentPane(contenedorFondo);

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
        panelPrincipal.setBackground(new Color(30, 30, 30, 210)); 
        panelPrincipal.setOpaque(false);

        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.add(panelPrincipal);

        JScrollPane scrollPane = new JScrollPane(contenedorCentrado);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contenedorFondo.add(scrollPane, BorderLayout.CENTER);

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
        errNombre = crearEtiquetaError(y + 35);
        AggCAP(txtNombre, "Primer Nombre (Ej: Ana)", y, errNombre); y += 85;

        errSegundoNombre = crearEtiquetaError(y + 35);
        AggCAP(txtSegundoNombre, "Segundo Nombre (Ej: María)", y, errSegundoNombre); y += 85;

        errApellido = crearEtiquetaError(y + 35);
        AggCAP(txtApellido, "Primer Apellido (Ej: Gómez)", y, errApellido); y += 85;

        errSegundoApellido = crearEtiquetaError(y + 35);
        AggCAP(txtSegundoApellido, "Segundo Apellido (Ej: Martínez)", y, errSegundoApellido); y += 85;

        errCedula = crearEtiquetaError(y + 35);
        AggCAP(txtCedula, "Cédula (Ej: 31133492)", y, errCedula); y += 85;

        errDireccion = crearEtiquetaError(y + 35);
        AggCAP(txtDireccion, "Dirección (Ej: Av.Principal #12)", y, errDireccion); y += 85;

        errTelefono = crearEtiquetaError(y + 35);
        AggCAP(txtTelefono, "Teléfono (Ej: 04128543219)", y, errTelefono); y += 85;

        errEmail = crearEtiquetaError(y + 35);
        AggCAP(txtEmail, "Correo Electrónico (Ej: anitalahuerfanita@gmail.com)", y, errEmail); y += 55;

        JLabel lblGen = new JLabel("Género:");
        lblGen.setForeground(new Color(150, 150, 150));
        lblGen.setBounds(x, y, 100, 25);
        panelPrincipal.add(lblGen);

        comboGenero.setBounds(x + 60, y, 70, 25);
        comboGenero.setBackground(new Color(59, 130, 246));
        comboGenero.setForeground(Color.WHITE);
        panelPrincipal.add(comboGenero);

        y += 60;
        JButton btnRegistrar = new JButton("Registrar Persona");
        btnRegistrar.setBounds(140, y, 220, 45);
        btnRegistrar.setBackground(new Color(34, 139, 34));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegistrar.addActionListener(e -> validarYRegistrar());
        panelPrincipal.add(btnRegistrar);

        y += 80; 
        panelPrincipal.setPreferredSize(new Dimension(500, y));
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void AggCAP(JTextField txt, String hint, int y, JLabel lblError) {
        JLabel lbl = new JLabel(hint);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setBounds(80, y - 25, 340, 20);
        panelPrincipal.add(lbl);

        txt.setBounds(80, y, 340, 35);
        txt.setBackground(new Color(45, 45, 45));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        
        txt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { lblError.setText(""); }
            public void removeUpdate(DocumentEvent e) { lblError.setText(""); }
            public void changedUpdate(DocumentEvent e) { lblError.setText(""); }
        });
        
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
        JButton btnVolverEdit = new JButton("Volver a editar");
        btnVolverEdit.setBounds(50, y, 180, 45);
        btnVolverEdit.setBackground(new Color(80, 80, 80));
        btnVolverEdit.setForeground(Color.WHITE);
        btnVolverEdit.addActionListener(e -> crearFormulario());
        panelPrincipal.add(btnVolverEdit);

        JButton btnConfirmar = new JButton("Confirmar Registro");
        btnConfirmar.setBounds(250, y, 200, 45);
        btnConfirmar.setBackground(new Color(0, 102, 204));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.addActionListener(e -> guardarPersona());
        panelPrincipal.add(btnConfirmar);

        y += 80;
        panelPrincipal.setPreferredSize(new Dimension(500, y));
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void validarYRegistrar() {
        errNombre.setText(""); errSegundoNombre.setText(""); errApellido.setText("");
        errSegundoApellido.setText(""); errCedula.setText(""); errDireccion.setText("");
        errTelefono.setText(""); errEmail.setText("");

        boolean ok = true;
        String obligatorio = "Este campo es obligatorio";
        
        if (txtNombre.getText().trim().isEmpty()) { errNombre.setText(obligatorio); ok = false; }
        else if (txtNombre.getText().trim().length() < 2 || !txtNombre.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errNombre.setText("Mínimo 2 caracteres únicamente alfabéticos."); ok = false; }

        if (txtSegundoNombre.getText().trim().isEmpty()) { errSegundoNombre.setText(obligatorio); ok = false; }
        else if (txtSegundoNombre.getText().trim().length() < 2 || !txtSegundoNombre.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errSegundoNombre.setText("Mínimo 2 caracteres únicamente alfabéticos."); ok = false; }

        if (txtApellido.getText().trim().isEmpty()) { errApellido.setText(obligatorio); ok = false; }
        else if (txtApellido.getText().trim().length() < 2 || !txtApellido.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errApellido.setText("Mínimo 2 caracteres únicamente alfabéticos."); ok = false; }

        if (txtSegundoApellido.getText().trim().isEmpty()) { errSegundoApellido.setText(obligatorio); ok = false; }
        else if (txtSegundoApellido.getText().trim().length() < 2 || !txtSegundoApellido.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) { errSegundoApellido.setText("Mínimo 2 caracteres únicamente alfabéticos."); ok = false; }

        if (txtCedula.getText().trim().isEmpty()) { errCedula.setText(obligatorio); ok = false; }
        else if (!txtCedula.getText().matches("\\d{5,10}")) { errCedula.setText("Cédula inválida (5-10 dígitos)."); ok = false; }

        if (txtDireccion.getText().trim().isEmpty()) { errDireccion.setText(obligatorio); ok = false; }

        if (txtTelefono.getText().trim().isEmpty()) { errTelefono.setText(obligatorio); ok = false; }
        else if (!txtTelefono.getText().matches("(\\+?\\d{10,15}|0\\d{9,14})")) { errTelefono.setText("Formato inválido."); ok = false; }
        
        if (txtEmail.getText().trim().isEmpty()) { errEmail.setText(obligatorio); ok = false; }
        else if (!txtEmail.getText().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) { errEmail.setText("Email inválido."); ok = false; }

        if (ok) {
            mostrarConfirmacion();
        }
    }

    private void guardarPersona() {
        Conectar conecta = new Conectar();
        
        String sqlCheck = "SELECT cedula, email, telefono FROM personas WHERE cedula = ? OR email = ? OR telefono = ?";
        String sqlInsert = "INSERT INTO personas (nombre, segundo_nombre, apellido, segundo_apellido, cedula, direccion, genero, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (java.sql.Connection con = conecta.getConexion()) {

            try (java.sql.PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                String cedulaTxt = txtCedula.getText().trim();
                String emailTxt = txtEmail.getText().trim();
                String tlfTxt = txtTelefono.getText().trim();
                
                pstCheck.setString(1, cedulaTxt);
                pstCheck.setString(2, emailTxt);
                pstCheck.setString(3, tlfTxt);
                
                java.sql.ResultSet rs = pstCheck.executeQuery();
                
                if (rs.next()) {
                    String duplicado = "";
                    if (rs.getString("cedula").equals(cedulaTxt)) duplicado = "La Cédula";
                    else if (rs.getString("email").equalsIgnoreCase(emailTxt)) duplicado = "El Correo";
                    else if (rs.getString("telefono").equals(tlfTxt)) duplicado = "El Teléfono";

                    ImageIcon iconoError = cargarIcono("meowl_icon_error.png", 50, 50);
                    JOptionPane.showMessageDialog(this, "¡REGISTRO DENEGADO!\n" + duplicado + " ya existe.", "Estado del Registro", JOptionPane.PLAIN_MESSAGE, iconoError);
                    return; 
                }
            }

            try (java.sql.PreparedStatement pst = con.prepareStatement(sqlInsert)) {
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
                    ImageIcon iconoMeowlz = cargarIcono("meowl_icon_aprobado.png", 50, 50);
                    JOptionPane.showMessageDialog(this, "¡PERSONA REGISTRADA EXITOSAMENTE!", "Estado del Registro", JOptionPane.PLAIN_MESSAGE, iconoMeowlz);
                    this.dispose();
                    new MenuRegistro().setVisible(true);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de Base de Datos: " + e.getMessage());
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