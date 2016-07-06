<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<div role="alert" class="alert <h:outputText value="#{message.alertClass}" />">
    <strong><h:outputText value="#{message.title}" /></strong>
    <p><h:outputText value="#{message.text}" escape="#{false}" /><p>
</div>