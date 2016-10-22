<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="mailingListMini">
		<g:set var="entityName" value="${message(code: 'example.label', default: 'example')}" />
		<title><g:message code="default.admin.menu.label" args="[entityName]" default="Welcome to ${entityName}" /></title>
	</head>
	<body>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			
<g:form action="index2">
ReportName : <g:textField name="report" value="${bean.report }"/>
Country: <g:select name="countrySelected" from="${bean.countries}" optionKey="name" optionValue="value"/>
<g:submitButton name="submit" value="submit"/>
</g:form>
</body>
</html>