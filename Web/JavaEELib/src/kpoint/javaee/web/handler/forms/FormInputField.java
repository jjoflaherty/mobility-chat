/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.handler.forms;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;

/**
 *
 * @author Steven
 */
public class FormInputField
{
    private static final String SUCCESS_CLASS = "has-success";
    private static final String INFO_CLASS = "has-info";
    private static final String WARNING_CLASS = "has-warning";
    private static final String ERROR_CLASS = "has-error";
    private static final String ALERT_SUCCESS_CLASS = "alert-success";
    private static final String ALERT_INFO_CLASS = "alert-info";
    private static final String ALERT_WARNING_CLASS = "alert-warning";
    private static final String ALERT_ERROR_CLASS = "alert-danger";


    private String fieldName;
    private List<FacesMessage> messages = new ArrayList<FacesMessage>();
    private String fieldClass = "";
    private String alertClass = "";


    public FormInputField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
    public String getFieldClass() {
        return this.fieldClass;
    }
    public String getAlertClass() {
        return this.alertClass;
    }
    public List<FacesMessage> getMessages() {
        return this.messages;
    }


    public void setInfo() {
        this.fieldClass = INFO_CLASS;
        this.alertClass = ALERT_INFO_CLASS;
    }
    public void setSuccess() {
        this.fieldClass = SUCCESS_CLASS;
        this.alertClass = ALERT_SUCCESS_CLASS;
    }
    public void setWarning() {
        this.fieldClass = WARNING_CLASS;
        this.alertClass = ALERT_WARNING_CLASS;
    }
    public void setError() {
        this.fieldClass = ERROR_CLASS;
        this.alertClass = ALERT_ERROR_CLASS;
    }

    public void addMessage(FacesMessage message) {
        this.messages.add(message);
    }
}
