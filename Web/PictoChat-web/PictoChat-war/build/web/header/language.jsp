<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<li class="dropdown">
    <a href="#">
        <a4j:outputPanel styleClass="#{localeHandler.localeFlagClass}" layout="block" />
        <span><h:outputText value="#{localeHandler.localeLabel} " /></span>
    </a>

    <ul class="dropdown-menu">
        <h:form style="margin: 0px;">
            <a4j:repeat value="#{localeHandler.locales}" var="locale">
                <li>
                    <h:commandLink actionListener="#{localeHandler.localeChanged}">
                        <a4j:outputPanel styleClass="#{locale.flagClass}" layout="block" />
                        <span><h:outputText value="#{locale.label}" /></span>
                        
                        <f:attribute name="locale" value="#{locale.localeCode}" />
                    </h:commandLink>
                </li>
            </a4j:repeat>
        </h:form>
    </ul>
</li>