package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import practicae.PracticaE; 

public class MenuAcademico extends JFrame {

    private static Clip musicaClip;
    private GloboTexto lblGlobo;

    class GloboTexto extends JLabel {
        public GloboTexto() {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Arial", Font.ITALIC, 14));
            setForeground(Color.BLACK);
            setVisible(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 230));
            g2.fillRoundRect(0, 0, getWidth() - 20, getHeight() - 1, 20, 20);
            
            Path2D.Double pointer = new Path2D.Double();
            pointer.moveTo(getWidth() - 20, 20);   
            pointer.lineTo(getWidth(), 10);        
            pointer.lineTo(getWidth() - 20, 40);   
            pointer.closePath();
            g2.fill(pointer);

            g2.setColor(new Color(150, 150, 150));
            g2.drawRoundRect(0, 0, getWidth() - 20, getHeight() - 1, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }

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
                g2d.setColor(new Color(0, 0, 0, 170)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }
    }

    public MenuAcademico() {
        setTitle("Pynanzas - Panel Académico");
        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo contenedor = new PanelFondo();

        JLabel lblTitulo = new JLabel("GESTIÓN ACADÉMICA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(59, 130, 246)); 
        lblTitulo.setBounds(50, 40, 400, 40); 
        contenedor.add(lblTitulo);

        lblGlobo = new GloboTexto();
        lblGlobo.setBounds(24, 110, 420, 85); 
        contenedor.add(lblGlobo);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        canvas3D.setBounds(450, 20, 300, 250); 
        contenedor.add(canvas3D);

        SimpleUniverse universo = new SimpleUniverse(canvas3D);
        universo.getViewingPlatform().setNominalViewingTransform();
        PracticaE meowl3D = new PracticaE();
        BranchGroup escena = meowl3D.createSceneGraph();
        universo.addBranchGraph(escena);

        Alpha maxwellAlpha = meowl3D.getMaxwellAlpha();
        canvas3D.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                maxwellAlpha.resume();
                mostrarMensaje("¡Hola! Soy el asistente académico de Meowlnanzas :3");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                maxwellAlpha.pause();
                maxwellAlpha.setStartTime(System.currentTimeMillis());
                lblGlobo.setVisible(false);
            }
        });

        JButton btnProfesores = new JButton("PROFESORES");
        btnProfesores.setBounds(100, 280, 280, 160); 
        estilizarBoton(btnProfesores, "profesor_icon.png"); 

        btnProfesores.addActionListener(e -> {
            new SubMenuProfesor().setVisible(true);
            this.dispose(); 
        });
        
        btnProfesores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mostrarMensaje("Registra y gestiona los datos de los docentes");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblGlobo.setVisible(false);
            }
        });
        contenedor.add(btnProfesores);

        JButton btnCursos = new JButton("CURSOS");
        btnCursos.setBounds(420, 280, 280, 160);
        estilizarBoton(btnCursos, null); 
        
        btnCursos.addActionListener(e -> {
            new SubMenuCursos().setVisible(true);
            this.dispose(); 
        }); 
        
        btnCursos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mostrarMensaje("Administra los cursos y planes de estudio");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblGlobo.setVisible(false);
            }
        });
        contenedor.add(btnCursos);

        JButton btnSalir = new JButton("CERRAR SESIÓN");
        btnSalir.setBounds(325, 500, 150, 30);
        btnSalir.setBackground(new Color(150, 0, 0, 100));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.addActionListener(e -> {
            if (musicaClip != null) {
                musicaClip.stop();
            }

            new JavaPynanzas().setVisible(true);
            this.dispose();
        });
        contenedor.add(btnSalir);

        add(contenedor);
        reproducirMusica("meowl_music.wav"); 
    }

    private void mostrarMensaje(String texto) {
        lblGlobo.setText("<html><div style='padding-right: 25px; text-align: center;'>" + texto + "</div></html>");
        lblGlobo.setVisible(true);
    }

    private void estilizarBoton(JButton btn, String rutaIcono) {
        if (rutaIcono != null) {
            try {
                ImageIcon iconoOriginal = new ImageIcon("resources/" + rutaIcono);
                Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(imgEscalada));
            } catch (Exception e) {}
        }
        
        btn.setBackground(new Color(20, 20, 20, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 22)); 
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(40, 40, 40, 220)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(20, 20, 20, 200)); }
        });
    }
    
    private void reproducirMusica(String ruta) {
        if (musicaClip != null && musicaClip.isRunning()) return;
        try {
            File archivoMusica = new File("resources/" + ruta);
            if (archivoMusica.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoMusica);
                musicaClip = AudioSystem.getClip();
                musicaClip.open(audioStream);
                musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
                FloatControl gainControl = (FloatControl) musicaClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-12.0f); 
                musicaClip.start();
            }
        } catch (Exception e) {
            System.err.println("Error de audio: " + e.getMessage());
        }
    }
}