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

package com.liferay.analytics.reports.layout.display.page.internal.info.item;

import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.layout.display.page.info.item.LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.display.contributor.field.InfoDisplayContributorField;
import com.liferay.info.display.contributor.field.InfoDisplayContributorFieldTracker;
import com.liferay.info.item.InfoItemServiceTracker;
import com.liferay.info.type.WebImage;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
@Component(service = {AnalyticsReportsInfoItem.class, LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem.class })
public class LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemImpl
	implements LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem {

	@Override
	public String getAuthorName(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider) {

		return StringPool.BLANK;
	}

	@Override
	public long getAuthorUserId(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider) {

		return 0L;
	}

	@Override
	public WebImage getAuthorWebImage(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		Locale locale) {

		return null;
	}

	@Override
	public List<Locale> getAvailableLocales(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider) {

		return Optional.ofNullable(
			_groupLocalService.fetchGroup(
				layoutDisplayPageObjectProvider.getGroupId())
		).map(
			Group::getAvailableLanguageIds
		).map(
			LocaleUtil::fromLanguageIds
		).map(
			ListUtil::fromArray
		).orElseGet(
			() -> Collections.singletonList(LocaleUtil.getDefault())
		);
	}

	@Override
	public Locale getDefaultLocale(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider) {

		return Optional.ofNullable(
			_groupLocalService.fetchGroup(
				layoutDisplayPageObjectProvider.getGroupId())
		).map(
			Group::getDefaultLanguageId
		).map(
			LocaleUtil::fromLanguageId
		).orElseGet(
			LocaleUtil::getDefault
		);
	}

	@Override
	public Date getPublishDate(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		return new Date();
	}

	private Optional<ThemeDisplay> _getThemeDisplayOptional() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return Optional.ofNullable(serviceContext.getThemeDisplay());
		}

		return Optional.empty();
	}

	@Override
	public String getCanonicalURL(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
		Locale locale) {

		Optional<ThemeDisplay> themeDisplayOptional =
			_getThemeDisplayOptional();

		if (!themeDisplayOptional.isPresent()) {
			return StringPool.BLANK;
		}

		return themeDisplayOptional.map(
			themeDisplay -> {
				Optional<Layout> layoutOptional = _getLayoutOptional(
					layoutDisplayPageObjectProvider);

				return layoutOptional.map(
					layout -> {
						HttpServletRequest httpServletRequest =
							themeDisplay.getRequest();

						LayoutDisplayPageObjectProvider
							initialLayoutDisplayPageObjectProvider =
							(LayoutDisplayPageObjectProvider)
								httpServletRequest.getAttribute(
									LayoutDisplayPageWebKeys.
										LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

						httpServletRequest.setAttribute(
							LayoutDisplayPageWebKeys.
								LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
							layoutDisplayPageObjectProvider);

						String completeURL = _portal.getCurrentCompleteURL(
							httpServletRequest);

						try {
							String canonicalURL = _portal.getCanonicalURL(
								completeURL, themeDisplay, layout, false,
								false);

							LayoutSEOLink layoutSEOLink =
								_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
									layout, locale, canonicalURL,
									_portal.getAlternateURLs(
										canonicalURL, themeDisplay, layout));

							return layoutSEOLink.getHref();
						}
						catch (PortalException portalException) {
							_log.error(portalException, portalException);

							return StringPool.BLANK;
						}
						finally {
							httpServletRequest.setAttribute(
								LayoutDisplayPageWebKeys.
									LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
								initialLayoutDisplayPageObjectProvider);
						}
					}
				).orElse(
					StringPool.BLANK
				);
			}
		).orElse(
			StringPool.BLANK
		);
	}

	@Reference
	private Portal _portal;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Override
	public String getTitle(
		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider,
		Locale locale) {

		try {
			ClassName className = _classNameLocalService.getClassName(
				layoutDisplayPageObjectProvider.getClassNameId());

			List<InfoDisplayContributorField<? extends Object>>
				infoDisplayContributorFields =
					_infoDisplayContributorFieldTracker.
						getInfoDisplayContributorFields(
							className.getClassName());

			if (infoDisplayContributorFields == null) {
				throw new NoSuchModelException(
					"No info display contributor field found for " +
						className.getClassName());
			}

			for (InfoDisplayContributorField<?> infoDisplayContributorField :
					infoDisplayContributorFields) {

				InfoDisplayContributorField<Object>
					infoDisplayContributorField1 =
						(InfoDisplayContributorField<Object>)
							infoDisplayContributorField;

				infoDisplayContributorField1.getValue(
					layoutDisplayPageObjectProvider.getDisplayObject(), locale);
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return StringPool.BLANK;
	}

	@Override
	public boolean isShow(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		Optional<Layout> layoutOptional = _getLayoutOptional(layoutDisplayPageObjectProvider);

		return layoutOptional.filter(
			layout -> layout.isTypeAssetDisplay()
		).filter(
			layout -> !_isEmbeddedPersonalApplicationLayout(layout)
		).filter(
			layout -> {
				try {
					return _hasEditPermission(
						layoutDisplayPageObjectProvider, layout,
						PermissionThreadLocal.getPermissionChecker());
				}
				catch (PortalException portalException) {
					_log.error(portalException, portalException);

					return false;
				}
			}
		).map(
			layout -> true
		).orElse(
			false
		);
	}

	private Optional<Layout> _getLayoutOptional(
			LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		if ((layoutDisplayPageObjectProvider == null) ||
			(layoutDisplayPageObjectProvider.getDisplayObject() == null)) {

			return Optional.empty();
		}

		try {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
					layoutDisplayPageObjectProvider.getGroupId(),
					layoutDisplayPageObjectProvider.getClassNameId(),
					layoutDisplayPageObjectProvider.getClassPK(),
					layoutDisplayPageObjectProvider.getClassTypeId());

			if (layoutPageTemplateEntry == null) {
				return null;
			}

			return Optional.ofNullable(
				_layoutLocalService.fetchLayout(
					layoutPageTemplateEntry.getPlid()));
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return Optional.empty();
	}

	private boolean _hasEditPermission(
			LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
			Layout layout, PermissionChecker permissionChecker)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(
					layoutDisplayPageObjectProvider.getClassNameId());

		AssetRenderer<?> assetRenderer = null;

		if (assetRendererFactory != null) {
			assetRenderer = assetRendererFactory.getAssetRenderer(
				layoutDisplayPageObjectProvider.getClassPK());
		}

		if (((assetRenderer == null) ||
			 !assetRenderer.hasEditPermission(permissionChecker)) &&
			!LayoutPermissionUtil.contains(
				permissionChecker, layout, ActionKeys.UPDATE)) {

			return false;
		}

		return true;
	}

	private boolean _isEmbeddedPersonalApplicationLayout(Layout layout) {
		if (layout.isTypeControlPanel()) {
			return false;
		}

		String layoutFriendlyURL = layout.getFriendlyURL();

		if (layout.isSystem() &&
			layoutFriendlyURL.equals(
				PropsUtil.get(PropsKeys.CONTROL_PANEL_LAYOUT_FRIENDLY_URL))) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemImpl.class);

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoDisplayContributorFieldTracker
		_infoDisplayContributorFieldTracker;

	@Reference
	private InfoItemServiceTracker _infoItemServiceTracker;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private UserLocalService _userLocalService;

}