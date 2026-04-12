package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javapynanzas.JavaPynanzas.PanelFondo;
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
    
    
    // Variables dinámicas
    private double costoCuotaIndividual = 0; 
    private double saldoActualCurso = 0;

        class ItemBanco{
        int id;
        String nombre;
        double valorExtra;
        
        public ItemBanco(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public ItemBanco(int id, String nombre, double valorExtra) {
            this.id = id;
            this.nombre = nombre;
            this.valorExtra = valorExtra;
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
                // Botón Volver al Menú Principal
        JButton btnVolver = new JButton("←");
        btnVolver.setBounds(15, 15, 45, 30);
        btnVolver.setBackground(new Color(60, 60, 60));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.addActionListener(e -> {
            new MenuRegistro().setVisible(true);
            this.dispose();
        });
        add(btnVolver);
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        panelPrincipal.setBackground(new Color(35, 35, 35, 220));
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

        String sql = "SELECT p.nombre, p.apellido, i.id_inscripcion, c.nombre as curso, " +
                     "i.saldo_restante, c.costo_curso " + // Seguimos pidiendo los campos igual
                     "FROM personas p " +
                     "JOIN Inscripciones i ON p.id_persona = i.id_persona " +
                     "JOIN Cursos c ON i.id_curso = c.id_curso " +
                     "WHERE p.cedula = ? AND i.saldo_restante > 0";

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
            String saldoStr = rs.getString("saldo_restante");
            String costoTotalStr = rs.getString("costo_curso");

            double saldoDbl = (saldoStr != null) ? Double.parseDouble(saldoStr.replace(",", ".")) : 0;
            double costoTotalDbl = (costoTotalStr != null) ? Double.parseDouble(costoTotalStr.replace(",", ".")) : 0;

            comboCursos.addItem(new ItemCurso(
                rs.getInt("id_inscripcion"), 
                rs.getString("curso"), 
                saldoDbl,
                costoTotalDbl
            ));
        } while (rs.next());
    
    comboCursos.setBounds(100, 80, 400, 30);
    comboCursos.addActionListener(e -> recalcularLogicaCuotas());
    panelPrincipal.add(comboCursos);

    // Label indicativo de cuotas
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
    lblMontoRequerido.setForeground(new Color(144, 238, 144)); // Verde para resaltar el monto
    lblMontoRequerido.setFont(new Font("Arial", Font.BOLD, 16));
    lblMontoRequerido.setBounds(0, 200, 600, 25);
    panelPrincipal.add(lblMontoRequerido);

    // Selector de método
    JLabel lblMetodo = new JLabel("Método de pago:", SwingConstants.CENTER);
    lblMetodo.setForeground(Color.GRAY);
    lblMetodo.setBounds(0, 240, 600, 20);
    panelPrincipal.add(lblMetodo);

    comboMetodo = new JComboBox<>(new String[]{"efectivo", "transferencia"});
    comboMetodo.setBounds(225, 265, 150, 30);
    panelPrincipal.add(comboMetodo);

    // Elementos de Banco (Inicialmente ocultos)
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

    // Observaciones y Botón
    JLabel lblObs = new JLabel("Observaciones:", SwingConstants.CENTER);
    lblObs.setForeground(Color.GRAY);
    lblObs.setBounds(100, 310, 400, 20); // Posición inicial para "efectivo"
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

    // Lógica de movimiento dinámico
    comboMetodo.addActionListener(e -> {
        boolean esTransferencia = comboMetodo.getSelectedItem().equals("transferencia");
        
        // Mostrar/Ocultar banco
        lblBancos.setVisible(esTransferencia);
        comboBancos.setVisible(esTransferencia);
        
        if (esTransferencia) {
            // Empujar hacia abajo las observaciones y el botón
            lblObs.setBounds(100, 390, 400, 20);
            txtObservaciones.setBounds(100, 415, 400, 35);
            btnRegistrar.setBounds(225, 465, 150, 40);
        } else {
            // Regresar a la posición original (subir)
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
            // Cálculo dinámico: El costo de cada cuota es el costo total / 3
            costoCuotaIndividual = selected.costoTotalCurso / 3;

            comboCuotas.removeAllItems();
            // Determinamos cuántas cuotas puede pagar según el saldo que le queda
            int maxCuotasPosibles = (int) Math.round(saldoActualCurso / costoCuotaIndividual);
            
            for (int i = 1; i <= maxCuotasPosibles; i++) {
                comboCuotas.addItem(i);
            }
        }
    }

    private void actualizarEtiquetaMonto() {
        if (comboCuotas.getSelectedItem() != null) {
            int cantidad = (int) comboCuotas.getSelectedItem();
            double total = cantidad * costoCuotaIndividual;
            lblMontoRequerido.setText("Monto total a pagar: Bs. " + String.format(java.util.Locale.US, "%.2f", total));
        }
    }

    private void ejecutarPago() {
        ItemCurso item = (ItemCurso) comboCursos.getSelectedItem();
        int cantCuotas = (int) comboCuotas.getSelectedItem();

        double montoAPagar = Math.round((cantCuotas * costoCuotaIndividual) * 100.0) / 100.0;
        double nuevoSaldo = Math.round((item.saldoRestante - montoAPagar) * 100.0) / 100.0;

        if (nuevoSaldo < 0.01) {
            nuevoSaldo = 0.00;
        }

        String metodo = (String) comboMetodo.getSelectedItem();
        ItemBanco bancoSeleccionado = (ItemBanco) comboBancos.getSelectedItem();

        if (metodo.equals("transferencia") && (bancoSeleccionado == null || bancoSeleccionado.id == 0)) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un banco para la transferencia.", "WARNING", JOptionPane.PLAIN_MESSAGE, iconoWarning);
            return;
        }

        Integer idBanco = (metodo.equals("transferencia")) ? bancoSeleccionado.id : null;

        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion()) {
            con.setAutoCommit(false);

            String nroReferencia = metodo.equals("transferencia") ? generarReferenciaAleatoria() : "No aplica";

            // 1. Insertar el Pago
            String sqlPago = "INSERT INTO Pagos (id_inscripcion, fecha_pago, monto_pagado, id_banco, nro_cuota, metodo_pago, observaciones, nro_referencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstP = con.prepareStatement(sqlPago)) {
                pstP.setInt(1, item.idInscripcion);
                pstP.setString(2, java.time.LocalDate.now().toString());
                pstP.setDouble(3, montoAPagar); // JDBC envía el double correctamente
                if (idBanco == null) pstP.setNull(4, Types.INTEGER); else pstP.setInt(4, idBanco);
                pstP.setInt(5, cantCuotas); 
                pstP.setString(6, metodo);
                pstP.setString(7, txtObservaciones.getText());
                pstP.setString(8, nroReferencia);
                pstP.executeUpdate();
            }

            // 2. ACTUALIZAR INSCRIPCIÓN (El saldo se guarda sin problemas)
            String sqlIns = "UPDATE Inscripciones SET saldo_restante = ?, estado = ? WHERE id_inscripcion = ?";
            try (PreparedStatement pstI = con.prepareStatement(sqlIns)) {
                pstI.setDouble(1, nuevoSaldo); // JDBC maneja el formato decimal aquí
                pstI.setString(2, (nuevoSaldo <= 0.05) ? "finalizado" : "activa");
                pstI.setInt(3, item.idInscripcion);
                pstI.executeUpdate();
            }

            con.commit(); // Todo bien, guardamos cambios

            // Generar PDF y mostrar resumen
            String bancoNombre = (idBanco != null) ? bancoSeleccionado.nombre : "No aplica";
            GenPdf.generarReciboPago(item.idInscripcion, String.valueOf(cantCuotas), lblNombreAlumno.getText(), txtCedula.getText(), item.nombreCurso, metodo, montoAPagar, metodo, bancoNombre, java.time.LocalDate.now().toString(), txtObservaciones.getText(), nroReferencia);

            mostrarResumenFinal(cantCuotas, montoAPagar, nuevoSaldo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar: " + e.getMessage(), "ERROR", JOptionPane.PLAIN_MESSAGE, iconoError);
        }
    }

    private void mostrarResumenFinal(int cuotas, double monto, double saldoFinal) {
        panelPrincipal.removeAll();
        int y = 180;

        JLabel lblRes = new JLabel("¡Pago Exitoso!", SwingConstants.CENTER);
        lblRes.setForeground(new Color(144, 238, 144));
        lblRes.setFont(new Font("Arial", Font.BOLD, 20));
        lblRes.setBounds(0, y, 600, 30);
        panelPrincipal.add(lblRes);

        y += 40;
        JLabel lblDetalle = new JLabel(String.format("Cuotas: %d | Total: Bs. %.2f | Pendiente: Bs. %.2f", cuotas, monto, saldoFinal), SwingConstants.CENTER);
        lblDetalle.setForeground(Color.WHITE);
        lblDetalle.setBounds(0, y, 600, 30);
        panelPrincipal.add(lblDetalle);

        y += 60;
        JButton btnBack = new JButton("<- Retroceder");
        btnBack.setBounds(200, y, 200, 40);
        btnBack.setBackground(new Color(60, 60, 60));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(e -> inicializarBusqueda());
        panelPrincipal.add(btnBack);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }
        private void cargarBancos() {
        Conectar conecta = new Conectar();
        try (Connection con = conecta.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_banco, nombre FROM Bancos")) {
            while (rs.next()) {
                comboBancos.addItem(new ItemBanco(rs.getInt("id_banco"), rs.getString("nombre")));
                }
            } catch (SQLException e) { System.err.println(e.getMessage()); }
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