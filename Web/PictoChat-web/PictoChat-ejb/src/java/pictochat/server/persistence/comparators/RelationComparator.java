/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence.comparators;

import java.util.Comparator;
import pictochat.server.persistence.Relation;

/**
 *
 * @author Steven
 */
public class RelationComparator implements Comparator<Relation> {

    public int compare(Relation r1, Relation r2) {
        return r1.getUser().getFullName().compareTo(r2.getUser().getFullName());
    }

}
