<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<f:view locale="#{localeHandler.locale}">
    <body class="red ct-navbar--fixedTop cssAnimate ct-js-navbarMakeSmaller ct-js-enableWayPoints" nav="use-app-as-coach">
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
                <jsp:param name="content" value="${rbManual.coach_manual}" />
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
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/client-list.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/client-list.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputText escape="false" value="#{rbManualApp.coach_p1}" />
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/client-detail.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/client-detail.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputText escape="false" value="#{rbManualApp.coach_p2}" />
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/chat-1.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/chat-1.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputText escape="false" value="#{rbManualApp.coach_p3}" />
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/chat-2.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/chat-2.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputText escape="false" value="#{rbManualApp.coach_p4}" />
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/compose-1.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/compose-1.png" />
                                    </h:outputLink>
                                </div>
                                <h:outputText escape="false" value="#{rbManualApp.coach_p5}" />
                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom40">
                                <h:outputText escape="false" value="#{rbManualApp.coach_p6}" />

                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/compose-3.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/compose-3.png" />
                                    </h:outputLink>
                                </div>
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/compose-2.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/compose-2.png" />
                                    </h:outputLink>
                                </div>

                                <div class="clearfix"></div>
                            </section>
                            <section class="ct-u-paddingBottom80">
                                <h:outputText escape="false" value="#{rbManualApp.coach_p7}" />

                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/switch-3.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/switch-3.png" />
                                    </h:outputLink>
                                </div>
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/switch-2.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/switch-2.png" />
                                    </h:outputLink>
                                </div>
                                <div class="manual-image right small">
                                    <h:outputLink value="/AbleChat/resources/images/manual/app/coach/#{localeHandler.locale}/switch-1.png" target="_blank">
                                        <h:graphicImage value="/resources/images/manual/app/coach/#{localeHandler.locale}/switch-1.png" />
                                    </h:outputLink>
                                </div>

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
