/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.bundles;

/**
 *
 * @author Steven
 */
public enum Bundles
{
    REGISTER("rbRegister"), LOGIN("rbLogin"), PASSWORD("rbPassword"),
    INVITE_COACHES("rbNetworkInvite"), ADMIN_COACHES("rbNetworkAdmin"), ADMIN_FRIENDS("rbFriendsAdmin"),
    CREATE_PAGE("rbPageCreate"), EDIT_PAGE("rbPageEdit"), EDIT_BUTTONS("rbButtonsEdit"),
    ENUM_ACTION("rbAction"), ENUM_ICON("rbIcon"),
    ROLE("rbRole");

    private String bundleName;

    private Bundles(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public String toString() {
        return this.bundleName;
    }
}
