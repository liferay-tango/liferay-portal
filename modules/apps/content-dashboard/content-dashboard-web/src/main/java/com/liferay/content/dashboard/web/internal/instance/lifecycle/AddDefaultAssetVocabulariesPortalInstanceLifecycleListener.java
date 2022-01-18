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

package com.liferay.content.dashboard.web.internal.instance.lifecycle;

import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemAssetVocabularyClassNameMapperTracker;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryTracker;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddDefaultAssetVocabulariesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		_addAssetVocabulary(
			company, PropsValues.ASSET_VOCABULARY_DEFAULT,
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC, null);

		Collection<String> classNames =
			_contentDashboardItemFactoryTracker.getClassNames();

		Stream<String> stream = classNames.stream();

		String[] assetVocabularyTypeClassNames = stream.map(
			_contentDashboardItemAssetVocabularyClassNameMapperTracker::
				getContentDashboardItemAssetVocabularyClassName
		).toArray(
			size -> new String[size]
		);

		_addAssetVocabulary(
			company, "audience",
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
			assetVocabularyTypeClassNames);
		_addAssetVocabulary(
			company, "stage", AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
			assetVocabularyTypeClassNames);
	}

	private void _addAssetVocabulary(
			Company company, String name, int visibilityType,
			String[] assetVocabularyTypeClassNames)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(),
				StringUtil.toLowerCase(GetterUtil.getString(name)));

		if (assetVocabulary != null) {
			return;
		}

		User defaultUser = company.getDefaultUser();

		Set<Locale> locales = LanguageUtil.getCompanyAvailableLocales(
			company.getCompanyId());

		Stream<Locale> stream = locales.stream();

		Map<Locale, String> titleMap = stream.map(
			locale -> new AbstractMap.SimpleEntry<>(
				locale, LanguageUtil.get(locale, name))
		).collect(
			Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
		);

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		if (assetVocabularyTypeClassNames != null) {
			long[] classNameIds =
				new long[assetVocabularyTypeClassNames.length];
			long[] classTypePKs =
				new long[assetVocabularyTypeClassNames.length];
			boolean[] requireds =
				new boolean[assetVocabularyTypeClassNames.length];

			for (int i = 0; i < assetVocabularyTypeClassNames.length; i++) {
				classNameIds[i] = _portal.getClassNameId(
					assetVocabularyTypeClassNames[i]);
				classTypePKs[i] = AssetCategoryConstants.ALL_CLASS_TYPE_PK;
				requireds[i] = false;
			}

			assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
				classNameIds, classTypePKs, requireds);
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		_assetVocabularyLocalService.addVocabulary(
			null, defaultUser.getUserId(), company.getGroupId(), name,
			StringPool.BLANK, titleMap, Collections.emptyMap(),
			assetVocabularySettingsHelper.toString(), visibilityType,
			serviceContext);
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ContentDashboardItemAssetVocabularyClassNameMapperTracker
		_contentDashboardItemAssetVocabularyClassNameMapperTracker;

	@Reference
	private ContentDashboardItemFactoryTracker
		_contentDashboardItemFactoryTracker;

	@Reference
	private Portal _portal;

}