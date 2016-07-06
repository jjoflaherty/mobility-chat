<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<f:view locale="#{localeHandler.locale}">
    <body class="blue ct-navbar--fixedTop cssAnimate ct-js-enableWayPoints">
        <%-- Styles --%>
        <%@ include file="/resources/css.jsp" %>
        <%@ include file="/resources/js.jsp" %>

        <div class="ct-pageWrapper" id="ct-js-wrapper">
            <%-- Header --%>
            <nav role="navigation" class="navbar navbar-inverse ct-navbar--logoright">
                <%@ include file="/header.jsp" %>
            </nav>

            <%-- Page header --%>
            <jsp:include page="/WEB-INF/templates/plain_page_header.jsp">
                <jsp:param name="heading" value="${rbActivation.welcome}" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-paddingTop20 ct-u-paddingBottom100 ct-u-diagonalTopLeft ct-u-diagonalBottomRight" data-height="100%">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${rbApp.name}" />
                        <jsp:param name="subtitle" value="${rbApp.info}" />
                        <jsp:param name="rightClass" value="fa fa-comments" />
                    </jsp:include>

                    <div class="row">
                        <div class="col-sm-4 col-md-4 ct-u-marginBottom70"></div>
                        <div class="col-sm-4 col-md-4 ct-u-marginBottom70">
                            <div class="ct-iconBox ct-iconBox--default">
                                <div class="ct-iconBox-icon ct-iconBox-icon--default">
                                    <i class="fa fa-user-plus"></i>
                                </div>
                                <div class="ct-iconBox-content">
                                    <h4 class="ct-iconBox-title text-lowercase ct-fw-600">
                                        <h:outputText value=" #{rbActivation.activation}" />
                                    </h4>
                                    <p>
                                        <h:outputText value=" #{rbActivation.success}" />
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4 col-md-4 ct-u-marginBottom70"></div>
                    </div>
                </div>
            </section>
        </div>

        <div id="ct-sectionTitle-fixed" class="ct-sectionTitle-fixed is-inactive">
            <div class="container">undefined</div>
        </div>
    </body>
</f:view>