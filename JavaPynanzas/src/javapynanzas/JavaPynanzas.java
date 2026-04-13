package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JavaPynanzas extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMensaje;

    class PanelFondo extends JPanel {
        private Image imagen;
        public PanelFondo() {
            try {
                imagen = ImageIO.read(new File("resources/meowl_login.jpg"));
            } catch (IOException e) {
                System.err.println("Error cargando la imagen de Meowl: " + e.getMessage());
                setBackground(new Color(30, 30, 30));
            }
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 150)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public JavaPynanzas() {
        setTitle("Pynanzas - Autenticación");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        PanelFondo panelPrincipal = new PanelFondo();

        JLabel lblTitulo = new JLabel("MEOWLNANZAS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Inicia sesión para continuar");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(220, 220, 220));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsuario = new JTextField();
        estilizarInput(txtUsuario);

        txtPassword = new JPasswordField();
        estilizarInput(txtPassword);

        btnLogin = new JButton("INGRESAR");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(new Color(59, 130, 246)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(new Color(239, 68, 68)); 
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));

        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));
        panelPrincipal.add(lblSubtitulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 50)));
        panelPrincipal.add(lblUser);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));
        panelPrincipal.add(txtUsuario);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(lblPass);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));
        panelPrincipal.add(txtPassword);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(lblMensaje);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(btnLogin);

        add(panelPrincipal, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> validarLogin());
    }

    private void estilizarInput(JTextField campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); 
        campo.setBackground(new Color(20, 20, 20, 180)); 
        campo.setForeground(Color.WHITE);
        campo.setFont(new Font("Arial", Font.BOLD, 14));
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) 
        ));
    }

   private void validarLogin() {
        String user = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblMensaje.setText("Por favor, rellena todos los campos.");
            return;
        }

        Conectar conecta = new Conectar();
        java.sql.Connection con = conecta.getConexion();

        if (con != null) {
            try {
                java.sql.Statement st = con.createStatement();
                String sql = "SELECT * FROM Usuarios WHERE username = '" + user + "' AND password_ = '" + pass + "'";
                java.sql.ResultSet rs = st.executeQuery(sql);

                if (rs.next()) {
  
                    String rol = rs.getString("rol");

                    ImageIcon iconoMeowl = cargarIcono("meowl_icon_aprobado.png", 50, 50);
                    JOptionPane.showMessageDialog(
                        this, 
                        "¡Usuario correcto! Iniciando sesión...", 
                        "Bienvenido a Meowlnanzas", 
                        JOptionPane.PLAIN_MESSAGE, 
                        iconoMeowl
                    );

                    if ("coordinador".equalsIgnoreCase(rol) || "academico".equalsIgnoreCase(rol)) {
                        MenuAcademico academico = new MenuAcademico();
                        academico.setVisible(true);
                    } else {
                        MenuPrincipal menu = new MenuPrincipal();
                        menu.setVisible(true);
                    }

                    this.dispose(); 

                } else {
                    lblMensaje.setText("Usuario o contraseña incorrectos.");
                }
                con.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                lblMensaje.setText("Error en la conexión.");
            }
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JavaPynanzas().setVisible(true);
        });
    }
}