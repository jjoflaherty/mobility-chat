/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import pictochat.server.dal.local.IUserDAOLocal;

/**
 *
 * @author Steven
 */
public class LocaleHandler
{
    private static final Logger LOG = Logger.getLogger(LocaleHandler.class);


    private String localeFromGet;


    private LocaleFDTO locale;
    private List<LocaleFDTO> locales = new ArrayList<LocaleFDTO>();

    @PostConstruct
    public void init() {
        Map<String, LocaleFDTO> locales = new HashMap<String, LocaleFDTO>();
        locales.put("en", new LocaleFDTO("EN", "en", "flag flag-us", false));
        locales.put("nl", new LocaleFDTO("NL", "nl", "flag flag-nl", false));

        this.locales = new ArrayList<LocaleFDTO>(locales.values());

        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LOG.info("Browser locale: " + currentLocale.getLanguage());

        this.locale = locales.get(currentLocale.getLanguage());
    }

    public String getLocaleLabel() {
        return this.locale.getLabel();
    }
    public String getLocaleFlagClass() {
        return this.locale.getFlagClass();
    }
    public Locale getLocale() {
        return new Locale(this.locale.getLocaleCode());
    }

    public List<LocaleFDTO> getLocales() {
        return this.locales;
    }

    public void localeChanged(ActionEvent event) {
        String newLocaleValue = (String)event.getComponent().getAttributes().get("locale");

        this.setLocaleFromGet(newLocaleValue);
   }


    public String getLocaleFromGet() {
        return localeFromGet;
    }
    public void setLocaleFromGet(String localeFromGet) {
        if (localeFromGet != null) {
            this.localeFromGet = localeFromGet;

            for (LocaleFDTO locale : locales)
                if (locale.getLocaleCode().equals(localeFromGet)) {
                    this.locale = locale;
                    LOG.info("Changing locale to: " + locale.getLocaleCode());
                    FacesContext.getCurrentInstance().getViewRoot().setLocale(this.getLocale());
                }
        }
    }





    //DAO Objects
    private IUserDAOLocal userDAO;

    public IUserDAOLocal getUserDAO() {
        return userDAO;
    }
    public void setUserDAO(IUserDAOLocal userDAO) {
        this.userDAO = userDAO;
    }
}
