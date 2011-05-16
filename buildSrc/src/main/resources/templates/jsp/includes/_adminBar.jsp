<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<stripes:layout-definition>
	<div id="adminBar">
		<ul>
			<li><stripes:link href="/">Home</stripes:link></li>
			<c:forEach items="${links}" var="link">
				<li><stripes:link beanclass="${bean.class}" event="${link}">${link}</stripes:link></li>
			</c:forEach>
		</ul>
	</div>
</stripes:layout-definition>