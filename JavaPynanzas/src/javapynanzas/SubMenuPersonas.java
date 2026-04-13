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

        // Barra de navegación superior
        JPanel barraNavegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        barraNavegacion.setOpaque(false);

        JButton btnModoRegistro = new JButton("Registrar");
        JButton btnModoActualizar = new JButton("Actualizar");

        estilizarBotonNav(btnModoRegistro);
        estilizarBotonNav(btnModoActualizar);

        btnModoRegistro.addActionListener(e -> {
            limpiarCampos();
            crearFormulario();
        });
        btnModoActualizar.addActionListener(e -> {
            limpiarCampos();
            crearFormularioActualizar();
        });

        barraNavegacion.add(btnModoRegistro);
        barraNavegacion.add(btnModoActualizar);
        contenedorFondo.add(barraNavegacion, BorderLayout.NORTH);

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

    private void estilizarBotonNav(JButton btn) {
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
    }

    private void inicializarCampos() {
        txtNombre = new JTextField(); txtSegundoNombre = new JTextField();
        txtApellido = new JTextField(); txtSegundoApellido = new JTextField();
        txtCedula = new JTextField(); txtDireccion = new JTextField();
        txtTelefono = new JTextField(); txtEmail = new JTextField();
        comboGenero = new JComboBox<>(new String[]{"M", "F"});
    }

    private void limpiarCampos() {
        txtNombre.setText(""); txtSegundoNombre.setText("");
        txtApellido.setText(""); txtSegundoApellido.setText("");
        txtCedula.setText(""); txtDireccion.setText("");
        txtTelefono.setText(""); txtEmail.setText("");
        comboGenero.setSelectedIndex(0);
    }

    private void crearFormulario() {
        panelPrincipal.removeAll();
        int y = 40;

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

        y = agregarCamposFormulario(y + 60, true);

        JButton btnRegistrar = new JButton("Registrar Persona");
        btnRegistrar.setBounds(140, y, 220, 45);
        btnRegistrar.setBackground(new Color(34, 139, 34));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegistrar.addActionListener(e -> validarYProcesar(true));
        panelPrincipal.add(btnRegistrar);

        finalizarLayout(y + 80);
    }

    private void crearFormularioActualizar() {
        panelPrincipal.removeAll();
        int y = 40;

        JLabel lblHeader = new JLabel("Actualizar Datos", SwingConstants.CENTER);
        lblHeader.setForeground(new Color(59, 130, 246));
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setBounds(0, y, 500, 25);
        panelPrincipal.add(lblHeader);

        y += 50;
        JLabel lblBusqueda = new JLabel("Cédula a buscar:");
        lblBusqueda.setForeground(Color.WHITE);
        lblBusqueda.setBounds(80, y, 150, 20);
        panelPrincipal.add(lblBusqueda);

        txtCedula.setBounds(80, y + 25, 230, 35);
        panelPrincipal.add(txtCedula);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(320, y + 25, 100, 35);
        btnBuscar.setBackground(new Color(70, 70, 70));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.addActionListener(e -> buscarPersona());
        panelPrincipal.add(btnBuscar);

        y = agregarCamposFormulario(y + 90, false);

        JButton btnActualizar = new JButton("Actualizar Persona");
        btnActualizar.setBounds(140, y, 220, 45);
        btnActualizar.setBackground(new Color(0, 102, 204));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 15));
        btnActualizar.addActionListener(e -> validarYProcesar(false));
        panelPrincipal.add(btnActualizar);

        finalizarLayout(y + 80);
    }

    private int agregarCamposFormulario(int y, boolean esRegistro) {
        errNombre = crearEtiquetaError(y + 35);
        AggCAP(txtNombre, "Primer Nombre", y, errNombre); y += 85;

        errSegundoNombre = crearEtiquetaError(y + 35);
        AggCAP(txtSegundoNombre, "Segundo Nombre", y, errSegundoNombre); y += 85;

        errApellido = crearEtiquetaError(y + 35);
        AggCAP(txtApellido, "Primer Apellido", y, errApellido); y += 85;

        errSegundoApellido = crearEtiquetaError(y + 35);
        AggCAP(txtSegundoApellido, "Segundo Apellido", y, errSegundoApellido); y += 85;

        if (esRegistro) {
            errCedula = crearEtiquetaError(y + 35);
            AggCAP(txtCedula, "Cédula", y, errCedula); y += 85;
        }

        errDireccion = crearEtiquetaError(y + 35);
        AggCAP(txtDireccion, "Dirección", y, errDireccion); y += 85;

        errTelefono = crearEtiquetaError(y + 35);
        AggCAP(txtTelefono, "Teléfono", y, errTelefono); y += 85;

        errEmail = crearEtiquetaError(y + 35);
        AggCAP(txtEmail, "Correo Electrónico", y, errEmail); y += 55;

        JLabel lblGen = new JLabel("Género:");
        lblGen.setForeground(new Color(150, 150, 150));
        lblGen.setBounds(80, y, 100, 25);
        panelPrincipal.add(lblGen);

        comboGenero.setBounds(140, y, 70, 25);
        panelPrincipal.add(comboGenero);

        return y + 60;
    }

    private void finalizarLayout(int totalY) {
        panelPrincipal.setPreferredSize(new Dimension(500, totalY));
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void buscarPersona() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            ImageIcon iconoWarning = cargarIcono("meowl_icon_warning.png", 50,50);
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una cédula para buscar.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            return;
        }

        Conectar conecta = new Conectar();
        String sql = "SELECT * FROM personas WHERE cedula = ?";
        try (java.sql.Connection con = conecta.getConexion();
             java.sql.PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, cedula);
            java.sql.ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre"));
                txtSegundoNombre.setText(rs.getString("segundo_nombre"));
                txtApellido.setText(rs.getString("apellido"));
                txtSegundoApellido.setText(rs.getString("segundo_apellido"));
                txtDireccion.setText(rs.getString("direccion"));
                txtTelefono.setText(rs.getString("telefono"));
                txtEmail.setText(rs.getString("email"));
                comboGenero.setSelectedItem(rs.getString("genero"));
                
            } else {
                ImageIcon iconoError = cargarIcono("meowl_icon_error.png", 50, 50);
                JOptionPane.showMessageDialog(this, "No se encontró registro con esa cédula.", "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void validarYProcesar(boolean esRegistro) {
        JLabel[] errores = {errNombre, errSegundoNombre, errApellido, errSegundoApellido, errCedula, errDireccion, errTelefono, errEmail};
        for (JLabel e : errores) if (e != null) e.setText("");

        boolean ok = true;
        String obligatorio = "Este campo es obligatorio";

        if (txtNombre.getText().trim().isEmpty()) { errNombre.setText(obligatorio); ok = false; }
        if (txtApellido.getText().trim().isEmpty()) { errApellido.setText(obligatorio); ok = false; }
        if (esRegistro && txtCedula.getText().trim().isEmpty()) { errCedula.setText(obligatorio); ok = false; }
        if (txtEmail.getText().trim().isEmpty()) { errEmail.setText(obligatorio); ok = false; }
        else if (!txtEmail.getText().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) { errEmail.setText("Email inválido."); ok = false; }

        if (ok) {
            mostrarConfirmacion(esRegistro);
        }
    }

    // --- REINSTALADA: TU LÓGICA DE CONFIRMACIÓN ORIGINAL ---
    private void mostrarConfirmacion(boolean esRegistro) {
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
        btnVolverEdit.addActionListener(e -> {
            if (esRegistro) crearFormulario();
            else crearFormularioActualizar();
        });
        panelPrincipal.add(btnVolverEdit);

        JButton btnConfirmar = new JButton(esRegistro ? "Confirmar Registro" : "Confirmar Actualización");
        btnConfirmar.setBounds(250, y, 200, 45);
        btnConfirmar.setBackground(new Color(0, 102, 204));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.addActionListener(e -> {
            if (esRegistro) guardarPersona();
            else ejecutarActualizacion();
        });
        panelPrincipal.add(btnConfirmar);

        finalizarLayout(y + 100);
    }

    private void ejecutarActualizacion() {
        Conectar conecta = new Conectar();
        String sql = "UPDATE personas SET nombre=?, segundo_nombre=?, apellido=?, segundo_apellido=?, direccion=?, genero=?, telefono=?, email=? WHERE cedula=?";

        try (java.sql.Connection con = conecta.getConexion();
             java.sql.PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtNombre.getText().trim());
            pst.setString(2, txtSegundoNombre.getText().trim());
            pst.setString(3, txtApellido.getText().trim());
            pst.setString(4, txtSegundoApellido.getText().trim());
            pst.setString(5, txtDireccion.getText().trim());
            pst.setString(6, (String) comboGenero.getSelectedItem());
            pst.setString(7, txtTelefono.getText().trim());
            pst.setString(8, txtEmail.getText().trim());
            pst.setString(9, txtCedula.getText().trim());

            if (pst.executeUpdate() > 0) {
                ImageIcon iconoAprobado = cargarIcono("meowl_icon_aprobado.png", 50, 50);
                JOptionPane.showMessageDialog(this, "¡DATOS ACTUALIZADOS!","APROBADO", JOptionPane.PLAIN_MESSAGE, iconoAprobado);
                limpiarCampos();
                crearFormulario(); 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void AggCAP(JTextField txt, String hint, int y, JLabel lblError) {
        JLabel lbl = new JLabel(hint);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setBounds(80, y - 25, 340, 20);
        panelPrincipal.add(lbl);

        txt.setBounds(80, y, 340, 35);
        txt.setBackground(new Color(45, 45, 45));
        txt.setForeground(Color.WHITE);
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

    private void guardarPersona() {
        Conectar conecta = new Conectar();
        String sqlCheck = "SELECT cedula, email, telefono FROM personas WHERE cedula = ? OR email = ? OR telefono = ?";
        String sqlInsert = "INSERT INTO personas (nombre, segundo_nombre, apellido, segundo_apellido, cedula, direccion, genero, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (java.sql.Connection con = conecta.getConexion()) {
            try (java.sql.PreparedStatement pstCheck = con.prepareStatement(sqlCheck)) {
                pstCheck.setString(1, txtCedula.getText().trim());
                pstCheck.setString(2, txtEmail.getText().trim());
                pstCheck.setString(3, txtTelefono.getText().trim());
                java.sql.ResultSet rs = pstCheck.executeQuery();

                if (rs.next()) {
                    String duplicado = "";
                    if (rs.getString("cedula").equals(txtCedula.getText().trim())) duplicado = "La Cédula";
                    else if (rs.getString("email").equalsIgnoreCase(txtEmail.getText().trim())) duplicado = "El Correo";
                    else if (rs.getString("telefono").equals(txtTelefono.getText().trim())) duplicado = "El Teléfono";
                    
                    ImageIcon iconoError = cargarIcono("meowl_icon_error.png", 50, 50);
                    JOptionPane.showMessageDialog(this, "¡ERROR!\n" + duplicado + " ya está registrado.", "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
                    crearFormulario();
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

                if (pst.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "¡REGISTRO EXITOSO!");
                    limpiarCampos();
                    crearFormulario();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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