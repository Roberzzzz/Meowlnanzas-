package javapynanzas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubMenuConsultasPersonas extends JFrame {

    private JTextField txtDato;
    private JComboBox<String> cbFiltro;
    private JComboBox<String> cbResultados;
    private JPanel pnlContenedorTablas;
    private JLabel lblNombrePersona;
    private JTextArea txtSaldoPendiente;

    class PanelFondo extends JPanel {
        private Image imagen;
        public PanelFondo() {
            try {
                imagen = ImageIO.read(new File("resources/meowl_login.jpg"));
            } catch (Exception e) {
                setBackground(new Color(26, 26, 26)); 
            }
            setLayout(new BorderLayout()); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 190)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }
    }

    public SubMenuConsultasPersonas() {
        setTitle("Pynanzas - Historial Detallado");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedorPrincipal = new PanelFondo();

        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        pnlNorte.setOpaque(false);
        
        JButton btnRegresar = new JButton("← Volver");
        estilizarBotonSecundario(btnRegresar);
        btnRegresar.addActionListener(e -> {
            MenuConsultas menu = new MenuConsultas();
            menu.setLocation(this.getLocation());
            menu.setVisible(true);
            this.dispose();
        });
        
        JLabel lblBuscarPor = new JLabel("Buscar Por:");
        lblBuscarPor.setFont(new Font("Arial", Font.BOLD, 14));
        lblBuscarPor.setForeground(Color.WHITE);

        cbFiltro = new JComboBox<>(new String[]{"Cédula", "Nombre", "Apellido"});
        cbFiltro.setBackground(new Color(45, 45, 45));
        cbFiltro.setForeground(Color.WHITE);
        cbFiltro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(59, 130, 246) : new Color(35, 35, 35));
                setForeground(Color.WHITE);
                return this;
            }
        });
        cbFiltro.addActionListener(e -> {
            txtDato.setText("");
            cbResultados.removeAllItems();
            pnlContenedorTablas.removeAll();
            txtSaldoPendiente.setText("");
            lblNombrePersona.setText("  Busque una persona y presione Consultar");
            pnlContenedorTablas.revalidate();
            pnlContenedorTablas.repaint();
        });
        
        txtDato = new JTextField(12);
        estilizarInput(txtDato);
        txtDato.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarDinamico();
            }
        });

        cbResultados = new JComboBox<>();
        cbResultados.setPreferredSize(new Dimension(200, 30));
        cbResultados.setBackground(new Color(45, 45, 45));
        cbResultados.setForeground(Color.WHITE);
        cbResultados.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(59, 130, 246) : new Color(35, 35, 35));
                setForeground(Color.WHITE);
                return this;
            }
        });

        JButton btnBuscar = new JButton("Consultar Estado");
        estilizarBotonPrincipal(btnBuscar);
        btnBuscar.addActionListener(e -> ejecutarConsulta());
        
        pnlNorte.add(btnRegresar);
        pnlNorte.add(lblBuscarPor);
        pnlNorte.add(cbFiltro);
        pnlNorte.add(txtDato);
        pnlNorte.add(cbResultados);
        pnlNorte.add(btnBuscar);

        pnlContenedorTablas = new JPanel();
        pnlContenedorTablas.setLayout(new BoxLayout(pnlContenedorTablas, BoxLayout.Y_AXIS));
        pnlContenedorTablas.setOpaque(false);

        JScrollPane scrollPrincipal = new JScrollPane(pnlContenedorTablas);
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getViewport().setOpaque(false);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);

        lblNombrePersona = new JLabel("  Busque una persona y presione Consultar", JLabel.LEFT);
        lblNombrePersona.setFont(new Font("Arial", Font.BOLD, 18));
        lblNombrePersona.setForeground(new Color(59, 130, 246)); 
        lblNombrePersona.setPreferredSize(new Dimension(900, 50)); 

        JPanel pnlCentroMaster = new JPanel(new BorderLayout());
        pnlCentroMaster.setOpaque(false);
        pnlCentroMaster.add(lblNombrePersona, BorderLayout.NORTH);
        pnlCentroMaster.add(scrollPrincipal, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setOpaque(false);
        pnlSur.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)), 
                "Resumen Financiero Total", 0, 0, new Font("Arial", Font.BOLD, 12), Color.LIGHT_GRAY));

        txtSaldoPendiente = new JTextArea(3, 30) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        txtSaldoPendiente.setOpaque(false); 
        txtSaldoPendiente.setEditable(false);
        txtSaldoPendiente.setBackground(new Color(20, 20, 20, 180));
        txtSaldoPendiente.setForeground(Color.WHITE);
        txtSaldoPendiente.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtSaldoPendiente.setMargin(new Insets(10, 10, 10, 10));

        pnlSur.add(new JScrollPane(txtSaldoPendiente), BorderLayout.CENTER);

        contenedorPrincipal.add(pnlNorte, BorderLayout.NORTH);
        contenedorPrincipal.add(pnlCentroMaster, BorderLayout.CENTER);
        contenedorPrincipal.add(pnlSur, BorderLayout.SOUTH);
        
        add(contenedorPrincipal);
    }

    private void buscarDinamico() {
        String valor = txtDato.getText().trim();
        if (valor.isEmpty()) {
            cbResultados.removeAllItems();
            return;
        }

        String columna = cbFiltro.getSelectedItem().toString().toLowerCase().replace("é", "e");
        Conectar conecta = new Conectar();
        Connection conn = conecta.getConexion();
        
        try {
            String sql = "SELECT cedula, nombre, apellido FROM Personas WHERE UPPER(" + columna + ") LIKE UPPER('" + valor + "%')";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            cbResultados.removeAllItems();
            while (rs.next()) {
                cbResultados.addItem(rs.getString("cedula") + " - " + rs.getString("nombre") + " " + rs.getString("apellido"));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void ejecutarConsulta() {
        if (cbResultados.getSelectedItem() == null) return;
        
        String seleccion = cbResultados.getSelectedItem().toString();
        String cedula = seleccion.split(" - ")[0];

        pnlContenedorTablas.removeAll();
        txtSaldoPendiente.setText("");
        
        Conectar conecta = new Conectar();
        Connection conn = conecta.getConexion();
        if (conn == null) return;

        try {
            Statement stmt = conn.createStatement();
            
            String sqlPer = "SELECT nombre || ' ' || apellido FROM Personas WHERE cedula = '" + cedula + "'";
            ResultSet rs1 = stmt.executeQuery(sqlPer);
            if (!rs1.next()) {
                lblNombrePersona.setText("  Persona no encontrada");
                pnlContenedorTablas.revalidate(); pnlContenedorTablas.repaint(); return;
            }
            lblNombrePersona.setText("  Historial de: " + rs1.getString(1));

            String sqlPagos = "SELECT c.nombre as curso, p.fecha_pago, p.monto_pagado, p.nro_cuota, " +
                              "p.metodo_pago, b.nombre as banco, i.saldo_restante " +
                              "FROM Pagos p " +
                              "JOIN Inscripciones i ON p.id_inscripcion = i.id_inscripcion " +
                              "JOIN Personas pe ON i.id_persona = pe.id_persona " +
                              "JOIN Cursos c ON i.id_curso = c.id_curso " +
                              "LEFT JOIN Bancos b ON p.id_banco = b.id_banco " +
                              "WHERE pe.cedula = '" + cedula + "' " +
                              "ORDER BY c.nombre, p.fecha_pago ASC";

            ResultSet rs2 = stmt.executeQuery(sqlPagos);

            Map<String, ArrayList<Object[]>> cursosMap = new LinkedHashMap<>();
            boolean tieneDeudaTotal = false;

            while (rs2.next()) {
                String curso = rs2.getString("curso");
                String monto = String.format(java.util.Locale.US, "%.2f", Double.parseDouble(rs2.getString("monto_pagado")));
                String saldo = String.format(java.util.Locale.US, "%.2f", Double.parseDouble(rs2.getString("saldo_restante")));
                
                Object[] fila = {
                    rs2.getString("fecha_pago"), "Bs. " + monto,
                    (rs2.getInt("nro_cuota") == 0 ? "Única" : rs2.getInt("nro_cuota")),
                    rs2.getString("metodo_pago"), (rs2.getString("banco") == null ? "N/A" : rs2.getString("banco")),
                    "Bs. " + saldo
                };

                cursosMap.putIfAbsent(curso, new ArrayList<>());
                cursosMap.get(curso).add(fila);
                if (Double.parseDouble(saldo) > 0.01) tieneDeudaTotal = true;
            }

            for (String curso : cursosMap.keySet()) {
                agregarSeccionCurso(curso, cursosMap.get(curso));
            }

            if (tieneDeudaTotal) {
                txtSaldoPendiente.setForeground(new Color(255, 100, 100));
                txtSaldoPendiente.setText(">>> ESTADO: SALDO PENDIENTE DETECTADO.\nRevisar los cursos marcados con saldo mayor a 0.");
            } else if (!cursosMap.isEmpty()) {
                txtSaldoPendiente.setForeground(new Color(100, 255, 100));
                txtSaldoPendiente.setText(">>> ESTADO: SOLVENTE.\nNo existen deudas pendientes para esta persona.");
            }

            conn.close();
            pnlContenedorTablas.revalidate();
            pnlContenedorTablas.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void agregarSeccionCurso(String nombreCurso, ArrayList<Object[]> datos) {
        double ultimoSaldo = 0;
        if (!datos.isEmpty()) {
            Object[] ultimaFila = datos.get(datos.size() - 1);
            String saldoStr = ultimaFila[5].toString().replace("Bs. ", "");
            ultimoSaldo = Double.parseDouble(saldoStr);
        }

        JPanel pnlCurso = new JPanel(new BorderLayout());
        pnlCurso.setOpaque(false); 

        JButton btnBarra = new JButton(" ►  CURSO: " + nombreCurso.toUpperCase());
        btnBarra.setContentAreaFilled(false); 
        btnBarra.setOpaque(false);            
        btnBarra.setHorizontalAlignment(SwingConstants.LEFT);
        btnBarra.setFocusPainted(false);
        btnBarra.setFont(new Font("Arial", Font.BOLD, 13));
        
        Color colorFondo = (ultimoSaldo <= 0.01) ? new Color(34, 139, 34, 180) : new Color(150, 40, 40, 180);
        btnBarra.setBackground(colorFondo);
        btnBarra.setForeground(Color.WHITE);
        btnBarra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        
        btnBarra.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(c.getBackground());
                g2.fillRect(0, 0, c.getWidth(), c.getHeight());
                g2.dispose();
                super.paint(g, c);
            }
        });
        
        String[] col = {"Fecha", "Monto", "Cuota", "Método", "Banco", "Saldo"};
        DefaultTableModel mod = new DefaultTableModel(col, 0);
        for (Object[] d : datos) mod.addRow(d);

        JTable tabla = new JTable(mod);
        configurarTabla(tabla);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getViewport().setBackground(new Color(30, 30, 30)); 
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        scrollTabla.setPreferredSize(new Dimension(900, 150));
        scrollTabla.setVisible(false);

        btnBarra.addActionListener(e -> {
            boolean estaVisible = scrollTabla.isVisible();
            scrollTabla.setVisible(!estaVisible);
            btnBarra.setText((estaVisible ? " ► " : " ▼ ") + " CURSO: " + nombreCurso.toUpperCase());
            pnlContenedorTablas.revalidate();
        });

        pnlCurso.add(btnBarra, BorderLayout.NORTH);
        pnlCurso.add(scrollTabla, BorderLayout.CENTER);
        pnlCurso.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));

        pnlContenedorTablas.add(pnlCurso);
    }

    private void configurarTabla(JTable t) {
        t.setBackground(new Color(35, 35, 35)); 
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 60));
        t.setRowHeight(28);
        t.getTableHeader().setBackground(new Color(50, 50, 50));
        t.getTableHeader().setForeground(new Color(59, 130, 246)); 
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void estilizarInput(JTextField t) {
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setFont(new Font("Arial", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void estilizarBotonPrincipal(JButton b) {
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(59, 130, 246)); 

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void estilizarBotonSecundario(JButton b) {
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(60, 60, 60, 180)); 

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(90, 90, 90, 210));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(60, 60, 60, 180));
            }
        });
        b.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
    }
}