<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="col-sm-6 col-md-12">
    <h:form>
        <section class="widget">
            <div class="widget-inner">
                <h4 class="text-lowercase">My organisations</h4>
                <ul>
                    <a4j:outputPanel layout="none" rendered="#{empty sessionHandler.organisationsWhereActiveMember}">
                        U bent nog niet aangesloten bij een organisatie
                    </a4j:outputPanel>
                    <a4j:outputPanel layout="none" rendered="#{not empty sessionHandler.organisationsWhereActiveMember}">
                        <a4j:repeat value="#{sessionHandler.organisationsWhereActiveMember}" var="o">
                            <li class="<h:outputText value="current-cat" rendered="#{o.isCurrent}" />">
                                <h:commandLink action="#{sessionHandler.showOrganisation}">
                                    <h:outputText value="#{o.name}" />

                                    <f:param name="organisation" value="#{o.id}" />
                                </h:commandLink>
                            </li>
                        </a4j:repeat>
                    </a4j:outputPanel>
                </ul>
            </div>
        </section>
    </h:form>
</div>