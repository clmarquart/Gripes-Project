<%@ include file="/WEB-INF/jsp/includes/taglibs.jsp" %>
<stripes:layout-render name='/WEB-INF/jsp/layout/main.jsp' pageTitle='Page View'>
	<stripes:layout-component name="adminBar">
		<stripes:layout-render name="/WEB-INF/jsp/includes/_adminBar.jsp"/>
	</stripes:layout-component>

	<stripes:layout-component name="content">
		<stripes:form beanclass="PACKAGE.UserActionBean">
			<div>
				Username: <input type="text" name="username" value="" />
			</div>
			<div>
				Password: <input type="password" name="password" value="" />
			</div>
			<div>
				<stripes:hidden name="loginUrl" />
				<input type="submit" name="authenticate" value="Login" />
			</div>
		</stripes:form>
	</stripes:layout-component>
</stripes:layout-render>
