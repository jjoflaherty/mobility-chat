/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.handler.forms;

/**
 *
 * @author Steven
 */
public class Message
{
    private String text;
    private String title;
    private String alertClass;

    public Message(String title, String text, String alertClass) {
        this.text = text;
        this.title = title;
        this.alertClass = alertClass;
    }

    public String getText() {
        return text;
    }
    public String getTitle() {
        return title;
    }
    public String getAlertClass() {
        return alertClass;
    }
}
