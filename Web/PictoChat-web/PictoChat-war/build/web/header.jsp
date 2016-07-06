<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>


<div class="container">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
        <h:outputLink value="#{navigateTo.KPoint}" styleClass="navbar-brand">
            <img alt="K-POINT" src="./assets/images/demo-content/logo.png">
        </h:outputLink>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse">
        <ul class="nav navbar-nav ct-navbarNav--defaultActive ct-navbar--fadeIn yamm">
            <a4j:outputPanel layout="none" rendered="#{not sessionHandler.loggedIn}">
                <li>
                    <h:outputLink value="#{navigateTo.home}">
                        <h:outputText value="#{rbMenu.home} " />
                    </h:outputLink>
                </li>
            </a4j:outputPanel>
            <a4j:outputPanel layout="none" rendered="#{sessionHandler.loggedIn}">
                <li>
                    <h:outputLink value="#{navigateTo.secure.dashboard}">
                        <h:outputText value="#{rbMenu.home} " />
                    </h:outputLink>
                </li>
            </a4j:outputPanel>
        </ul>

        <ul class="nav navbar-nav navbar-right ct-navbarNav--defaultActive ct-navbar--fadeIn yamm">
            <%@ include file="header/language.jsp" %>
            <%@ include file="header/manual.jsp" %>

            <a4j:outputPanel layout="none" rendered="#{not sessionHandler.loggedIn}">
                <%@ include file="/header/login.jsp" %>
                <%@ include file="/header/register.jsp" %>
            </a4j:outputPanel>

            <a4j:outputPanel layout="none" rendered="#{sessionHandler.loggedIn}">
                <%@ include file="/header/profile.jsp" %>
            </a4j:outputPanel>
        </ul>
    </div>
</div>





<%--
<header id="header">
    <div class="container">
        <div class="header-inner clearfix">
            <div class="header-branding">
                <h:outputLink value="#{navigateTo.home}">
                    <h:graphicImage alt="Home" value="/resources/images/bnb.png" />
                </h:outputLink>
            </div>
            <div class="header-navbar">
                <div styleClass="header-tools">
                    <h:outputLink value="#{navigateTo.home}">
                        <h:outputText value="#{nav.home}" />
                    </h:outputLink>
                </div>
                <div class="header-search">
                    <%@ include file="header/search.jsp" %>
                </div>
                <a4j:outputPanel styleClass="header-menu" rendered="#{sessionHandler.isAdmin or sessionHandler.isLicensee}" layout="block">
                    <%@ include file="header/tools/admin.jsp" %>
                </a4j:outputPanel>
                <a4j:outputPanel id="header-tools" styleClass="header-tools">
                    <a4j:outputPanel styleClass="header-login" rendered="#{not sessionHandler.isLoggedIn}" layout="block">
                        <%@ include file="header/links/login.jsp" %>
                    </a4j:outputPanel>
                    <a4j:outputPanel styleClass="header-register" rendered="#{not sessionHandler.isLoggedIn}" layout="block">
                        <%@ include file="header/links/register.jsp" %>
                    </a4j:outputPanel>
                    <a4j:outputPanel styleClass="header-menu" rendered="#{sessionHandler.isLoggedIn}" layout="block">
                        <%@ include file="header/tools/profile.jsp" %>
                    </a4j:outputPanel>
                    <a4j:outputPanel styleClass="header-icon" rendered="#{sessionHandler.isLoggedIn}" layout="block">
                        <%@ include file="header/tools/messages.jsp" %>
                    </a4j:outputPanel>
                    <a4j:outputPanel styleClass="header-logout" rendered="#{sessionHandler.isLoggedIn}" layout="block">
                        <%@ include file="header/links/logout.jsp" %>
                    </a4j:outputPanel>
                    <div class="header-language">
                        <%@ include file="header/tools/language.jsp" %>
                    </div>
                </a4j:outputPanel>
            </div>
            <button class="search-toggle button">
                <i class="fa fa-search"></i>
            </button>
            <button class="navbar-toggle button">
                <i class="fa fa-bars"></i>
            </button>
        </div>
    </div>
</header>--%>