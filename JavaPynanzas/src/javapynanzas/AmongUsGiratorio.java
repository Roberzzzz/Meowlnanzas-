package javapynanzas;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.Color;

public class AmongUsGiratorio {

    public AmongUsGiratorio() {
        SimpleUniverse universo = new SimpleUniverse();
        universo.getViewingPlatform().setNominalViewingTransform();
        
        BranchGroup escena = crearEscena();
        escena.compile();
        universo.addBranchGraph(escena);
    }

    public BranchGroup crearEscena() {
        BranchGroup root = new BranchGroup();
        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 100.0);

        TransformGroup grupoGiro = new TransformGroup();
        grupoGiro.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(grupoGiro);

        Appearance cuerpoApp = crearApariencia(Color.RED, true);
        Appearance visorApp = crearApariencia(new Color(135, 206, 235), true);
        Appearance negroApp = crearApariencia(Color.BLACK, false);
        Appearance blancoApp = crearApariencia(Color.WHITE, false);
        Appearance blueApp = crearApariencia(Color.BLUE, false);
        Appearance lenguaApp = crearApariencia(new Color(255, 50, 100), true);

        // Geometría del among us
        grupoGiro.addChild(new Cylinder(0.25f, 0.4f, cuerpoApp));
        grupoGiro.addChild(crearTransform(0.0f, 0.2f, 0.0f, new Sphere(0.25f, cuerpoApp)));
        grupoGiro.addChild(crearTransform(0.0f, -0.2f, 0.0f, new Sphere(0.25f, cuerpoApp)));
        grupoGiro.addChild(crearTransform(0.0f, 0.45f, 0.0f, new Cone(0.18f, 0.35f, blueApp)));
        grupoGiro.addChild(crearTransform(0.0f, 0.62f, 0.0f, new Sphere(0.05f, blancoApp)));

        Transform3D tVisor = new Transform3D();
        tVisor.setScale(new Vector3d(1.1, 0.6, 0.5));
        tVisor.setTranslation(new Vector3f(0.0f, 0.1f, 0.22f));
        TransformGroup tgVisor = new TransformGroup(tVisor);
        tgVisor.addChild(new Sphere(0.18f, visorApp));
        grupoGiro.addChild(tgVisor);

        grupoGiro.addChild(crearBoca(negroApp, lenguaApp));
        grupoGiro.addChild(crearOjosYCejas(negroApp));
        grupoGiro.addChild(crearManoCompleta(-0.35f, -0.05f, 0.1f, cuerpoApp, true));
        grupoGiro.addChild(crearManoCompleta(0.35f, -0.05f, 0.1f, cuerpoApp, false));
        grupoGiro.addChild(crearPataLarga(-0.13f, -0.35f, cuerpoApp));
        grupoGiro.addChild(crearPataLarga(0.13f, -0.35f, cuerpoApp));
        grupoGiro.addChild(crearTransform(0.0f, -0.05f, -0.22f, new Box(0.15f, 0.22f, 0.08f, cuerpoApp)));

        Alpha alphaCuerpo = new Alpha(-1, 5000);
        RotationInterpolator spinCuerpo = new RotationInterpolator(alphaCuerpo, grupoGiro);
        spinCuerpo.setSchedulingBounds(bounds);
        grupoGiro.addChild(spinCuerpo);

        Appearance verdeApp = crearApariencia(Color.GREEN, true);
        root.addChild(crearSistemaMosquito(bounds, verdeApp));

        DirectionalLight luz = new DirectionalLight(new Color3f(Color.WHITE), new Vector3f(-1f, -1f, -1f));
        luz.setInfluencingBounds(bounds);
        root.addChild(luz);
        root.addChild(new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f)));

        return root;
    }

