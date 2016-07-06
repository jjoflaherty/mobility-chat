<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<f:view locale="#{localeHandler.locale}">
    <body class="red ct-navbar--fixedTop cssAnimate ct-js-navbarMakeSmaller ct-js-enableWayPoints" nav="admin-coaches">
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
                <jsp:param name="heading" value="${rbMenu.manual}" />
                <jsp:param name="content" value="${rbManual.admin_coaches}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="" />
                        <jsp:param name="subtitle" value="" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row">

                        <div class="col-md-9 col-md-push-3">

                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right">
                                    <h:outputLink value="/AbleChat/resources/images/manual/#{localeHandler.locale}/admin_coaches_dashboard.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/#{localeHandler.locale}/admin_coaches_dashboard.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputFormat escape="false" value="#{rbManualClients.admin_coaches_p1}">
                                    <f:param value="#{rbDashboard.admin_coaches}" />
                                </h:outputFormat>
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right">
                                    <h:outputLink value="/AbleChat/resources/images/manual/#{localeHandler.locale}/admin_coaches.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/#{localeHandler.locale}/admin_coaches.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputFormat escape="false" value="#{rbManualClients.admin_coaches_p2a}">
                                    <f:param value="#{rbNetworkAdmin.active}" />
                                    <f:param value="#{rbNetworkAdmin.blocked}" />
                                </h:outputFormat>
                                <a4j:repeat value="#{adminClientRelationsHandler.columns}" var="role">
                                    <h:outputFormat escape="false" value="#{rbManualClients[role.name]}">
                                        <f:param value="#{rbRole[role.header]}" />
                                    </h:outputFormat>
                                </a4j:repeat>
                                <h:outputFormat escape="false" value="#{rbManualClients.admin_coaches_p2b}">
                                    <f:param value="#{rbNetworkAdmin.remove}" />
                                </h:outputFormat>
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right">
                                    <h:outputLink value="/AbleChat/resources/images/manual/#{localeHandler.locale}/invite_coach_admin.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/#{localeHandler.locale}/invite_coach_admin.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputFormat escape="false" value="#{rbManualClients.admin_coaches_p3}">
                                    <f:param value="#{rbNetworkAdmin.invite_members}" />
                                </h:outputFormat>
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom80">
                                <div class="manual-image right">
                                    <h:outputLink value="/AbleChat/resources/images/manual/#{localeHandler.locale}/invite_coach.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/#{localeHandler.locale}/invite_coach.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputFormat escape="false" value="#{rbManualClients.admin_coaches_p4}">
                                    <f:param value="#{rbRichUpload.add_control}" />
                                    <f:param value="#{rbRichUpload.clear}" />
                                </h:outputFormat>
                                <div class="clearfix"></div>
                            </section>

                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">
                                <%@ include file="/WEB-INF/sidebars/manual.jsp" %>
                            </div>
                        </div>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>
