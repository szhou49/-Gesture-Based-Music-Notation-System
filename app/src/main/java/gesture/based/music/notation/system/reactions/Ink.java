package gesture.based.music.notation.system.reactions;

import java.awt.*;
import java.util.ArrayList;
import java.io.*;
import gesture.based.music.notation.system.music.I;
import gesture.based.music.notation.system.music.UC;
import gesture.based.music.notation.system.graphics.G;
import gesture.based.music.notation.system.graphics.G.BBox;

public class Ink implements I.Show{

    public static Buffer BUFFER = new Buffer();

    public Norm norm;

    public G.VS vs;

    public Ink(){
        /* 
        super(BUFFER.n);
        for (int i = 0; i < BUFFER.n; i++) {
            points[i].set(BUFFER.points[i]);
        }
        */

        /*
        super(UC.normSampleSize);
        Ink.BUFFER.subSample(this);
        G.V.T.set(BUFFER.bbox, TEMP);
        transform();
        G.V.T.set(TEMP, BUFFER.bbox.getNewVS());
        transform();
        */

        norm = new Norm(); //load from BUFFER
        vs = BUFFER.bbox.getNewVS();
    }

    @Override
    public void show(Graphics g) {
        g.setColor(UC.inkColor);
        norm.drawAt(g, vs);
    }
    //..........Norm..........

    public static class Norm extends G.PL implements Serializable{

        public static final int N = UC.normSampleSize, MAX = UC.normCoordinateMax;

        public static final G.VS NCS = new G.VS(0, 0, MAX, MAX); // normalized coordinate system

        public Norm() {
            super(N);
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bbox, NCS); // transform from bbox to normalized coordinate system
            transform(); // transform of points
        }

        public int dist(Norm norm) {
            int res = 0;
            for (int i = 0; i < N; i ++) {
                int dx = points[i].x - norm.points[i].x;
                int dy = points[i].y - norm.points[i].y;
                res += dx * dx + dy * dy;
            }
            return res;
        }
        
        public void blend(Norm norm, int blend) {
            for (int i = 0; i < N; i ++) {
                points[i].blend(norm.points[i], blend);
            }
        }

        public void drawAt(Graphics g, G.VS vs) {
            G.V.T.set(NCS, vs);
            for (int i = 1; i < N; i ++) {
                g.drawLine(points[i - 1].tx(), points[i - 1].ty(), points[i].tx(), points[i].ty());
            }
        }
    }

    //..........Buffer........

    public static class Buffer extends G.PL implements I.Show, I.Area {

        public static final int MAX = UC.inkBufferMax;

        public int n; // number of points in Buffer

        public BBox bbox = new BBox();

        private Buffer() {super(MAX);} //Singleton

        public void clear() {n = 0;}

        public void add(int x, int y) {
            if (n < MAX) {
                points[n++].set(x, y); 
            }
            bbox.add(x, y);
        }

        public void show(Graphics g) {this.drawN(g, n);}

        public boolean hit(int x, int y) {return true;}

        public void dn(int x, int y) {
            clear(); 
            add(x, y); 
            bbox.set(x, y);
        }

        public void subSample(G.PL pl) {
            int k = pl.size(); // number of points in pl
            for (int i = 0; i < k; i ++) {
                pl.points[i].set(this.points[i * (n - 1) / (k - 1)]);
            }
        }

        public void drag(int x, int y) {add(x, y);}

        public void up(int x, int y) {add(x, y);}
    }

    //..........List..........

    public static class List extends ArrayList<Ink> implements I.Show {
        
        public void show(Graphics g) {for (Ink ink: this) {ink.show(g);}}
    }
    
}
