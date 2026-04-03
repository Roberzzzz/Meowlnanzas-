package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class MenuPrincipal extends JFrame {

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
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 170)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public MenuPrincipal() {
        setTitle("Pynanzas - Panel de Selección");
        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedor = new PanelFondo();
        
        JLabel lblTitulo = new JLabel("PANEL DE CONTROL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 30, 800, 40);
        contenedor.add(lblTitulo);

        JButton btnRegistro = new JButton("REGISTRO");
        btnRegistro.setBounds(100, 200, 280, 180); 
        estilizarBotonConIcono(btnRegistro, "registro_icon.png");
        
        btnRegistro.addActionListener(e -> {

            MenuRegistro opciones = new MenuRegistro();
            
            opciones.setLocation(this.getLocation()); 
            
            opciones.setVisible(true);
            this.dispose(); 
        });
        
        contenedor.add(btnRegistro);

        JButton btnConsultas = new JButton("CONSULTAS");
        btnConsultas.setBounds(420, 200, 280, 180);
        estilizarBotonConIcono(btnConsultas, "consulta_icon.png");
        contenedor.add(btnConsultas);

        JButton btnSalir = new JButton("CERRAR SESIÓN");
        btnSalir.setBounds(325, 500, 150, 30);
        btnSalir.setBackground(new Color(150, 0, 0, 100));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.addActionListener(e -> {
            JavaPynanzas login = new JavaPynanzas();
            login.setLocation(this.getLocation());
            login.setVisible(true);
            this.dispose();
        });
        contenedor.add(btnSalir);

        add(contenedor);
    }

    private void estilizarBotonConIcono(JButton btn, String rutaIcono) {
        try {
            ImageIcon iconoOriginal = new ImageIcon("resources/" + rutaIcono);
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono: " + rutaIcono);
        }

        btn.setBackground(new Color(20, 20, 20, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(10); 
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}