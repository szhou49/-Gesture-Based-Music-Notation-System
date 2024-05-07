package gesture.based.music.notation.system.music;

import gesture.based.music.notation.system.reactions.Gesture;
import gesture.based.music.notation.system.reactions.Mass;
import gesture.based.music.notation.system.reactions.Reaction;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head>{
    
    public Staff staff;
    public int line;
    public Time time;
    public Glyph forcedGlyph = null;
    public Stem stem = null; // can be null
    public boolean wrongSide = false;

    public Head(Staff staff, int x, int y) {
        super("NOTE");
        this.staff = staff;
        this.time = staff.sys.getTime(x);
        time.heads.add(this);
        /*int H = staff.H();
        int top = staff.yTop() - H;
        this.line = (y - top + H/2)/H - 1; 
        */
        this.line = staff.lineOfY(y);
        System.out.println("Line: " + line);
        addReaction(new Reaction("S-S") {

            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int w = Head.this.w(), hY = Head.this.y();
                if (y1 > y || y2 < y) {return UC.NoBid;}
                int hL = Head.this.time.x, hR = hL + w;
                if (x < hL - 2*w || x > hR + 2*w) {return UC.NoBid;}
                if (x < hL + w/2) {return hL - x;}
                if (x > hR - w/2) {return x - hR;}
                return UC.NoBid;
            }

            @Override
            public void act(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                Staff staff = Head.this.staff;
                Time t = Head.this.time;
                int w = Head.this.w();
                boolean up = (x > t.x + w/2);
                if (Head.this.stem == null) {
                    Stem.getStem(staff, t, y1, y2, up);
                }
                else {t.unStemHeads(y1, y2);}
            }
            
        });

        addReaction(new Reaction("DOT") {

            @Override
            public int bid(Gesture g) {
                int xH = Head.this.x(), yH = Head.this.y(), H = Head.this.staff.H(), w = Head.this.w();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xH || x > xH + 2*w || y < yH - H || y > yH + H) {return UC.NoBid;}
                return Math.abs(xH + w - x) + Math.abs(yH - y);
            }

            @Override
            public void act(Gesture g) {
                if (Head.this.stem != null) {
                    Head.this.stem.cycleDot();
                }
            }
            
        });
    }

    public void show(Graphics g) {
        int H = staff.H();
        (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
        if (stem != null) {
            int off = UC.gapRestToFirstDot, sp = UC.gapBetweenAugDot;
            for (int i = 0; i < stem.nDot; i++) {
                g.fillOval(time.x + off + i * sp, y() - 3*H/2, H*2/3, H*2/3);
            }
        }
    }

    public int y() {return staff.yLine(line);}

    public int x() {
        int res = time.x;
        if (wrongSide) {
            res += (stem != null && stem.isUp) ? w() : - w();
        }
        return res;
    }

    public Glyph normalGlyph() {
        if (stem == null) {return Glyph.HEAD_Q;}
        if (stem.nFlag == -1) {return Glyph.HEAD_HALF;}
        if (stem.nFlag == -2) {return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }

    public void deleteMass() {
        // stub
        time.heads.remove(this);
    }

    public void unStem() {
        if (stem != null) {
            stem.heads.remove(this);
            if (stem.heads.size() == 0) {stem.deleteStem();}
            stem = null; 
            wrongSide = false;
        }
    }

    public void joinStem(Stem s) {
        if (stem != null) {unStem();}
        s.heads.add(this);
        stem = s;
    }

    public int w() {return 24 * staff.H() / 10;}

    @Override
    public int compareTo(Head h) {
        return (this.staff.iStaff != h.staff.iStaff) ? this.staff.iStaff - h.staff.iStaff : this.line - h.line;
    }

    //..............List.........

    public static class List extends ArrayList<Head> {
        
    }
}
