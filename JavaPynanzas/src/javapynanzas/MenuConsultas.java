package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class MenuConsultas extends JFrame {

    class PanelFondo extends JPanel {
        private Image imagen;

        public PanelFondo() {
            try {
                imagen = ImageIO.read(new File("resources/meowl_login.jpg"));
            } catch (Exception e) {
                setBackground(new Color(26, 26, 26)); 
            }
            setLayout(null); 
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

    public MenuConsultas() {
        setTitle("Pynanzas - Consultas");
        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedor = new PanelFondo();

        JLabel lblTitulo = new JLabel("MENÚ DE CONSULTAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 38)); 
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 80, 800, 50); 
        contenedor.add(lblTitulo);

        int anchoBtn = 180; 
        int altoBtn = 110;  
        int yBotones = 200; 
        int espacio = 30;   
        
        int xInicio = (800 - (anchoBtn * 2 + espacio)) / 2;

        JButton btnPorPersona = crearBotonMenu("POR PERSONA", xInicio, yBotones, anchoBtn, altoBtn);
        btnPorPersona.addActionListener(e -> {
            SubMenuConsultasPersonas subMenuPersona = new SubMenuConsultasPersonas();
            subMenuPersona.setLocation(this.getLocation());
            subMenuPersona.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnPorPersona);

        JButton btnPorCurso = crearBotonMenu("POR CURSO", xInicio + anchoBtn + espacio, yBotones, anchoBtn, altoBtn);
        btnPorCurso.addActionListener(e -> {
            SubMenuConsultasCursos subMenuCursos = new SubMenuConsultasCursos();
            subMenuCursos.setLocation(this.getLocation());
            subMenuCursos.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnPorCurso);


        JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setBounds(275, 480, 250, 40); 
        btnVolver.setBackground(new Color(45, 45, 45, 200));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 14));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnVolver.addActionListener(e -> {
            MenuPrincipal principal = new MenuPrincipal();
            principal.setLocation(this.getLocation());
            principal.setVisible(true);
            this.dispose(); 
        });
        contenedor.add(btnVolver);

        add(contenedor);
    }

    private JButton crearBotonMenu(String texto, int x, int y, int ancho, int alto) {
        JButton btn = new JButton("<html><center>" + texto + "</center></html>");
        btn.setBounds(x, y, ancho, alto);
        btn.setBackground(new Color(60, 60, 60, 160));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(59, 130, 246)); 
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60, 160));
                btn.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            }
        });

        return btn;
    }
}