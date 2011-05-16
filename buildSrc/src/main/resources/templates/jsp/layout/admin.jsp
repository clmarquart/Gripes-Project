<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<%
pageContext.request.setAttribute("context",pageContext.request.contextPath);
%>

<stripes:layout-definition>
	<html>
        <head>
            <title>Gripes Administration</title>
            <link rel="stylesheet" type="text/css" xhref="${context}/style/admin.css"/>

            <stripes:layout-component name="html_head"/>
        </head>
        <body>
            <stripes:layout-component name="header">
                <jsp:include page="/layout/_adminHeader.jsp"/>
            </stripes:layout-component>

            <div class="pageContent">
                <stripes:layout-component name="contents"/>
            </div>

            <stripes:layout-component name="footer">
                <jsp:include page="/layout/_adminFooter.jsp"/>
            </stripes:layout-component>
        </body>
    </html>
</stripes:layout-definition>