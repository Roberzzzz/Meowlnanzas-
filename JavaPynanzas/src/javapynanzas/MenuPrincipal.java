package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;   
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import javax.imageio.ImageIO;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import practicae.PracticaE; 

public class MenuPrincipal extends JFrame {
    
    private static Clip musicaClip;
    private static Clip musicaSpinClip; 
    private long posicionMusicaPrincipal = 0; 
    private GloboTexto lblGlobo; 

    private final int[] KONAMI_CODE = {
        KeyEvent.VK_UP, KeyEvent.VK_UP, 
        KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_B, KeyEvent.VK_A
    };
    private int indiceKonami = 0;

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

    public MenuPrincipal() {
        setTitle("Meowlnanzas - Panel de Selección");
        setSize(800, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cargarAudioSpin("meowl_spin_music.wav");

        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KONAMI_CODE[indiceKonami]) {
                    indiceKonami++;
                    if (indiceKonami == KONAMI_CODE.length) {
                        abrirCreditosSecretos();
                        indiceKonami = 0;
                    }
                } else {
                    indiceKonami = 0;
                }
            }
        });

        PanelFondo contenedor = new PanelFondo();
        
        JLabel lblTitulo = new JLabel("PANEL DE CONTROL");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
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
                intercambiarMusicaAlGirar(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                maxwellAlpha.pause();
                maxwellAlpha.setStartTime(System.currentTimeMillis());
                intercambiarMusicaAlGirar(false);
            }
        });

        JButton btnRegistro = new JButton("REGISTRO");
        btnRegistro.setBounds(100, 280, 280, 160); 
        estilizarBotonConIcono(btnRegistro, "registro_icon.png");

        btnRegistro.addActionListener(e -> {
            MenuRegistro ventanaRegistro = new MenuRegistro();
            ventanaRegistro.setLocation(this.getLocation()); 
            ventanaRegistro.setVisible(true);
            this.dispose(); 
        });
        
        btnRegistro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mostrarMensaje("Aquí puedes gestionar las inscripciones, pagos y cursos :3");
                requestFocusInWindow(); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblGlobo.setVisible(false);
            }
        });
        contenedor.add(btnRegistro);

        JButton btnConsultas = new JButton("CONSULTAS");
        btnConsultas.setBounds(420, 280, 280, 160);
        estilizarBotonConIcono(btnConsultas, "consulta_icon.png");
        
        btnConsultas.addActionListener(e -> {
            MenuConsultas ventanaConsultas = new MenuConsultas();
            ventanaConsultas.setLocation(this.getLocation());
            ventanaConsultas.setVisible(true);
            this.dispose(); 
        }); 
        
        btnConsultas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mostrarMensaje("Aquí puedes consultar la solvencia de los estudiantes");
                requestFocusInWindow(); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblGlobo.setVisible(false);
            }
        });
        contenedor.add(btnConsultas);

        JButton btnSalir = new JButton("CERRAR SESIÓN");
        btnSalir.setBounds(325, 500, 150, 30);
        btnSalir.setBackground(new Color(150, 0, 0, 100));
        btnSalir.setForeground(Color.WHITE);

        btnSalir.addActionListener(e -> {
            detenerMusicaTotal();
            new JavaPynanzas().setVisible(true);
            this.dispose();
        });
        contenedor.add(btnSalir);

        add(contenedor);
        reproducirMusica("meowl_music.wav"); 
    }

    private void abrirCreditosSecretos() {
        if (musicaClip != null) musicaClip.stop();
        if (musicaSpinClip != null) musicaSpinClip.stop();
        

        FrameSecretoProhibidoLeerPorFavorxd creditos = new FrameSecretoProhibidoLeerPorFavorxd(musicaClip);
        creditos.setVisible(true);
        
        creditos.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (musicaClip != null) {
                    musicaClip.setMicrosecondPosition(posicionMusicaPrincipal);
                    musicaClip.start();
                }
            }
        });
    }

    private void detenerMusicaTotal() {
        if (musicaClip != null) musicaClip.stop();
        if (musicaSpinClip != null) musicaSpinClip.stop();
    }

    private void intercambiarMusicaAlGirar(boolean girando) {
        try {
            if (girando) {
                if (musicaClip != null && musicaClip.isRunning()) {
                    posicionMusicaPrincipal = musicaClip.getMicrosecondPosition();
                    musicaClip.stop();
                }
                if (musicaSpinClip != null) {
                    musicaSpinClip.setFramePosition(0);
                    musicaSpinClip.loop(Clip.LOOP_CONTINUOUSLY);
                    musicaSpinClip.start();
                }
            } else {
                if (musicaSpinClip != null) {
                    musicaSpinClip.stop();
                }
                if (musicaClip != null) {
                    musicaClip.setMicrosecondPosition(posicionMusicaPrincipal);
                    musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
                    musicaClip.start();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarAudioSpin(String ruta) {
        try {
            File file = new File("resources/" + ruta);
            if (file.exists()) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                musicaSpinClip = AudioSystem.getClip();
                musicaSpinClip.open(stream);
                FloatControl gain = (FloatControl) musicaSpinClip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(-5.0f); 
            }
        } catch (Exception e) {
            System.out.println("Error cargando audio spin: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String texto) {
        lblGlobo.setText("<html><div style='padding-right: 25px; text-align: center;'>" + texto + "</div></html>");
        lblGlobo.setVisible(true);
    }

    private void estilizarBotonConIcono(JButton btn, String rutaIcono) {
        try {
            ImageIcon iconoOriginal = new ImageIcon("resources/" + rutaIcono);
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {}
        btn.setBackground(new Color(20, 20, 20, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
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
                gainControl.setValue(-8.0f); 
                musicaClip.start();
            }
        } catch (Exception e) {
            System.out.println("Error al reproducir audio: " + e.getMessage());
        }
    }
}