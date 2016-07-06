/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.navigation;


/**
 *
 * @author Steven
 */
public abstract class AbstractNavigatingBean<T extends AbstractPageNavigationHandler>
{
    protected static final String BEAN_NAME = "navigateTo";


    private T pageNavigationHandler;

    public T getNavigateTo() {
        return pageNavigationHandler;
    }
    public void setNavigateTo(T navigationHandler) {
        this.pageNavigationHandler = navigationHandler;
    }
}
