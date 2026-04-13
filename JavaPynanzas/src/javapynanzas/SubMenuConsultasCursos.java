package javapynanzas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class SubMenuConsultasCursos extends JFrame {

    private JComboBox<String> cbCursos;
    private JPanel pnlContenedorTablas;
    private JLabel lblTituloCurso;
    private JTextArea txtResumenInscritos;
    private JScrollPane scrollPrincipal;
    private boolean hayDeudoresGlobal = false;
    private JComboBox<String> cbFiltroReporte;
    private JButton btnReportePDF;

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
    
    private void prepararDatosReporte() {
        String curso = (String) cbCursos.getSelectedItem();
        String filtro = (String) cbFiltroReporte.getSelectedItem();
        if (curso == null) return;

        Conectar conecta = new Conectar();
        Connection conn = conecta.getConexion();

        java.util.List<Object[]> listaParaPdf = new java.util.ArrayList<>();
        double totalPendienteGlobal = 0;

        try {
            String sql = "SELECT pe.cedula, pe.nombre || ' ' || pe.apellido as alumno, " +
                         "p.fecha_pago, p.monto_pagado, p.nro_cuota, p.metodo_pago, " +
                         "b.nombre as banco, i.saldo_restante " +
                         "FROM Inscripciones i " +
                         "JOIN Personas pe ON i.id_persona = pe.id_persona " +
                         "JOIN Cursos c ON i.id_curso = c.id_curso " +
                         "LEFT JOIN Pagos p ON i.id_inscripcion = p.id_inscripcion " +
                         "LEFT JOIN Bancos b ON p.id_banco = b.id_banco " +
                         "WHERE c.nombre = ? ORDER BY alumno, p.fecha_pago ASC";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, curso);
            ResultSet rs = pst.executeQuery();

            String cedulaActual = "";
            java.util.List<Object[]> pagosAlumno = null;
            Object[] alumnoActual = null;

            while (rs.next()) {
                String cedula = rs.getString("cedula");
                double saldo = rs.getDouble("saldo_restante");

                boolean esSolvente = saldo <= 0.01;
                if (filtro.equals("Solventes") && !esSolvente) continue;
                if (filtro.equals("Deudores") && esSolvente) continue;

                if (!cedula.equals(cedulaActual)) {
                    cedulaActual = cedula;
                    pagosAlumno = new java.util.ArrayList<>();
                    alumnoActual = new Object[]{
                        rs.getString("alumno") + " - C.I. " + cedula,
                        "Bs. " + String.format("%.2f", saldo),
                        pagosAlumno
                    };
                    listaParaPdf.add(alumnoActual);
                    totalPendienteGlobal += saldo;
                }

                if (rs.getString("fecha_pago") != null) {
                    pagosAlumno.add(new Object[]{
                        rs.getString("fecha_pago"),
                        "Bs. " + rs.getString("monto_pagado"),
                        rs.getInt("nro_cuota") == 0 ? "Única" : "Cuota " + rs.getInt("nro_cuota"),
                        rs.getString("metodo_pago"),
                        rs.getString("banco") == null ? "No aplica" : rs.getString("banco")
                    });
                }
            }
            conn.close();

            if (listaParaPdf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para este filtro.");
            } else {
                GenPdf.generarReporteCurso(curso, filtro, listaParaPdf, totalPendienteGlobal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SubMenuConsultasCursos() {
        setTitle("Meowlnanzas - Consulta por Curso");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        PanelFondo fondoMeowl = new PanelFondo();
        setContentPane(fondoMeowl); 
        setLayout(new BorderLayout());

        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            pnlNorte.setOpaque(false); 

            JButton btnRegresar = new JButton("← Volver");
            estilizarBotonRegresar(btnRegresar);
            btnRegresar.addActionListener(e -> {
                new MenuConsultas().setVisible(true);
                this.dispose();
            });

            JLabel lblCurso = new JLabel("Curso:");
            lblCurso.setForeground(Color.WHITE);
            cbCursos = new JComboBox<>();
            llenarComboBoxCursos();
            estilizarCombo(cbCursos);

            JLabel lblFiltro = new JLabel("Filtro PDF:");
            lblFiltro.setForeground(Color.WHITE);
            cbFiltroReporte = new JComboBox<>(new String[]{"Todos", "Solventes", "Deudores"});
            estilizarCombo(cbFiltroReporte);

            JButton btnConsultar = new JButton("Ver Inscritos");
            estilizarBotonBusqueda(btnConsultar);
            btnConsultar.addActionListener(e -> ejecutarConsulta());

            btnReportePDF = new JButton("Generar PDF");
            estilizarBotonBusqueda(btnReportePDF);
            btnReportePDF.setBackground(new Color(46, 139, 87)); 
            btnReportePDF.addActionListener(e -> prepararDatosReporte());

            pnlNorte.add(btnRegresar);
            pnlNorte.add(lblCurso);
            pnlNorte.add(cbCursos);
            pnlNorte.add(btnConsultar);
            pnlNorte.add(lblFiltro);
            pnlNorte.add(cbFiltroReporte);
            pnlNorte.add(btnReportePDF);

        pnlContenedorTablas = new JPanel();
        pnlContenedorTablas.setLayout(new BoxLayout(pnlContenedorTablas, BoxLayout.Y_AXIS));
        pnlContenedorTablas.setOpaque(false);
        pnlContenedorTablas.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        scrollPrincipal = new JScrollPane(pnlContenedorTablas);
        scrollPrincipal.setOpaque(false);
        scrollPrincipal.getViewport().setOpaque(false);
        scrollPrincipal.setBorder(null);
        
        scrollPrincipal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPrincipal.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(25);

        lblTituloCurso = new JLabel("  Seleccione un curso para ver el listado", JLabel.LEFT);
        lblTituloCurso.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloCurso.setForeground(new Color(59, 130, 246));
        lblTituloCurso.setOpaque(false); 
        lblTituloCurso.setPreferredSize(new Dimension(900, 45));

        JPanel pnlCentroMaster = new JPanel(new BorderLayout());
        pnlCentroMaster.setOpaque(false);
        pnlCentroMaster.add(lblTituloCurso, BorderLayout.NORTH);
        pnlCentroMaster.add(scrollPrincipal, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setOpaque(false);
        pnlSur.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), 
                "Resumen de Inscripción", 0, 0, null, Color.LIGHT_GRAY));

        txtResumenInscritos = new JTextArea(3, 30) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        txtResumenInscritos.setOpaque(false);
        txtResumenInscritos.setEditable(false);
        txtResumenInscritos.setBackground(new Color(20, 20, 20, 180));
        txtResumenInscritos.setFont(new Font("Monospaced", Font.BOLD, 15));
        txtResumenInscritos.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollResumen = new JScrollPane(txtResumenInscritos);
        scrollResumen.setOpaque(false);
        scrollResumen.getViewport().setOpaque(false);
        scrollResumen.setBorder(null);
        
        pnlSur.add(scrollResumen, BorderLayout.CENTER);

        add(pnlNorte, BorderLayout.NORTH);
        add(pnlCentroMaster, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);
    }

    private void ejecutarConsulta() {
        String cursoSeleccionado = (String) cbCursos.getSelectedItem();
        if (cursoSeleccionado == null) return;

        pnlContenedorTablas.removeAll();
        lblTituloCurso.setText("  Listado: " + cursoSeleccionado.toUpperCase());
        hayDeudoresGlobal = false; 

        Conectar conecta = new Conectar();
        Connection conn = conecta.getConexion();

        try {
            String sql = "SELECT pe.cedula, pe.nombre || ' ' || pe.apellido as estudiante, " +
                         "p.fecha_pago, p.monto_pagado, p.nro_cuota, i.saldo_restante " +
                         "FROM Inscripciones i " +
                         "JOIN Personas pe ON i.id_persona = pe.id_persona " +
                         "JOIN Cursos c ON i.id_curso = c.id_curso " +
                         "LEFT JOIN Pagos p ON i.id_inscripcion = p.id_inscripcion " +
                         "WHERE c.nombre = '" + cursoSeleccionado + "' " +
                         "ORDER BY estudiante, p.fecha_pago ASC";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Map<String, ArrayList<Object[]>> estudiantesMap = new LinkedHashMap<>();
            int totalInscritos = 0;

    while (rs.next()) {
        String estudiante = rs.getString("cedula") + " - " + rs.getString("estudiante");

        String montoRaw = rs.getString("monto_pagado");
        String saldoRaw = rs.getString("saldo_restante");

        if (montoRaw == null) montoRaw = "0.00";
        if (saldoRaw == null) saldoRaw = "0.00";

        double montoNum = Double.parseDouble(montoRaw.replace(",", "."));
        double saldoNum = Double.parseDouble(saldoRaw.replace(",", "."));

        if (saldoNum > 0.01) hayDeudoresGlobal = true;

        String montoFinal = "Bs. " + String.format(java.util.Locale.US, "%.2f", montoNum);
        String saldoFinal = "Bs. " + String.format(java.util.Locale.US, "%.2f", saldoNum);

        String cuota = rs.getInt("nro_cuota") == 0 ? "Única" : String.valueOf(rs.getInt("nro_cuota"));

        Object[] fila = {
            rs.getString("fecha_pago") != null ? rs.getString("fecha_pago") : "Sin pagos",
            montoFinal, 
            cuota, 
            saldoFinal
        };

        if (!estudiantesMap.containsKey(estudiante)) {
            estudiantesMap.put(estudiante, new ArrayList<>());
            totalInscritos++;
        }
        estudiantesMap.get(estudiante).add(fila);
    }

            for (String est : estudiantesMap.keySet()) {
                agregarSeccionEstudiante(est, estudiantesMap.get(est));
            }

            if (hayDeudoresGlobal) {
                txtResumenInscritos.setForeground(new Color(255, 100, 100));
                txtResumenInscritos.setText(">>> CURSO: " + cursoSeleccionado + "\n>>> TOTAL ESTUDIANTES: " + totalInscritos + "\n>>> ESTADO: EXISTEN SALDOS PENDIENTES");
            } else {
                txtResumenInscritos.setForeground(new Color(100, 255, 100));
                txtResumenInscritos.setText(">>> CURSO: " + cursoSeleccionado + "\n>>> TOTAL ESTUDIANTES: " + totalInscritos + "\n>>> ESTADO: TODOS SOLVENTES");
            }

            conn.close();
            pnlContenedorTablas.revalidate();
            pnlContenedorTablas.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void llenarComboBoxCursos() {
        Conectar conecta = new Conectar();
        Connection conn = conecta.getConexion();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre FROM Cursos ORDER BY nombre ASC");
            while (rs.next()) {
                cbCursos.addItem(rs.getString(1));
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cargar cursos: " + e.getMessage());
        }
    }

    private void agregarSeccionEstudiante(String nombreEstudiante, ArrayList<Object[]> datos) {
        double saldoRestante = 0;
        if (!datos.isEmpty()) {
            String s = datos.get(datos.size() - 1)[3].toString();
            try {
                s = s.replace("Bs. ", "").trim();
                saldoRestante = Double.parseDouble(s);
            } catch (Exception e) {
                saldoRestante = 0;
            }
        }

        JPanel pnlEst = new JPanel(new BorderLayout());
        pnlEst.setOpaque(false);
        pnlEst.setMaximumSize(new Dimension(880, Integer.MAX_VALUE));
        pnlEst.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnBarra = new JButton(" ►  " + nombreEstudiante);
        btnBarra.setContentAreaFilled(false);
        btnBarra.setOpaque(false);
        btnBarra.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 5, 5);
                g2.dispose();
                super.paint(g, c);
            }
        });
        btnBarra.setHorizontalAlignment(SwingConstants.LEFT);
        btnBarra.setFocusPainted(false);
        btnBarra.setFont(new Font("Arial", Font.BOLD, 13));

        if (saldoRestante <= 0.01) {
            btnBarra.setBackground(new Color(34, 139, 34, 200)); 
            btnBarra.setForeground(Color.WHITE);
        } else {
            btnBarra.setBackground(new Color(150, 40, 40, 200)); 
            btnBarra.setForeground(new Color(255, 200, 200));
        }

        btnBarra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        String[] col = {"Fecha Pago", "Monto", "Cuota", "Saldo Restante"};
        DefaultTableModel mod = new DefaultTableModel(col, 0) {
        
            @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
      };
    
        for (Object[] d : datos) mod.addRow(d);
    
        JTable tabla = new JTable(mod);
        configurarTabla(tabla);

        int alturaTotal = (datos.size() * 30) + 32 + 5;
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(850, alturaTotal));
        scrollTabla.setMaximumSize(new Dimension(850, alturaTotal));
        scrollTabla.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollTabla.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTabla.setVisible(false);
        scrollTabla.setOpaque(false);
        scrollTabla.getViewport().setOpaque(false);

        MouseWheelListener red = e -> scrollPrincipal.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, scrollPrincipal));
        tabla.addMouseWheelListener(red);
        scrollTabla.addMouseWheelListener(red);

        btnBarra.addActionListener(e -> {
            boolean visible = scrollTabla.isVisible();
            scrollTabla.setVisible(!visible);
            btnBarra.setText((visible ? " ► " : " ▼ ") + nombreEstudiante);
            pnlContenedorTablas.revalidate();
            pnlContenedorTablas.repaint();
        });

        pnlEst.add(btnBarra, BorderLayout.NORTH);
        pnlEst.add(scrollTabla, BorderLayout.CENTER);
        pnlEst.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pnlContenedorTablas.add(pnlEst);
    }

    private void configurarTabla(JTable t) {
        t.setBackground(new Color(30, 30, 30));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 60));
        t.setRowHeight(30);
        t.setFillsViewportHeight(true);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setBackground(new Color(15, 15, 15));
        t.getTableHeader().setForeground(new Color(59, 130, 246));
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void estilizarBotonRegresar(JButton b) {
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBackground(new Color(70, 70, 70, 180));
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
    }

    private void estilizarBotonBusqueda(JButton b) {
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
    }

    private void estilizarCombo(JComboBox<String> c) {
        c.setBackground(new Color(50, 50, 50));
        c.setForeground(Color.WHITE);
        c.setPreferredSize(new Dimension(200, 30));
    }
}