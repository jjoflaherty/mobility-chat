<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="col-sm-6 col-md-12">
    <section class="widget">
        <div class="widget-inner">
            <h4 class="text-lowercase">
                <h:outputText value="#{rbNetworkInvite.actions}" />
            </h4>
            <ul>
                <li>
                    <h:outputLink value="#{navigateTo.secure.client.coaches.admin}">
                        <h:outputText value="#{rbNetworkInvite.back}" />
                        <span class="pull-right ct-blogWidget-count"></span>
                    </h:outputLink>
                </li>
            </ul>
        </div>
    </section>
</div>