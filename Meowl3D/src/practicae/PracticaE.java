package practicae; 

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.image.TextureLoader;
import java.awt.*;
import java.awt.event.*; 
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Box;

public class PracticaE extends JPanel implements ItemListener {

    private Alpha rotarAlpha;
    private RotationInterpolator rotador;
    private Alpha saludoAlphaDer;
    private Alpha saludoAlphaIzq;
    private Alpha flyAlpha;
    private Alpha maxwellAlpha; 

    public PracticaE() {
        setLayout(new BorderLayout());
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas, BorderLayout.CENTER);

        SimpleUniverse universo = new SimpleUniverse(canvas);
        BranchGroup escena = createSceneGraph();
        escena.compile();

        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);

        // ==========================================
        // CONTROL MOUSE: MODO MAXWELL
        // ==========================================
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                maxwellAlpha.resume();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                maxwellAlpha.pause();
                maxwellAlpha.setStartTime(System.currentTimeMillis() - maxwellAlpha.getTriggerTime());
                maxwellAlpha.setStartTime(System.currentTimeMillis()); 
            }
        });
    }

    public BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();
        BoundingSphere boundsAnim = new BoundingSphere(new Point3d(0,0,0), 100);

        TransformGroup maxwellTG = new TransformGroup();
        maxwellTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        maxwellAlpha = new Alpha(-1, 100);
        maxwellAlpha.setStartTime(System.currentTimeMillis());
        maxwellAlpha.pause();

        Transform3D ejePersonalizado = new Transform3D();

        RotationInterpolator maxwellRot = new RotationInterpolator(maxwellAlpha, maxwellTG, ejePersonalizado, 0.0f, (float)Math.PI*2.0f);
        maxwellRot.setSchedulingBounds(boundsAnim);
        maxwellTG.addChild(maxwellRot);

        TransformGroup mainTG = new TransformGroup();
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        int flags = Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS;

        // =======================
        // APARIENCIAS
        // =======================
        Appearance negro = crearApariencia(new Color3f(Color.BLACK));
        Appearance marron = crearApariencia(new Color3f(new Color(130, 69, 19)));
        
        Appearance lookCabeza = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookOrejaIzq = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookOrejaDer = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookTronco = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookAlaIz = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookAlaDer = crearApariencia(new Color3f(Color.WHITE));
        Appearance lookBack = crearApariencia(new Color3f(Color.WHITE));

        try {
            lookCabeza.setTexture(new TextureLoader("resources3D/gaticox.png", this).getTexture());
            lookCabeza.setTextureAttributes(new TextureAttributes(TextureAttributes.MODULATE, new Transform3D(), new Color4f(), TextureAttributes.FASTEST));
        } catch (Exception e) { System.out.println("Error textura cabeza"); }

        try {
            lookTronco.setTexture(new TextureLoader("resources3D/tronco.png", this).getTexture());
            lookTronco.setTextureAttributes(new TextureAttributes(TextureAttributes.MODULATE, new Transform3D(), new Color4f(), TextureAttributes.FASTEST));
        } catch (Exception e) { System.out.println("Error textura tronco"); }

        // =======================
        // CUERPO Y CABEZA
        // =======================
        Transform3D tCuerpo = new Transform3D();
        tCuerpo.setScale(new Vector3d(1.0, 1.4, 0.8));
        TransformGroup cuerpoTG = new TransformGroup(tCuerpo);
        cuerpoTG.addChild(new Sphere(0.2f, flags, 200, lookTronco));

        Transform3D tCabeza = new Transform3D();
        tCabeza.setTranslation(new Vector3f(0f, 0.32f, 0f));
        Transform3D escalaCabeza = new Transform3D();
        escalaCabeza.setScale(new Vector3d(1.13, 1.1, 1));
        tCabeza.mul(escalaCabeza);
        TransformGroup cabezaTG = new TransformGroup(tCabeza);
        cabezaTG.addChild(new Sphere(0.13f, flags, 100, lookCabeza));

        // =======================
        // OREJAS
        // =======================
        try {
            lookOrejaIzq.setTexture(new TextureLoader("resources3D/orejaiz.png", this).getTexture());
            lookOrejaDer.setTexture(new TextureLoader("resources3D/orejader.png", this).getTexture());
        } catch (Exception e) {}

        Transform3D tOreja1 = new Transform3D();
        tOreja1.setTranslation(new Vector3f(-0.1f, 0.46f, 0f));
        Transform3D rotOreja1 = new Transform3D();
        rotOreja1.rotZ(Math.toRadians(20)); 
        tOreja1.mul(rotOreja1);
        TransformGroup oreja1TG = new TransformGroup(tOreja1);
        oreja1TG.addChild(new Cone(0.04f, 0.1f, flags, lookOrejaIzq));

        Transform3D tOreja2 = new Transform3D();
        tOreja2.setTranslation(new Vector3f(0.1f, 0.46f, 0f));
        Transform3D rotOreja2 = new Transform3D();
        rotOreja2.rotZ(Math.toRadians(-20)); 
        tOreja2.mul(rotOreja2);
        TransformGroup oreja2TG = new TransformGroup(tOreja2);
        oreja2TG.addChild(new Cone(0.04f, 0.1f, flags, lookOrejaDer));

        // =======================
        // ALAS CON MOVIMIENTO
        // =======================
        try {
            lookAlaIz.setTexture(new TextureLoader("resources3D/alaxiz.png", this).getTexture());
            lookAlaDer.setTexture(new TextureLoader("resources3D/alax.png", this).getTexture());
        } catch (Exception e) {}

        Vector3d scaleVec = new Vector3d(0.9, 1.7, 0.8);

        Transform3D posAla1 = new Transform3D();
        posAla1.setTranslation(new Vector3f(-0.1f, 0.02f, 0f));
        TransformGroup ala1PosTG = new TransformGroup(posAla1);
        TransformGroup ala1RotTG = new TransformGroup();
        ala1RotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        saludoAlphaIzq = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 1000, 200, 0, 1000, 200, 0);
        RotationInterpolator saludoIz = new RotationInterpolator(saludoAlphaIzq, ala1RotTG, new Transform3D(), (float)Math.toRadians(-30), (float)Math.toRadians(30));
        saludoIz.setSchedulingBounds(boundsAnim);
        ala1RotTG.addChild(saludoIz);

        Transform3D tAla1Visual = new Transform3D();
        Transform3D rotBaseAla1 = new Transform3D();
        rotBaseAla1.rotZ(Math.toRadians(-15));
        tAla1Visual.mul(rotBaseAla1);
        tAla1Visual.setScale(scaleVec);
        TransformGroup ala1VisualTG = new TransformGroup(tAla1Visual);
        ala1VisualTG.addChild(new Sphere(0.15f, flags, 100, lookAlaIz));
        ala1RotTG.addChild(ala1VisualTG);
        ala1PosTG.addChild(ala1RotTG);

        Transform3D posAla2 = new Transform3D();
        posAla2.setTranslation(new Vector3f(0.1f, 0.02f, 0f));
        TransformGroup ala2PosTG = new TransformGroup(posAla2);
        TransformGroup ala2RotTG = new TransformGroup();
        ala2RotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        saludoAlphaDer = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 1000, 200, 0, 1000, 200, 0);
        RotationInterpolator saludoDer = new RotationInterpolator(saludoAlphaDer, ala2RotTG, new Transform3D(), (float)Math.toRadians(30), (float)Math.toRadians(-30));
        saludoDer.setSchedulingBounds(boundsAnim);
        ala2RotTG.addChild(saludoDer);

        Transform3D tAla2Visual = new Transform3D();
        Transform3D rotBaseAla2 = new Transform3D();
        rotBaseAla2.rotZ(Math.toRadians(15));
        tAla2Visual.mul(rotBaseAla2);
        tAla2Visual.setScale(scaleVec);
        TransformGroup ala2VisualTG = new TransformGroup(tAla2Visual);
        ala2VisualTG.addChild(new Sphere(0.15f, flags, 100, lookAlaDer));
        ala2RotTG.addChild(ala2VisualTG);
        ala2PosTG.addChild(ala2RotTG);

        // =======================
        // RESTO DEL CUERPO
        // =======================
        Transform3D tPata1 = new Transform3D();
        tPata1.setTranslation(new Vector3f(-0.045f, -0.3f, 0f));
        TransformGroup pata1TG = new TransformGroup(tPata1);
        pata1TG.addChild(new Cylinder(0.02f, 0.1f, flags, lookTronco));

        Transform3D tPata2 = new Transform3D();
        tPata2.setTranslation(new Vector3f(0.045f, -0.3f, 0f));
        TransformGroup pata2TG = new TransformGroup(tPata2);
        pata2TG.addChild(new Cylinder(0.02f, 0.1f, flags, lookTronco));

        Transform3D tPataB1 = new Transform3D();
        tPataB1.setTranslation(new Vector3f(-0.045f, -0.35f, 0.02f));
        Transform3D escalaBase = new Transform3D();
        escalaBase.setScale(new Vector3d(1.13, 0.3, 1.4));
        tPataB1.mul(escalaBase);
        TransformGroup pata1BTG = new TransformGroup(tPataB1);
        pata1BTG.addChild(new Sphere(0.035f, flags, lookTronco));

        Transform3D tPataB2 = new Transform3D();
        tPataB2.setTranslation(new Vector3f(0.045f, -0.35f, 0.02f));
        tPataB2.mul(escalaBase);
        TransformGroup pata2BTG = new TransformGroup(tPataB2);
        pata2BTG.addChild(new Sphere(0.035f, flags, lookTronco));

        float[] posXGarras = {-0.02f, 0f, 0.015f};
        for (int i = 0; i < 3; i++) {
            mainTG.addChild(crearGarra(-0.045f + posXGarras[i], -0.35f, 0.068f, negro));
            mainTG.addChild(crearGarra(0.045f + posXGarras[i], -0.35f, 0.068f, negro));
        }

        Transform3D tRama = new Transform3D();
        tRama.setTranslation(new Vector3f(0f, -0.38f, 0f));
        Transform3D escalaRama = new Transform3D();
        escalaRama.setScale(new Vector3d(2.0, 0.2, 0.2));
        tRama.mul(escalaRama);
        TransformGroup ramaTG = new TransformGroup(tRama);
        ramaTG.addChild(new Box(0.2f, 0.1f, 0.2f, marron));

        try { lookBack.setTexture(new TextureLoader("resources3D/back.png", this).getTexture()); } catch (Exception e) {}

        Transform3D tColax = new Transform3D();
        tColax.setTranslation(new Vector3f(0f, -0.05f, -0.09f));
        Transform3D escalaCola = new Transform3D();
        escalaCola.setScale(new Vector3d(1, 1.6, 0.4));
        tColax.mul(escalaCola);
        TransformGroup colaTG = new TransformGroup(tColax);
        colaTG.addChild(new Sphere(0.18f, flags, 100, lookBack));

        Transform3D tCola2x = new Transform3D();
        tCola2x.setTranslation(new Vector3f(0f, -0.14f, -0.085f));
        Transform3D escalaCola2 = new Transform3D();
        escalaCola2.setScale(new Vector3d(0.8, 1.6, 0.35));
        tCola2x.mul(escalaCola2);
        TransformGroup cola2TG = new TransformGroup(tCola2x);
        cola2TG.addChild(new Sphere(0.18f, flags, 100, lookBack));

        mainTG.addChild(cuerpoTG);
        mainTG.addChild(cabezaTG);
        mainTG.addChild(oreja1TG);
        mainTG.addChild(oreja2TG);
        mainTG.addChild(ala1PosTG);
        mainTG.addChild(ala2PosTG);
        mainTG.addChild(pata1TG);
        mainTG.addChild(pata2TG);
        mainTG.addChild(ramaTG);
        mainTG.addChild(pata1BTG);
        mainTG.addChild(pata2BTG);
        mainTG.addChild(colaTG);
        mainTG.addChild(cola2TG);
        
        maxwellTG.addChild(mainTG);
        root.addChild(maxwellTG);

        root.addChild(crearMosca(boundsAnim));

        // ILUMINACIÓN
        AmbientLight luz = new AmbientLight(new Color3f(Color.WHITE));
        luz.setInfluencingBounds(boundsAnim);
        root.addChild(luz);

        DirectionalLight luzDir = new DirectionalLight(new Color3f(Color.WHITE), new Vector3f(0f, -1f, -1f));
        luzDir.setInfluencingBounds(boundsAnim);
        root.addChild(luzDir);

        return root;
    }

    private TransformGroup crearMosca(BoundingSphere bounds) {
        TransformGroup orbitaTG = new TransformGroup();
        orbitaTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        flyAlpha = new Alpha(-1, 3000); 
        RotationInterpolator flyInterp = new RotationInterpolator(flyAlpha, orbitaTG);
        flyInterp.setSchedulingBounds(bounds);
        orbitaTG.addChild(flyInterp);

        Transform3D tMosca = new Transform3D();
        tMosca.setTranslation(new Vector3f(0.6f, 0.35f, 0f));        
        Transform3D rotOrientacion = new Transform3D();
        rotOrientacion.rotY(Math.toRadians(90));
        tMosca.mul(rotOrientacion);

        TransformGroup flyPosTG = new TransformGroup(tMosca);
      
        Appearance flyBodyApp = crearApariencia(new Color3f(0.1f, 0.1f, 0.1f)); 
        Appearance flyEyeApp = crearApariencia(new Color3f(0.6f, 0.0f, 0.0f));  
    
        Appearance flyWingApp = new Appearance();
        TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.6f);
        flyWingApp.setTransparencyAttributes(ta);
        Material matWing = new Material();
        matWing.setDiffuseColor(new Color3f(0.8f, 0.8f, 1.0f));
        flyWingApp.setMaterial(matWing);

        Transform3D tAbdo = new Transform3D();
        tAbdo.setTranslation(new Vector3f(-0.04f, 0f, 0f));
        tAbdo.setScale(new Vector3d(1.4, 1.0, 1.0));
        TransformGroup abdoTG = new TransformGroup(tAbdo);
        abdoTG.addChild(new Sphere(0.03f, flyBodyApp));
        flyPosTG.addChild(abdoTG);

        TransformGroup thoraxTG = new TransformGroup();
        thoraxTG.addChild(new Sphere(0.025f, flyBodyApp));
        flyPosTG.addChild(thoraxTG);
        
        Transform3D tHead = new Transform3D();
        tHead.setTranslation(new Vector3f(0.035f, 0f, 0f));
        TransformGroup headTG = new TransformGroup(tHead);
        headTG.addChild(new Sphere(0.018f, flyBodyApp));
        
        Transform3D tOjoI = new Transform3D();
        tOjoI.setTranslation(new Vector3f(0.005f, 0.005f, 0.012f));
        TransformGroup ojoITG = new TransformGroup(tOjoI);
        ojoITG.addChild(new Sphere(0.012f, flyEyeApp));
        headTG.addChild(ojoITG);

        Transform3D tOjoD = new Transform3D();
        tOjoD.setTranslation(new Vector3f(0.005f, 0.005f, -0.012f));
        TransformGroup ojoDTG = new TransformGroup(tOjoD);
        ojoDTG.addChild(new Sphere(0.012f, flyEyeApp));
        headTG.addChild(ojoDTG);
        
        flyPosTG.addChild(headTG);

        Transform3D tAlaI = new Transform3D();
        tAlaI.setTranslation(new Vector3f(-0.01f, 0.02f, 0.03f));
        tAlaI.setScale(new Vector3d(1.8, 0.1, 0.8));
        Transform3D rotAlaI = new Transform3D();
        rotAlaI.rotY(Math.toRadians(-25));
        tAlaI.mul(rotAlaI);
        TransformGroup alaITG = new TransformGroup(tAlaI);
        alaITG.addChild(new Box(0.03f, 0.005f, 0.03f, flyWingApp));
        flyPosTG.addChild(alaITG);

        Transform3D tAlaD = new Transform3D();
        tAlaD.setTranslation(new Vector3f(-0.01f, 0.02f, -0.03f));
        tAlaD.setScale(new Vector3d(1.8, 0.1, 0.8));
        Transform3D rotAlaD = new Transform3D();
        rotAlaD.rotY(Math.toRadians(25));
        tAlaD.mul(rotAlaD);
        TransformGroup alaDTG = new TransformGroup(tAlaD);
        alaDTG.addChild(new Box(0.03f, 0.005f, 0.03f, flyWingApp));
        flyPosTG.addChild(alaDTG);

        orbitaTG.addChild(flyPosTG);
        return orbitaTG;
    }

    private TransformGroup crearGarra(float x, float y, float z, Appearance app) {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3f(x, y, z));
        Transform3D rot = new Transform3D();
        rot.rotX(Math.toRadians(90));
        t.mul(rot);
        TransformGroup tg = new TransformGroup(t);
        tg.addChild(new Cone(0.008f, 0.03f, app));
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
        boolean activo = (e.getStateChange() == ItemEvent.SELECTED);
        if (activo) {
            saludoAlphaDer.resume();
            saludoAlphaIzq.resume();
            flyAlpha.resume();
        } else {
            saludoAlphaDer.pause();
            saludoAlphaIzq.pause();
            flyAlpha.pause();
        }
    }
    
    public Canvas3D getCanvas() {
        return (Canvas3D) getComponent(0);
    }

    //getter para que MenuPrincipal pueda adjuntar el MouseListener al canvas3D externo y controlar el giro de Meowl
    public Alpha getMaxwellAlpha() {
        return maxwellAlpha;
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Meowl 3D oiiiaoiia");
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