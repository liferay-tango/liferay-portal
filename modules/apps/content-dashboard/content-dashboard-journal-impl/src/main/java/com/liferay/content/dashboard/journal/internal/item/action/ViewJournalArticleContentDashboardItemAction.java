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

package com.liferay.content.dashboard.journal.internal.item.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class ViewJournalArticleContentDashboardItemAction
	implements ContentDashboardItemAction {

	public ViewJournalArticleContentDashboardItemAction(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, JournalArticle journalArticle,
		Language language, LayoutLocalService layoutLocalService,
		LayoutSEOLinkManager layoutSEOLinkManager, Portal portal) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_journalArticle = journalArticle;
		_language = language;
		_layoutLocalService = layoutLocalService;
		_layoutSEOLinkManager = layoutSEOLinkManager;
		_portal = portal;
	}

	@Override
	public String getIcon() {
		return "view";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "view");
	}

	@Override
	public String getName() {
		return "view";
	}

	@Override
	public Type getType() {
		return Type.VIEW;
	}

	@Override
	public String getURL() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(themeDisplay.getLocale(), themeDisplay);
	}

	@Override
	public String getURL(Locale locale) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(locale, themeDisplay);
	}

	private Layout _getDisplayPageTemplateLayout() {
		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		LayoutPageTemplateEntry assetDisplayPageLayoutPageTemplateEntry =
			AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
				assetEntry.getGroupId(), assetEntry.getClassNameId(),
				assetEntry.getClassPK(), assetEntry.getClassTypeId());

		return _layoutLocalService.fetchLayout(
			assetDisplayPageLayoutPageTemplateEntry.getPlid());
	}

	private String _getViewURL(Locale locale, ThemeDisplay themeDisplay) {
		try {
			ThemeDisplay clonedThemeDisplay =
				(ThemeDisplay)themeDisplay.clone();

			clonedThemeDisplay.setScopeGroupId(_journalArticle.getGroupId());

			String friendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey(), locale,
					clonedThemeDisplay);

			Layout layout = _getDisplayPageTemplateLayout();

			String canonicalURL = _portal.getCanonicalURL(
				friendlyURL, clonedThemeDisplay, layout, false, false);

			List<LayoutSEOLink> localizedLayoutSEOLinks =
				_layoutSEOLinkManager.getLocalizedLayoutSEOLinks(
					layout, locale, canonicalURL,
					_language.getAvailableLocales());

			List<LayoutSEOLink> layoutSEOLinksWithCurrentLocale =
				ListUtil.filter(
					localizedLayoutSEOLinks,
					link -> {
						String hrefLang = link.getHrefLang();

						if (hrefLang != null) {
							String normalizedHrefLang = StringUtil.replace(
								hrefLang, '-', '_');

							return StringUtil.equalsIgnoreCase(
								normalizedHrefLang, locale.toString());
						}

						return false;
					});

			return Optional.ofNullable(
				layoutSEOLinksWithCurrentLocale.get(0)
			).map(
				url -> {
					String backURL = ParamUtil.getString(
						_httpServletRequest, "backURL");

					if (Validator.isNotNull(backURL)) {
						return HttpComponentsUtil.setParameter(
							url.getHref(), "p_l_back_url", backURL);
					}

					return HttpComponentsUtil.setParameter(
						url.getHref(), "p_l_back_url",
						themeDisplay.getURLCurrent());
				}
			).orElse(
				StringPool.BLANK
			);
		}
		catch (CloneNotSupportedException | PortalException exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewJournalArticleContentDashboardItemAction.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final JournalArticle _journalArticle;
	private final Language _language;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutSEOLinkManager _layoutSEOLinkManager;
	private final Portal _portal;

}