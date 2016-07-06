/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.resources;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

/**
 *
 * @author Steven
 */
public class ResourceBundleWrapper<T extends Enum<T>>
{
    private java.util.ResourceBundle resourceBundle;

    public ResourceBundleWrapper(T name) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Application app = ctx.getApplication();
        this.resourceBundle = app.getResourceBundle(ctx, name.toString());
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }
}
