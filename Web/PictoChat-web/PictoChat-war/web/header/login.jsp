<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<li>
    <h:outputLink value="#{navigateTo.login.login}">
        <h:outputText value="" styleClass="fa fa-sign-in" />
        <h:outputText value=" #{rbMenu.login}" />
    </h:outputLink>
</li>