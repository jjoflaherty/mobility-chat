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

        <%@ include file="/resources/js_minicolors.jsp" %>

        <div class="ct-pageWrapper" id="ct-js-wrapper">
            <%-- Header --%>
            <nav role="navigation" class="navbar navbar-inverse ct-navbar--logoright">
                <%@ include file="/header.jsp" %>
            </nav>

            <%-- Page header --%>
            <jsp:include page="/WEB-INF/templates/page_header.jsp">
                <jsp:param name="heading" value="${rbButtonsEdit.title}" />
                <jsp:param name="content" value="${rbButtonsEdit.subtitle}" />
                <jsp:param name="supClass" value="" />
            </jsp:include>

            <%-- Content --%>
            <section class="ct-u-diagonalTopLeft ct-u-diagonalBottomRight">
                <div class="container">

                    <%-- Section header --%>
                    <jsp:include page="/WEB-INF/templates/section_header.jsp">
                        <jsp:param name="heading" value="${editPageButtonsHandler.activePage.name}" />
                        <jsp:param name="subtitle" value="" />
                        <jsp:param name="rightClass" value="" />
                    </jsp:include>

                    <div class="row">

                        <div class="col-md-9 col-md-push-3">

                            <section class="ct-u-paddingBottom80">
                                <header class="ct-pageSectionHeader ct-pageSectionHeader--text ct-u-marginBottom50">
                                    <h3 class="ct-fw-600">

                                    </h3>
                                </header>
                                <div>

                                    <div>
                                        <div class="row">
                                            <div class="col-sm-12">
                                                <div class="row">
                                                    <div class="col-sm-6">
                                                        <h:form id="edit_form">

                                                            <h4 class="ct-u-marginBottom30">
                                                                <h:outputText value="#{rbButtonsEdit.icon}" />
                                                            </h4>
                                                            <h:panelGroup layout="block" styleClass="form-group">
                                                                <h:selectOneMenu id="icon" styleClass="form-control input-lg"
                                                                                 value="#{editPageButtonsHandler.activeButton.icon}"
                                                                                 valueChangeListener="#{editPageButtonsHandler.iconChanged}">
                                                                    <f:selectItems value="#{editPageButtonsHandler.icons}" />
                                                                    <a4j:support event="onchange" reRender="edit_form" />
                                                                </h:selectOneMenu>
                                                                <h:outputLabel value="#{rbButtonsEdit.icon}" for="icon" />
                                                            </h:panelGroup>

                                                            <h:panelGroup layout="block" rendered="#{editPageButtonsHandler.showImageUpload}"
                                                                          styleClass="form-group #{editPageButtonsHandler.imageMessages.fieldClass}">
                                                                <rich:fileUpload id="avatar"
                                                                                 immediateUpload="true"
                                                                                 maxFilesQuantity="1"
                                                                                 listHeight="100%"
                                                                                 acceptedTypes="jpg, jpeg, gif, png"
                                                                                 fileUploadListener="#{editPageButtonsHandler.uploadListener}"

                                                                                 addControlLabel="#{rbRichUpload.add_control}"
                                                                                 clearAllControlLabel="#{rbRichUpload.clear_all}"
                                                                                 clearControlLabel="#{rbRichUpload.clear}"
                                                                                 stopEntryControlLabel="#{rbRichUpload.stop}"
                                                                                 uploadControlLabel="#{rbRichUpload.upload}">
                                                                    <a4j:support event="onuploadcomplete" reRender="avatar_preview" />
                                                                    <a4j:support event="onclear" reRender="avatar"/>
                                                                </rich:fileUpload>
                                                                <h:outputLabel value="#{rbButtonsEdit.image}" for="avatar" />
                                                            </h:panelGroup>

                                                            <h:panelGroup layout="block" rendered="#{editPageButtonsHandler.showImageName}"
                                                                          styleClass="form-group">
                                                                <h:inputText id="name" value="#{editPageButtonsHandler.activeButton.url}" />
                                                                <h:outputLabel value="#{rbButtonsEdit.name}" for="name" />

                                                                <a4j:commandLink id="translateButtonSubmit" rendered="#{editPageButtonsHandler.showImageName}"
                                                                                 action="#{editPageButtonsHandler.translateTextToPictos}" reRender="translation"
                                                                                 ajaxSingle="true"
                                                                                 process="name, icon"
                                                                                 styleClass="btn ct-btn--perspective btn-primary btn-md">
                                                                    <h:outputText value="" styleClass="fa fa-refresh" />
                                                                    <h:outputText value=" #{rbButtonsEdit.translate}" />
                                                                </a4j:commandLink>
                                                            </h:panelGroup>

                                                            <a4j:outputPanel id="translation" layout="block">
                                                                <h:outputText value="#{rbButtonsEdit.select_picto}" rendered="#{not empty editPageButtonsHandler.resultPictos}" />
                                                                <br/>

                                                                <a4j:repeat value="#{editPageButtonsHandler.resultPictos}" var="picto">

                                                                    <div class="col-sm-3" style="margin-bottom: 12.5px;">
                                                                        <a4j:commandLink action="#{editPageButtonsHandler.selectPicto}" reRender="edit_form, page_form">
                                                                            <h:panelGroup layout="block"
                                                                                          styleClass="page-button"
                                                                                          style="background-image: url('#{picto}');">
                                                                            </h:panelGroup>
                                                                            <f:param name="url" value="#{picto}" />
                                                                        </a4j:commandLink>
                                                                    </div>

                                                                </a4j:repeat>
                                                            </a4j:outputPanel>
                                                            <div class="clearfix"></div>

                                                            <h4 class="ct-u-marginBottom30">
                                                                <h:outputText value="#{rbButtonsEdit.action}" />
                                                            </h4>
                                                            <h:panelGroup layout="block" styleClass="form-group">
                                                                <h:selectOneMenu id="action" styleClass="form-control input-lg"
                                                                                 value="#{editPageButtonsHandler.activeButton.action}"
                                                                                 valueChangeListener="#{editPageButtonsHandler.actionChanged}">
                                                                    <f:selectItems value="#{editPageButtonsHandler.actions}" />
                                                                    <a4j:support event="onchange" reRender="edit_form" />
                                                                </h:selectOneMenu>
                                                                <h:outputLabel value="#{rbButtonsEdit.action}" for="action" />
                                                            </h:panelGroup>

                                                            <h:panelGroup layout="block" rendered="#{editPageButtonsHandler.showTextInput}"
                                                                          styleClass="form-group">
                                                                <h:inputText id="text" value="#{editPageButtonsHandler.activeButton.text}" />
                                                                <h:outputLabel value="#{rbButtonsEdit.text}" for="text" />
                                                            </h:panelGroup>

                                                            <h:panelGroup layout="block" rendered="#{editPageButtonsHandler.showPageDropdown}"
                                                                          styleClass="form-group">
                                                                <h:selectOneMenu id="page" value="#{editPageButtonsHandler.targetPageId}" styleClass="form-control input-lg">
                                                                    <f:selectItems value="#{editPageButtonsHandler.pages}" />
                                                                </h:selectOneMenu>
                                                                <h:outputLabel value="#{rbButtonsEdit.page}" for="page" />
                                                            </h:panelGroup>

                                                            <h4 class="ct-u-marginBottom30">
                                                                <h:outputText value="#{rbButtonsEdit.appearance}" />
                                                            </h4>
                                                            <h:panelGroup layout="block" styleClass="form-group">
                                                                <h:inputText id="color" value="#{editPageButtonsHandler.activeButton.color}" styleClass="minicolors" />
                                                                <h:outputLabel value="#{rbButtonsEdit.background_color}" for="color" />
                                                            </h:panelGroup>


                                                            <a4j:commandLink id="savePageButtonSubmit" action="#{editPageButtonsHandler.save}" reRender="page_form"
                                                                             styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                                                <h:outputText value="" styleClass="fa fa-check-square-o" />
                                                                <h:outputText value=" #{rbButtonsEdit.save}" />
                                                            </a4j:commandLink>

                                                            <a4j:commandLink id="deletePageButtonSubmit" action="#{editPageButtonsHandler.delete}" reRender="edit_form, page_form"
                                                                             styleClass="btn ct-btn--perspective btn-primary btn-lg">
                                                                <h:outputText value="" styleClass="fa fa-remove" />
                                                                <h:outputText value=" #{rbButtonsEdit.delete}" />
                                                            </a4j:commandLink>


                                                            <script type="text/javascript">
                                                                (function($){
                                                                    $(document).ready(function(){
                                                                        $("#edit_form .minicolors").minicolors();
                                                                    });
                                                                }(jQuery));
                                                            </script>
                                                        </h:form>
                                                    </div>
                                                    <div style="background-color: #cccccc; border: 5px solid black; border-radius: 10px; padding: 12.5px;" class="col-sm-6">
                                                        <h:form id="page_form">
                                                            <a4j:repeat value="#{editPageButtonsHandler.findAllButtonRowsForCurrentPage}" var="row">

                                                                <div class="row" style="margin-bottom: 12.5px;">
                                                                    <a4j:repeat value="#{row.buttons}" var="button">

                                                                        <div class="col-sm-4">
                                                                            <a4j:outputPanel layout="block">
                                                                                <a4j:commandLink action="#{editPageButtonsHandler.select}" reRender="edit_form, page_form">
                                                                                    <h:panelGroup layout="block"
                                                                                                  styleClass="page-button #{button.cell eq editPageButtonsHandler.activeCell ? 'selected' : ''}"
                                                                                                  style="background-color: #{button.color}; #{button.buttonImageStyle};">
                                                                                    </h:panelGroup>
                                                                                    <f:param name="cell" value="#{button.cell}" />
                                                                                </a4j:commandLink>

                                                                                <rich:dragSupport dragIndicator=":indicator" dragType="button" dragValue="#{button}">
                                                                                    <rich:dndParam name="cell" value="#{button.cell}" />
                                                                                </rich:dragSupport>
                                                                                <rich:dropSupport acceptedTypes="button" reRender="edit_form, page_form"
                                                                                                  dropListener="#{editPageButtonsHandler.processDrop}"
                                                                                                  dropValue="#{button}" />
                                                                            </a4j:outputPanel>
                                                                        </div>

                                                                    </a4j:repeat>
                                                                </div>

                                                            </a4j:repeat>
                                                        </h:form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            </section>

                        </div>
                        <div class="col-md-3 col-md-pull-9 ct-js-sidebar">
                            <div class="row">
                                <%@ include file="/WEB-INF/sidebars/pagebuttons_edit_actions.jsp" %>
                            </div>
                        </div>

                    </div>

                </div>
            </section>
        </div>
    </body>
</f:view>