<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<div class="col-sm-6">

    <h:form id="login_form">
        <h:panelGroup layout="block" styleClass="form-group #{sessionHandler.emailMessages.fieldClass}">
            <h:inputText id="email" value="#{sessionHandler.user.email}" styleClass="form-control input-lg"
                         required="true" requiredMessage="#{rbLogin.email_required}" />
            <h:outputLabel value="#{rbLogin.email}" for="email" />
        </h:panelGroup>

        <h:panelGroup layout="block" styleClass="form-group #{sessionHandler.passwordMessages.fieldClass}">
            <h:inputSecret id="password" value="#{sessionHandler.user.password}" styleClass="form-control input-lg"
                           required="true" requiredMessage="#{rbLogin.password_required}" />
            <h:outputLabel value="#{rbLogin.password}" for="password" />
        </h:panelGroup>

        <h:commandLink action="#{sessionHandler.login}" id="submit"
                       styleClass="btn ct-btn--perspective btn-primary btn-lg">
            <h:outputText value="" styleClass="fa fa-sign-in" />
            <h:outputText value=" #{rbLogin.login}" />
        </h:commandLink>

        <h:commandButton styleClass="hidden-button" action="#{sessionHandler.login}" />
    </h:form>

</div>

<div class="col-sm-6">
    <a4j:repeat value="#{sessionHandler.allMessages}" var="mg">
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