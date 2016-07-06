<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >

<f:view locale="#{localeHandler.locale}">
    <div id="ct-ajaxContent" style="border: 1px solid; padding: 20px;">
        <div>
            <h4 class="ct-fw-600">
                <h:outputText value="#{rbPageDetail.preview}" />
            </h4>
            <div class="row">
                <div class="col-sm-12">
                    <h:panelGroup layout="block" style="border: 5px solid black; border-radius: 10px; padding: 12.5px;" styleClass="#{previewPageHandler.previewStyle}">
                        <a4j:repeat value="#{previewPageHandler.findAllButtonRowsForCurrentPage}" var="row">

                            <div class="row" style="margin-bottom: 12.5px;">
                                <a4j:repeat value="#{row.buttons}" var="button">

                                    <h:panelGroup layout="block" styleClass="#{button.columnStyle}">
                                        <h:panelGroup layout="block"
                                                      styleClass="page-button"
                                                      style="background-color: #{button.color}; #{button.buttonImageStyle};">
                                        </h:panelGroup>
                                    </h:panelGroup>

                                </a4j:repeat>
                            </div>

                        </a4j:repeat>
                    </h:panelGroup>
                    <div class="col-sm-4">
                        <%--<h5>Connected pages</h5>
                        <ul>
                            <li>Food</li>
                            <li>Sports</li>
                        </ul>--%>
                    </div>
                    <div class="col-sm-4">
                        <section class="widget">
                            <div class="widget-inner">
                                <h5 class="text-lowercase">
                                    <h:outputText value="#{rbPageDetail.actions}" />
                                </h5>
                                <ul>
                                    <li>
                                        <h:form id="editButtonsLink_form">
                                            <h:commandLink id="editButtonsLink" action="#{editPageButtonsHandler.show}">
                                                <h:outputText value="#{rbPageDetail.edit_buttons}" />
                                                <f:param name="page" value="#{previewPageHandler.activePage.id}" />
                                            </h:commandLink>
                                        </h:form>
                                    </li>
                                    <li>
                                        <h:form id="editLink_form">
                                            <h:commandLink id="editLink" action="#{editPageHandler.show}">
                                                <h:outputText value="#{rbPageDetail.edit_page}" />
                                                <f:param name="page" value="#{previewPageHandler.activePage.id}" />
                                            </h:commandLink>
                                        </h:form>
                                    </li>
                                    <li>
                                        <a4j:commandLink oncomplete="#{rich:component('deletePagePanel')}.show();" rendered="#{not previewPageHandler.activePage.isStartPage}">
                                            <h:outputText value="#{rbPageDetail.delete}" />
                                            <a4j:actionparam name="pageId" value="#{previewPageHandler.activePage.id}" assignTo="#{deletePageHandler.pageId}"/>
                                        </a4j:commandLink>
                                    </li>
                                </ul>
                            </div>
                        </section>
                    </div>
                </div>
            </div>
        </div>

        <rich:modalPanel id="deletePagePanel" autosized="true" width="200">
            <f:facet name="header">
                <h:outputFormat value="#{rbPageDetail.delete_confirm}">
                    <f:param value="#{deletePageHandler.pageId}" />
                </h:outputFormat>
            </f:facet>
            <br/>
            <h:form>
                <table width="100%">
                    <tbody>
                        <tr>
                            <td align="center" width="50%">
                                <a4j:commandLink value="#{rbPageDetail.yes}" oncomplete="#{rich:component('deletePagePanel')}.hide(); return true;" action="#{deletePageHandler.delete}" />
                            </td>
                            <td align="center" width="50%">
                                <a4j:commandLink value="#{rbPageDetail.no}" onclick="#{rich:component('deletePagePanel')}.hide(); return false;" />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </h:form>
        </rich:modalPanel>
    </div>
</f:view>