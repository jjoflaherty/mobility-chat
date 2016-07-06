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

        <a4j:loadScript src="/resources/js/ct-portfolioAjax/init.js" />

        <div class="ct-pageWrapper" id="ct-js-wrapper">
            <%-- Header --%>
            <nav role="navigation" class="navbar navbar-inverse ct-navbar--logoright">
                <%@ include file="/header.jsp" %>
            </nav>

            <%-- Page header --%>
            <jsp:include page="/WEB-INF/templates/page_header.jsp">
                <jsp:param name="heading" value="${rbPageAdmin.title}" />
                <jsp:param name="content" value="${rbPageAdmin.subtitle}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${showClientHandler.activeClient.fullName}" />
                        <jsp:param name="subtitle" value="" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row">

                        <h:form>
                            <div class="col-md-9 col-md-push-3">

                                <section class="ct-u-paddingBottom30">
                                    <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom20">
                                        <h3 class="ct-fw-600">

                                        </h3>
                                    </header>
                                    <div>

                                        <div class="ct-galleryAjax">
                                            <a4j:repeat value="#{showClientHandler.findAllPageRowsForCurrentClient}" var="row">

                                                <div class="row">
                                                    <a4j:repeat value="#{row.pages}" var="page">

                                                        <div class="col-sm-3">
                                                            <div class="ct-gallery-item ct-gallery-item--secundary">
                                                                <div class="ct-gallery-itemInner">
                                                                    <h:outputLink value="detail.jsf?id=#{page.id}"
                                                                                  styleClass="ct-js-galleryAjax-getAjaxItem">
                                                                        <span data-rel="#ct-galleryAjax-Details<h:outputText value="#{row.index}" />"></span>

                                                                        <div class="ct-iconBox ct-iconBox--default">
                                                                            <div class="ct-iconBox-icon ct-iconBox-icon--default">
                                                                                <h:outputText value="" styleClass="fa fa-file-text" rendered="#{page.isStartPage}" />
                                                                                <h:outputText value="" styleClass="fa fa-file-text-o" rendered="#{not page.isStartPage}" />
                                                                            </div>
                                                                            <div class="ct-iconBox-content">
                                                                                <h4 class="ct-iconBox-title ct-fw-600">
                                                                                    <h:outputText value="#{page.name}" />
                                                                                </h4>
                                                                            </div>
                                                                        </div>
                                                                    </h:outputLink>

                                                                    <div class="ct-gallery-item-number">
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                    </a4j:repeat>
                                                </div>

                                                <!-- galleryContainer -->
                                                <div style="display:none;" class="ct-galleryAjax-Details" id="ct-galleryAjax-Details<h:outputText value="#{row.index}" />"></div>
                                            </a4j:repeat>
                                        </div>

                                    </div>
                                </section>

                            </div>
                            <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                                <div class="row">
                                    <%@ include file="/WEB-INF/sidebars/page_admin_actions.jsp" %>
                                </div>
                            </div>
                        </h:form>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>