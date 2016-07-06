<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<li class="dropdown">
    <h:outputLink>
        <h:outputText value="#{rbMenu.profile} " />
        <h:outputText value="" styleClass="fa fa-angle-double-down" />
    </h:outputLink>
    <ul class="dropdown-menu">
        <li>
            <div class="yamm-content">
                <div class="row">
                    <div class="col-sm-12">
                        <h5 class="text-uppercase ct-fw-600 ct-menu-header">
                            <h:outputText value="#{sessionHandler.loginInfo}" />
                        </h5>
                        <ul class="list-unstyled">
                            <li>
                                <h:form>
                                    <h:commandLink action="#{editUserHandler.show}">
                                        <f:param name="user" value="#{sessionHandler.loggedInUser.id}" />

                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                        <h:outputText value=" #{rbProfile.edit_profile}" />
                                    </h:commandLink>
                                </h:form>
                            </li>

                            <li>
                                <h:form>
                                    <h:commandLink action="#{changePasswordHandler.show}">
                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                        <h:outputText value=" #{rbPassword.change_password}" />
                                    </h:commandLink>
                                </h:form>
                            </li>

                            <%@ include file="/header/logout.jsp" %>
                        </ul>
                    </div>
                </div>
            </div>
        </li>
    </ul>
</li>
