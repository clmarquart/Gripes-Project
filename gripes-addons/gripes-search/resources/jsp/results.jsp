<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>

<stripes:useActionBean id="bean" beanclass="PACKAGE.action.SearchActionBean"/>
<stripes:layout-render name='../layout/main.jsp' pageTitle="Search: ${param['query']}">
	<stripes:layout-component name="adminBar">
		<stripes:layout-render name="../includes/_adminBar.jsp" bean="${bean}" links="list" />
	</stripes:layout-component>

	<stripes:layout-component name="content">
		<c:forEach items="${bean.results}" var="result">
			<div class='fieldRow'>
				<stripes:link beanclass="${result.action}" event="view">
					<stripes:param name="${fn:toLowerCase(result.entity.class.simpleName)}" value="${result.entity.id}" />
					${result.entity.class} - ${result.entity.id}
				</stripes:link>
			</div>
		</c:forEach>
		<div id="searchBar">
			<form action="${contextPath}/search" id="searchForm" method="get">
				<input type="text" name="query" class="searchInputBox" />
				<input type="submit" name="doSearch" class="searchSubmit" value="Search" />
			</form>
		</div>
	</stripes:layout-component>
</stripes:layout-render>
