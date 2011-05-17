<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<% request.setAttribute("context", new PACKAGE.util.base.BaseActionBeanContext()); %>

<stripes:layout-render name='WEB-INF/jsp/layout/main.jsp' pageTitle='Gripes Application'>
	<stripes:layout-component name="adminBar">
		<stripes:layout-render name="WEB-INF/jsp/includes/_adminBar.jsp"/>
	</stripes:layout-component>
	<stripes:layout-component name="content">
		<ul>
			<c:forEach items="${context.actionableEntities}" var="item">
				<li>
					<a href="${contextPath}/${fn:toLowerCase(item.simpleName)}/list">${item.simpleName} List</a>
				</li>
			</c:forEach>
		</ul>
	</stripes:layout-component>
</stripes:layout-render>
