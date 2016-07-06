/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.locale;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import org.apache.log4j.Logger;
import pictochat.client.web.navigation.PageNavigationHandler;

/**
 *
 * @author Steven
 */
public class RequestLocaleHandler
{
    private static final Logger LOG = Logger.getLogger(RequestLocaleHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;

    private LocaleHandler localeHandler;
    private String localeFromGet;


    public LocaleHandler getLocaleHandler() {
        return localeHandler;
    }
    public void setLocaleHandler(LocaleHandler localeHandler) {
        this.localeHandler = localeHandler;
    }

    public String getLocaleFromGet() {
        return localeFromGet;
    }
    public void setLocaleFromGet(String localeFromGet) {
        if (localeFromGet != null && !localeFromGet.isEmpty()) {
            this.localeFromGet = localeFromGet;
            this.localeHandler.setLocaleFromGet(localeFromGet);

            try {
                FacesContext ctx = FacesContext.getCurrentInstance();
                ExternalContext ectx = ctx.getExternalContext();
                ectx.redirect(navigatingHandler.getNavigateTo().getHome());
            } catch (IOException ex) {
                LOG.error(null, ex);
            }
        }
    }

    @PostConstruct
    public void postConstruct() {
        this.navigatingHandler = new AbstractNavigatingHandler(){
            @Override
            public Logger getLogger() {
                return LOG;
            }

            @Override
            public MethodLogger getMethodLogger() {
                return METHOD_LOG;
            }
        };
        this.navigatingHandler.postConstruct();
    }
}
