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

package com.liferay.content.dashboard.web.internal.servlet.taglib.util;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class ContentDashboardDropdownItemsProvider {

	public ContentDashboardDropdownItemsProvider(
		Language language, HttpServletRequest httpServletRequest,
		Portal portal) {

		_language = language;
		_httpServletRequest = httpServletRequest;
		_portal = portal;

		_currentURL = ParamUtil.getString(
			httpServletRequest, "currentURL",
			_portal.getCurrentURL(httpServletRequest));
	}

	public List<DropdownItem> getDropdownItems(
		ContentDashboardItem<?> contentDashboardItem) {

		Locale locale = _portal.getLocale(_httpServletRequest);

		DropdownItemList dropdownItemList = DropdownItemList.of(
			(DropdownItem[])TransformUtil.transformToArray(
				contentDashboardItem.getContentDashboardItemActions(
					_httpServletRequest, ContentDashboardItemAction.Type.VIEW,
					ContentDashboardItemAction.Type.EDIT),
				contentDashboardItemAction -> _toDropdownItem(
					contentDashboardItemAction, locale),
				DropdownItem.class));

		dropdownItemList.addAll(
			DropdownItemList.of(
				() -> {
					RequestBackedPortletURLFactory
						requestBackedPortletURLFactory =
							RequestBackedPortletURLFactoryUtil.create(
								_httpServletRequest);

					LiferayPortletURL liferayPortletURL =
						(LiferayPortletURL)
							requestBackedPortletURLFactory.createResourceURL(
								_getPortletId(_httpServletRequest));

					liferayPortletURL.setParameter("backURL", _currentURL);

					InfoItemReference infoItemReference =
						contentDashboardItem.getInfoItemReference();

					liferayPortletURL.setParameter(
						"className", infoItemReference.getClassName());
					liferayPortletURL.setParameter(
						"classPK",
						String.valueOf(infoItemReference.getClassPK()));

					liferayPortletURL.setResourceID(
						"/content_dashboard/get_content_dashboard_item_info");

					return DropdownItemBuilder.setData(
						HashMapBuilder.<String, Object>put(
							"action", "showInfo"
						).put(
							"className", infoItemReference.getClassName()
						).put(
							"classPK", infoItemReference.getClassPK()
						).put(
							"fetchURL", String.valueOf(liferayPortletURL)
						).build()
					).setIcon(
						"info-circle-open"
					).setLabel(
						_language.get(locale, "info")
					).setQuickAction(
						true
					).build();
				}));

		dropdownItemList.addAll(
			TransformUtil.transform(
				contentDashboardItem.getContentDashboardItemActions(
					_httpServletRequest,
					ContentDashboardItemAction.Type.VIEW_IN_PANEL),
				contentDashboardItemAction -> _toViewInPanelDropdownItem(
					contentDashboardItem, contentDashboardItemAction, locale)));

		return dropdownItemList;
	}

	private String _getPortletId(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getId();
	}

	private PortletURL _getRenderURL(HttpServletRequest httpServletRequest) {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return requestBackedPortletURLFactory.createRenderURL(
			_getPortletId(httpServletRequest));
	}

	private DropdownItem _toDropdownItem(
		ContentDashboardItemAction contentDashboardItemAction, Locale locale) {

		if (contentDashboardItemAction == null) {
			return null;
		}

		return DropdownItemBuilder.setHref(
			contentDashboardItemAction.getURL(locale)
		).setIcon(
			contentDashboardItemAction.getIcon()
		).setLabel(
			contentDashboardItemAction.getLabel(locale)
		).setQuickAction(
			true
		).build();
	}

	private DropdownItem _toViewInPanelDropdownItem(
		ContentDashboardItem contentDashboardItem,
		ContentDashboardItemAction contentDashboardItemAction, Locale locale) {

		InfoItemReference infoItemReference =
			contentDashboardItem.getInfoItemReference();

		return DropdownItemBuilder.setData(
			HashMapBuilder.<String, Object>put(
				"action", "showMetrics"
			).put(
				"className", infoItemReference.getClassName()
			).put(
				"classPK", infoItemReference.getClassPK()
			).put(
				"fetchURL", contentDashboardItemAction.getURL(locale)
			).build()
		).setIcon(
			contentDashboardItemAction.getIcon()
		).setLabel(
			contentDashboardItemAction.getLabel(locale)
		).setQuickAction(
			true
		).build();
	}

	private final String _currentURL;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

}