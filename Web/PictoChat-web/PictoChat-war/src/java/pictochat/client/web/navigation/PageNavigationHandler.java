package pictochat.client.web.navigation;

import kpoint.javaee.web.navigation.AbstractPageNavigationHandler;
import kpoint.javaee.web.navigation.PageNavigationItem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steven
 */
public class PageNavigationHandler extends AbstractPageNavigationHandler
{
    private static final String DEFAULT_ROOT = "/AbleChat";


    public PageNavigationHandler() {
        this(DEFAULT_ROOT);
    }
    public PageNavigationHandler(String root) {
        super(root);
    }


    public String getHome() {
        return new PageNavigationItem(root, "main.jsf").getFullPath();
    }
    public String getHomeAction() {
        return "showMain";
    }

    public String getKPoint() {
        return "http://en.k-point.be/";
    }


    public Secure getSecure() {
        return new Secure(this.root);
    }
    public class Secure extends PageNavigationItem {
        public Secure(PageNavigationItem parent) {
            super(parent);
        }

        public Client getClient() {
            return new Client(this);
        }
        public class Client extends PageNavigationItem {
            public Client(PageNavigationItem parent) {
                super(parent);
            }

            public Coaches getCoaches() {
                return new Coaches(this);
            }
            public class Coaches extends PageNavigationItem {
                public Coaches(PageNavigationItem parent) {
                    super(parent);
                }

                public String getInviteAction() {
                    return "inviteCoaches";
                }

                public String getAdmin() {
                return new PageNavigationItem(this, "admin.jsf").getFullPath();
            }
                public String getAdminAction() {
                    return "adminCoaches";
                }
            }

            public Friends getFriends() {
                return new Friends(this);
            }
            public class Friends extends PageNavigationItem {
                public Friends(PageNavigationItem parent) {
                    super(parent);
                }

                public String getAdmin() {
                    return new PageNavigationItem(this, "admin.jsf").getFullPath();
                }
                public String getAdminAction() {
                    return "adminFriends";
                }
            }

            public String getCreate() {
                return new PageNavigationItem(this, "create.jsf").getFullPath();
            }
            public String getCreateAction() {
                return "createClient";
            }

            public String getEditAction() {
                return "editClient";
            }
        }

        public User getUser() {
            return new User(this);
        }
        public class User extends PageNavigationItem {
            public User(PageNavigationItem parent) {
                super(parent);
            }

            public String getEdit() {
                return new PageNavigationItem(this, "edit.jsf").getFullPath();
            }
            public String getEditAction() {
                return "editUser";
            }

            public String getPasswordAction() {
                return "showPassword";
            }
        }

        public Page getPage() {
            return new Page(this);
        }
        public class Page extends PageNavigationItem {
            public Page(PageNavigationItem parent) {
                super(parent);
            }

            public String getCreate() {
                return new PageNavigationItem(this, "create.jsf").getFullPath();
            }

            public String getAdmin() {
                return new PageNavigationItem(this, "admin.jsf").getFullPath();
            }
            public String getAdminAction() {
                return "adminPage";
            }

            public String getEditAction() {
                return "editPage";
            }

            public Buttons getButtons() {
                return new Buttons(this);
            }
            public class Buttons extends PageNavigationItem {
                public Buttons(PageNavigationItem parent) {
                    super(parent);
                }

                public String getEditAction() {
                    return "editPageButtons";
                }
            }
        }

        public String getDashboard() {
            return new PageNavigationItem(this, "dashboard.jsf").getFullPath();
        }
        public String getDashboardAction() {
            return "showDashboard";
        }
    }

    public Login getLogin() {
        return new Login(this.root);
    }
    public class Login extends PageNavigationItem {
        public Login(PageNavigationItem parent) {
            super(parent);
        }

        public String getLogin() {
            return new PageNavigationItem(this, "login.jsf").getFullPath();
        }
    }

    public Register getRegister() {
        return new Register(this.root);
    }
    public class Register extends PageNavigationItem {
        public Register(PageNavigationItem parent) {
            super(parent);
        }

        public String getRegister() {
            return new PageNavigationItem(this, "register.jsf").getFullPath();
        }
        public String getActivationSuccess() {
            return new PageNavigationItem(this, "activation/success.jsf").getFullPath();
        }
    }


    public Manual getManual() {
        return new Manual(this.root);
    }
    public class Manual extends PageNavigationItem {
        public Manual(PageNavigationItem parent) {
            super(parent);
        }

        public GettingStarted getGettingStarted() {
            return new GettingStarted(this);
        }
        public class GettingStarted extends PageNavigationItem {
            public GettingStarted(PageNavigationItem parent) {
                super(parent);
            }

            public String getRequirements() {
                return new PageNavigationItem(this, "requirements.jsf").getFullPath();
            }
            public String getRegisterAccount() {
                return new PageNavigationItem(this, "registerAccount.jsf").getFullPath();
            }
        }

        public ManageClients getManageClients() {
            return new ManageClients(this);
        }
        public class ManageClients extends PageNavigationItem {
            public ManageClients(PageNavigationItem parent) {
                super(parent);
            }

            public String getAddClient() {
                return new PageNavigationItem(this, "addClient.jsf").getFullPath();
            }
            public String getEditClient() {
                return new PageNavigationItem(this, "editClient.jsf").getFullPath();
            }
            public String getAdminCoaches() {
                return new PageNavigationItem(this, "adminCoaches.jsf").getFullPath();
            }
            public String getAdminFriends() {
                return new PageNavigationItem(this, "adminFriends.jsf").getFullPath();
            }
        }

        public ProvidingContent getProvidingContent() {
            return new ProvidingContent(this);
        }
        public class ProvidingContent extends PageNavigationItem {
            public ProvidingContent(PageNavigationItem parent) {
                super(parent);
            }

            public String getAddContent() {
                return new PageNavigationItem(this, "addContent.jsf").getFullPath();
            }
            public String getAddPage() {
                return new PageNavigationItem(this, "addPage.jsf").getFullPath();
            }
            public String getEditPage() {
                return new PageNavigationItem(this, "editPage.jsf").getFullPath();
            }
            public String getEditButtons() {
                return new PageNavigationItem(this, "editButtons.jsf").getFullPath();
            }
        }

        public App getApp() {
            return new App(this);
        }
        public class App extends PageNavigationItem {
            public App(PageNavigationItem parent) {
                super(parent);
            }

            public String getSetup() {
                return new PageNavigationItem(this, "setup.jsf").getFullPath();
            }
            public String getCoachManual() {
                return new PageNavigationItem(this, "coachManual.jsf").getFullPath();
            }
            public String getClientManual() {
                return new PageNavigationItem(this, "clientManual.jsf").getFullPath();
            }
        }

        public String getMain() {
            return new PageNavigationItem(this, "main.jsf").getFullPath();
        }
    }


    public System getSystem() {
        return new System(this.root);
    }
    public class System extends PageNavigationItem {
        public System(PageNavigationItem parent) {
            super(parent);
        }

        public String getNotActive() {
            return new PageNavigationItem(this, "not_active.jsf").getFullPath();
        }

        public String getNotAuthorized() {
            return new PageNavigationItem(this, "not_authorized.jsf").getFullPath();
        }
        public String getNotAuthorizedAction() {
            return "notAuthorized";
        }

        public String getNotLoggedIn() {
            return new PageNavigationItem(this, "not_logged_in.jsf").getFullPath();
        }
    }
}
