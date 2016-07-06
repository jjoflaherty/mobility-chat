/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.fdto;

import kpoint.javaee.web.handler.forms.FormDataObject;

/**
 *
 * @author Steven
 */
public class EmailFDTO extends FormDataObject
{
    private String email;
    private Boolean readOnly = false;

    public EmailFDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }
    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }   
}
