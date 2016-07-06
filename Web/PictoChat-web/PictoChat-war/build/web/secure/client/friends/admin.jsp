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
                <jsp:param name="heading" value="${rbFriendsAdmin.title}" />
                <jsp:param name="content" value="${rbFriendsAdmin.subtitle}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${rbFriendsAdmin.section_title}" />
                        <jsp:param name="subtitle" value="${rbFriendsAdmin.section_subtitle}" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row">

                        <div class="col-md-9 col-md-push-3">
                            <section class="ct-u-paddingBottom30">
                                <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom20">
                                    <h3 class="text-lowercase ct-fw-600">
                                        <h:outputText value="#{rbFriendsAdmin.current_friends}" />
                                    </h3>
                                </header>
                                <div>
                                    <a4j:outputPanel id="friends_panel" ajaxRendered="true">
                                        <h:form id="friends_form">
                                            <rich:dataTable id="friends"
                                                            styleClass="table"
                                                            columnClasses="text-center,text-right"
                                                            value="#{adminFriendsHandler.findAllFriendRowsForCurrentClient}"
                                                            var="friend">
                                                <rich:column>
                                                    <f:facet name="header">
                                                        <h:outputText value="#{rbFriendsAdmin.name}" />
                                                    </f:facet>
                                                    <h:outputText value="#{friend.name}" />
                                                </rich:column>
                                                <rich:column>
                                                    <a4j:commandLink id="deleteFriendButton" action="#{adminFriendsHandler.removeFriend}"
                                                                     reRender="friends_panel, :clients_panel"
                                                                     styleClass="btn ct-btn--perspective btn-primary btn-sm">
                                                        <f:param name="client" value="#{friend.id}" />

                                                        <h:outputText value="" styleClass="fa fa-remove" />
                                                        <h:outputText value=" #{rbFriendsAdmin.remove}" />
                                                    </a4j:commandLink>
                                                </rich:column>
                                            </rich:dataTable>
                                        </h:form>
                                    </a4j:outputPanel>
                                </div>
                            </section>

                            <section class="ct-u-paddingBottom30">
                                <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom40">
                                    <h3 class="text-lowercase ct-fw-600">
                                        <h:outputText value="#{rbFriendsAdmin.new_friend}" />
                                    </h3>
                                </header>
                                <div>
                                    <a4j:outputPanel id="clients_panel" ajaxRendered="true">
                                        <h:form id="clients_form">
                                            <h:panelGroup layout="block" styleClass="form-group #{adminFriendsHandler.newFriendMessages.fieldClass}">
                                                <h:selectOneMenu id="client" value="#{adminFriendsHandler.newFriendId}" styleClass="form-control input-lg">
                                                    <f:selectItems value="#{adminFriendsHandler.newFriends}" />
                                                </h:selectOneMenu>
                                                <h:outputLabel value="#{rbFriendsAdmin.name}" for="client" />
                                            </h:panelGroup>

                                            <a4j:commandLink id="addFriendSubmit" styleClass="btn ct-btn--perspective btn-primary btn-lg"
                                                             action="#{adminFriendsHandler.addFriend}" reRender="clients_form,:friends_form">
                                                <h:outputText value="" styleClass="fa fa-check-square-o" />
                                                <h:outputText value=" #{rbFriendsAdmin.add}" />
                                            </a4j:commandLink>
                                        </h:form>
                                    </a4j:outputPanel>
                                </div>
                            </section>
                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">
                                <%@ include file="/WEB-INF/sidebars/friend_actions.jsp" %>



                                <%--<h:form>
                                    <a4j:commandLink id="updateClientsSubmit" action="#{adminFriendsHandler.save}" styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                        <h:outputText value="" styleClass="fa fa-check-square-o" />
                                        <h:outputText value=" #{rbFriendsAdmin.save}" />
                                    </a4j:commandLink>
                                </h:form>--%>
                            </div>
                        </div>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>