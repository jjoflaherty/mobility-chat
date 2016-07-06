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
                <jsp:param name="heading" value="${rbPassword.title}" />
                <jsp:param name="supClass" value="fa fa-users" />
                <jsp:param name="content" value="${rbPassword.subtitle}" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${sessionHandler.loginInfo}" />
                        <jsp:param name="rightClass" value="fa fa-users" />
                    </jsp:include>

                    <div class="row">

                        <div class="col-md-9 col-md-push-3">

                            <section class="ct-u-paddingBottom80">
                                <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom50">
                                    <h3 class="ct-fw-600">
                                        <h:outputText value="#{rbPassword.section_title}" />
                                    </h3>
                                </header>

                                <div class="row">
                                    <div class="col-sm-6">

                                        <h:form id="password_form">
                                            <h:panelGroup layout="block" styleClass="form-group #{changePasswordHandler.passwordMessages.fieldClass}">
                                                <h:inputSecret id="oldPassword" value="#{changePasswordHandler.oldPassword}" styleClass="form-control input-lg"
                                                               autocomplete="off"
                                                               validator="#{changePasswordHandler.validateOldPassword}"
                                                               required="true" requiredMessage="#{rbPassword.old_password_required}" />
                                                <h:outputLabel value="#{rbPassword.oldPassword}" for="oldPassword" />
                                            </h:panelGroup>

                                            <h:panelGroup layout="block" styleClass="form-group #{changePasswordHandler.newPasswordMessages.fieldClass}">
                                                <h:inputSecret id="newPassword" value="#{changePasswordHandler.newPassword}" styleClass="form-control input-lg"
                                                               validator="#{changePasswordHandler.validateNewPassword}"
                                                               required="true" requiredMessage="#{rbPassword.new_password_required}" />
                                                <h:outputLabel value="#{rbPassword.newPassword}" for="newPassword" />
                                            </h:panelGroup>

                                            <h:panelGroup layout="block" styleClass="form-group #{changePasswordHandler.repeatPasswordMessages.fieldClass}">
                                                <h:inputSecret id="confirmPassword" value="#{changePasswordHandler.confirmPassword}" styleClass="form-control input-lg"
                                                               validator="#{changePasswordHandler.validatePasswordMatch}"
                                                               required="true" requiredMessage="#{rbPassword.repeat_required}" />
                                                <h:outputLabel value="#{rbPassword.repeatPassword}" for="confirmPassword" />
                                            </h:panelGroup>

                                            <h:commandLink action="#{changePasswordHandler.save}" styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                                <h:outputText value="" styleClass="fa fa-plus-circle" />
                                                <h:outputText value=" #{rbPassword.save}" />
                                            </h:commandLink>

                                            <h:commandButton styleClass="hidden-button" action="#{changePasswordHandler.save}" />
                                        </h:form>

                                    </div>
                                    <div class="col-sm-6">
                                        <a4j:repeat value="#{changePasswordHandler.allMessages}" var="mg">
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
                            </section>

                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">

                            </div>
                        </div>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>