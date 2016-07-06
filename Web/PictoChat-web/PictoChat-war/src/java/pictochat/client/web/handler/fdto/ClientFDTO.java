/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.handler.fdto;

import kpoint.javaee.web.handler.forms.IndexedFormDataObject;
import pictochat.server.persistence.Client;

/**
 *
 * @author Steven
 */
public class ClientFDTO extends IndexedFormDataObject<Long>
{
    private Client client;

    public ClientFDTO(Long id, Long keyId, Client client) {
        super(id, keyId);

        this.client = client;
    }

    public Client getClient() {
        return client;
    }
    public String getName() {
        return client.getFullName();
    }
    public Long getId() {
        return this.getKeyId();
    }
}