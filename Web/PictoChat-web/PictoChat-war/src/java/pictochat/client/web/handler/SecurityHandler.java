package pictochat.client.web.handler;


import java.util.ArrayList;
import org.apache.log4j.Logger;
import pictochat.server.persistence.Role;

/**
 *
 * @author Steven
 */
public class SecurityHandler
{
    private static final Logger LOG = Logger.getLogger(SecurityHandler.class);

    private Role globalRole;

    public Role getGlobalRole() {
        return this.globalRole;
    }
    public void setGlobalRole(Role role) {
        globalRole = role;
    }


    //Helpers
    private boolean isGlobalAdmin() {
        if (this.globalRole != null) {
            ArrayList<String> allowedRoles = new ArrayList<String>();
            allowedRoles.add(Role.DEVELOPER);
            allowedRoles.add(Role.SUPERADMIN);

            return allowedRoles.contains(this.globalRole.getName());
        }

        return false;
    }
    private boolean isDeveloper() {
        if (this.globalRole != null) {
            ArrayList<String> allowedRoles = new ArrayList<String>();
            allowedRoles.add(Role.DEVELOPER);

            return allowedRoles.contains(this.globalRole.getName());
        }

        return false;
    }
}
