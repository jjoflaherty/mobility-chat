<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<a4j:outputPanel layout="none">
    <div class="col-sm-6 col-md-12">
        <section class="widget">
            <div class="widget-inner">
                <h4 class="text-lowercase">
                    <h:outputText value="#{rbDashboard.actions}" />
                </h4>
                <ul>
                    <li>
                        <h:form>
                            <h:commandLink action="#{createClientHandler.resetAndLoad}">
                                <h:outputText value="#{rbDashboard.new_client}" />
                                <span class="pull-right ct-blogWidget-count"></span>
                            </h:commandLink>
                        </h:form>
                    </li>
                </ul>
            </div>
        </section>
    </div>
</a4j:outputPanel>