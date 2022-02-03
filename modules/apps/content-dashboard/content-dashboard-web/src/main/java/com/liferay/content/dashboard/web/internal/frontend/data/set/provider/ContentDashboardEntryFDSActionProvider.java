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

package com.liferay.content.dashboard.web.internal.frontend.data.set.provider;

import com.liferay.content.dashboard.web.internal.constants.ContentDashboardFDSNames;
import com.liferay.content.dashboard.web.internal.frontend.data.set.model.ContentDashboardFDSEntry;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryTracker;
import com.liferay.content.dashboard.web.internal.servlet.taglib.util.ContentDashboardDropdownItemsProvider;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	immediate = true,
	property = "fds.data.provider.key=" + ContentDashboardFDSNames.CONTENT_DASHBOARD_ENTRIES,
	service = FDSActionProvider.class
)
public class ContentDashboardEntryFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
		long groupId, HttpServletRequest httpServletRequest, Object model) {

		ContentDashboardDropdownItemsProvider
			contentDashboardDropdownItemsProvider =
				new ContentDashboardDropdownItemsProvider(
					_language, httpServletRequest, _portal);

		ContentDashboardFDSEntry contentDashboardFDSEntry =
			(ContentDashboardFDSEntry)model;

		InfoItemReference infoItemReference =
			contentDashboardFDSEntry.getInfoItemReference();

		Optional<ContentDashboardItemFactory<?>>
			contentDashboardItemFactoryOptional =
				_contentDashboardItemFactoryTracker.
					getContentDashboardItemFactoryOptional(
						infoItemReference.getClassName());

		return (List<DropdownItem>) contentDashboardItemFactoryOptional.map(
			contentDashboardItemFactory -> {
				try {
					return contentDashboardDropdownItemsProvider.
						getDropdownItems(
							contentDashboardItemFactory.create(
								infoItemReference.getClassPK()));
				}
				catch (PortalException e) {
					e.printStackTrace();
				}

				return new ArrayList<>();
			}
		).orElseGet(
			Collections::emptyList
		);
	}

	@Reference
	private ContentDashboardItemFactoryTracker
		_contentDashboardItemFactoryTracker;

	@Reference
	private Http _http;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}