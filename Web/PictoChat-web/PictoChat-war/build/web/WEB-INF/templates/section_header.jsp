<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<header class="ct-pageSectionHeader ct-pageSectionHeader--numbered ct-u-marginTop100 ct-u-marginBottom70 ct-js-wayPoint">
    <h2 class="ct-fw-600">
        <span><c:out value="${param.heading}" /></span>
        <small class="ct-u-colorMotive ct-fw-300"><c:out value="${param.subtitle}" /></small>
    </h2>
    <span class="ct-pageSectionHeader-rightContent ct-u-colorMotive <c:out value="${param.rightClass}" />">
        <c:out value="${param.rightContent}" />
    </span>
</header>