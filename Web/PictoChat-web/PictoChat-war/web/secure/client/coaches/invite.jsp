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
                <jsp:param name="heading" value="${rbNetworkInvite.title}" />
                <jsp:param name="content" value="${rbNetworkInvite.subtitle}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight ct-u-paddingBottom50">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${adminClientRelationsHandler.client.fullName}" />
                        <jsp:param name="subtitle" value="" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row ct-u-paddingBottom70">
                        <div class="col-md-9 col-md-push-3">
                            <div class="col-sm-12">

                                <h:form id="invite_form">
                                    <h:panelGroup layout="block" styleClass="form-group">
                                        <a4j:repeat value="#{inviteUsersHandler.emails}" var="email">
                                            <h:panelGroup layout="block" styleClass="form-group row">
                                                <div class="col-sm-6">
                                                    <h:inputText id="email" value="#{email.email}" styleClass="form-control input-lg"
                                                                 readonly="#{email.readOnly}" disabled="#{email.readOnly}" />
                                                </div>
                                                <div class="col-sm-6">
                                                    <a4j:repeat value="#{email.messages}" var="message">
                                                        <%@ include file="/WEB-INF/templates/alert_small.jsp" %>
                                                    </a4j:repeat>
                                                </div>
                                            </h:panelGroup>
                                        </a4j:repeat>
                                        <h:outputLabel value="#{rbNetworkInvite.emails}" />
                                    </h:panelGroup>

                                    <a4j:outputPanel layout="none" rendered="#{inviteUsersHandler.hasMessages}">
                                        <a4j:repeat value="#{inviteUsersHandler.messagesAndClear}" var="message">
                                            <%@ include file="/WEB-INF/templates/alert.jsp" %>
                                        </a4j:repeat>
                                    </a4j:outputPanel>

                                    <a4j:commandButton id="addEmail" value="#{rbNetworkInvite.add_email}" styleClass="btn ct-btn--perspective btn-default btn-lg"
                                                       ajaxSingle="true" action="#{inviteUsersHandler.addEmail}" reRender="invite_form" />

                                    <h:commandButton id="inviteUsersSubmit" value="#{rbNetworkInvite.invite}" styleClass="btn ct-btn--perspective btn-primary btn-lg"
                                                     action="#{inviteUsersHandler.inviteUsers}" />
                                </h:form>

                            </div>

                            <div class="col-sm-6">

                                <a4j:repeat value="#{inviteUsersHandler.allMessages}" var="mg">
                                    <h:panelGroup layout="block" rendered="#{not empty mg.messages}" styleClass="alert #{mg.alertClass}">
                                        <h:outputText value="#{mg.fieldName}" />
                                        <ul>
                                            <a4j:repeat value="#{mg.messages}" var="m">
                                                <li><h:outputText value="#{m.summary}" /></li>
                                            </a4j:repeat>
                                        </ul>
                                    </h:panelGroup>
                                </a4j:repeat>

                            </div>
                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">
                                <%@ include file="/WEB-INF/sidebars/network_invite_actions.jsp" %>
                            </div>
                        </div>
                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>
