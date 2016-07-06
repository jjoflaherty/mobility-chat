<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<li class="dropdown">
    <h:outputLink>
        <h:outputText value="#{rbMenu.organisations} " />
        <h:outputText value="" styleClass="fa fa-angle-double-down" />
    </h:outputLink>
    <ul class="dropdown-menu">
        <li>
            <div class="yamm-content">
                <div class="row">
                    <div class="col-sm-12">
                        <a4j:outputPanel layout="none" rendered="#{not empty sessionHandler.activeMembershipsForCurrentUser}">
                            <h5 class="text-uppercase ct-fw-600 ct-menu-header">Aangesloten
                                <small>U bent lid bij</small>
                            </h5>
                            <ul class="list-unstyled">
                                <a4j:repeat value="#{sessionHandler.activeMembershipsForCurrentUser}" var="member">
                                    <li>
                                        <h:form>
                                            <h:commandLink action="#{sessionHandler.showOrganisation}">
                                                <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                                <h:outputText value=" #{member.organisation.name}" />

                                                <f:param name="organisation" value="#{member.organisation.id}" />
                                            </h:commandLink>
                                        </h:form>
                                    </li>
                                </a4j:repeat>
                            </ul>
                        </a4j:outputPanel>
                    </div>
                    <div class="col-sm-12">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">Lid worden
                            <small>Sluit u aan bij een organisatie</small>
                        </h5>
                        <ul class="list-unstyled">
                            <li>
                                <h:form>
                                    <h:commandLink action="#{searchOrganisationHandler.resetAndGoto}">
                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                        <h:outputText value=" Naar formulier gaan" />
                                    </h:commandLink>
                                </h:form>
                            </li>
                        </ul>
                    </div>
                    <div class="col-sm-12">
                        <a4j:outputPanel layout="none" rendered="#{not empty sessionHandler.activeMembershipsForCurrentUser}">
                            <h5 class="text-uppercase ct-fw-600 ct-menu-header">Leden uitnodigen
                                <small>Nodig gebruikers uit om lid te worden van uw organisaties</small>
                            </h5>
                            <ul class="list-unstyled">
                                <li>
                                    <h:form>
                                        <h:commandLink action="#{inviteMembersHandler.resetAndGo}">
                                            <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                            <h:outputText value=" Naar formulier gaan" />
                                        </h:commandLink>
                                    </h:form>
                                </li>
                            </ul>
                        </a4j:outputPanel>
                    </div>
                    <div class="col-sm-12">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">Nieuw
                            <small>Start zelf een organisatie</small>
                        </h5>
                        <ul class="list-unstyled">
                            <li>
                                <h:outputLink value="#{navigateTo.secure.organisation.create}">
                                    <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                    <h:outputText value=" Naar formulier gaan" />
                                </h:outputLink>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </li>
    </ul>
</li>
