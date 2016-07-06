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
            <jsp:include page="/WEB-INF/templates/dashboard_page_header.jsp">
                <jsp:param name="heading" value="${rbApp.name}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight ct-u-paddingBottom50">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${rbDashboard.title}" />
                        <jsp:param name="subtitle" value="" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row">

                        <div class="col-md-9 col-md-push-3">
                            <h:form>
                                <div class="ct-galleryAjax">
                                    <a4j:repeat value="#{listClientsHandler.findAllClientRowsForCurrentUser}" var="row">

                                        <div class="row ct-u-marginBottom30">
                                            <a4j:repeat value="#{row.clients}" var="client">

                                                <div class="col-sm-4">
                                                    <div class="ct-gallery-item ct-gallery-item--secundary">
                                                        <div class="ct-gallery-itemInner">
                                                            <h:commandLink action="#{showClientHandler.show}">
                                                                <f:param name="client" value="#{client.id}" />

                                                                <div class="ct-gallery-itemImage">
                                                                    <h:graphicImage value="#{client.avatar.imageUrl}" />
                                                                </div>
                                                                <div class="ct-gallery-itemDescription">
                                                                    <span class="ct-gallery-itemDescription-title"><h:outputText value="#{client.fullName}" /></span>
                                                                </div>
                                                            </h:commandLink>
                                                        </div>
                                                    </div>

                                                    <h:commandLink action="#{editClientHandler.show}">
                                                        <f:param name="client" value="#{client.id}" />

                                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                                        <h:outputText value=" #{rbDashboard.edit_client}" />
                                                    </h:commandLink><br/>
                                                    <h:commandLink action="#{showClientHandler.show}">
                                                        <f:param name="client" value="#{client.id}" />

                                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                                        <h:outputText value=" #{rbDashboard.customize_pages}" />
                                                    </h:commandLink><br/>
                                                    <h:commandLink action="#{adminClientRelationsHandler.show}">
                                                        <f:param name="client" value="#{client.id}" />

                                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                                        <h:outputText value=" #{rbDashboard.admin_coaches}" />
                                                    </h:commandLink><br/>
                                                    <h:commandLink action="#{adminFriendsHandler.show}">
                                                        <f:param name="client" value="#{client.id}" />

                                                        <h:outputText value="" styleClass="fa fa-fw fa-chevron-right" />
                                                        <h:outputText value=" #{rbDashboard.admin_friends}" />
                                                    </h:commandLink>
                                                </div>

                                            </a4j:repeat>
                                        </div>

                                    </a4j:repeat>
                                </div>
                            </h:form>
                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">
                                <%@ include file="/WEB-INF/sidebars/dashboard_actions.jsp" %>
                            </div>
                        </div>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>
