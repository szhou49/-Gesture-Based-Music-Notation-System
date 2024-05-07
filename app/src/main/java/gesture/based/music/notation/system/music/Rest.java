package gesture.based.music.notation.system.music;

import java.awt.*;

import gesture.based.music.notation.system.reactions.Gesture;
import gesture.based.music.notation.system.reactions.Reaction;

public class Rest extends Duration {

    public Staff staff;
    public Time time;
    public int line = 4;

    public Rest(Staff staff, Time time) {
        this.staff = staff;
        this.time = time;

        addReaction(new Reaction("E-E") {

            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.NoBid;}
                return Math.abs(y - Rest.this.staff.yLine(4));
            }

            @Override
            public void act(Gesture g) {Rest.this.incFlag();}
            
        });

        addReaction(new Reaction("W-W") {

            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.NoBid;}
                return Math.abs(y - Rest.this.staff.yLine(4));
            }

            @Override
            public void act(Gesture g) {Rest.this.decFlag();}
            
        });

        addReaction(new Reaction("DOT") {

            @Override
            public int bid(Gesture g) {
                int yR = Rest.this.y();
                int xR = Rest.this.time.x;
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xR || x > xR + 40 || y < yR - 40 || y > yR + 40) {return UC.NoBid;}
                return Math.abs(x - xR) + Math.abs(y - yR);
            }

            @Override
            public void act(Gesture g) {Rest.this.cycleDot();}
            
        });
    }

    public int y() {return staff.yLine(line);} 
    
    public void show(Graphics g) {
        int H = staff.H(), y = y();
        if (nFlag == -2) {Glyph.REST_W.showAt(g, H, time.x, y);}
        if (nFlag == -1) {Glyph.REST_H.showAt(g, H, time.x, y);}
        if (nFlag == 0) {Glyph.REST_Q.showAt(g, H, time.x, y);}
        if (nFlag == 1) {Glyph.REST_1F.showAt(g, H, time.x, y);}
        if (nFlag == 2) {Glyph.REST_2F.showAt(g, H, time.x, y);}
        if (nFlag == 3) {Glyph.REST_3F.showAt(g, H, time.x, y);}
        if (nFlag == 4) {Glyph.REST_4F.showAt(g, H, time.x, y);}
        int xO = UC.gapRestToFirstDot, sp = UC.gapBetweenAugDot;
        for (int i = 0; i < nDot; i++) {
            g.fillOval(time.x + xO + i * sp, y - 3 * H / 2, H * 2 /3, H * 2 /3);
        }
    }
}
