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

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardFDSNames;
import com.liferay.content.dashboard.web.internal.frontend.data.set.model.ContentDashboardFDSEntry;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryTracker;
import com.liferay.content.dashboard.web.internal.search.request.ContentDashboardSearchContextBuilder;
import com.liferay.content.dashboard.web.internal.searcher.ContentDashboardSearchRequestBuilderFactory;
import com.liferay.content.dashboard.web.internal.util.ContentDashboardSearchClassNameUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	immediate = true,
	property = "fds.data.provider.key=" + ContentDashboardFDSNames.CONTENT_DASHBOARD_ENTRIES,
	service = FDSDataProvider.class
)
public class ContentDashboardEntryFDSDataProvider
	implements FDSDataProvider<ContentDashboardFDSEntry> {

	@Override
	public List<ContentDashboardFDSEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchResponse searchResponse = _getSearchResponse(
			httpServletRequest, fdsPagination.getEndPosition(),
			fdsPagination.getStartPosition(), sort);

		List<ContentDashboardItem<?>> contentDashboardItems =
			_getContentDashboardItems(searchResponse.getDocuments71());

		Stream<ContentDashboardItem<?>> stream = contentDashboardItems.stream();

		return stream.map(
			contentDashboardItem -> new ContentDashboardFDSEntry(
				contentDashboardItem, themeDisplay.getLocale())
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		SearchResponse search = _searcher.search(
			_contentDashboardSearchRequestBuilderFactory.builder(
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, _assetCategoryLocalService,
					_assetVocabularyLocalService
				).build()
			).build());

		return search.getTotalHits();
	}

	private List<ContentDashboardItem<?>> _getContentDashboardItems(
		List<Document> documents) {

		Stream<Document> stream = documents.stream();

		return stream.map(
			this::_toContentDashboardItemOptional
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	private SearchResponse _getSearchResponse(
		HttpServletRequest httpServletRequest, int end, int start, Sort sort) {

		return _searcher.search(
			_contentDashboardSearchRequestBuilderFactory.builder(
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, _assetCategoryLocalService,
					_assetVocabularyLocalService
				).withEnd(
					end
				).withStart(
					start
				).withSort(
					sort
				).build()
			).build());
	}

	private Optional<ContentDashboardItem<?>> _toContentDashboardItemOptional(
		ContentDashboardItemFactory<?> contentDashboardItemFactory,
		Document document) {

		try {
			return Optional.of(
				contentDashboardItemFactory.create(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return Optional.empty();
		}
	}

	private Optional<ContentDashboardItem<?>> _toContentDashboardItemOptional(
		Document document) {

		Optional<ContentDashboardItemFactory<?>>
			contentDashboardItemFactoryOptional =
				_contentDashboardItemFactoryTracker.
					getContentDashboardItemFactoryOptional(
						ContentDashboardSearchClassNameUtil.getClassName(
							document.get(Field.ENTRY_CLASS_NAME)));

		return contentDashboardItemFactoryOptional.flatMap(
			contentDashboardItemFactory -> _toContentDashboardItemOptional(
				contentDashboardItemFactory, document));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardEntryFDSDataProvider.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ContentDashboardItemFactoryTracker
		_contentDashboardItemFactoryTracker;

	@Reference
	private ContentDashboardSearchRequestBuilderFactory
		_contentDashboardSearchRequestBuilderFactory;

	@Reference
	private Portal _portal;

	@Reference
	private Searcher _searcher;

}