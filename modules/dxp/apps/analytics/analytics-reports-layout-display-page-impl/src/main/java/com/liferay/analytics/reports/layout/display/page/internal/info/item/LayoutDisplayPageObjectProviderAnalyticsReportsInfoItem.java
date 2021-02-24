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
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
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
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = AnalyticsReportsInfoItem.class)
public class LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem
	implements AnalyticsReportsInfoItem<LayoutDisplayPageObjectProvider> {

	@Override
	public String getAuthorName(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		return StringPool.BLANK;
	}

	@Override
	public long getAuthorUserId(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		return 0L;
	}

	@Override
	public WebImage getAuthorWebImage(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
		Locale locale) {

		return null;
	}

	@Override
	public List<Locale> getAvailableLocales(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		return Optional.ofNullable(
			_groupLocalService.fetchGroup(
				layoutDisplayPageObjectProvider.getGroupId())
		).map(
			Group::getGroupId
		).map(
			_language::getAvailableLocales
		).map(
			ListUtil::fromCollection
		).orElseGet(
			() -> Collections.singletonList(LocaleUtil.getDefault())
		);
	}

	@Override
	public Locale getDefaultLocale(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

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

	@Override
	public String getTitle(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
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

		Layout layout = _getLayout(layoutDisplayPageObjectProvider);

		if (layout == null) {
			return false;
		}

		if (!layout.isTypeAssetDisplay()) {
			return false;
		}

		if (_isEmbeddedPersonalApplicationLayout(layout)) {
			return false;
		}

		try {
			if (!_hasEditPermission(
					layout, layoutDisplayPageObjectProvider,
					PermissionThreadLocal.getPermissionChecker())) {

				return false;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return false;
		}

		return true;
	}

	private Layout _getLayout(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		if ((layoutDisplayPageObjectProvider == null) ||
			(layoutDisplayPageObjectProvider.getDisplayObject() == null)) {

			return null;
		}

		try {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				AssetDisplayPageUtil.getAssetDisplayPageLayoutPageTemplateEntry(
					layoutDisplayPageObjectProvider.getGroupId(),
					layoutDisplayPageObjectProvider.getClassNameId(),
					layoutDisplayPageObjectProvider.getClassPK(),
					layoutDisplayPageObjectProvider.getClassTypeId());

			return _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return null;
	}

	private boolean _hasEditPermission(
			Layout layout,
			LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider,
			PermissionChecker permissionChecker)
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
		LayoutDisplayPageObjectProviderAnalyticsReportsInfoItem.class);

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
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private UserLocalService _userLocalService;

}