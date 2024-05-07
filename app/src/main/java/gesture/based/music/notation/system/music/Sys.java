package gesture.based.music.notation.system.music;

import static gesture.based.music.notation.system.music.Editor.PAGE;
import java.awt.Graphics;
import java.util.ArrayList;

import gesture.based.music.notation.system.reactions.Gesture;
import gesture.based.music.notation.system.reactions.Mass;
import gesture.based.music.notation.system.reactions.Reaction;

public class Sys extends Mass{
    
    public Stem.List stems = new Stem.List();
    public Time.List times;
    public ArrayList<Staff> staffs = new ArrayList<Staff>();
    public Page page = PAGE;
    public int iSys;
    public Sys.Fmt fmt;

    public Sys(int iSys, Sys.Fmt fmt) {
        super("BACK");
        this.iSys = iSys;
        this.fmt = fmt;
        for (int i = 0; i < fmt.size(); i++) {
            staffs.add(new Staff(this, i, fmt.get(i)));
        }
        this.times = new Time.List(this);

        addReaction(new Reaction("E-E") {

            @Override
            public int bid(Gesture g) {
                int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
                if (stems.fetchReject(y1, y2)) {return UC.NoBid;}
                ArrayList<Stem> temp = stems.allIntersector(x1, y1, x2, y2);
                if (temp.size() < 2) {return UC.NoBid;}
                System.out.println("Crossed: " + temp.size() + " stems");
                Beam b = temp.get(0).beam;
                for (Stem s:temp) {
                    if (s.beam != b) {return UC.NoBid;}
                }
                System.out.println("All stems share same beam");
                if (b == null && temp.size() != 2) {return UC.NoBid;}
                if (b != null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)) {return UC.NoBid;}
                return 50;
            }

            @Override
            public void act(Gesture g) {
                int x1 = g.vs.xL(), y1 = g.vs.yL(), x2 = g.vs.xH(), y2 = g.vs.yH();
                ArrayList<Stem> temp = stems.allIntersector(x1, y1, x2, y2);
                Beam b = temp.get(0).beam;
                if (b == null) {
                    new Beam(temp.get(0), temp.get(1));
                } else {
                    for (Stem s:temp) {s.incFlag();}
                }
            }
            
        });
    }

    public void show(Graphics g) {
        int y = yTop(), x = PAGE.margins.left;
        g.drawLine(x, y, x, y + fmt.height());
    }

    public Time getTime(int x) {return times.getTime(x);}

    public int yTop() {return page.sysTop(iSys);}

    public int staffTop(int iStaff) {return yTop() + fmt.staffOffset.get(iStaff);} 

    //................Sys.Fmt............

    public static class Fmt extends ArrayList<Staff.Fmt>{

        public ArrayList<Integer> staffOffset = new ArrayList<Integer>();

        public int height() {
            int last = size() - 1;
            return staffOffset.get(last) + get(last).height();
        }

        public void showAt(Graphics g, int y) {
            for (int i = 0; i < size(); i++) {
                get(i).showAt(g, y + staffOffset.get(i));
            }
        }
    }
}
