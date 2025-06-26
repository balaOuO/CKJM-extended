package gr.spinellis.ckjm;

import java.util.TreeSet; /**
 * It is designed to introduce a connection between method name (id) and the set of fields that are touched by this method
 */
public class TreeSetWithId<type> extends TreeSet<type> {
    private String id = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
