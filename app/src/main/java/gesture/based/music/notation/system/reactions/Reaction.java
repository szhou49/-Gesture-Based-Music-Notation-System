package gesture.based.music.notation.system.reactions;

import gesture.based.music.notation.system.music.I;
import gesture.based.music.notation.system.music.UC;
import java.util.*;

public abstract class Reaction implements I.React{
    
    public Shape shape;

    private static Map byShape = new Map();

    public static List initialReactions = new List(); // Use by Undo to restart every thing

    public Reaction(String shapeName) {
        shape = Shape.DB.get(shapeName);
        if (shape == null) {
            System.out.println("WTF? Shape DB does not know: " + shapeName);
        }
    }

    public void enable() {
        List list = byShape.getList(shape);
        if (!list.contains(this)) {
            list.add(this);
        }
    }

    public void disable() {
        List list = byShape.getList(shape);
        list.remove(this);
    }

    public static Reaction best(Gesture g) {
        return byShape.getList(g.shape).lowBid(g);
    }

    public static void nuke() {
        // used to reset UNDO
        byShape = new Map();
        initialReactions.enable();
    }

    //..................List............................

    public static class List extends ArrayList<Reaction> {

        public void addReaction(Reaction r) {
            add(r);
            r.enable();
        }

        public void removeReaction(Reaction r) {
            remove(r);
            r.disable();
        }

        public void clearAll() {
            for (Reaction r:this) {
                r.disable();
            }
            clear();
        }

        public Reaction lowBid(Gesture g) {
            // can return null
            Reaction res = null;
            int bestSoFar = UC.NoBid;
            for (Reaction r:this) {
                int b = r.bid(g);
                if (b < bestSoFar) {
                    bestSoFar = b;
                    res = r;
                }
            }
            return res;
        }

        public void enable() {
            for (Reaction r:this) {
                r.enable();
            }
        }
    }

    //..................Map............................

    public static class Map extends HashMap<Shape, Reaction.List> {
        
        public List getList(Shape s) {
            // Always succeed
            List res = get(s);
            if (res == null) {
                res = new List();
                put(s, res);
            }
            return res;
        }
    }
}
