package javapynanzas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SubMenuConsultasPersonas extends JFrame {

    private JTextField txtCedula;
    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;
    private JLabel lblNombrePersona;
    private JTextArea txtSaldoPendiente;

    public SubMenuConsultasPersonas() {
        setTitle("Pynanzas - Consulta por Persona");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlNorte = new JPanel();
        pnlNorte.setBackground(new Color(30, 30, 30));
        pnlNorte.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel lblCedula = new JLabel("Cédula:");
        lblCedula.setForeground(Color.WHITE);
        lblCedula.setFont(new Font("Arial", Font.BOLD, 14));
        
        txtCedula = new JTextField(15);
        txtCedula.setBackground(new Color(50, 50, 50));
        txtCedula.setForeground(Color.WHITE);
        txtCedula.setCaretColor(Color.WHITE);
        txtCedula.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

        JButton btnBuscar = new JButton("Buscar Historial");
        estilizarBotonBusqueda(btnBuscar);
        
        pnlNorte.add(lblCedula);
        pnlNorte.add(txtCedula);
        pnlNorte.add(btnBuscar);

        JPanel pnlCentro = new JPanel(new BorderLayout());
        pnlCentro.setBackground(new Color(26, 26, 26));
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblNombrePersona = new JLabel("Persona: (Ingrese cédula)");
        lblNombrePersona.setForeground(new Color(59, 130, 246));
        lblNombrePersona.setFont(new Font("Arial", Font.BOLD, 18));
        pnlCentro.add(lblNombrePersona, BorderLayout.NORTH);

        String[] columnas = {"Curso", "Fecha", "Monto", "Cuota", "Método", "Banco", "Saldo Restante"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaPagos = new JTable(modeloTabla);
        configurarTabla();
        
        JScrollPane scrollTabla = new JScrollPane(tablaPagos);
        scrollTabla.getViewport().setBackground(new Color(40, 40, 40));
        pnlCentro.add(scrollTabla, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setBackground(new Color(30, 30, 30));
        pnlSur.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Saldos Pendientes / Estado Financiero",
                0, 0, new Font("Arial", Font.ITALIC, 12), Color.LIGHT_GRAY));

        txtSaldoPendiente = new JTextArea(4, 30);
        txtSaldoPendiente.setEditable(false);
        txtSaldoPendiente.setBackground(new Color(40, 40, 40));
        txtSaldoPendiente.setForeground(new Color(255, 100, 100)); 
        txtSaldoPendiente.setFont(new Font("Monospaced", Font.BOLD, 13));
        pnlSur.add(new JScrollPane(txtSaldoPendiente), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> ejecutarConsulta());
        
        add(pnlNorte, BorderLayout.NORTH);
        add(pnlCentro, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);
    }

    private void ejecutarConsulta() {
        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una cédula.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0); 
        txtSaldoPendiente.setText(""); 

        Conectar conecta = new Conectar();
        Connection conn = conecta.getConexion();

        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Error: No se pudo conectar a la base de datos.");
            return;
        }

        try {
            Statement stmt = conn.createStatement();

            String sqlPersona = "SELECT nombre || ' ' || apellido FROM Personas WHERE cedula = '" + cedula + "'";
            ResultSet rs1 = stmt.executeQuery(sqlPersona);

            if (!rs1.next()) {
                lblNombrePersona.setText("Persona: No encontrada");
                conn.close();
                return;
            }
            lblNombrePersona.setText("Persona: " + rs1.getString(1));

            String sqlPagos = "SELECT c.nombre, p.fecha_pago, p.monto_pagado, p.nro_cuota, " +
                              "p.metodo_pago, b.nombre, i.saldo_restante " +
                              "FROM Pagos p " +
                              "JOIN Inscripciones i ON p.id_inscripcion = i.id_inscripcion " +
                              "JOIN Personas pe ON i.id_persona = pe.id_persona " +
                              "JOIN Cursos c ON i.id_curso = c.id_curso " +
                              "LEFT JOIN Bancos b ON p.id_banco = b.id_banco " +
                              "WHERE pe.cedula = '" + cedula + "' " +
                              "ORDER BY c.nombre, p.nro_cuota";
            
            ResultSet rs2 = stmt.executeQuery(sqlPagos);

            boolean tieneDeuda = false;

            while (rs2.next()) {
                double saldo = rs2.getDouble(7);
                
                modeloTabla.addRow(new Object[]{
                    rs2.getString(1), rs2.getString(2), "Bs. " + rs2.getDouble(3),
                    (rs2.getInt(4) == 0 ? "Única" : rs2.getInt(4)),
                    rs2.getString(5), (rs2.getString(6) == null ? "N/A" : rs2.getString(6)),
                    "Bs. " + saldo
                });

                if (saldo > 0.01) {
                    tieneDeuda = true;
                }
            }

            if (!tieneDeuda && modeloTabla.getRowCount() > 0) {
                txtSaldoPendiente.setForeground(new Color(100, 255, 100)); 
                txtSaldoPendiente.setText(">>> ESTADO: SOLVENTE. Todos los cursos están pagados.");
            } else if (tieneDeuda) {
                txtSaldoPendiente.setForeground(new Color(255, 150, 150));
                txtSaldoPendiente.setText(">>> ESTADO: SALDO PENDIENTE DETECTADO.\nRevisar columnas de saldo en la tabla.");
            } else {
                txtSaldoPendiente.setText(">>> No se encontraron registros para esta persona.");
            }

            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error DB: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        tablaPagos.setBackground(new Color(45, 45, 45));
        tablaPagos.setForeground(Color.WHITE);
        tablaPagos.setGridColor(new Color(80, 80, 80));
        tablaPagos.getTableHeader().setBackground(new Color(60, 60, 60));
        tablaPagos.getTableHeader().setForeground(Color.WHITE);
        tablaPagos.setRowHeight(25);
    }

    private void estilizarBotonBusqueda(JButton btn) {
        btn.setBackground(new Color(59, 130, 246));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}