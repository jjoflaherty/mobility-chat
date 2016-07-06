/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.templates;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Steven
 */
public abstract class TemplateResource
{
    public static String getVerificationMailHtml(String userName, String url, Locale locale) throws TemplateNotFoundException, TemplateException, MalformedTemplateNameException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("userName", userName);
        data.put("url", url);

        return processTemplate("verification.html", data, locale);
    }
    public static String getInvitationMailHtml(String userName, String sender, String client, String url, Locale locale) throws TemplateNotFoundException, TemplateException, MalformedTemplateNameException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("userName", userName);
        data.put("sender", sender);
        data.put("client", client);
        data.put("url", url);

        return processTemplate("invite.html", data, locale);
    }
    public static String getInvitationNewMailHtml(String userName, String sender, String client, String email, String password, String url, Locale locale) throws TemplateNotFoundException, TemplateException, MalformedTemplateNameException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("userName", userName);
        data.put("sender", sender);
        data.put("client", client);
        data.put("email", email);
        data.put("password", password);
        data.put("url", url);

        return processTemplate("invite_new.html", data, locale);
    }

    private static String processTemplate(String name, Map<String, Object> data, Locale locale) throws TemplateNotFoundException, MalformedTemplateNameException, TemplateException, IOException {
        StringWriter sw = new StringWriter();
        Template template = FreeMarkerProcessor.getTemplate(name, locale);
        template.process(data, sw);

        return sw.toString();
    }
}
