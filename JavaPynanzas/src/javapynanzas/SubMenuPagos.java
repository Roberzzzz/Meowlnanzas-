package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;

public class SubMenuPagos extends JFrame {

    private JPanel panelPrincipal;
    private JTextField txtCedula, txtObservaciones;
    private JComboBox<ItemCurso> comboCursos;
    private JComboBox<Integer> comboCuotas;
    private JComboBox<String> comboMetodo;
    private JComboBox<ItemBanco> comboBancos;
    private JLabel lblNombreAlumno, lblMontoRequerido, lblBancos;
    private ImageIcon iconoWarning = cargarIcono("meowl_icon_warning.png", 50, 50);
    private ImageIcon iconoError = cargarIcono("meowl_icon_error.png", 50, 50);
    private double costoCuotaIndividual = 0;
    private double saldoActualCurso = 0;

    class ItemBanco {
        int id;
        String nombre;
        double valorExtra;

        public ItemBanco(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    class ItemCurso {
        int idInscripcion;
        String nombreCurso;
        double saldoRestante;
        double costoTotalCurso;

        public ItemCurso(int id, String nombre, double saldo, double costoTotal) {
            this.idInscripcion = id;
            this.nombreCurso = nombre;
            this.saldoRestante = saldo;
            this.costoTotalCurso = costoTotal;
        }

        @Override
        public String toString() {
            return nombreCurso + " (Pendiente: Bs. " + String.format(java.util.Locale.US, "%.2f", saldoRestante) + ")";
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
                g2d.setColor(new Color(0, 0, 0, 190));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public SubMenuPagos() {
        setTitle("Pynanzas - Registro de Pagos");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedorFondo = new PanelFondo();
        setContentPane(contenedorFondo);

        JButton btnVolver = new JButton("←");
        btnVolver.setBounds(15, 15, 45, 30);
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> {
            new MenuRegistro().setVisible(true);
            this.dispose();
        });
        add(btnVolver);

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
        panelPrincipal.setBackground(new Color(35, 35, 35, 220));
        panelPrincipal.setOpaque(false);

        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.add(panelPrincipal);
        contenedorFondo.add(contenedorCentrado, BorderLayout.CENTER);

        inicializarBusqueda();
    }

    private void inicializarBusqueda() {
        panelPrincipal.removeAll();
        panelPrincipal.setPreferredSize(new Dimension(600, 500));

        JLabel lblTitulo = new JLabel("Pago de Cuotas", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(255, 204, 102));
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBounds(0, 40, 600, 30);
        panelPrincipal.add(lblTitulo);

        txtCedula = new JTextField();
        txtCedula.setBounds(150, 130, 300, 35);
        txtCedula.setBackground(new Color(50, 50, 50));
        txtCedula.setForeground(Color.WHITE);
        txtCedula.setHorizontalAlignment(JTextField.CENTER);
        panelPrincipal.add(txtCedula);

        JButton btnConsultar = new JButton("Consultar inscripciones");
        btnConsultar.setBounds(200, 180, 200, 35);
        btnConsultar.setBackground(new Color(48, 84, 150));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.addActionListener(e -> buscarAlumno());
        panelPrincipal.add(btnConsultar);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void buscarAlumno() {
        String cedula = txtCedula.getText().trim();
        Conectar conecta = new Conectar();
        String sql = "SELECT p.nombre, p.apellido, i.id_inscripcion, c.nombre as curso, i.saldo_restante, c.costo_curso " +
                     "FROM personas p JOIN Inscripciones i ON p.id_persona = i.id_persona " +
                     "JOIN Cursos c ON i.id_curso = c.id_curso WHERE p.cedula = ? AND i.saldo_restante > 0";

        try (Connection con = conecta.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, cedula);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                mostrarFormularioPago(rs);
            } else {
                JOptionPane.showMessageDialog(this, "No hay inscripciones pendientes.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.PLAIN_MESSAGE, iconoError);
        }
    }

    private void mostrarFormularioPago(ResultSet rs) throws SQLException {
        String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
        panelPrincipal.removeAll();

        lblNombreAlumno = new JLabel(nombreCompleto, SwingConstants.CENTER);
        lblNombreAlumno.setForeground(Color.WHITE);
        lblNombreAlumno.setFont(new Font("Arial", Font.BOLD, 18));
        lblNombreAlumno.setBounds(0, 30, 600, 25);
        panelPrincipal.add(lblNombreAlumno);

        comboCursos = new JComboBox<>();
        do {
            String sRaw = rs.getString("saldo_restante");
            String cRaw = rs.getString("costo_curso");
            double sDbl = (sRaw != null) ? Double.parseDouble(sRaw.replace(",", ".")) : 0;
            double cDbl = (cRaw != null) ? Double.parseDouble(cRaw.replace(",", ".")) : 0;
            comboCursos.addItem(new ItemCurso(rs.getInt("id_inscripcion"), rs.getString("curso"), sDbl, cDbl));
        } while (rs.next());

        comboCursos.setBounds(100, 80, 400, 30);
        comboCursos.addActionListener(e -> recalcularLogicaCuotas());
        panelPrincipal.add(comboCursos);

        JLabel lblCuotas = new JLabel("Cantidad de cuotas a pagar:", SwingConstants.CENTER);
        lblCuotas.setForeground(Color.GRAY);
        lblCuotas.setBounds(0, 130, 600, 20);
        panelPrincipal.add(lblCuotas);

        comboCuotas = new JComboBox<>();
        comboCuotas.setBounds(250, 155, 100, 30);
        comboCuotas.setBackground(new Color(45, 45, 45));
        comboCuotas.setForeground(Color.WHITE);
        comboCuotas.addActionListener(e -> actualizarEtiquetaMonto());
        panelPrincipal.add(comboCuotas);

        lblMontoRequerido = new JLabel("Monto total: Bs. 0.00", SwingConstants.CENTER);
        lblMontoRequerido.setForeground(new Color(144, 238, 144));
        lblMontoRequerido.setFont(new Font("Arial", Font.BOLD, 16));
        lblMontoRequerido.setBounds(0, 200, 600, 25);
        panelPrincipal.add(lblMontoRequerido);

        JLabel lblMetodo = new JLabel("Método de pago:", SwingConstants.CENTER);
        lblMetodo.setForeground(Color.GRAY);
        lblMetodo.setBounds(0, 240, 600, 20);
        panelPrincipal.add(lblMetodo);

        comboMetodo = new JComboBox<>(new String[]{"efectivo", "transferencia"});
        comboMetodo.setBounds(225, 265, 150, 30);
        panelPrincipal.add(comboMetodo);

        lblBancos = new JLabel("Seleccione el banco de destino:", SwingConstants.CENTER);
        lblBancos.setForeground(new Color(255, 204, 102));
        lblBancos.setBounds(0, 310, 600, 25);
        lblBancos.setVisible(false);
        panelPrincipal.add(lblBancos);

        comboBancos = new JComboBox<>();
        comboBancos.addItem(new ItemBanco(0, "----- Seleccione Banco -----"));
        cargarBancos();
        comboBancos.setBounds(150, 340, 300, 35);
        comboBancos.setBackground(new Color(45, 45, 45));
        comboBancos.setForeground(Color.WHITE);
        comboBancos.setVisible(false);
        panelPrincipal.add(comboBancos);

        JLabel lblObs = new JLabel("Observaciones:", SwingConstants.CENTER);
        lblObs.setForeground(Color.GRAY);
        lblObs.setBounds(100, 310, 400, 20);
        panelPrincipal.add(lblObs);

        txtObservaciones = new JTextField("Sin observaciones");
        txtObservaciones.setBounds(100, 335, 400, 35);
        txtObservaciones.setBackground(new Color(45, 45, 45));
        txtObservaciones.setForeground(Color.WHITE);
        panelPrincipal.add(txtObservaciones);

        JButton btnRegistrar = new JButton("Registrar pago");
        btnRegistrar.setBounds(225, 390, 150, 40);
        btnRegistrar.setBackground(new Color(48, 84, 150));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.addActionListener(e -> ejecutarPago());
        panelPrincipal.add(btnRegistrar);

        comboMetodo.addActionListener(e -> {
            boolean esT = comboMetodo.getSelectedItem().equals("transferencia");
            lblBancos.setVisible(esT);
            comboBancos.setVisible(esT);
            if (esT) {
                lblObs.setBounds(100, 390, 400, 20);
                txtObservaciones.setBounds(100, 415, 400, 35);
                btnRegistrar.setBounds(225, 465, 150, 40);
            } else {
                lblObs.setBounds(100, 310, 400, 20);
                txtObservaciones.setBounds(100, 335, 400, 35);
                btnRegistrar.setBounds(225, 390, 150, 40);
            }
            panelPrincipal.revalidate();
            panelPrincipal.repaint();
        });

        recalcularLogicaCuotas();
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void recalcularLogicaCuotas() {
        ItemCurso selected = (ItemCurso) comboCursos.getSelectedItem();
        if (selected != null) {
            saldoActualCurso = selected.saldoRestante;
            costoCuotaIndividual = selected.costoTotalCurso / 3;
            comboCuotas.removeAllItems();
            int maxC = (int) Math.round(saldoActualCurso / costoCuotaIndividual);
            for (int i = 1; i <= maxC; i++) comboCuotas.addItem(i);
        }
    }

    private void actualizarEtiquetaMonto() {
        if (comboCuotas.getSelectedItem() != null) {
            int q = (int) comboCuotas.getSelectedItem();
            double t = q * costoCuotaIndividual;
            lblMontoRequerido.setText("Monto total a pagar: Bs. " + String.format(java.util.Locale.US, "%.2f", t));
        }
    }

    private void ejecutarPago() {
        ItemCurso item = (ItemCurso) comboCursos.getSelectedItem();
        if (comboCuotas.getSelectedItem() == null) return;
        int cant = (int) comboCuotas.getSelectedItem();
        double monto = Math.round((cant * costoCuotaIndividual) * 100.0) / 100.0;
        double nSaldo = Math.round((item.saldoRestante - monto) * 100.0) / 100.0;
        if (nSaldo < 0.01) nSaldo = 0.00;

        String met = (String) comboMetodo.getSelectedItem();
        ItemBanco bSel = (ItemBanco) comboBancos.getSelectedItem();
        if (met.equals("transferencia") && (bSel == null || bSel.id == 0)) {
            JOptionPane.showMessageDialog(this, "Seleccione un banco.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            return;
        }

        Integer idB = (met.equals("transferencia")) ? bSel.id : null;
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion()) {
            con.setAutoCommit(false);
            String ref = met.equals("transferencia") ? generarReferenciaAleatoria() : "No aplica";
            String sqlP = "INSERT INTO Pagos (id_inscripcion, fecha_pago, monto_pagado, id_banco, nro_cuota, metodo_pago, observaciones, nro_referencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstP = con.prepareStatement(sqlP)) {
                pstP.setInt(1, item.idInscripcion);
                pstP.setString(2, java.time.LocalDate.now().toString());
                pstP.setDouble(3, monto);
                if (idB == null) pstP.setNull(4, Types.INTEGER); else pstP.setInt(4, idB);
                pstP.setInt(5, cant);
                pstP.setString(6, met);
                pstP.setString(7, txtObservaciones.getText());
                pstP.setString(8, ref);
                pstP.executeUpdate();
            }
            String sqlI = "UPDATE Inscripciones SET saldo_restante = ?, estado = ? WHERE id_inscripcion = ?";
            try (PreparedStatement pstI = con.prepareStatement(sqlI)) {
                pstI.setDouble(1, nSaldo);
                pstI.setString(2, (nSaldo <= 0.05) ? "finalizado" : "activa");
                pstI.setInt(3, item.idInscripcion);
                pstI.executeUpdate();
            }
            con.commit();
            String bNm = (idB != null) ? bSel.nombre : "No aplica";
            GenPdf.generarReciboPago(item.idInscripcion, String.valueOf(cant), lblNombreAlumno.getText(), txtCedula.getText(), item.nombreCurso, met, monto, met, bNm, java.time.LocalDate.now().toString(), txtObservaciones.getText(), ref);
            mostrarResumenFinal(cant, monto, nSaldo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
        }
    }

    private void mostrarResumenFinal(int c, double m, double s) {
        panelPrincipal.removeAll();
        int y = 180;
        JLabel lR = new JLabel("¡Pago Exitoso!", SwingConstants.CENTER);
        lR.setForeground(new Color(144, 238, 144));
        lR.setFont(new Font("Arial", Font.BOLD, 20));
        lR.setBounds(0, y, 600, 30);
        panelPrincipal.add(lR);
        y += 40;
        JLabel lD = new JLabel(String.format(java.util.Locale.US, "Cuotas: %d | Total: Bs. %.2f | Pendiente: Bs. %.2f", c, m, s), SwingConstants.CENTER);
        lD.setForeground(Color.WHITE);
        lD.setBounds(0, y, 600, 30);
        panelPrincipal.add(lD);
        y += 60;
        JButton bB = new JButton("<- Retroceder");
        bB.setBounds(200, y, 200, 40);
        bB.setBackground(new Color(60, 60, 60));
        bB.setForeground(Color.WHITE);
        bB.addActionListener(e -> inicializarBusqueda());
        panelPrincipal.add(bB);
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private void cargarBancos() {
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_banco, nombre FROM Bancos")) {
            while (rs.next()) comboBancos.addItem(new ItemBanco(rs.getInt("id_banco"), rs.getString("nombre")));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    private String generarReferenciaAleatoria() {
        String c = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random r = new java.util.Random();
        while (sb.length() < 8) sb.append(c.charAt(r.nextInt(c.length())));
        return sb.toString();
    }

    private ImageIcon cargarIcono(String n, int w, int h) {
        try {
            return new ImageIcon(new ImageIcon("resources/" + n).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }
}