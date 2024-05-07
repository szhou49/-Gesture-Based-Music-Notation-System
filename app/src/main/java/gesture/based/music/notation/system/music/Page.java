package gesture.based.music.notation.system.music;

import static gesture.based.music.notation.system.music.Editor.PAGE;
import java.awt.Graphics;
import java.util.ArrayList;
import gesture.based.music.notation.system.reactions.Gesture;
import gesture.based.music.notation.system.reactions.Mass;
import gesture.based.music.notation.system.reactions.Reaction;

public class Page extends Mass{
    
    public Margins margins = new Margins();

    public Sys.Fmt sysFmt;

    public int sysGap, nSys;

    public ArrayList<Sys> sysList = new ArrayList<Sys>();

    public Page(Sys.Fmt sysFmt) {
        super("BACK");
        this.sysFmt = sysFmt;

        addReaction(new Reaction("E-W") {
            public int bid(Gesture gesture) {
                int y = gesture.vs.yM();
                if (y <= PAGE.margins.top + sysFmt.height() + 30) {return UC.NoBid;}
                return 0;
            }

            public void act(Gesture gesture) {
                int y = gesture.vs.yM();
                PAGE.addNewStaff(y - PAGE.margins.top);
            }
        });

        addReaction(new Reaction("E-E") {
            // add new system
            public int bid(Gesture gesture) {
                int y = gesture.vs.yM();
                int yBot = PAGE.sysTop(sysList.size());
                if (y <= yBot) {return UC.NoBid;}
                return 50;
            }

            public void act(Gesture gesture) {
                int y = gesture.vs.yM();
                if (PAGE.sysList.size() == 1) {
                    PAGE.sysGap = y - PAGE.sysTop(1);
                }
                PAGE.addNewSys();
            }
        });
    }

    public void addNewSys() {
        sysList.add(new Sys(sysList.size(), sysFmt));

    }

    public void addNewStaff(int yOff) {
        Staff.Fmt sf = new Staff.Fmt();
        int n = sysFmt.size();
        sysFmt.add(new Staff.Fmt());
        sysFmt.staffOffset.add(yOff);
        for (int i = 0; i < sysList.size(); i++) {
            Sys sys = sysList.get(i);
            sys.staffs.add(new Staff(sys, n, sf));
        }
    }

    public int sysTop(int iSys) {
        return margins.top + iSys * (sysFmt.height() + sysGap);
    }

    public void show(Graphics g) {
        for (int i = 0; i < sysList.size(); i++) {
            sysFmt.showAt(g, sysTop(i));
        }
    }

    //............Margins............

    public static class Margins {

        private static int MM = 50;

        public int top = MM;

        public int left = MM;

        public int bot = UC.mainWindowHeight - MM;

        public int right = UC.mainWindowWidth - MM;
    }
}