private TransformGroup crearSistemaMosquito(BoundingSphere bounds, Appearance appVerde) {
        TransformGroup grupoOrbita = new TransformGroup();
        grupoOrbita.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D transBase = new Transform3D();
        transBase.setTranslation(new Vector3f(0.0f, 0.2f, -0.7f)); 

        Transform3D rotYOrientacion = new Transform3D();
        rotYOrientacion.rotY(Math.toRadians(210)); 
        transBase.mul(rotYOrientacion);

        TransformGroup mosquitoNave = new TransformGroup(transBase);
        grupoOrbita.addChild(mosquitoNave);

        // Cuerpo y Aguja
        mosquitoNave.addChild(new Sphere(0.05f, appVerde));
        Transform3D tAguja = new Transform3D();
        tAguja.rotZ(Math.toRadians(90)); 
        tAguja.setTranslation(new Vector3f(-0.08f, 0.0f, 0.0f));
        TransformGroup tgAguja = new TransformGroup(tAguja);
        tgAguja.addChild(new Cone(0.015f, 0.08f, appVerde));
        mosquitoNave.addChild(tgAguja);
        
        //Ojos
        Appearance ojoApp = crearApariencia(Color.BLACK, false);
        // Ojo derecho e izquierdo 
        mosquitoNave.addChild(crearTransform(-0.035f, 0.02f, 0.02f, new Sphere(0.012f, ojoApp)));
        mosquitoNave.addChild(crearTransform(-0.035f, 0.02f, -0.02f, new Sphere(0.012f, ojoApp)));

        // Alas
        Appearance alaApp = crearApariencia(new Color(230, 230, 230), false);
        mosquitoNave.addChild(crearTransformConRotacionLateral(0.05f, 0.03f, 0.04f, -30, new Box(0.06f, 0.005f, 0.025f, alaApp)));
        mosquitoNave.addChild(crearTransformConRotacionLateral(0.05f, 0.03f, -0.04f, 30, new Box(0.06f, 0.005f, 0.025f, alaApp)));

        // Fila Izquierda (más cerca de la aguja)
        mosquitoNave.addChild(crearPataSimple(-0.018f, -0.04f, 0.03f, 20, appVerde));
        mosquitoNave.addChild(crearPataSimple(-0.018f, -0.04f, -0.03f, 20, appVerde));
        
        // Fila Central
        mosquitoNave.addChild(crearPataSimple(-0.002f, -0.05f, 0.03f, 0, appVerde));
        mosquitoNave.addChild(crearPataSimple(-0.002f, -0.05f, -0.03f, 0, appVerde));
        
        // Fila Derecha (hacia la parte de atrás)
        mosquitoNave.addChild(crearPataSimple(0.02f, -0.04f, 0.03f, -20, appVerde));
        mosquitoNave.addChild(crearPataSimple(0.02f, -0.04f, -0.03f, -20, appVerde));

        // Órbita
        Alpha alphaMosquito = new Alpha(-1, 8000); 
        RotationInterpolator orbit = new RotationInterpolator(alphaMosquito, grupoOrbita);
        orbit.setMinimumAngle(0.0f);
        orbit.setMaximumAngle(-(float)Math.PI * 2.0f); 
        orbit.setSchedulingBounds(bounds);
        grupoOrbita.addChild(orbit);

        return grupoOrbita;
    }

    private TransformGroup crearPataSimple(float x, float y, float z, float rot, Appearance appVerde) {
        Transform3D t = new Transform3D();
        t.rotZ(Math.toRadians(rot));
        t.setTranslation(new Vector3f(x, y, z));
        TransformGroup tg = new TransformGroup(t);
        tg.addChild(new Cylinder(0.003f, 0.05f, appVerde));
        return tg;
    }

    private TransformGroup crearBoca(Appearance negro, Appearance lengua) {
        TransformGroup g = new TransformGroup();
        g.addChild(crearTransformConEscala(0, -0.12f, 0.25f, 0.6, 0.3, 0.1, new Sphere(0.2f, negro)));
        g.addChild(crearTransformConEscala(0, -0.14f, 0.28f, 0.4, 0.15, 0.6, new Sphere(0.15f, lengua)));
        return g;
    }

    private TransformGroup crearOjosYCejas(Appearance negro) {
        TransformGroup g = new TransformGroup();
        g.addChild(crearTransformConEscala(-0.07f, 0.08f, 0.31f, 0.15, 0.35, 0.1, new Sphere(0.15f, negro)));
        g.addChild(crearTransformConEscala(0.07f, 0.08f, 0.31f, 0.15, 0.35, 0.1, new Sphere(0.15f, negro)));
        g.addChild(crearTransformConRotacionZ(-0.08f, 0.16f, 0.315f, -35, new Box(0.06f, 0.015f, 0.01f, negro)));
        g.addChild(crearTransformConRotacionZ(0.08f, 0.16f, 0.315f, 35, new Box(0.06f, 0.015f, 0.01f, negro)));
        return g;
    }

    private TransformGroup crearManoCompleta(float x, float y, float z, Appearance app, boolean izquierda) {
        TransformGroup grupoMano = crearTransform(x, y, z);
        grupoMano.addChild(new Sphere(0.068f, app));
        float offsetDedo = izquierda ? 0.04f : -0.04f;
        grupoMano.addChild(crearTransformConEscala(offsetDedo, 0.06f, 0.02f, 0.5, 1.2, 0.5, new Sphere(0.03f, app)));
        return grupoMano;
    }

    private TransformGroup crearPataLarga(float x, float y, Appearance app) {
        TransformGroup gp = crearTransform(x, y, 0.0f);
        gp.addChild(new Cylinder(0.09f, 0.4f, app));
        gp.addChild(crearTransform(0.0f, -0.2f, 0.0f, new Sphere(0.09f, app)));
        return gp;
    }

    private TransformGroup crearTransform(float x, float y, float z) {
        Transform3D t = new Transform3D(); t.setTranslation(new Vector3f(x, y, z));
        return new TransformGroup(t);
    }
    
    private TransformGroup crearTransform(float x, float y, float z, Node nodo) {
        TransformGroup tg = crearTransform(x, y, z); tg.addChild(nodo);
        return tg;
    }

    private TransformGroup crearTransformConEscala(float x, float y, float z, double sx, double sy, double sz, Node nodo) {
        Transform3D t = new Transform3D();
        t.setScale(new Vector3d(sx, sy, sz));
        t.setTranslation(new Vector3f(x, y, z));
        TransformGroup tg = new TransformGroup(t); tg.addChild(nodo);
        return tg;
    }

    private TransformGroup crearTransformConRotacionZ(float x, float y, float z, double grados, Node nodo) {
        Transform3D t = new Transform3D();
        t.setRotation(new AxisAngle4d(0, 0, 1, Math.toRadians(grados)));
        t.setTranslation(new Vector3f(x, y, z));
        TransformGroup tg = new TransformGroup(t); tg.addChild(nodo);
        return tg;
    }

    private TransformGroup crearTransformConRotacionLateral(float x, float y, float z, double grados, Node nodo) {
        Transform3D t = new Transform3D();
        t.setRotation(new AxisAngle4d(0, 1, 0, Math.toRadians(grados)));
        t.setTranslation(new Vector3f(x, y, z));
        TransformGroup tg = new TransformGroup(t); tg.addChild(nodo);
        return tg;
    }

    private Appearance crearApariencia(Color col, boolean brillo) {
        Appearance ap = new Appearance();
        Material m = new Material();
        m.setDiffuseColor(new Color3f(col));
        if(brillo) { m.setSpecularColor(new Color3f(Color.WHITE)); m.setShininess(110.0f); }
        ap.setMaterial(m);
        return ap;
    }

    public static void main(String[] args) {
        new AmongUsGiratorio();
    }
}