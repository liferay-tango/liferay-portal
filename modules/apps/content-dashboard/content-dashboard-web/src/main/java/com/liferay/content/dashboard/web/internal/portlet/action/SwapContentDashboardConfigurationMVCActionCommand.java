/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * @author David Arques
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/swap_content_dashboard_configuration"
	},
	service = MVCActionCommand.class
)
public class SwapContentDashboardConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put("success", false);

		try {
			PortletPreferences portletPreferences =
				actionRequest.getPreferences();

			String[] assetVocabularyNames = portletPreferences.getValues(
				"assetVocabularyNames", new String[0]);

			if (assetVocabularyNames.length == 2) {
				String tempAssetVocabularyName = assetVocabularyNames[0];
				assetVocabularyNames[0] = assetVocabularyNames[1];
				assetVocabularyNames[1] = tempAssetVocabularyName;
				portletPreferences.setValues(
					"assetVocabularyNames", assetVocabularyNames);
				portletPreferences.store();
				jsonObject.put("success", true);
			}
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			JSONUtil.put(
				"error",
				LanguageUtil.get(
					actionRequest.getLocale(), "an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SwapContentDashboardConfigurationMVCActionCommand.class);

}