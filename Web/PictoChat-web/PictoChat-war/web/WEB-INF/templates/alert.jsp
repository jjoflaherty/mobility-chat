<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<div role="alert" class="alert <h:outputText value="#{message.alertClass}" />">
    <button data-dismiss="alert" class="close" type="button">
        <span aria-hidden="true">×</span><span class="sr-only">Close</span>
    </button>

    <strong><h:outputText value="#{message.title}" /></strong>
    <p>
        <h:outputText value="#{message.text}" escape="#{false}" />
    </p>
</div>