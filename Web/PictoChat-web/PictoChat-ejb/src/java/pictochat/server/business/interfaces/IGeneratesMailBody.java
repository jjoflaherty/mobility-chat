/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.business.interfaces;

import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public interface IGeneratesMailBody {
    String generateMailBody(User user);
}