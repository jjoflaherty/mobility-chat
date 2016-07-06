/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.orlando.forms;

/**
 *
 * @author Steven
 */
public abstract class AbstractActionFormHandler extends AbstractFormHandler
{   
    protected Boolean actionCompleted = false;

    public Boolean getActionCompleted() {
        return actionCompleted;
    }
    public void setActionCompleted(Boolean actionCompleted) {
        this.actionCompleted = actionCompleted;
    }

    public String emptyAction() {
        return null;
    }
}
