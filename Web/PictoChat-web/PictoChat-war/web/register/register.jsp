<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<f:view locale="#{localeHandler.locale}">
    <body class="blue ct-navbar--fixedTop cssAnimate
          ct-js-navbarMakeSmaller ct-js-enableWayPoints">
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
                <jsp:param name="heading" value="${rbRegister.title}" />
                <jsp:param name="supClass" value="fa fa-plus-circle" />
                <jsp:param name="content" value="${rbRegister.subtitle}" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-paddingBoth100 ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <div class="row">
                        <div class="col-sm-6">

                            <h:form id="register_form">
                                <h:panelGroup layout="block" styleClass="form-group #{registerHandler.emailMessages.fieldClass}">
                                    <h:inputText id="email" value="#{registerHandler.user.email}" styleClass="form-control input-lg"
                                                 validator="#{registerHandler.validateEmail}"
                                                 required="true" requiredMessage="#{rbRegister.email_required}" />
                                    <h:outputLabel value="#{rbRegister.email}" for="email" />
                                </h:panelGroup>

                                <h:panelGroup layout="block" styleClass="form-group #{registerHandler.firstNameMessages.fieldClass}">
                                    <h:inputText id="firstName" value="#{registerHandler.user.firstName}" styleClass="form-control input-lg"
                                                 required="true" requiredMessage="#{rbRegister.first_name_required}" />
                                    <h:outputLabel value="#{rbRegister.firstName}" for="firstName" />
                                </h:panelGroup>

                                <h:panelGroup layout="block" styleClass="form-group #{registerHandler.lastNameMessages.fieldClass}">
                                    <h:inputText id="lastName" value="#{registerHandler.user.lastName}" styleClass="form-control input-lg"
                                                 required="true" requiredMessage="#{rbRegister.last_name_required}" />
                                    <h:outputLabel value="#{rbRegister.lastName}" for="lastName" />
                                </h:panelGroup>

                                <h:panelGroup layout="block" styleClass="form-group #{registerHandler.passwordMessages.fieldClass}">
                                    <h:inputSecret id="password" value="#{registerHandler.user.password}" styleClass="form-control input-lg"
                                                   validator="#{registerHandler.validatePassword}"
                                                   required="true" requiredMessage="#{rbRegister.password_required}" />
                                    <h:outputLabel value="#{rbRegister.password}" for="password" />
                                </h:panelGroup>

                                <h:panelGroup layout="block" styleClass="form-group #{registerHandler.repeatPasswordMessages.fieldClass}">
                                    <h:inputSecret id="password2" value="#{registerHandler.user.password}" styleClass="form-control input-lg"
                                                   validator="#{registerHandler.validatePasswordMatch}"
                                                   required="true" requiredMessage="#{rbRegister.repeat_required}" />
                                    <h:outputLabel value="#{rbRegister.repeatPassword}" for="password2" />
                                </h:panelGroup>

                                <h:panelGroup layout="block" styleClass="form-group #{registerHandler.avatarMessages.fieldClass}">
                                    <rich:fileUpload id="avatar"
                                                     immediateUpload="true"
                                                     maxFilesQuantity="1"
                                                     listHeight="100%"
                                                     locale="#{localeHandler.locale}"
                                                     acceptedTypes="jpg, jpeg, gif, png"
                                                     required="true" requiredMessage="#{rbRegister.image_required}"
                                                     fileUploadListener="#{registerHandler.uploadListener}"

                                                     addControlLabel="#{rbRichUpload.add_control}"
                                                     clearAllControlLabel="#{rbRichUpload.clear_all}"
                                                     clearControlLabel="#{rbRichUpload.clear}"
                                                     stopEntryControlLabel="#{rbRichUpload.stop}"
                                                     uploadControlLabel="#{rbRichUpload.upload}">
                                        <a4j:support event="onuploadcomplete" reRender="avatar_preview" />
                                        <a4j:support event="onclear" reRender="avatar"/>
                                    </rich:fileUpload>
                                    <h:outputLabel value="#{rbRegister.image}" for="avatar" />

                                    <a4j:outputPanel id="avatar_preview">
                                        <h:graphicImage value="#{registerHandler.avatar.imageUrl}" rendered="#{not empty registerHandler.avatar}" />
                                    </a4j:outputPanel>
                                </h:panelGroup>

                                <h:commandLink action="#{registerHandler.register}" styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                    <h:outputText value="" styleClass="fa fa-plus-circle" />
                                    <h:outputText value=" #{rbRegister.register}" />
                                </h:commandLink>

                                <h:commandButton styleClass="hidden-button" action="#{registerHandler.register}" />
                            </h:form>

                        </div>

                        <div class="col-sm-6">
                            <a4j:repeat value="#{registerHandler.allMessages}" var="mg">
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

                </div>
            </section>
        </div>
    </body>
</f:view>
