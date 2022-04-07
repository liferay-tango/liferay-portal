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

package com.liferay.content.dashboard.item.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author David Arques
 * @author Yurena Cabrera
 */
public interface ContentDashboardItemAction {

	public String getIcon();

	public String getLabel(Locale locale);

	public String getName();

	public Type getType();

	public String getURL();

	public String getURL(Locale locale);

	public default JSONArray getAlternates (List<Locale> locales, Locale defaultLocale){
		return JSONUtil.putAll(
			locales.stream().map(
				locale -> JSONUtil.put(
					"default",
					Objects.equals(
						locale, defaultLocale)
				).put(
					"languageId", LocaleUtil.toBCP47LanguageId(locale)
				).put(
					"viewURL", getURL(locale)
				)
			).toArray());
	}

	public enum Type {

		DELETE, DOWNLOAD, EDIT, PREVIEW, PREVIEW_IMAGE, VIEW, VIEW_IN_PANEL

	}

}