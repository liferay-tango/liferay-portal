<%@ page
    import="com.liferay.segments.configuration.provider.SegmentsConfigurationProvider" %>
<%@ page import="com.liferay.segments.configuration.SegmentsConfiguration" %>
<%@ include file="/init.jsp" %>

<%
    String redirect = ParamUtil.getString(request, "redirect");

    PortletURL portletURL = renderResponse.createRenderURL();

    if (Validator.isNull(redirect)) {
        redirect = portletURL.toString();
    }

    SegmentsConfigurationProvider segmentsConfigurationProvider = (SegmentsConfigurationProvider)request.getAttribute(
        SegmentsConfiguration.class.getName());
%>
<clay:sheet
    size="full"
>
	<h2>
		<liferay-ui:message key="segments-service-company-configuration-name" />

		<c:if test="<%= true %>">
			<liferay-ui:icon-menu
				cssClass="float-right"
				direction="right"
				markupView="lexicon"
				showWhenSingleIcon="<%= true %>"
			>
				
				<portlet:actionURL name="/configuration_admin/delete_configuration" var="deleteConfigActionURL">
					<portlet:param name="redirect" value="" />
					<portlet:param name="factoryPid" value="" />
					<portlet:param name="pid" value="" />
				</portlet:actionURL>

				<liferay-ui:icon
					message="reset-default-values"
					method="post"
					url=""
				/>

				<portlet:resourceURL id="/configuration_admin/export_configuration" var="exportURL">
					<portlet:param name="factoryPid" value="" />
					<portlet:param name="pid" value="" />
				</portlet:resourceURL>

				<liferay-ui:icon
					message="export"
					method="get"
					url="http://example.com"
				/>
			</liferay-ui:icon-menu>
		</c:if>
	</h2>

	<aui:form action="my_action" method="post" name="fm">
		<c:if test="<%= true %>">
			<aui:alert closeable="<%= false %>" id="errorAlert" type="info">
				<liferay-ui:message key="this-configuration-is-not-saved-yet.-the-values-shown-are-the-default" />
			</aui:alert>
		</c:if>

		<div class="form-group">
			<clay:checkbox
				checked="<%= segmentsConfigurationProvider.isSegmentationEnabled(themeDisplay.getCompanyId()) %>"
				className="mb-3"
				disabled="<%= !segmentsConfigurationProvider.isSegmentationEnabled(themeDisplay.getCompanyGroupId()) %>"
				id='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
				label='<%= LanguageUtil.get(request, "segmentation-enabled-name") %>'
				name='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal"><liferay-ui:message key="segmentation-enabled-description" /></div>
			</div>
		</div>

		<div class="form-group">
			<clay:checkbox
				checked="<%= segmentsConfigurationProvider.isRoleSegmentationEnabled(themeDisplay.getCompanyId()) %>"
				disabled="<%= !segmentsConfigurationProvider.isRoleSegmentationEnabled(themeDisplay.getCompanyGroupId()) %>"
				id='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
				label='<%= LanguageUtil.get(request, "role-segmentation-enabled-name") %>'
				name='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="role-segmentation-enabled-description" />
				</div>
			</div>
		</div>

		<div class="sheet-footer">
			<div class="btn-group-item">
				<div class="btn-group-item">
					<c:choose>
						<c:when test="<%= true %>">
							<clay:button
								displayType="primary"
								id='<%= liferayPortletResponse.getNamespace() + "update" %>'
								label='<%= LanguageUtil.get(request, "update") %>'
								name='<%= liferayPortletResponse.getNamespace() + "update" %>'
								type="submit"
							/>
						</c:when>
						<c:otherwise>
							<clay:button
								displayType="primary"
								id='<%= liferayPortletResponse.getNamespace() + "save" %>'
								label='<%= LanguageUtil.get(request, "save") %>'
								name='<%= liferayPortletResponse.getNamespace() + "save" %>'
								type="submit"
							/>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="btn-group-item">
					<clay:link
						displayType="secondary"
						href="<%= redirect %>"
						id='<%= liferayPortletResponse.getNamespace() + "cancel" %>'
						label='<%= LanguageUtil.get(request, "cancel") %>'
						type="button"
					/>
				</div>
			</div>
		</div>
	</aui:form>
</clay:sheet>