<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<a4j:outputPanel layout="none">
    <div class="col-sm-6 col-md-12">
        <section class="widget">
            <div class="widget-inner">
                <h4 class="text-lowercase">
                    <h:outputText value="#{rbManual.getting_started}" />
                </h4>
                <ul>
                    <li class="requirements">
                        <h:outputLink value="#{navigateTo.manual.gettingStarted.requirements}">
                            <h:outputText value="#{rbManual.requirements}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="register-account">
                        <h:outputLink value="#{navigateTo.manual.gettingStarted.registerAccount}">
                            <h:outputText value="#{rbManual.register_account}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="add-client">
                        <h:outputLink value="#{navigateTo.manual.manageClients.addClient}">
                            <h:outputText value="#{rbManual.add_client_profile}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="admin-pages">
                        <h:outputLink value="#{navigateTo.manual.providingContent.addContent}">
                            <h:outputText value="#{rbManual.add_content}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="installing-app">
                        <h:outputLink value="#{navigateTo.manual.app.setup}">
                            <h:outputText value="#{rbManual.installing_app}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                </ul>
            </div>
        </section>
    </div>

    <div class="col-sm-6 col-md-12">
        <section class="widget">
            <div class="widget-inner">
                <h4 class="text-lowercase">
                    <h:outputText value="#{rbManual.managing_your_clients}" />
                </h4>
                <ul>
                    <li class="add-client">
                        <h:outputLink value="#{navigateTo.manual.manageClients.addClient}">
                            <h:outputText value="#{rbManual.add_client}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="edit-client">
                        <h:outputLink value="#{navigateTo.manual.manageClients.editClient}">
                            <h:outputText value="#{rbManual.edit_client}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="admin-coaches">
                        <h:outputLink value="#{navigateTo.manual.manageClients.adminCoaches}">
                            <h:outputText value="#{rbManual.admin_coaches}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="admin-friends">
                        <h:outputLink value="#{navigateTo.manual.manageClients.adminFriends}">
                            <h:outputText value="#{rbManual.admin_friends}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                </ul>
            </div>
        </section>
    </div>

    <div class="col-sm-6 col-md-12">
        <section class="widget">
            <div class="widget-inner">
                <h4 class="text-lowercase">
                    <h:outputText value="#{rbManual.providing_content}" />
                </h4>
                <ul>
                    <li class="admin-pages">
                        <h:outputLink value="#{navigateTo.manual.providingContent.addContent}">
                            <h:outputText value="#{rbManual.admin_pages}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="add-page">
                        <h:outputLink value="#{navigateTo.manual.providingContent.addPage}">
                            <h:outputText value="#{rbManual.creating_pages}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="change-layout">
                        <h:outputLink value="#{navigateTo.manual.providingContent.editPage}">
                            <h:outputText value="#{rbManual.change_page_layout}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="add-content">
                        <h:outputLink value="#{navigateTo.manual.providingContent.editButtons}">
                            <h:outputText value="#{rbManual.edit_buttons}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                </ul>
            </div>
        </section>
    </div>

    <div class="col-sm-6 col-md-12">
        <section class="widget">
            <div class="widget-inner">
                <h4 class="text-lowercase">
                    <h:outputText value="#{rbManual.using_app}" />
                </h4>
                <ul>
                    <li class="use-app-as-user">
                        <h:outputLink value="#{navigateTo.manual.app.clientManual}">
                            <h:outputText value="#{rbManual.as_a_client}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                    <li class="use-app-as-coach">
                        <h:outputLink value="#{navigateTo.manual.app.coachManual}">
                            <h:outputText value="#{rbManual.as_a_coach}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:outputLink>
                    </li>
                </ul>
            </div>
        </section>
    </div>
</a4j:outputPanel>

<script type="text/javascript">
    (function($){
        $(document).ready(function(){
            var tags = $("body").attr("nav");
            tags = tags.split(/(\s+)/);

            $.each(tags, function(index, value){
                $("li." + value).addClass("current-cat");
            });
        });
    }(jQuery));
</script>