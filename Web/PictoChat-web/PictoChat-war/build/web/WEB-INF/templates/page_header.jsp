<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<header class="ct-pageHeader ct-pageHeader--type2 ct-pageHeader--motive ct-u-paddingBoth10 ct-pageHeader--hasDescription">
    <div class="container ct-u-triangleBottomLeft">
        <div class="row">
            <div class="col-md-8">
                <h1 class="text-lowercase ct-fw-600 ct-u-colorWhite">
                    <c:out value="${param.heading}" />
                    <sup>
                        <span class="<c:out value="${param.supClass}" />"></span>
                    </sup>
                </h1>
            </div>
            <div class="col-md-4">
                <span class="ct-u-colorWhite">
                    <c:out value="${param.content}" />
                </span>
            </div>
        </div>
    </div>
</header>