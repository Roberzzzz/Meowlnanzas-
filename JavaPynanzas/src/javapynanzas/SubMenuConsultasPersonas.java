package javapynanzas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubMenuConsultasPersonas extends JFrame {

    private JTextField txtCedula;
    private JPanel pnlContenedorTablas;
    private JLabel lblNombrePersona;
    private JTextArea txtSaldoPendiente;

    public SubMenuConsultasPersonas() {
        setTitle("Pynanzas - Historial Detallado");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlNorte.setBackground(new Color(30, 30, 30));
        
        JButton btnRegresar = new JButton("← Volver");
        estilizarBotonRegresar(btnRegresar);
        btnRegresar.addActionListener(e -> {
            MenuConsultas menu = new MenuConsultas();
            menu.setVisible(true);
            this.dispose();
        });
        
        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setForeground(Color.WHITE);
        txtCedula = new JTextField(15);
        estilizarInput(txtCedula);

        JButton btnBuscar = new JButton("Consultar Estado");
        estilizarBotonBusqueda(btnBuscar);
        
        pnlNorte.add(btnRegresar);
        pnlNorte.add(lblCedula);
        pnlNorte.add(txtCedula);
        pnlNorte.add(btnBuscar);

        pnlContenedorTablas = new JPanel();
        pnlContenedorTablas.setLayout(new BoxLayout(pnlContenedorTablas, BoxLayout.Y_AXIS));
        pnlContenedorTablas.setBackground(new Color(26, 26, 26));

        JScrollPane scrollPrincipal = new JScrollPane(pnlContenedorTablas);
        scrollPrincipal.setBorder(null);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);

        lblNombrePersona = new JLabel("  Seleccione un cliente para ver sus cursos", JLabel.LEFT);
        lblNombrePersona.setFont(new Font("Arial", Font.BOLD, 18));
        lblNombrePersona.setForeground(new Color(59, 130, 246)); 
        lblNombrePersona.setBackground(Color.BLACK);            
        lblNombrePersona.setOpaque(true);                     
        lblNombrePersona.setPreferredSize(new Dimension(900, 50)); 

        JPanel pnlCentroMaster = new JPanel(new BorderLayout());
        pnlCentroMaster.add(lblNombrePersona, BorderLayout.NORTH);
        pnlCentroMaster.add(scrollPrincipal, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setBackground(new Color(30, 30, 30));
        pnlSur.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), 
                "Resumen Financiero Total", 0, 0, null, Color.LIGHT_GRAY));

        txtSaldoPendiente = new JTextArea(3, 30);
        txtSaldoPendiente.setEditable(false);
        txtSaldoPendiente.setBackground(new Color(40, 40, 40));
        txtSaldoPendiente.setFont(new Font("Monospaced", Font.BOLD, 14));
        pnlSur.add(new JScrollPane(txtSaldoPendiente), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> ejecutarConsulta());
        
        add(pnlNorte, BorderLayout.NORTH);
        add(pnlCentroMaster, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);
    }
    
    private void estilizarBotonRegresar(JButton b) {
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void ejecutarConsulta() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) return;

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
                revalidate(); repaint(); return;
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
            btnBarra.setHorizontalAlignment(SwingConstants.LEFT);
            btnBarra.setFocusPainted(false);
            btnBarra.setFont(new Font("Arial", Font.BOLD, 13));

            if (ultimoSaldo <= 0.01) {
                btnBarra.setBackground(new Color(34, 139, 34)); 
                btnBarra.setForeground(Color.WHITE);
            } else {
                btnBarra.setBackground(new Color(150, 40, 40));
                btnBarra.setForeground(new Color(255, 200, 200));
            }

            btnBarra.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60)),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)));

            String[] col = {"Fecha", "Monto", "Cuota", "Método", "Banco", "Saldo"};
            DefaultTableModel mod = new DefaultTableModel(col, 0);
            for (Object[] d : datos) mod.addRow(d);

            JTable tabla = new JTable(mod);
            configurarTabla(tabla);

            JScrollPane scrollTabla = new JScrollPane(tabla);
            scrollTabla.getViewport().setBackground(new Color(30, 30, 30)); 
            scrollTabla.setBackground(new Color(30, 30, 30));
            scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
            scrollTabla.setPreferredSize(new Dimension(900, 150));
            scrollTabla.setVisible(false);

            btnBarra.addActionListener(e -> {
                boolean estaVisible = scrollTabla.isVisible();
                scrollTabla.setVisible(!estaVisible);
                btnBarra.setText((estaVisible ? " ► " : " ▼ ") + " CURSO: " + nombreCurso.toUpperCase());

                pnlContenedorTablas.revalidate();
                pnlContenedorTablas.repaint();
            });

            pnlCurso.add(btnBarra, BorderLayout.NORTH);
            pnlCurso.add(scrollTabla, BorderLayout.CENTER);

            pnlCurso.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

            pnlContenedorTablas.add(pnlCurso);
        }

        private void configurarTabla(JTable t) {
            t.setBackground(new Color(35, 35, 35)); 
            t.setForeground(Color.WHITE);
            t.setGridColor(new Color(60, 60, 60));
            t.setRowHeight(28);
            t.setFillsViewportHeight(true); 

            t.getTableHeader().setBackground(new Color(50, 50, 50));
            t.getTableHeader().setForeground(new Color(59, 130, 246)); 
            t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        }

    private void estilizarInput(JTextField t) {
        t.setBackground(new Color(50, 50, 50));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
    }

    private void estilizarBotonBusqueda(JButton b) {
        b.setBackground(new Color(59, 130, 246));
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}