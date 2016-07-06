/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.handler.forms;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Steven
 */
public abstract class FormDataObject
{
    protected List<Message> messages = new ArrayList<Message>();

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
}
