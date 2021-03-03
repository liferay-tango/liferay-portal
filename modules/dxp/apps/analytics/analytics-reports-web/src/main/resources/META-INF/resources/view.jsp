<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
--%>

<%@ include file="/init.jsp" %>

<%
String analyticsReportsPanelState = SessionClicks.get(request, "com.liferay.analytics.reports.web_panelState", "closed");
String auditPanelState = SessionClicks.get(request, "com.liferay.analytics.reports.web_auditPanelState", "closed");
%>

<c:choose>
	<c:when test='<%= Objects.equals(analyticsReportsPanelState, "open") %>'>
		<liferay-util:include page="/analytics_reports_panel.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= Objects.equals(auditPanelState, "open") %>'>
		<liferay-util:include page="/view_audit.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>

<aui:script>
	var analyticsReportsPanelToggle = document.getElementById(
		'<portlet:namespace />analyticsReportsPanelToggleId'
	);

	var analyticsReportsSidenavInstance = Liferay.SideNavigation.initialize(
		analyticsReportsPanelToggle
	);

	analyticsReportsSidenavInstance.on('open.lexicon.sidenav', (event) => {
		Liferay.Util.Session.set(
			'com.liferay.analytics.reports.web_panelState',
			'open'
		);
	});

	analyticsReportsSidenavInstance.on('closed.lexicon.sidenav', (event) => {
		Liferay.Util.Session.set(
			'com.liferay.analytics.reports.web_panelState',
			'closed'
		);
	});

	Liferay.once('screenLoad', () => {
		Liferay.SideNavigation.destroy(analyticsReportsPanelToggle);
	});

	var auditPanelToggle = document.getElementById(
		'<portlet:namespace />auditPanelToggleId'
	);

	var auditSidenavInstance = Liferay.SideNavigation.initialize(auditPanelToggle);

	auditSidenavInstance.on('open.lexicon.sidenav', (event) => {
		Liferay.Util.Session.set(
			'com.liferay.analytics.reports.web_auditPanelState',
			'open'
		);
	});

	auditSidenavInstance.on('closed.lexicon.sidenav', (event) => {
		Liferay.Util.Session.set(
			'com.liferay.analytics.reports.web_auditPanelState',
			'closed'
		);
	});

	Liferay.once('screenLoad', () => {
		Liferay.SideNavigation.destroy(auditPanelToggle);
	});
</aui:script>