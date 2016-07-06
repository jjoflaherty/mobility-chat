/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.navigation;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;

/**
 *
 * @author Steven
 */
public abstract class AbstractNavigatingFilter<T extends AbstractPageNavigationHandler> extends AbstractNavigatingBean<T> implements Filter
{
    private FilterConfig config;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        getMethodLogger().debug("doFilter", "");

        Object bean = this.config.getServletContext().getAttribute(BEAN_NAME);

        if (bean == null) {
            getMethodLogger().debug("doFilter", "bean not found");
            throw new NullPointerException(String.format("Required bean '%s' could not be found. Provide appropriate application scoped bean.", BEAN_NAME));
        }

        this.setNavigateTo((T)bean);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }
    @Override
    public void destroy() {
        this.config = null;
    }

    public abstract Logger getLogger();
    public abstract MethodLogger getMethodLogger();
}
