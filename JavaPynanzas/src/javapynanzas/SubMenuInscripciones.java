package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;

public class SubMenuInscripciones extends JFrame {

    private JPanel panelPrincipal;
    private JTextField txtCedulaValidar, txtObservaciones;
    private JComboBox<Item> comboCursos; 
    private JComboBox<String> comboModalidad, comboMetodo;
    private JComboBox<Item> comboBancos; 
    private JLabel lblNombreAlumno, lblMontoCalculado, lblBancoTitulo;
    private String cedulaValidada = "";
    
    private ImageIcon iconoWarning = cargarIcono("meowl_icon_warning.png", 50, 50);
    private ImageIcon iconoError = cargarIcono("meowl_icon_error.png", 50, 50);

    class Item {
        int id;
        String nombre;
        double valorExtra;
        
        public Item(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Item(int id, String nombre, double valorExtra) {
            this.id = id;
            this.nombre = nombre;
            this.valorExtra = valorExtra;
        }

        @Override
        public String toString() {
            return nombre; 
        }
    }

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
                g2d.setColor(new Color(0, 0, 0, 180)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public SubMenuInscripciones() {
        setTitle("Pynanzas - Inscripción de Alumnos");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedorFondo = new PanelFondo();
        setContentPane(contenedorFondo);

        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        panelPrincipal.setBackground(new Color(30, 30, 30, 210));

        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.add(panelPrincipal);

        JScrollPane scrollPane = new JScrollPane(contenedorCentrado);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contenedorFondo.add(scrollPane, BorderLayout.CENTER);

        inicializarInterfazValidacion();
    }

    private void inicializarInterfazValidacion() {
        panelPrincipal.removeAll();
        int y = 40;

        JButton btnVolver = new JButton("<-");
        btnVolver.setBounds(15, 15, 45, 30);
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        panelPrincipal.add(btnVolver);

        JLabel lblHeader = new JLabel("Inscripción de Alumno", SwingConstants.CENTER);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setBounds(0, y, 500, 30);
        panelPrincipal.add(lblHeader);

        y += 60;
        JLabel lblCed = new JLabel("Cédula del Alumno:");
        lblCed.setForeground(new Color(180, 180, 180));
        lblCed.setBounds(80, y, 340, 20);
        panelPrincipal.add(lblCed);

        y += 25;
        txtCedulaValidar = new JTextField();
        txtCedulaValidar.setBounds(80, y, 340, 35);
        txtCedulaValidar.setBackground(new Color(45, 45, 45));
        txtCedulaValidar.setForeground(Color.WHITE);
        txtCedulaValidar.setCaretColor(Color.WHITE);
        txtCedulaValidar.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        
        txtCedulaValidar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!cedulaValidada.isEmpty()) {
                    cedulaValidada = ""; 
                    inicializarInterfazValidacion();
                    txtCedulaValidar.requestFocusInWindow(); 
                }
            }
        });
        panelPrincipal.add(txtCedulaValidar);

        y += 50;
        JButton btnValidar = new JButton("Validar Cédula");
        btnValidar.setBounds(150, y, 200, 40);
        btnValidar.setBackground(new Color(48, 84, 150));
        btnValidar.setForeground(Color.WHITE);
        btnValidar.setFont(new Font("Arial", Font.BOLD, 14));
        btnValidar.addActionListener(e -> validarCedulaBaseDatos());
        panelPrincipal.add(btnValidar);

        y += 60;
        lblNombreAlumno = new JLabel("", SwingConstants.CENTER);
        lblNombreAlumno.setForeground(Color.LIGHT_GRAY);
        lblNombreAlumno.setFont(new Font("Arial", Font.BOLD, 16));
        lblNombreAlumno.setBounds(0, y, 500, 25);
        panelPrincipal.add(lblNombreAlumno);

        panelPrincipal.setPreferredSize(new Dimension(500, 600));
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void validarCedulaBaseDatos() {
        String cedula = txtCedulaValidar.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una cédula.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            return;
        }

        Conectar conecta = new Conectar();
        String sql = "SELECT id_persona, nombre, segundo_nombre, apellido, segundo_apellido FROM personas WHERE cedula = ?";

        try (Connection con = conecta.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, cedula);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String nombreCompleto = String.format("%s %s %s %s", 
                    rs.getString("nombre"), 
                    rs.getString("segundo_nombre"), 
                    rs.getString("apellido"), 
                    rs.getString("segundo_apellido")).replaceAll("\\s+", " ").trim();
                
                lblNombreAlumno.setText(nombreCompleto);
                lblNombreAlumno.setForeground(Color.GREEN);
                cedulaValidada = cedula;
                mostrarFormularioCompleto();
            } else {
                JOptionPane.showMessageDialog(this, "Cédula no encontrada.", "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
                lblNombreAlumno.setText("Cédula no encontrada.");
                lblNombreAlumno.setForeground(Color.RED);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void mostrarFormularioCompleto() {
        
        // esto es para evitar la superposicion de elementos si se hacen cosas locas de user xD, osea limpia el formulario con respecto a los elementos que tiene no su contenido ojito
        Component[] componentes = panelPrincipal.getComponents();
        for (Component c : componentes) {
            if (c.getY() > lblNombreAlumno.getY()) {
                panelPrincipal.remove(c);
            }
        }
        
        int y = lblNombreAlumno.getY() + 40;
        int x = 80;

        JLabel lblCur = new JLabel("Curso disponible:");
        lblCur.setForeground(new Color(180, 180, 180));
        lblCur.setBounds(x, y, 340, 20);
        panelPrincipal.add(lblCur);

        y += 25;
        comboCursos = new JComboBox<>();
        comboCursos.addItem(new Item(0, "-----", 0));
        cargarCursos();
        comboCursos.setBounds(x, y, 340, 30);
        comboCursos.setBackground(new Color(45, 45, 45));
        comboCursos.setForeground(Color.WHITE);
        comboCursos.addActionListener(e -> actualizarMonto());
        panelPrincipal.add(comboCursos);

        y += 45;
        JLabel lblMod = new JLabel("Modalidad de Pago:");
        lblMod.setForeground(new Color(180, 180, 180));
        lblMod.setBounds(x, y, 340, 20);
        panelPrincipal.add(lblMod);

        y += 25;
        comboModalidad = new JComboBox<>(new String[]{"-----", "cuotas", "total"});
        comboModalidad.setBounds(x, y, 340, 30);
        comboModalidad.setBackground(new Color(45, 45, 45));
        comboModalidad.setForeground(Color.WHITE);
        comboModalidad.addActionListener(e -> actualizarMonto());
        panelPrincipal.add(comboModalidad);

        y += 40;
        lblMontoCalculado = new JLabel("");
        lblMontoCalculado.setForeground(Color.YELLOW);
        lblMontoCalculado.setFont(new Font("Arial", Font.ITALIC, 14));
        lblMontoCalculado.setBounds(x, y, 340, 25);
        panelPrincipal.add(lblMontoCalculado);

        y += 40;
        JLabel lblMet = new JLabel("Método de Pago:");
        lblMet.setForeground(new Color(180, 180, 180));
        lblMet.setBounds(x, y, 340, 20);
        panelPrincipal.add(lblMet);

        y += 25;
        comboMetodo = new JComboBox<>(new String[]{"-----", "efectivo", "transferencia"});
        comboMetodo.setBounds(x, y, 340, 30);
        comboMetodo.setBackground(new Color(45, 45, 45));
        comboMetodo.setForeground(Color.WHITE);
        comboMetodo.addActionListener(e -> mostrarBancosSiAplica());
        panelPrincipal.add(comboMetodo);

        y += 45;
        lblBancoTitulo = new JLabel("Banco:");
        lblBancoTitulo.setForeground(new Color(180, 180, 180));
        lblBancoTitulo.setBounds(x, y, 340, 20);
        lblBancoTitulo.setVisible(false);
        panelPrincipal.add(lblBancoTitulo);

        y += 25;
        comboBancos = new JComboBox<>();
        comboBancos.addItem(new Item(0, "-----"));
        cargarBancos();
        comboBancos.setBounds(x, y, 340, 30);
        comboBancos.setBackground(new Color(45, 45, 45));
        comboBancos.setForeground(Color.WHITE);
        comboBancos.setVisible(false);
        panelPrincipal.add(comboBancos);

        y += 45;
        JLabel lblObs = new JLabel("Observaciones (Opcional):");
        lblObs.setForeground(new Color(180, 180, 180));
        lblObs.setBounds(x, y, 340, 20);
        panelPrincipal.add(lblObs);

        y += 25;
        txtObservaciones = new JTextField();
        txtObservaciones.setBounds(x, y, 340, 35);
        txtObservaciones.setBackground(new Color(45, 45, 45));
        txtObservaciones.setForeground(Color.WHITE);
        txtObservaciones.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        panelPrincipal.add(txtObservaciones);

        y += 60;
        JButton btnRegistrar = new JButton("Inscribir Alumno");
        btnRegistrar.setBounds(140, y, 220, 45);
        btnRegistrar.setBackground(new Color(34, 139, 34));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegistrar.addActionListener(e -> ejecutarInscripcion());
        panelPrincipal.add(btnRegistrar);

        y += 80;
        panelPrincipal.setPreferredSize(new Dimension(500, y));
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void cargarCursos() {
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_curso, nombre, costo_curso FROM Cursos")) {
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int id = rs.getInt("id_curso");
                double costo = rs.getDouble("costo_curso");
                String textoLabel = nombre + " (Bs. " + String.format("%.2f", costo) + ")";
                comboCursos.addItem(new Item(id, textoLabel, costo));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void cargarBancos() {
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_banco, nombre FROM Bancos")) {
            while (rs.next()) {
                comboBancos.addItem(new Item(rs.getInt("id_banco"), rs.getString("nombre")));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private void actualizarMonto() {
        Item cursoSeleccionado = (Item) comboCursos.getSelectedItem();
        String modalidad = (String) comboModalidad.getSelectedItem();

        if (cursoSeleccionado == null || cursoSeleccionado.id == 0 || modalidad.equals("-----")) {
            lblMontoCalculado.setText("");
            return;
        }

        double costo = cursoSeleccionado.valorExtra;
        if (modalidad.equals("total")) {
            lblMontoCalculado.setText("Monto total a pagar: Bs. " + String.format("%.2f", costo));
        } else {
            lblMontoCalculado.setText("Monto de cuota 1 (de 3): Bs. " + String.format("%.2f", costo / 3));
        }
    }

    private void mostrarBancosSiAplica() {
        boolean esTransferencia = "transferencia".equals(comboMetodo.getSelectedItem());
        lblBancoTitulo.setVisible(esTransferencia);
        comboBancos.setVisible(esTransferencia);
    }

    private String generarReferenciaAleatoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        while (sb.length() < 8) {
            int index = (int) (rnd.nextFloat() * caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return sb.toString();
    }

    private void ejecutarInscripcion() {
        Item cursoItem = (Item) comboCursos.getSelectedItem();
        String modalidad = (String) comboModalidad.getSelectedItem();
        String metodo = (String) comboMetodo.getSelectedItem();
        String observaciones = txtObservaciones.getText().trim();
        
        if (cursoItem.id == 0 || modalidad.equals("-----") || metodo.equals("-----")) {
            JOptionPane.showMessageDialog(this, "Debe completar los campos obligatorios.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            return;
        }

        Integer idCurso = cursoItem.id;
        Double costoTotal = cursoItem.valorExtra;
        Integer idBanco = null;

        if (metodo.equals("transferencia")) {
            Item bancoItem = (Item) comboBancos.getSelectedItem();
            if (bancoItem.id == 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un banco para transferencias.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
                return;
            }
            idBanco = bancoItem.id;
        }

        Conectar conecta = new Conectar();
        Connection con = null;

        try {
            con = conecta.getConexion();
            con.setAutoCommit(false); 

            int idPersona = -1;
            String sqlPersona = "SELECT id_persona FROM personas WHERE cedula = ?";
            try (PreparedStatement pstP = con.prepareStatement(sqlPersona)) {
                pstP.setString(1, cedulaValidada);
                ResultSet rsP = pstP.executeQuery();
                if (rsP.next()) idPersona = rsP.getInt("id_persona");
            }

            String sqlCheck = "SELECT COUNT(*) FROM Inscripciones WHERE id_persona = ? AND id_curso = ?";
            try (PreparedStatement pstC = con.prepareStatement(sqlCheck)) {
                pstC.setInt(1, idPersona);
                pstC.setInt(2, idCurso);
                ResultSet rsC = pstC.executeQuery();
                if (rsC.next() && rsC.getInt(1) > 0) {
                    ImageIcon iconoInfo = cargarIcono("meowl_icon_info.png", 50, 50);
                    JOptionPane.showMessageDialog(this, "Esta persona ya está inscrita en este curso.", "INFO", JOptionPane.PLAIN_MESSAGE, iconoInfo);
                    con.rollback();
                    return;
                }
            }

            double montoPorCuota = Math.round((costoTotal / 3) * 100.0) / 100.0;
            String estado = modalidad.equals("total") ? "finalizado" : "activa";
            double saldoRestante = modalidad.equals("total") ? 0.0 : Math.round((costoTotal - montoPorCuota) * 100.0) / 100.0;
            String fechaActual = java.time.LocalDate.now().toString();

            String sqlIns = "INSERT INTO Inscripciones (id_persona, id_curso, fecha_inscripcion, modalidad_pago, estado, saldo_restante) VALUES (?, ?, ?, ?, ?, ?)";
            int idInscripcion = -1;
            try (PreparedStatement pstI = con.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                pstI.setInt(1, idPersona);
                pstI.setInt(2, idCurso);
                pstI.setString(3, fechaActual);
                pstI.setString(4, modalidad);
                pstI.setString(5, estado);
                pstI.setDouble(6, saldoRestante);
                pstI.executeUpdate();
                ResultSet rsKeys = pstI.getGeneratedKeys();
                if (rsKeys.next()) idInscripcion = rsKeys.getInt(1);
            }

            String nroReferencia = null;
            if (metodo.equals("transferencia")) {
                nroReferencia = generarReferenciaAleatoria();
            }
            double montoPagado = modalidad.equals("total") ? costoTotal : montoPorCuota;
            Integer nroCuota = modalidad.equals("total") ? null : 1;

            String sqlPago = "INSERT INTO Pagos (id_inscripcion, fecha_pago, monto_pagado, nro_cuota, metodo_pago, id_banco, observaciones, nro_referencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstPago = con.prepareStatement(sqlPago)) {
                pstPago.setInt(1, idInscripcion);
                pstPago.setString(2, fechaActual);
                pstPago.setDouble(3, montoPagado);
                if (nroCuota == null) pstPago.setNull(4, Types.INTEGER); else pstPago.setInt(4, nroCuota);
                pstPago.setString(5, metodo);
                if (idBanco == null) pstPago.setNull(6, Types.INTEGER); else pstPago.setInt(6, idBanco);
                pstPago.setString(7, observaciones);
                pstPago.setString(8, nroReferencia);
                pstPago.executeUpdate();
            }

            con.commit(); 
            
            String nroCuotaFinal = (nroCuota == null) ? "Único" : nroCuota.toString();
            String nombreBanco = metodo.equals("transferencia") ? comboBancos.getSelectedItem().toString() : "No aplica";
            
            GenPdf.generarReciboPago(
                idInscripcion, 
                nroCuotaFinal, 
                lblNombreAlumno.getText(), 
                cedulaValidada, 
                cursoItem.nombre, 
                modalidad, 
                montoPagado, 
                metodo, 
                nombreBanco, 
                fechaActual, 
                observaciones, 
                nroReferencia
            );
            ImageIcon iconoNice = cargarIcono("meowl_icon_aprobado.png", 50, 50);
            JOptionPane.showMessageDialog(this, "¡INSCRIPCIÓN EXITOSA!", "REGISTRO EXITOSO", JOptionPane.PLAIN_MESSAGE, iconoNice);
            this.dispose();
            new MenuRegistro().setVisible(true);

        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) {}
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