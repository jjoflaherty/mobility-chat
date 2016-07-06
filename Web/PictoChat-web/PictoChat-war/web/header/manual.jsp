<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<li class="dropdown yamm-fw">
    <h:outputLink value="#{navigateTo.manual.main}">
        <h:outputText value="#{rbMenu.manual} " />
        <h:outputText value="" styleClass="fa fa-angle-double-down" />
    </h:outputLink>
    <ul class="dropdown-menu">
        <li>
            <div class="yamm-content">
                <div class="row">
                    <div class="col-sm-6 col-md-3">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">
                            <h:outputText value="#{rbManual.getting_started}" />
                        </h5>
                        <ul class="list-unstyled">
                            <li><h:outputLink value="#{navigateTo.manual.gettingStarted.requirements}">
                                    <i class="fa fa-fw fa-home"></i><h:outputText value=" #{rbManual.requirements}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.gettingStarted.registerAccount}">
                                    <i class="fa fa-fw fa-home"></i><h:outputText value=" #{rbManual.register_account}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.manageClients.addClient}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.add_client_profile}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.providingContent.addContent}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.add_content}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.app.setup}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.installing_app}" />
                                </h:outputLink></li>
                        </ul>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">
                            <h:outputText value="#{rbManual.providing_content}" />
                            <small><h:outputText value="#{rbManual.with_pages_and_pictos}" /></small>
                        </h5>
                        <ul class="list-unstyled">
                            <li><h:outputLink value="#{navigateTo.manual.providingContent.addContent}">
                                    <i class="fa fa-fw fa-file-text-o"></i><h:outputText value=" #{rbManual.add_content}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.providingContent.addPage}">
                                    <i class="fa fa-fw fa-files-o"></i><h:outputText value=" #{rbManual.creating_pages}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.providingContent.editPage}">
                                    <i class="fa fa-fw fa-table"></i><h:outputText value=" #{rbManual.change_page_layout}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.providingContent.editButtons}">
                                    <i class="fa fa-fw fa-file-text-o"></i><h:outputText value=" #{rbManual.edit_buttons}" />
                                </h:outputLink></li>
                        </ul>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">
                            <h:outputText value="#{rbManual.managing_your_clients}" />
                        </h5>
                        <ul class="list-unstyled">
                            <li><h:outputLink value="#{navigateTo.manual.manageClients.addClient}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.add_client}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.manageClients.editClient}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.edit_client}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.manageClients.adminCoaches}">
                                    <i class="fa fa-fw fa-group"></i><h:outputText value=" #{rbManual.admin_coaches}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.manageClients.adminFriends}">
                                    <i class="fa fa-fw fa-smile-o"></i><h:outputText value=" #{rbManual.admin_friends}" />
                                </h:outputLink></li>
                        </ul>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">
                            <h:outputText value="#{rbManual.using_app}" />
                        </h5>
                        <ul class="list-unstyled">
                            <li><h:outputLink value="#{navigateTo.manual.app.clientManual}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.as_a_client}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.app.coachManual}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.as_a_coach}" />
                                </h:outputLink></li>
                            <li><h:outputLink value="#{navigateTo.manual.app.setup}">
                                    <i class="fa fa-fw fa-user-plus"></i><h:outputText value=" #{rbManual.installing_app}" />
                                </h:outputLink></li>
                        </ul>
                    </div>
                </div>
            </div>
        </li>
    </ul>
</li>