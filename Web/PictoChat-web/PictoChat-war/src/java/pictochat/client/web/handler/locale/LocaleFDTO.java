/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.locale;

/**
 *
 * @author Steven
 */
public class LocaleFDTO {
    private String label;
    private String localeCode;
    private String flagClass;
    private String activeClass;

    public LocaleFDTO(String label, String localeCode, String flagClass, Boolean active) {
        this.label = label;
        this.localeCode = localeCode;
        this.flagClass = flagClass;
        this.activeClass = active ? "active" : "";
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getLocaleCode() {
        return localeCode;
    }
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getFlagClass() {
        return flagClass;
    }
    public void setFlagClass(String flagClass) {
        this.flagClass = flagClass;
    }

    public String getActiveClass() {
        return activeClass;
    }
    public void setActiveClass(String activeClass) {
        this.activeClass = activeClass;
    }
}
