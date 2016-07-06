/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.templates;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author Steven
 */
public class FreeMarkerProcessor
{
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FreeMarkerProcessor.class);

    private static FreeMarkerProcessor processor;
    private Configuration configuration;


    protected FreeMarkerProcessor() {
        configuration = new Configuration();
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        configuration.setClassForTemplateLoading(TemplateResource.class, "");
        configuration.setTagSyntax(Configuration.ANGLE_BRACKET_TAG_SYNTAX);
    }

    public static Template getTemplate(String name, Locale locale) throws TemplateNotFoundException, MalformedTemplateNameException {
        Template template = null;

        try {
            template = getInstance().configuration.getTemplate(name, locale);
        } catch (ParseException ex) {
            LOG.error(null, ex);
        } catch (IOException ex) {
            LOG.error(null, ex);
        }

        return template;
    }

    /**
     * Return a preconfigured instance of the template processing engine.
     *
     * @return a preconfigured instance of the template processor class.
     */
    public static FreeMarkerProcessor getInstance() {
        if (FreeMarkerProcessor.processor == null)
            processor = new FreeMarkerProcessor();

        return processor;
    }
}
