package gesture.based.music.notation.system.reactions;

import gesture.based.music.notation.system.music.I;
import java.awt.*;

public abstract class Mass extends Reaction.List implements gesture.based.music.notation.system.music.I.Show{

    public Layer layer;
    public static int massID = 1;

    public Mass(String layerName) {
        layer = Layer.byName.get(layerName);
        if (layer != null) { 
            layer.add(this);
        } else {
            System.out.println("Bad layer name: " + layerName);
        }
    }
    // fix bug in arrayList remove

    public int hashCode = massID++;

    public boolean equals(Object o) {return this == o;}

    public int hashCode() {return hashCode;}

    public void deleteMass() {
        clearAll();
        // remove reactions from the list Mass
        layer.remove(this);
    }

    public void show(Graphics g) {} //stub
}
