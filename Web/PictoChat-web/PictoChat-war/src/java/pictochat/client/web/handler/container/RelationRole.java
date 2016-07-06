/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.container;

import pictochat.server.persistence.Role;

/**
 *
 * @author Steven
 */
public class RelationRole
{
    private Role role;
    private Boolean selected;
    private Boolean readOnly;

    public RelationRole(Role role, Boolean selected, Boolean readOnly) {
        this.role = role;
        this.selected = selected;
        this.readOnly = readOnly;
    }

    public Role getRole() {
        return role;
    }
    public Boolean getReadOnly() {
        return readOnly;
    }


    public Boolean getSelected() {
        return selected;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
