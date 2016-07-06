<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="col-sm-6 col-md-12">
    <section class="widget">
        <div class="widget-inner">
            <h4 class="text-lowercase">Acties</h4>
            <ul>
                <li>
                    <h:outputLink value="#{navigateTo.secure.dashboard}">
                        <h:outputText value="#{rbFriendsAdmin.back}" />
                        <span class="pull-right ct-blogWidget-count"></span>
                    </h:outputLink>
                </li>
                <li>
                    <h:form>
                        <a4j:commandLink action="#{adminFriendsHandler.save}">
                            <h:outputText value="#{rbFriendsAdmin.save_and_return}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </a4j:commandLink>

                        <%--<h:commandLink action="#{inviteUsersHandler.show}">
                            <f:param name="client" value="#{adminClientRelationsHandler.client.id}" />

                            <h:outputText value="#{rbNetworkAdmin.invite_members}" />
                            <span class="pull-right ct-blogWidget-count"></span>
                        </h:commandLink>--%>
                    </h:form>
                </li>
            </ul>
        </div>
    </section>
</div>