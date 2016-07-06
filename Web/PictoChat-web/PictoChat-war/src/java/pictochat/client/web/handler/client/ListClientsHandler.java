package pictochat.client.web.handler.client;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import pictochat.client.web.handler.fdto.DashboardClientRowFDTO;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class ListClientsHandler
{
    private static final int COLUMNS = 3;


    private User activeUser;

    public User getActiveUser() {
        return activeUser;
    }
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }


    public List<DashboardClientRowFDTO> getFindAllClientRowsForCurrentUser() {
        List<DashboardClientRowFDTO> rows = new ArrayList<DashboardClientRowFDTO>();

        if (this.activeUser != null) {
            List<Client> clients = new ArrayList<Client>();

            Integer index = 0;
            for (Relation relation : this.activeUser.getRelations()) {
                Client client = relation.getClient();
                clients.add(client);

                if (clients.size() >= COLUMNS) {
                    rows.add(new DashboardClientRowFDTO(index++, clients));
                    clients = new ArrayList<Client>();
                }
            }

            if (!clients.isEmpty())
                rows.add(new DashboardClientRowFDTO(index++, clients));
        }

        return rows;
    }
}
