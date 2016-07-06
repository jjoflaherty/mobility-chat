<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<f:view locale="#{localeHandler.locale}">
    <body class="blue ct-navbar--fixedTop cssAnimate ct-js-navbarMakeSmaller ct-js-enableWayPoints">
        <%-- Styles --%>
        <%@ include file="/resources/css.jsp" %>
        <%@ include file="/resources/js.jsp" %>

        <div class="ct-pageWrapper" id="ct-js-wrapper">
            <%-- Header --%>
            <nav role="navigation" class="navbar navbar-inverse ct-navbar--logoright">
                <%@ include file="/header.jsp" %>
            </nav>

            <%-- Page header --%>
            <jsp:include page="/WEB-INF/templates/page_header.jsp">
                <jsp:param name="heading" value="${rbNetworkAdmin.title}" />
                <jsp:param name="content" value="${rbNetworkAdmin.subtitle}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${adminClientRelationsHandler.client.fullName}" />
                        <jsp:param name="subtitle" value="" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row ct-u-paddingBottom70">
                        <div class="col-md-9 col-md-push-3">

                            <a4j:outputPanel layout="none" ajaxRendered="true" rendered="#{not empty adminClientRelationsHandler.findAcceptedRelationsForCurrentClient}">
                                <section class="ct-u-paddingBottom30">
                                    <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom20">
                                        <h3 class="text-lowercase ct-fw-600">
                                            <h:outputText value="#{rbNetworkAdmin.current_coaches}" />
                                        </h3>
                                    </header>
                                    <div>
                                        <h:form id="relations_form">
                                            <rich:dataTable id="relations"
                                                            styleClass="table"
                                                            value="#{adminClientRelationsHandler.findAcceptedRelationsForCurrentClient}"
                                                            var="relation">
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.coach}" />
                                                    </f:facet>
                                                    <h:outputText value="#{relation.name}" />
                                                </rich:column>
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.active}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.active}"
                                                                             readonly="#{not relation.canEditActive}"
                                                                             disabled="#{not relation.canEditActive}" />
                                                </rich:column>
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.blocked}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.blocked}"
                                                                             readonly="#{not relation.canEditBlocked}"
                                                                             disabled="#{not relation.canEditBlocked}" />
                                                </rich:column>
                                                <rich:columns value="#{adminClientRelationsHandler.columns}" var="column" index="i" styleClass="colCenter">
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbRole[column.header]}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.roles[i].selected}"
                                                                             readonly="#{relation.roles[i].readOnly}"
                                                                             disabled="#{relation.roles[i].readOnly}" />
                                                </rich:columns>
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.remove}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.remove}"
                                                                             readonly="#{not relation.canRemove}"
                                                                             disabled="#{not relation.canRemove}" />
                                                </rich:column>
                                            </rich:dataTable>

                                            <a4j:commandLink id="updateClientsSubmit" action="#{adminClientRelationsHandler.save}" reRender="relations_form"
                                                             styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                                <h:outputText value="" styleClass="fa fa-check-square-o" />
                                                <h:outputText value=" #{rbNetworkAdmin.save}" />
                                            </a4j:commandLink>
                                        </h:form>
                                    </div>
                                </section>
                            </a4j:outputPanel>

                            <a4j:outputPanel layout="none" ajaxRendered="true" rendered="#{not empty adminClientRelationsHandler.findOpenInvitationsForCurrentClient}">
                                <section class="ct-u-paddingBottom30">
                                    <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom20">
                                        <h3 class="text-lowercase ct-fw-600">
                                            <h:outputText value="#{rbNetworkAdmin.open_invitations}" />
                                        </h3>
                                    </header>
                                    <div>
                                        <h:form id="invitations_form">
                                            <rich:dataTable id="invitations"
                                                            styleClass="table"
                                                            value="#{adminClientRelationsHandler.findOpenInvitationsForCurrentClient}"
                                                            var="relation">
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.coach}" />
                                                    </f:facet>
                                                    <h:outputText value="#{relation.name}" />
                                                </rich:column>
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.active}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.active}"
                                                                             readonly="#{not relation.canEditActive}"
                                                                             disabled="#{not relation.canEditActive}" />
                                                </rich:column>
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.blocked}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.blocked}"
                                                                             readonly="#{not relation.canEditBlocked}"
                                                                             disabled="#{not relation.canEditBlocked}" />
                                                </rich:column>
                                                <rich:columns value="#{adminClientRelationsHandler.columns}" var="column" index="i" styleClass="colCenter">
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbRole[column.header]}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.roles[i].selected}"
                                                                             readonly="#{relation.roles[i].readOnly}"
                                                                             disabled="#{relation.roles[i].readOnly}" />
                                                </rich:columns>
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbNetworkAdmin.remove}" />
                                                    </f:facet>
                                                    <h:selectBooleanCheckbox value="#{relation.remove}"
                                                                             readonly="#{not relation.canRemove}"
                                                                             disabled="#{not relation.canRemove}" />
                                                </rich:column>
                                            </rich:dataTable>

                                            <a4j:outputPanel layout="none" rendered="#{adminClientRelationsHandler.hasMessages}">
                                                <a4j:repeat value="#{adminClientRelationsHandler.messagesAndClear}" var="message">
                                                    <%@ include file="/WEB-INF/templates/alert.jsp" %>
                                                </a4j:repeat>
                                            </a4j:outputPanel>

                                            <a4j:commandLink id="updateClientsSubmit" action="#{adminClientRelationsHandler.save}" reRender="invitations_form"
                                                             styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                                <h:outputText value="" styleClass="fa fa-check-square-o" />
                                                <h:outputText value=" #{rbNetworkAdmin.save}" />
                                            </a4j:commandLink>
                                        </h:form>
                                    </div>
                                </section>
                            </a4j:outputPanel>

                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">
                                <%@ include file="/WEB-INF/sidebars/network_actions.jsp" %>
                            </div>
                        </div>
                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>