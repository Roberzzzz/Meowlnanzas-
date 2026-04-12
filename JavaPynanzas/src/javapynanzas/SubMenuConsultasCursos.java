package javapynanzas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseWheelListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubMenuConsultasCursos extends JFrame {

    private JComboBox<String> cbCursos;
    private JPanel pnlContenedorTablas;
    private JLabel lblTituloCurso;
    private JTextArea txtResumenInscritos;
    private JScrollPane scrollPrincipal;

    public SubMenuConsultasCursos() {
        setTitle("Pynanzas - Consulta por Curso");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlNorte.setBackground(new Color(30, 30, 30));

        JButton btnRegresar = new JButton("← Volver");
        estilizarBotonRegresar(btnRegresar);
        btnRegresar.addActionListener(e -> {
            new MenuConsultas().setVisible(true);
            this.dispose();
        });

        JLabel lblCurso = new JLabel("Seleccione Curso:");
        lblCurso.setForeground(Color.WHITE);
        
        cbCursos = new JComboBox<>();
        llenarComboBoxCursos();
        estilizarCombo(cbCursos);

        JButton btnConsultar = new JButton("Ver Inscritos");
        estilizarBotonBusqueda(btnConsultar);
        btnConsultar.addActionListener(e -> ejecutarConsulta());

        pnlNorte.add(btnRegresar);
        pnlNorte.add(lblCurso);
        pnlNorte.add(cbCursos);
        pnlNorte.add(btnConsultar);

        pnlContenedorTablas = new JPanel();
        pnlContenedorTablas.setLayout(new BoxLayout(pnlContenedorTablas, BoxLayout.Y_AXIS));
        pnlContenedorTablas.setBackground(new Color(26, 26, 26));
        pnlContenedorTablas.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        scrollPrincipal = new JScrollPane(pnlContenedorTablas);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(25);

        lblTituloCurso = new JLabel("  Seleccione un curso para ver el listado", JLabel.LEFT);
        lblTituloCurso.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloCurso.setForeground(new Color(59, 130, 246));
        lblTituloCurso.setBackground(Color.BLACK);
        lblTituloCurso.setOpaque(true);
        lblTituloCurso.setPreferredSize(new Dimension(900, 45));

        JPanel pnlCentroMaster = new JPanel(new BorderLayout());
        pnlCentroMaster.add(lblTituloCurso, BorderLayout.NORTH);
        pnlCentroMaster.add(scrollPrincipal, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setBackground(new Color(30, 30, 30));
        pnlSur.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), 
                "Resumen de Inscripción", 0, 0, null, Color.LIGHT_GRAY));

        txtResumenInscritos = new JTextArea(3, 30);
        txtResumenInscritos.setEditable(false);
        txtResumenInscritos.setBackground(new Color(40, 40, 40));
        txtResumenInscritos.setForeground(Color.LIGHT_GRAY);
        txtResumenInscritos.setFont(new Font("Monospaced", Font.BOLD, 14));
        pnlSur.add(new JScrollPane(txtResumenInscritos), BorderLayout.CENTER);

        add(pnlNorte, BorderLayout.NORTH);
        add(pnlCentroMaster, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);
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

    private void ejecutarConsulta() {
        String cursoSeleccionado = (String) cbCursos.getSelectedItem();
        if (cursoSeleccionado == null) return;

        pnlContenedorTablas.removeAll();
        lblTituloCurso.setText("  Listado: " + cursoSeleccionado.toUpperCase());

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

                String montoFinal = (montoRaw != null) ? 
                    "Bs. " + String.format(java.util.Locale.US, "%.2f", Double.parseDouble(montoRaw.replace(",", "."))) : "N/A";

                String saldoFinal = (saldoRaw != null) ? 
                    String.format(java.util.Locale.US, "%.2f", Double.parseDouble(saldoRaw.replace(",", "."))) : "0.00";

                String cuota = rs.getInt("nro_cuota") == 0 ? "Única" : String.valueOf(rs.getInt("nro_cuota"));

                Object[] fila = {
                    rs.getString("fecha_pago") != null ? rs.getString("fecha_pago") : "Sin pagos",
                    montoFinal, 
                    cuota, 
                    "Bs. " + saldoFinal
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

            txtResumenInscritos.setText(">>> CURSO: " + cursoSeleccionado + "\n>>> TOTAL ESTUDIANTES: " + totalInscritos);

            conn.close();
            pnlContenedorTablas.revalidate();
            pnlContenedorTablas.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void agregarSeccionEstudiante(String nombreEstudiante, ArrayList<Object[]> datos) {
        double saldoRestante = 0;
        if (!datos.isEmpty()) {
            String s = datos.get(datos.size() - 1)[3].toString()
                        .replace("Bs. ", "")
                        .replace(",", ".");
            try {
                saldoRestante = Double.parseDouble(s);
            } catch (NumberFormatException e) {
                saldoRestante = 0;
            }
        }

        JPanel pnlEst = new JPanel(new BorderLayout());
        pnlEst.setOpaque(false);

        JButton btnBarra = new JButton(" ►  " + nombreEstudiante);
        btnBarra.setHorizontalAlignment(SwingConstants.LEFT);
        btnBarra.setFocusPainted(false);
        btnBarra.setFont(new Font("Arial", Font.BOLD, 13));

        if (saldoRestante <= 0.01) {
            btnBarra.setBackground(new Color(34, 139, 34)); // Verde
            btnBarra.setForeground(Color.WHITE);
        } else {
            btnBarra.setBackground(new Color(150, 40, 40)); // Rojo
            btnBarra.setForeground(new Color(255, 200, 200));
        }

        btnBarra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        String[] col = {"Fecha Pago", "Monto", "Cuota", "Saldo Restante"};
        DefaultTableModel mod = new DefaultTableModel(col, 0);
        for (Object[] d : datos) mod.addRow(d);
        JTable tabla = new JTable(mod);
        configurarTabla(tabla);

        int alturaTotal = (datos.size() * 30) + 32 + 5;
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(900, alturaTotal));
        scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, alturaTotal));
        scrollTabla.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollTabla.setVisible(false);

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
        t.getTableHeader().setBackground(new Color(15, 15, 15));
        t.getTableHeader().setForeground(new Color(59, 130, 246));
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void estilizarBotonRegresar(JButton b) {
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonBusqueda(JButton b) {
        b.setBackground(new Color(59, 130, 246));
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarCombo(JComboBox<String> c) {
        c.setBackground(new Color(50, 50, 50));
        c.setForeground(Color.WHITE);
        c.setPreferredSize(new Dimension(200, 30));
    }
}