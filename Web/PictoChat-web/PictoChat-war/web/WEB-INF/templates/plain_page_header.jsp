<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<header class="ct-pageHeader ct-pageHeader--type2 ct-pageHeader--motive ct-u-paddingBoth10">
    <div class="container ct-u-triangleBottomLeft">
        <div class="row">
            <div class="col-md-8">
                <h1 class="text-lowercase ct-fw-600 ct-u-colorWhite">
                    <c:out value="${param.heading}" />
                    <h:graphicImage value="/resources/images/launcher.png" style="width: 0.8em" />
                </h1>
            </div>
        </div>
    </div>
</header>