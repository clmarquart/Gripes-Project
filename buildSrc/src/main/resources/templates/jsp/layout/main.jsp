<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<stripes:layout-definition>
<html>
	<head>
		<title>${pageTitle}</title>
		<link href="${pageContext.request.contextPath}/style/main.css" rel="stylesheet" media="all" type="text/css" />
		<stripes:layout-component name="head" />
	</head>
	<body>
		<div id="container">
			<div id="header">
				<h2>Gripes Project</h2>
			</div>
			<div id="contentWrapper">
				<stripes:layout-component name="adminBar" />
				<div id="content">
					<h1>${pageTitle}</h1>
					<stripes:layout-component name="content" />
				</div>
			</div>
			<div id="footer">Footer</div>
		</div>
		<script src="${pageContext.request.contextPath}/script/jquery.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/script/main.js" type="text/javascript"></script>
	</body>
</html>
</stripes:layout-definition>
