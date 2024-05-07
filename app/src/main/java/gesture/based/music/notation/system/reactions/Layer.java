package gesture.based.music.notation.system.reactions;

import java.util.*;
import java.awt.*;
import gesture.based.music.notation.system.music.I;

public class Layer extends ArrayList<I.Show> implements I.Show {
    
    public static HashMap<String, Layer> byName = new HashMap<>();

    public static Layer ALL = new Layer("ALL");

    String name;

    public Layer(String name) {
        this.name = name;
        if (!name.equals("ALL")) {
            ALL.add(this);
        }
        byName.put(name, this);
    }

    @Override
    public void show(Graphics g) {
        for (I.Show item:this) {
            item.show(g);
        }
    }

    public static void nuke() {
        for (I.Show lay:ALL) {
            ((Layer) lay).clear();
        }
    }
}
