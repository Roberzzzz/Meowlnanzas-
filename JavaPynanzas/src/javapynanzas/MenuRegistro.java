package javapynanzas;

import javax.swing.*;
import java.awt.*;
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
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 200)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public MenuRegistro() {
        setTitle("Pynanzas - Registro");
        setSize(900, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedor = new PanelFondo();

        JLabel lblTitulo = new JLabel("Menú de Registro", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 40, 900, 45);
        contenedor.add(lblTitulo);

        contenedor.add(crearBotonHorizontal("Persona", 30));
        contenedor.add(crearBotonHorizontal("Inscripción", 245));
        contenedor.add(crearBotonHorizontal("Pago", 460));
        contenedor.add(crearBotonHorizontal("Cursos", 675));

        JButton btnVolver = new JButton("← Volver al Menú Principal");
        btnVolver.setBounds(30, 410, 250, 35);
        btnVolver.setBackground(new Color(40, 40, 40));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        
        btnVolver.addActionListener(e -> {
            MenuPrincipal principal = new MenuPrincipal();
            
            principal.setLocation(this.getLocation());
            
            principal.setVisible(true);
            this.dispose(); 
        });
        
        contenedor.add(btnVolver);

        add(contenedor);
    }

    private JButton crearBotonHorizontal(String texto, int x) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, 180, 190, 60); 
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}