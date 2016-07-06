/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.fdto;

import kpoint.javaee.web.handler.forms.IndexedFormDataObject;

/**
 *
 * @author Steven
 */
public class RelationRoleFDTO extends IndexedFormDataObject
{
    private Boolean selected;
    private Boolean readOnly;

    public RelationRoleFDTO(Long id, Long keyId, Boolean selected, Boolean readOnly) {
        super(id, keyId);

        this.selected = selected;
        this.readOnly = readOnly;
    }

    public Boolean getSelected() {
        return selected;
    }
    public Boolean getReadOnly() {
        return readOnly;
    }
}