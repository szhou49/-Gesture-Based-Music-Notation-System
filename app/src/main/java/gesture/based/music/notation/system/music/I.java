package gesture.based.music.notation.system.music;
import java.awt.*;

import gesture.based.music.notation.system.reactions.Gesture;

public interface I{
    public static interface Area{
        public boolean hit(int x, int y);
        public void dn(int x, int y);
        public void up(int x, int y);
        public void drag(int x, int y);
    }

    public static interface Show {public void show(Graphics g);}

    public static interface Act {public void act(Gesture gesture);}

    public static interface React extends Act {public int bid(Gesture gesture);}
}
