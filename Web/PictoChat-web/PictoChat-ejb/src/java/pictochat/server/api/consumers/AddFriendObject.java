/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.api.consumers;

import java.io.Serializable;

/**
 *
 * @author Steven
 */
public class AddFriendObject implements Serializable
{
    private Long id;
    private String clientCode;
    private String friendCode;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getClientCode() {
        return clientCode;
    }
    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getFriendCode() {
        return friendCode;
    }
    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
    }
}
