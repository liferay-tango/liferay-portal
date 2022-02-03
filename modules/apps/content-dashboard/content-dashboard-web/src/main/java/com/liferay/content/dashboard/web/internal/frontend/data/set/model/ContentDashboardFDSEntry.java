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

package com.liferay.content.dashboard.web.internal.frontend.data.set.model;

import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemSubtype;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Cristina González
 */
public class ContentDashboardFDSEntry {

	public ContentDashboardFDSEntry(
		ContentDashboardItem<?> contentDashboardItem, Locale locale) {

		_contentDashboardItem = contentDashboardItem;
		_locale = locale;
	}

	public InfoItemReference getInfoItemReference() {
		return _contentDashboardItem.getInfoItemReference();
	}

	public String getScope() {
		return _contentDashboardItem.getScopeName(_locale);
	}

	public Date getModifiedDate() {
		return _contentDashboardItem.getModifiedDate();
	}

	public List<StatusInfo> getStatus() {
		List<ContentDashboardItem.Version> versions =
			_contentDashboardItem.getVersions(_locale);

		Stream<ContentDashboardItem.Version> stream = versions.stream();

		return stream.map(
			version -> new StatusInfo(
				version.getLabel(),
				LanguageUtil.get(_locale, version.getLabel()))
		).collect(
			Collectors.toList()
		);
	}

	public String getSubType() {
		ContentDashboardItemSubtype contentDashboardItemSubtype =
			_contentDashboardItem.getContentDashboardItemSubtype();

		return contentDashboardItemSubtype.getLabel(_locale);
	}

	public String getTitle() {
		return _contentDashboardItem.getTitle(_locale);
	}

	public String getType() {
		return _contentDashboardItem.getTypeLabel(_locale);
	}

	private final ContentDashboardItem<?> _contentDashboardItem;
	private final Locale _locale;

}