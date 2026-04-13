//por favor no veas esto
//si lo lees antes de acabar la presentación
//no te vuelvo a hablar más nunca


























package javapynanzas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class FrameSecretoProhibidoLeerPorFavorxd extends JFrame {
    private Clip musicaEasterEgg;
    private Clip musicaMenuPrincipal;
    private long tiempoMusicaMenu = 0;

    class PanelFondo extends JPanel {
        private Image imagen;
        public PanelFondo() {
            try {
                imagen = ImageIO.read(new File("resources/ending_background.jpg")); 
            } catch (Exception e) {
                setBackground(new Color(15, 15, 15));
            }
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if (imagen != null) {
                g2d.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 130)); 
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            g2d.setColor(new Color(59, 130, 246));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        }
    }

    public FrameSecretoProhibidoLeerPorFavorxd(Clip musicaMenu) {
        this.musicaMenuPrincipal = musicaMenu;
        
        if (musicaMenuPrincipal != null && musicaMenuPrincipal.isRunning()) {
            tiempoMusicaMenu = musicaMenuPrincipal.getMicrosecondPosition();
            musicaMenuPrincipal.stop();
        }

        setTitle("PROYECTO MEOWLNANZAS - CRÉDITOS");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true); 

        PanelFondo panelPrincipal = new PanelFondo();
        setContentPane(panelPrincipal);

        JLabel lblLogo = new JLabel("BIG DIP TEAM", SwingConstants.CENTER);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblLogo.setBounds(0, 40, 500, 40);
        panelPrincipal.add(lblLogo);

        JTextArea txtCreditos = new JTextArea(
            "Desarrollado por:\n" +
            "Roberto Montero\n" +
            "Yahany Chacón\n" +
            "Juan Avila\n\n" +
            "Música: Tung Tung Tung Sahur (Teto Version)\n\n" +
            "En honor a Meowl y Pynanzas\n" +
            "Gracias a todos por hacer esta realidad\n" +
            "Pynanzas/Meowlnanzas 2025 - 2026"
        );
        txtCreditos.setEditable(false);
        txtCreditos.setOpaque(false);
        txtCreditos.setForeground(new Color(220, 220, 220)); 
        txtCreditos.setFont(new Font("Arial", Font.PLAIN, 16));
        txtCreditos.setBounds(100, 110, 350, 200);
        panelPrincipal.add(txtCreditos);

        JButton btnCerrar = new JButton("VOLVER AL PANEL");
        btnCerrar.setBounds(150, 330, 200, 35);
        btnCerrar.setBackground(new Color(30, 30, 30));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246)));
        
        btnCerrar.addActionListener(e -> {
            if (musicaEasterEgg != null) {
                musicaEasterEgg.stop();
                musicaEasterEgg.close();
            }
            
            if (musicaMenuPrincipal != null) {
                musicaMenuPrincipal.setMicrosecondPosition(tiempoMusicaMenu);
                musicaMenuPrincipal.loop(Clip.LOOP_CONTINUOUSLY);
                musicaMenuPrincipal.start();
            }
            
            this.dispose();
        });
        panelPrincipal.add(btnCerrar);

        reproducirMusicaSecreta("Meowlnanzas_credits.wav"); 
    }

    private void reproducirMusicaSecreta(String ruta) {
        try {
            File file = new File("resources/" + ruta);
            if (file.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                musicaEasterEgg = AudioSystem.getClip();
                musicaEasterEgg.open(ais);
                musicaEasterEgg.loop(Clip.LOOP_CONTINUOUSLY); 
                
                if (musicaEasterEgg.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) musicaEasterEgg.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-4.0f);
                }
                musicaEasterEgg.start();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}