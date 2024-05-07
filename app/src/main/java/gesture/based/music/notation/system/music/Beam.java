package gesture.based.music.notation.system.music;

import gesture.based.music.notation.system.reactions.Mass;
import java.awt.*;

public class Beam extends Mass{

    public Stem.List stems = new Stem.List();

    public static Polygon poly;

    static {
        int[] foo = {0, 0, 0, 0};
        poly = new Polygon(foo, foo, 4);
    }

    public static void setPoly(int x1, int y1, int x2, int y2, int h) {
        int[] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        int[] b = poly.ypoints;
        b[0] = y1; b[1] = y2; b[2] = y2 + h; b[3] = y1 + h;
    }

    public Beam(Stem f, Stem l) {
        super("NOTE");
        addStem(f);
        addStem(l);
    }

    public Stem first() {return stems.get(0);}

    public Stem last() {return stems.get(stems.size() - 1);}

    public void deleteBeam() {
        for (Stem s:stems) {
            s.beam = null;
        }
        deleteMass();
    }

    public void addStem(Stem s) {
        if (s.beam == null) {
            stems.add(s);
            s.beam = this;
            stems.sort();
            s.nFlag = 1; 
        }
    }

    public static int yOfX(int x, int x1, int y1, int x2, int y2) {
        int dy = y2 - y1, dx = x2 - x1;
        return (x - x1) * dy/dx + y1;
    }

    public static int mx1, my1, mx2, my2; // coordinates for master space beam

    public static int yOfX(int x) {return yOfX(x, mx1, my1, mx2, my2);}

    public static void setMasterBeam(int x1, int y1, int x2, int y2) {
        mx1 = x1;
        my1 = y1;
        mx2 = x2;
        my2 = y2;
    }

    public static boolean verticalLineCrossedSegment(int x, int y1, int y2, int bx, int by, int ex, int ey) {
        if (x < bx || x > ex) {return false;}
        int y = yOfX(x, bx, by, ex, ey);
        if (y1 < y2) {return y1 < y &&  y < y2;}
        return y2 < y && y < y1;
    }

    public void setMasterBeam() {
        mx1 = first().x();
        my1 = first().yBeamEnd();
        mx2 = last().x();
        my2 = last().yBeamEnd();
    }

    public void show(Graphics g) {
        g.setColor(Color.BLACK);
        drawBeamGroup(g);
    }

    public void drawBeamGroup(Graphics g) {
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.H(), sH = firstStem.isUp ? H : -H; // signed H
        int nPrev = 0, nCur = firstStem.nFlag, nNext = stems.get(1).nFlag;
        int px;
        int cx = firstStem.x();
        int bx = cx + 3*H;
        if (nCur > nNext) {drawBeamStack(g, nNext, nCur, cx, bx, sH);}
        for (int cur = 1; cur < stems.size(); cur++) {
            Stem scur = stems.get(cur);
            px = cx;
            cx = scur.x();
            nPrev = nCur;
            nCur = nNext;
            nNext = (cur < (stems.size() - 1)) ? stems.get(cur + 1).nFlag : 0;
            int nBack = Math.min(nPrev, nCur);
            drawBeamStack(g, 0, nBack, px, cx, sH);
            if (nCur > nPrev && nCur > nNext) {
                if (nPrev < nNext) {
                    bx = cx + 3*H;
                    drawBeamStack(g, nNext, nCur, cx, bx, sH);
                } else {
                    bx = cx - 3*H;
                    drawBeamStack(g, nPrev, nCur, bx, cx, sH);
                }
            }
        }
    }

    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int h) {
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for (int i = n1; i < n2; i++) {
            setPoly(x1, y1 + i*2*h, x2, y2 + i*2*h, h);
            g.fillPolygon(poly);
        }
    }
}
