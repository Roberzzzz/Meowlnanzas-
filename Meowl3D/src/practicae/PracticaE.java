package practicae;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.image.TextureLoader;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Box;


public class PracticaE extends JPanel implements ItemListener {

    private Alpha rotarAlpha;
    private RotationInterpolator rotador;

    public PracticaE() {
        setLayout(new BorderLayout());
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas, BorderLayout.CENTER);

        SimpleUniverse universo = new SimpleUniverse(canvas);
        BranchGroup escena = createSceneGraph();
        escena.compile();

        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
    }

    public BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        TransformGroup mainTG = new TransformGroup();
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        
        int flags = Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS;
        // =======================
        // APARIENCIAS
        // =======================
        Appearance negro = crearApariencia(new Color3f(Color.BLACK));
        Appearance marron = crearApariencia(new Color3f(new Color(130, 69, 19)));
        
        // APARIENCIA DE LA CABEZA CON TEXTURA
        Appearance lookCabeza = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookOrejaIzq = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookOrejaDer = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookTronco = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookAlaIz = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookAlaDer = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookBack = crearApariencia(new Color3f(Color.WHITE));

        try {
           
            Texture textura = new TextureLoader("resources3D/gaticox.png", this).getTexture();
            lookCabeza.setTexture(textura);
            
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookCabeza.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen ojox.png, se usará color blanco sólido.");
        }

        // =======================
        // CUERPO (búho)
        // =======================
        
        try {
            
            Texture texTronco = new TextureLoader("resources3D/tronco.png", this).getTexture();
            lookTronco.setTexture(texTronco);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookTronco.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("Error cargando troncox.png, se usará color sólido.");
     
        }
        
        Transform3D tCuerpo = new Transform3D();
        tCuerpo.setScale(new Vector3d(1.0, 1.4, 0.8));
        TransformGroup cuerpoTG = new TransformGroup(tCuerpo);
        Sphere cuerpo = new Sphere(0.2f,flags,200, lookTronco);
        cuerpoTG.addChild(cuerpo);

        //Cabeza uwu
        Transform3D tCabeza = new Transform3D();
        tCabeza.setTranslation(new Vector3f(0f, 0.32f, 0f));
        Transform3D escalaCabeza = new Transform3D();
        escalaCabeza.setScale(new Vector3d(1.13, 1.1, 1));
        tCabeza.mul(escalaCabeza);
        TransformGroup cabezaTG = new TransformGroup(tCabeza);
        
        Sphere cabeza = new Sphere(0.13f, flags, 100, lookCabeza);
        cabezaTG.addChild(cabeza);

        // =======================
        // OREJAS (gato)
        // =======================
        try {
        
            Texture texOrejaIz = new TextureLoader("resources3D/orejaiz.png", this).getTexture();
            lookOrejaIzq.setTexture(texOrejaIz);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookOrejaIzq.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("Error cargando orejaiz.png, se usará color sólido.");
        }
        
        try {
            Texture texOrejaDer = new TextureLoader("resources3D/orejader.png", this).getTexture();
            lookOrejaDer.setTexture(texOrejaDer);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookOrejaDer.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("Error cargando orejasx.png, se usará color sólido.");
     
        }

        Transform3D tOreja1 = new Transform3D();
        tOreja1.setTranslation(new Vector3f(-0.1f, 0.46f, 0f));
        Transform3D rotOreja1 = new Transform3D();
        rotOreja1.rotZ(Math.toRadians(20)); 
        tOreja1.mul(rotOreja1);
        TransformGroup oreja1TG = new TransformGroup(tOreja1);
        Cone oreja1 = new Cone(0.04f, 0.1f,flags, lookOrejaIzq);
        oreja1TG.addChild(oreja1);

        
        
        Transform3D tOreja2 = new Transform3D();
        tOreja2.setTranslation(new Vector3f(0.1f, 0.46f, 0f));
        Transform3D rotOreja2 = new Transform3D();
        rotOreja2.rotZ(Math.toRadians(-20)); 
        tOreja2.mul(rotOreja2);
        TransformGroup oreja2TG = new TransformGroup(tOreja2);
        Cone oreja2 = new Cone(0.04f, 0.1f,flags, lookOrejaDer);
        oreja2TG.addChild(oreja2);

        // =======================
        // ALAS
        // =======================
        
        try {
        
            Texture texAlaIz = new TextureLoader("resources3D/alaxiz.png", this).getTexture();
            lookAlaIz.setTexture(texAlaIz);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookAlaIz.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("Error cargando alax.png, se usará color sólido.");
     
        }
        
        try {
            Texture texAlaDer = new TextureLoader("resources3D/alax.png", this).getTexture();
            lookAlaDer.setTexture(texAlaDer);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookAlaDer.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("Error cargando alax.png, se usará color sólido.");
     
        }
        
        Transform3D tAla1 = new Transform3D();
        tAla1.setTranslation(new Vector3f(-0.1f, 0.02f, 0f));
        Transform3D rotAla1 = new Transform3D();
        rotAla1.rotZ(Math.toRadians(-15));
        Transform3D rotAlaY1 = new Transform3D();
        rotAlaY1.rotY(Math.toRadians(10));
        tAla1.mul(rotAla1);
        tAla1.mul(rotAlaY1);
        Transform3D escalaAla = new Transform3D();
        escalaAla.setScale(new Vector3d(0.9, 1.7, 0.8));
        tAla1.mul(escalaAla);
        TransformGroup ala1TG = new TransformGroup(tAla1);
        Sphere ala1 = new Sphere(0.15f, flags, 100,lookAlaIz);
        ala1TG.addChild(ala1);

        Transform3D tAla2 = new Transform3D();
        tAla2.setTranslation(new Vector3f(0.1f, 0.02f, 0f));
        Transform3D rotAla2 = new Transform3D();
        rotAla2.rotZ(Math.toRadians(15));
        Transform3D rotAlaY2 = new Transform3D();
        rotAlaY2.rotY(Math.toRadians(-10));
        tAla2.mul(rotAla2);
        tAla2.mul(rotAlaY2);
        tAla2.mul(escalaAla);
        TransformGroup ala2TG = new TransformGroup(tAla2);
        Sphere ala2 = new Sphere(0.15f, flags, 100, lookAlaDer);
        ala2TG.addChild(ala2);

        // =======================
        // PATAS Y GARRAS
        // =======================
        Transform3D tPata1 = new Transform3D();
        tPata1.setTranslation(new Vector3f(-0.045f, -0.3f, 0f));
        TransformGroup pata1TG = new TransformGroup(tPata1);
        Cylinder pata1 = new Cylinder(0.02f, 0.1f, flags,lookTronco);
        pata1TG.addChild(pata1);

        Transform3D tPata2 = new Transform3D();
        tPata2.setTranslation(new Vector3f(0.045f, -0.3f, 0f));
        TransformGroup pata2TG = new TransformGroup(tPata2);
        Cylinder pata2 = new Cylinder(0.02f, 0.1f, flags, lookTronco);
        pata2TG.addChild(pata2);

        // PATAS BASE (Esferas)
        Transform3D tPataB1 = new Transform3D();
        tPataB1.setTranslation(new Vector3f(-0.045f, -0.35f, 0.02f));
        Transform3D escalaBase = new Transform3D();
        escalaBase.setScale(new Vector3d(1.13, 0.3, 1.4));
        tPataB1.mul(escalaBase);
        TransformGroup pata1BTG = new TransformGroup(tPataB1);
        Sphere pataB1 = new Sphere(0.035f, flags, lookTronco);
        pata1BTG.addChild(pataB1);

        Transform3D tPataB2 = new Transform3D();
        tPataB2.setTranslation(new Vector3f(0.045f, -0.35f, 0.02f));
        tPataB2.mul(escalaBase);
        TransformGroup pata2BTG = new TransformGroup(tPataB2);
        Sphere pataB2 = new Sphere(0.035f, flags, lookTronco);
        pata2BTG.addChild(pataB2);

        // GARRAS
        float[] posXGarras = {-0.02f, 0f, 0.015f};
        for (int i = 0; i < 3; i++) {
            mainTG.addChild(crearGarra(-0.045f + posXGarras[i], -0.35f, 0.068f, negro));
            mainTG.addChild(crearGarra(0.045f + posXGarras[i], -0.35f, 0.068f, negro));
        }

        // =======================
        // RAMA
        // =======================
        
        Transform3D tRama = new Transform3D();
        tRama.setTranslation(new Vector3f(0f, -0.38f, 0f));
        Transform3D escalaRama = new Transform3D();
        escalaRama.setScale(new Vector3d(2.0, 0.2, 0.2));
        tRama.mul(escalaRama);
        TransformGroup ramaTG = new TransformGroup(tRama);
        Box rama = new Box(0.2f, 0.1f, 0.2f, marron);
        ramaTG.addChild(rama);
                
        try {
            
            Texture texBack = new TextureLoader("resources3D/back.png", this).getTexture();
            lookBack.setTexture(texBack);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            lookBack.setTextureAttributes(texAttr);
        } catch (Exception e) {
            System.out.println("Error cargando orejasx.png, se usará color sólido.");
     
        }
        
        Transform3D tColax = new Transform3D();
        tColax.setTranslation(new Vector3f(0f, -0.05f, -0.09f));
        Transform3D escalaCola = new Transform3D();
        escalaCola.setScale(new Vector3d(1, 1.6, 0.4));
        tColax.mul(escalaCola);
        TransformGroup colaTG = new TransformGroup(tColax);
        
        Sphere cola = new Sphere(0.18f, flags, 100, lookBack);
        colaTG.addChild(cola);
        
        Transform3D tCola2x = new Transform3D();
        tCola2x.setTranslation(new Vector3f(0f, -0.14f, -0.085f));
        Transform3D escalaCola2 = new Transform3D();
        escalaCola2.setScale(new Vector3d(0.8, 1.6, 0.35));
        tCola2x.mul(escalaCola2);
        TransformGroup cola2TG = new TransformGroup(tCola2x);
        
        Sphere cola2 = new Sphere(0.18f, flags, 100, lookBack);
        cola2TG.addChild(cola2);
        
        mainTG.addChild(cuerpoTG);
        mainTG.addChild(cabezaTG);

        mainTG.addChild(oreja1TG);
        mainTG.addChild(oreja2TG);
        mainTG.addChild(ala1TG);
        mainTG.addChild(ala2TG);
        mainTG.addChild(pata1TG);
        mainTG.addChild(pata2TG);
        mainTG.addChild(ramaTG);
        mainTG.addChild(pata1BTG);
        mainTG.addChild(pata2BTG);
        mainTG.addChild(colaTG);
        mainTG.addChild(cola2TG);
        
        root.addChild(mainTG);

        // ILUMINACIÓN
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 100);
        AmbientLight luz = new AmbientLight(new Color3f(Color.WHITE));
        luz.setInfluencingBounds(bounds);
        root.addChild(luz);

        DirectionalLight luzDir = new DirectionalLight(new Color3f(Color.WHITE), new Vector3f(0f, -1f, -1f));
        luzDir.setInfluencingBounds(bounds);
        root.addChild(luzDir);
        
        /*
        // ROTACIÓN
        rotarAlpha = new Alpha(-1, 4000);
        Transform3D eje = new Transform3D();
        rotador = new RotationInterpolator(rotarAlpha, mainTG, eje, 0f, (float)(2*Math.PI));
        rotador.setSchedulingBounds(bounds);
        mainTG.addChild(rotador);
        */

        return root;
    }

    private TransformGroup crearGarra(float x, float y, float z, Appearance app) {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3f(x, y, z));
        Transform3D rot = new Transform3D();
        rot.rotX(Math.toRadians(90));
        t.mul(rot);
        TransformGroup tg = new TransformGroup(t);
        Cone garra = new Cone(0.008f, 0.03f, app);
        tg.addChild(garra);
        return tg;
    }

    private Appearance crearApariencia(Color3f color) {
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(color);
        mat.setSpecularColor(new Color3f(Color.WHITE));
        mat.setShininess(20f);
        mat.setLightingEnable(true);
        app.setMaterial(mat);
        return app;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            rotador.setEnable(true);
            rotarAlpha.resume();
        } else {
            rotador.setEnable(false);
            rotarAlpha.pause();
        }
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Meowl 3D");
        PracticaE panel = new PracticaE();
        JCheckBox animacion = new JCheckBox("Animación Activa");
        animacion.setSelected(true);
        animacion.addItemListener(panel);
        ventana.setLayout(new BorderLayout());
        ventana.add(animacion, BorderLayout.NORTH);
        ventana.add(panel, BorderLayout.CENTER);
        ventana.setSize(1200, 800);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}