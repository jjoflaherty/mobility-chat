/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.fdto;

import java.util.ArrayList;
import java.util.List;
import kpoint.javaee.web.handler.forms.IndexedFormDataObject;
import pictochat.client.web.handler.container.RelationRole;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class RelationFDTO extends IndexedFormDataObject<Long>
{
    private String name;
    private Boolean accepted;
    private Boolean active;
    private Boolean blocked;
    private Boolean remove = false;
    private List<RelationRole> roles = new ArrayList<RelationRole>();

    private Boolean canEditActive;
    private Boolean canEditBlocked;
    private Boolean canRemove;

    public RelationFDTO(Long id, Long keyId, Relation relation, List<RelationRole> roles, Boolean canEditActive, Boolean canEditBlocked, Boolean canRemove) {
        super(id, keyId);

        User user = relation.getUser();
        if (user.getComplete())
            this.name = user.getFullName();
        else
            this.name = user.getEmail();

        this.accepted = relation.getAccepted();
        this.active = relation.getActive();
        this.blocked = relation.getBlocked();
        this.roles = roles;

        this.canEditActive = canEditActive;
        this.canEditBlocked = canEditBlocked;
        this.canRemove = canRemove;
    }

    public String getName() {
        return name;
    }
    public Boolean getAccepted() {
        return accepted;
    }
    public List<RelationRole> getRoles() {
        return roles;
    }

    public Boolean getCanEditActive() {
        return canEditActive;
    }
    public Boolean getCanEditBlocked() {
        return canEditBlocked;
    }
    public Boolean getCanRemove() {
        return canRemove;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getBlocked() {
        return blocked;
    }
    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getRemove() {
        return remove;
    }
    public void setRemove(Boolean remove) {
        this.remove = remove;
    }
}