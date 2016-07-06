/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.navigation;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;

/**
 *
 * @author Steven
 */
public abstract class AbstractNavigatingHandler<T extends AbstractPageNavigationHandler> extends AbstractNavigatingBean<T>
{
    @PostConstruct
    public void postConstruct() {
        getMethodLogger().debug("postConstruct", "");

        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = ctx.getExternalContext();
        Object bean = ectx.getApplicationMap().get(BEAN_NAME);

        if (bean == null) {
            getMethodLogger().fatal("postConstruct", "bean not found");
            throw new NullPointerException(String.format("Required bean '%s' could not be found. Provide appropriate application scoped bean.", BEAN_NAME));
        }

        this.setNavigateTo((T)bean);
    }

    public abstract Logger getLogger();
    public abstract MethodLogger getMethodLogger();
}
