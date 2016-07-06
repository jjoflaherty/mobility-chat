/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.web.navigation;

/**
 *
 * @author Steven
 */
public class PageNavigationItem
{
    private PageNavigationItem parent;
    private String path = null;

    public PageNavigationItem(PageNavigationItem parent, String path) {
        this(parent);

        this.path = path;
    }
    public PageNavigationItem(PageNavigationItem parent) {
        this.parent = parent;
    }

    public String getFullPath() {
        String fullPath;

        if (this.path != null)
           fullPath = this.path;
        else
           fullPath = this.getClass().getSimpleName().toLowerCase();

        if (this.parent != null)
            fullPath = this.parent.getFullPath() + "/" + fullPath;

        return fullPath;
    }
}
