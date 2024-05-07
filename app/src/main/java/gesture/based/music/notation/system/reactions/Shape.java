package gesture.based.music.notation.system.reactions;

import java.util.*;
import java.awt.*;
import gesture.based.music.notation.system.graphics.G;
import gesture.based.music.notation.system.music.UC;
import java.io.*;

public class Shape implements Serializable{

    public static Database DB = Database.load();

    public static Shape DOT = DB.get("DOT");

    // collections is backed by DB, any changes to DB show up in collection for convenience
    public static Collection<Shape> LIST = DB.values();

    public String name;

    public Prototype.List prototypes = new Prototype.List();

    public Shape(String name) {this.name = name;}

    //...............Prototype.....................

    public static class Prototype extends Ink.Norm implements Serializable {

        public int nBlend;
        
        public void blend(Ink.Norm norm) {
            blend(norm, nBlend++);
        }

        //................List....................

        public static class List extends ArrayList<Prototype> implements Serializable{

            public static Prototype bestMatch; // set by best distance

            public int bestDist(Ink.Norm norm) {
                bestMatch = null;
                int bestSoFar = UC.noMatchDistance;
                for (Prototype p:this) {
                    int d = p.dist(norm);
                    if (d < bestSoFar) {
                        bestMatch = p; 
                        bestSoFar = d;
                    }
                }
                return bestSoFar;
            }

            public void train(Ink.Norm norm) {
                if (bestDist(norm) < UC.noMatchDistance) {
                    bestMatch.blend(norm); // we found a match so blend
                } else {
                    add(new Shape.Prototype()); // No match new one created from ink buffer
                }
            }

            private static int m = 10, w = 60;

            private static G.VS showBox = new G.VS(m, m, w, w);

            public static final int showBoxHeight= m + w;

            public void show(Graphics g) {
                // draw a list of boxes on top of the screen

                g.setColor(Color.ORANGE);
                for (int i = 0; i < size(); i ++) {
                    Prototype p = get(i);
                    int x = m + i * (m + w);
                    showBox.loc.set(x, m);
                    p.drawAt(g, showBox);
                    g.drawString("" + p.nBlend, x, 20);
                }
            }
        }
    }

    //.................Database.....................

    public static class Database extends HashMap<String, Shape> {

        public Database() {
            put("DOT", new Shape("DOT"));
        }

        public Shape forceGet(String name) {
            if (!containsKey(name)) {
                put(name, new Shape(name));
            }
            return get(name);
        }

        public void train(String name, Ink.Norm norm) {
            if (islegal(name)) {
                forceGet(name).prototypes.train(norm);
            }
        }

        public static boolean islegal(String name) {
            return !name.equals("") && !name.equals("DOT");
        }

        public static Database load() {
            String fileName = UC.ShapeDBFile;

            Database res = null;

            try {
                System.out.println("attempting DB load..");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
                res =(Database) ois.readObject();
                System.out.println("Successful load - found" + res.keySet());
                ois.close();
            } 
            catch (Exception e) {
                System.out.println("Load failed.");
                System.out.println(e);
                res = new Database();
            }
            return res;
        }

        public static void save() {
            String fileName = UC.ShapeDBFile;
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
                oos.writeObject(DB);
                System.out.println("Save " + fileName);
                oos.close();
            }
            catch (Exception e) {
                System.out.println("Save DB failed.");
                System.out.println(e);
            }
        }
    }

    public static Shape recognize(Ink ink) {
        if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold) {
            return DOT;
        }
        Shape bestMatch = null;
        int bestSoFar = UC.noMatchDistance;
        for (Shape s:LIST) {
            int d = s.prototypes.bestDist(ink.norm);
            if (d < bestSoFar) {
                bestMatch = s; 
                bestSoFar = d;
            }
        }
        if(bestMatch == null){System.out.println("Recognize NOTHING");}else{System.out.println("Recognize "+bestMatch.name);}
        return bestMatch;
    }
}