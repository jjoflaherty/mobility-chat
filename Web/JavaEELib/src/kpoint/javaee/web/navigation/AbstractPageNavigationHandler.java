package kpoint.javaee.web.navigation;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steven
 */
public class AbstractPageNavigationHandler
{
    protected PageNavigationItem root;

    public AbstractPageNavigationHandler(String root) {
        this.root = new PageNavigationItem(null, root);
    }
}
