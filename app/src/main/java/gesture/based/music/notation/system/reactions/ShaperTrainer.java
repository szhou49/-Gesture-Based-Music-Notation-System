package gesture.based.music.notation.system.reactions;

import gesture.based.music.notation.system.graphics.G;
import gesture.based.music.notation.system.graphics.Window;

import java.awt.*;
import java.awt.event.*;

public class ShaperTrainer extends Window {
    
    public static String UNKNOWN = "<- this name currently unknown";

    public static String ILLEGAL = "<- this name is not legal";

    public static String KNOWN = "<- this name is a known shape";

    public static String curName = "";

    public static String curState = ILLEGAL;

    public static Shape.Prototype.List pList = new Shape.Prototype.List();

    public ShaperTrainer() {
        super("ShaperTrainer", 1000, 700);
    }

    public void setState() {
        curState = (!Shape.Database.islegal(curName)) ? ILLEGAL : UNKNOWN;
        if (curState == UNKNOWN) {
            if (Shape.DB.containsKey(curName)) {
                curState = KNOWN;
                pList = Shape.DB.get(curName).prototypes;
            }
            else {
                pList = null;
            }
        }
    }

    public void paintComponent(Graphics g) {
        G.fillBackground(g, Color.WHITE);
        g.setColor(Color.BLACK);
        g.drawString(curName, 600, 30);
        g.drawString(curState, 700, 30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if (pList != null) {pList.show(g);}
    }

    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        System.out.println("typed " + c);
        curName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : curName + c;
        if (c == 0x0D || c == 0x0A) {
            Shape.Database.save();
        }
        setState();
        repaint();
    }

    public void mousePressed(MouseEvent me) {Ink.BUFFER.dn(me.getX(), me.getY()); repaint();}

    public void mouseDragged(MouseEvent me) {Ink.BUFFER.drag(me.getX(), me.getY()); repaint();}

    public void mouseReleased(MouseEvent me) {
        int H = Shape.Prototype.List.showBoxHeight;
        if (me.getY() < H) {
            int index = me.getX() / H;
            if (pList != null && index < pList.size()) {
                pList.remove(index);
            }
            repaint();
            return;
        }
        Ink ink = new Ink();
        Shape.DB.train(curName, ink.norm);
        setState();
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new ShaperTrainer();
        Window.launch();
    }
}
