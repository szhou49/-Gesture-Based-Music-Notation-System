package gesture.based.music.notation.system.music;

import java.awt.Graphics;

import gesture.based.music.notation.system.reactions.Gesture;
import gesture.based.music.notation.system.reactions.Mass;
import gesture.based.music.notation.system.reactions.Reaction;

import static gesture.based.music.notation.system.music.Editor.PAGE;
public class Staff extends Mass{
    
    public Sys sys;

    public int iStaff;

    public Staff.Fmt fmt;

    public Staff(Sys sys, int iStaff, Staff.Fmt fmt) {
        super("BACK");
        this.sys = sys;
        this.iStaff = iStaff;
        this.fmt = fmt;

        addReaction(new Reaction("SW-SW") {// create a note head
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < PAGE.margins.left || x > PAGE.margins.right) {return UC.NoBid;}
                int H = Staff.this.H(), top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if (y < top || y > bot) {return UC.NoBid;}
                return 10;
            }

            public void act(Gesture g) {
                new Head(Staff.this, g.vs.xM(), g.vs.yM());
            }
        });

        addReaction(new Reaction("W-S") {// add QRest

            @Override
            public int bid(Gesture g) {
                int x = g.vs.xL(), y = g.vs.yM();
                if (x < PAGE.margins.left || x > PAGE.margins.right) {return UC.NoBid;}
                int H = Staff.this.H(), top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if (y < top || y > bot) {return UC.NoBid;}
                return 10;
            }

            @Override
            public void act(Gesture g) {
                Time time = Staff.this.sys.getTime(g.vs.xL());
                new Rest(Staff.this, time);
            }
            
        });

        addReaction(new Reaction("E-S") {// add QRest

            @Override
            public int bid(Gesture g) {
                int x = g.vs.xL(), y = g.vs.yM();
                if (x < PAGE.margins.left || x > PAGE.margins.right) {return UC.NoBid;}
                int H = Staff.this.H(), top = Staff.this.yTop() - H, bot = Staff.this.yBot() + H;
                if (y < top || y > bot) {return UC.NoBid;}
                return 10;
            }

            @Override
            public void act(Gesture g) {
                Time time = Staff.this.sys.getTime(g.vs.xL());
                (new Rest(Staff.this, time)) .incFlag();;
            }
            
        });
    }

    public int yTop() {return sys.staffTop(iStaff);}

    public int yBot() {return yTop() + fmt.height();}

    public int yLine(int line) {return yTop() + line * H();}

    public int lineOfY(int y) {
        int H = H();
        int bias = 100;
        int top = yTop() - H * bias;
        return (y - top + H/2)/H - bias; 
    }
    public int H() {return fmt.H;}

    //.......Staff.Fmt..............

    public static class Fmt {

        public int nLines = 5;

        public int H = 8;

        public int height() {return 2 * H * (nLines - 1);}

        public void showAt(Graphics g, int y) {
            int LEFT = PAGE.margins.left, RIGHT = PAGE.margins.right;

            for (int i = 0; i < nLines; i++) {
                g.drawLine(LEFT, y + 2 * H * i, RIGHT, y + 2 * H * i);
            }
        }
    }
}
