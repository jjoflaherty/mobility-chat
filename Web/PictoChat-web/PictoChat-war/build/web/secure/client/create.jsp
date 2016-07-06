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
                <jsp:param name="heading" value="${rbClientCreate.title}" />
                <jsp:param name="content" value="${rbClientCreate.subtitle}" />
                <jsp:param name="supClass" value="fa fa-user-plus" />
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

                            <section class="ct-u-paddingBottom80">
                                <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom50">
                                    <h3 class="ct-fw-600">
                                        <h:outputText value="#{rbClientCreate.section_title}" />
                                    </h3>
                                </header>
                                <div class="row">
                                    <div class="col-sm-6">

                                        <h:form id="client_form">
                                            <h:panelGroup layout="block" styleClass="form-group #{createClientHandler.firstNameMessages.fieldClass}">
                                                <h:inputText id="firstName" value="#{createClientHandler.client.firstName}" styleClass="form-control input-lg"
                                                             required="true" requiredMessage="#{rbClientCreate.first_name_required}" />
                                                <h:outputLabel value="#{rbClientCreate.firstName}" for="firstName" />
                                            </h:panelGroup>

                                            <h:panelGroup layout="block" styleClass="form-group #{createClientHandler.lastNameMessages.fieldClass}">
                                                <h:inputText id="lastName" value="#{createClientHandler.client.lastName}" styleClass="form-control input-lg"
                                                             required="true" requiredMessage="#{rbClientCreate.last_name_required}" />
                                                <h:outputLabel value="#{rbClientCreate.lastName}" for="lastName" />
                                            </h:panelGroup>

                                            <h:panelGroup layout="block" styleClass="form-group #{createClientHandler.avatarMessages.fieldClass}">
                                                <rich:fileUpload id="avatar"
                                                                 immediateUpload="true"
                                                                 maxFilesQuantity="1"
                                                                 listHeight="100%"
                                                                 acceptedTypes="jpg, jpeg, gif, png"
                                                                 fileUploadListener="#{createClientHandler.uploadListener}"

                                                                 addControlLabel="#{rbRichUpload.add_control}"
                                                                 clearAllControlLabel="#{rbRichUpload.clear_all}"
                                                                 clearControlLabel="#{rbRichUpload.clear}"
                                                                 stopEntryControlLabel="#{rbRichUpload.stop}"
                                                                 uploadControlLabel="#{rbRichUpload.upload}">
                                                    <a4j:support event="onuploadcomplete" reRender="avatar_preview" />
                                                    <a4j:support event="onclear" reRender="avatar"/>
                                                </rich:fileUpload>
                                                <h:outputLabel value="#{rbClientCreate.image}" for="avatar" />

                                                <a4j:outputPanel id="avatar_preview">
                                                    <h:graphicImage value="#{createClientHandler.clientAvatar.imageUrl}" rendered="#{not empty createClientHandler.clientAvatar}" />
                                                </a4j:outputPanel>
                                            </h:panelGroup>

                                            <h:commandLink action="#{createClientHandler.createClient}" styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                                <h:outputText value="" styleClass="fa fa-plus-circle" />
                                                <h:outputText value=" #{rbClientCreate.add}" />
                                            </h:commandLink>

                                            <h:commandButton styleClass="hidden-button" action="#{createClientHandler.createClient}" />
                                        </h:form>

                                    </div>
                                    <div class="col-sm-6">
                                        <a4j:repeat value="#{createClientHandler.allMessages}" var="mg">
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
                                <%@ include file="/WEB-INF/sidebars/client_create_actions.jsp" %>
                            </div>
                        </div>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>
