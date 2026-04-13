package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;

public class MenuRegistro extends JFrame {

    class PanelFondo extends JPanel {
        private Image imagen;
        public PanelFondo() {
            try {
                imagen = ImageIO.read(new File("resources/meowl_login.jpg"));
            } catch (Exception e) {
                setBackground(new Color(30, 30, 30));
            }
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }
    }

    public MenuRegistro() {
        setTitle("Pynanzas - Registro");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedor = new PanelFondo();

        JLabel lblTitulo = new JLabel("MENÚ DE REGISTRO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 60, 800, 45); 
        contenedor.add(lblTitulo);

        int yBotones = 220;
        int anchoBtn = 200; 
        int altoBtn = 80;  

        JButton btnPersona = crearBotonHorizontal("Persona", 75, yBotones, anchoBtn, altoBtn);
        btnPersona.addActionListener(e -> {
            SubMenuPersonas ventanaPersonitas = new SubMenuPersonas();
            ventanaPersonitas.setLocation(this.getLocation());
            ventanaPersonitas.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnPersona);
        
        JButton btnInscripcion = crearBotonHorizontal("Inscripción", 300, yBotones, anchoBtn, altoBtn);
        btnInscripcion.addActionListener(e -> {
            SubMenuInscripciones ventanitaInscripciones = new SubMenuInscripciones();
            ventanitaInscripciones.setLocation(this.getLocation());
            ventanitaInscripciones.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnInscripcion);

        JButton btnPago = crearBotonHorizontal("Pago", 525, yBotones, anchoBtn, altoBtn);
        btnPago.addActionListener(e->{
            SubMenuPagos ventanaPagos = new SubMenuPagos();
            ventanaPagos.setLocation(this.getLocation());
            ventanaPagos.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnPago);

        JButton btnCursos = crearBotonHorizontal("Cursos", 300, yBotones+100, anchoBtn, altoBtn);
        btnCursos.addActionListener(e -> {
            SubMenuCursos ventanaCursos = new SubMenuCursos();
            ventanaCursos.setLocation(this.getLocation());
            ventanaCursos.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnCursos);
        
        JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setBounds(275, 450, 250, 45); 
        btnVolver.setBackground(new Color(45, 45, 45));
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

    private JButton crearBotonHorizontal(String texto, int x, int y, int ancho, int alto) {
        JButton btn = new JButton(texto) {
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

        btn.setBounds(x, y, ancho, alto); 
        btn.setBackground(new Color(60, 60, 60, 180));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 17));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false); 

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(90, 90, 90, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60, 180));
            }
        });

        return btn;
    }
}