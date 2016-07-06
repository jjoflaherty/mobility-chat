/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.fdto;

import java.util.List;
import pictochat.server.persistence.Client;

/**
 *
 * @author Steven
 */
public class DashboardClientRowFDTO
{
    private Integer index;
    private List<Client> clients;

    public DashboardClientRowFDTO(Integer index, List<Client> clients) {
        this.index = index;
        this.clients = clients;
    }

    public Integer getIndex() {
        return index;
    }
    public List<Client> getClients() {
        return clients;
    }
}