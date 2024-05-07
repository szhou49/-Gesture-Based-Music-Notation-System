package gesture.based.music.notation.system.graphics;

import java.awt.*;
import java.util.Random;
import java.io.*;

public class G {
    public static Random RND = new Random();

    public static int rnd(int max) {
        return RND.nextInt(max);
    } //JS style

    public static Color rndColor() {
        return new Color(rnd(256),rnd(256),rnd(256));
    }

    public static void drawCircle(Graphics g, int x, int y, int r ) {
        g.drawOval(x - r, y - r, r + r, r + r);
    }

    public static void fillBackground(Graphics g, Color c) {
        g.setColor(c);
        g.fillRect(0, 0, 5000, 5000);
    }
    
    public static class V implements Serializable{
        public static Transform T = new Transform();

        public int x, y;

        public V(int x, int y) {
            set(x, y);
        }

        public V(V v){
            set(v.x, v.y);
        }

        public void set(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void set(V v) {
            this.x = v.x;
            this.y = v.y;
        }

        public void blend(V v, int k) {
            set((k*x + v.x) / (k + 1), (k*y + v.y) / (k + 1));
        }

        public void setT(V v) {
            set(v.tx(), v.ty());
        }

        public int tx() {
            return x * T.n / T.d + T.dx;
        }

        public int ty() {
            return y * T.n / T.d + T.dy;
        }
        public void add(V v){
            x += v.x;
            y += v.y;
        }

        public static class Transform {
            int dx, dy, n, d;

            public void set(VS ovs, VS nvs) {
                setScale(ovs.size.x, ovs.size.y, nvs.size.x, nvs.size.y);
                dx = setOff(ovs.loc.x, ovs.size.x, nvs.loc.x, nvs.size.x);
                dy = setOff(ovs.loc.y, ovs.size.y, nvs.loc.y, nvs.size.y);
            }

            public void set(BBox from, VS to) {
                setScale(from.h.size(), from.v.size(), to.size.x, to.size.y);
                dx = setOff(from.h.lo, from.h.size(), to.loc.x, to.size.x);
                dy = setOff(from.v.lo, from.v.size(), to.loc.y, to.size.y);
            }

            public void setScale(int oW, int oH, int nW, int nH) {
                n = (nW > nH) ? nW: nH;
                d = (oW > oH) ? oW: oH;
            }
            
            public int setOff(int oX, int oW, int nX, int nW) {
                return (-oX - oW / 2 ) * n / d + nX + nW/ 2;
            }
        }
    }
    
    public static class VS implements Serializable {
        public V loc, size;

        public VS(int x, int y, int w, int h) {
            loc = new V(x, y);
            size = new V(w, h);

        }

        public void fill(Graphics g, Color c) {
            g.setColor(c);
            g.fillRect(loc.x, loc.y, size.x, size.y);
        }

        public boolean hit(int x, int y) {
            return loc.x <= x && loc.y <= y && x <= (loc.x + size.x) && y <= (loc.y + size.y);
        }

        public int xL() {return loc.x;}

        public int xM() {return loc.x + size.x/2;}

        public int xH() {return loc.x + size.x;}

        public int yL() {return loc.y;}

        public int yM() {return loc.y + size.y/2;}

        public int yH() {return loc.y + size.y;}
    }
    
    //..................LoHi.................

    public static class LoHi implements Serializable{
        public int lo, hi;

        public LoHi(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
        }

        public void set(int val) {
            lo = val;
            hi = val;
        }

        public void add(int val) {
            if (val < lo) {
                lo = val;
            }
            if (val > hi) {
                hi = val;
            }
        }

        public int size() {return hi - lo > 0 ? hi - lo: 1;}
    }

    //..................BBox.................

    public static class BBox implements Serializable{
        public LoHi h, v; // horizontal range and vertical range

        public BBox() {
            h = new LoHi(0, 0);
            v = new LoHi(0, 0);
        }

        public void set(int x, int y) {
            h.set(x);
            v.set(y);
        }

        public void add(int x, int y) {
            h.add(x);
            v.add(y);
        }

        public void add(V v) {
            add(v.x, v.y);
        }

        public VS getNewVS() {
            return new VS(h.lo, v.lo, h.size(), v.size());
        }

        public void draw(Graphics g) {
            g.drawRect(h.lo, v.lo, h.size(), v.size());
        } 
    }

    //..................PL.................

    public static class PL implements Serializable{
        public V[] points;

        public PL(int n) {
            points = new V[n]; // Allicate an array
            for (int i = 0; i < n; i ++) {
                points[i] = new V(0, 0);
            }
        }

        public int size(){return points.length;}

        public void transform() {
            for (int i = 0; i < points.length; i ++) {
                points[i].setT(points[i]);
            }
        }

        public void drawN(Graphics g, int n) {
            g.setColor(Color.BLACK);
            for (int i = 1; i < n; i ++ ) {
                g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
            }
            drawNDots(g, n);
        }

        public void draw(Graphics g) {drawN(g, size());}

        public void drawNDots(Graphics g, int n) {
            g.setColor(Color.BLUE);
            for (int i = 0; i < n; i ++) {
                drawCircle(g, points[i].x, points[i].y, 4);
            }
        }
    }

    
}
