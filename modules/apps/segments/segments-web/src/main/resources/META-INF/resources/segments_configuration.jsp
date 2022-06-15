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
    </h2>

    <aui:form action="my_action" method="post" name="fm">
        <div class="form-group">
            <clay:checkbox
                checked="<%= segmentsConfigurationProvider.isSegmentationEnabled(themeDisplay.getCompanyId()) %>"
                className="mb-3"
                disabled="<%= !segmentsConfigurationProvider.isSegmentationEnabled(themeDisplay.getCompanyGroupId()) %>"
                id='<%=liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
                label='<%= LanguageUtil.get(request, "segmentation-enabled-name") %>'
                name='<%=liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
            />

            <div aria-hidden="true" class="form-feedback-group">
                <div class="form-text text-weight-normal"><liferay-ui:message key="segmentation-enabled-description" /></div>
            </div>
        </div>

        <div class="form-group">
            <clay:checkbox
                checked="<%= segmentsConfigurationProvider.isRoleSegmentationEnabled(themeDisplay.getCompanyId()) %>"
                disabled="<%= !segmentsConfigurationProvider.isRoleSegmentationEnabled(themeDisplay.getCompanyGroupId()) %>"
                id='<%=liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
                label='<%= LanguageUtil.get(request, "role-segmentation-enabled-name") %>'
                name='<%=liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
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
                    <clay:button
                        displayType="primary"
                        id='<%= liferayPortletResponse.getNamespace() + "update" %>'
                        label='<%= LanguageUtil.get(request, "update") %>'
                        name='<%= liferayPortletResponse.getNamespace() + "update" %>'
                        type="submit"
                    />
                </div>
                <div class="btn-group-item">
                    <clay:link
                        displayType="secondary"
                        id='<%= liferayPortletResponse.getNamespace() + "cancel" %>'
                        href="<%= redirect %>"
                        label='<%= LanguageUtil.get(request, "cancel") %>'
                        type="button"
                    />
                </div>
            </div>
        </div>
    </aui:form>
    
</clay:sheet>