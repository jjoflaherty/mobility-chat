/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.fdto;

import java.util.List;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
public class ClientPageRowFDTO
{
    private Integer index;
    private List<Page> pages;

    public ClientPageRowFDTO(Integer index, List<Page> pages) {
        this.index = index;
        this.pages = pages;
    }

    public Integer getIndex() {
        return index;
    }
    public List<Page> getPages() {
        return pages;
    }
}