/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.orlando.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.handler.forms.Message;

/**
 *
 * @author Steven
 */
public abstract class AbstractFormHandler
{
    private String formName;
    protected List<Message> messages = new ArrayList<Message>();


    //TODO Add this in later
    /*public AbstractFormHandler(String formName) {
        this.formName = formName;
    }*/


    public Boolean getHasMessages() {
        return !this.messages.isEmpty();
    }
    public List<Message> getMessagesAndClear() {
        List<Message> messages = this.getMessages();

        this.messages = new ArrayList<Message>();

        return messages;
    }
    public List<Message> getMessages() {
        return this.messages;
    }

    public void addSuccessMessage(String title, String messageText) {
        this.messages.add(new Message(title, messageText, "alert-success"));
    }
    public void addInfoMessage(String title, String messageText) {
        this.messages.add(new Message(title, messageText, "alert-info"));
    }
    public void addWarningMessage(String title, String messageText) {
        this.messages.add(new Message(title, messageText, "alert-warning"));
    }
    public void addErrorMessage(String title, String messageText) {
        this.messages.add(new Message(title, messageText, "alert-danger"));
    }


    protected FormInputField getFormInputField(String name, String id) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Iterator<FacesMessage> it = ctx.getMessages(/*this.formName + ":" + */id);

        Severity maxSeverity = null;
        List<FacesMessage> messages = new ArrayList<FacesMessage>();
        while (it.hasNext()) {
            FacesMessage message = it.next();

            if (maxSeverity == null || message.getSeverity().compareTo(maxSeverity) > 0)
                maxSeverity = message.getSeverity();

            messages.add(message);
        }

        FormInputField formInputField = new FormInputField(name);
        if (maxSeverity != null)
            if (maxSeverity.equals(FacesMessage.SEVERITY_WARN))
                formInputField.setWarning();
            else if (maxSeverity.equals(FacesMessage.SEVERITY_INFO))
                formInputField.setSuccess();
            else
                formInputField.setError();

        for (FacesMessage message : messages)
            if (message.getSeverity().equals(maxSeverity))
                formInputField.addMessage(message);

        return formInputField;
    }

    //Form Messages
    public abstract List<FormInputField> getAllMessages();
}
