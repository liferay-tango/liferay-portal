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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminDisplayContext;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemSubtype;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.type.WebImage;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.BrowserSnifferImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.io.ByteArrayOutputStream;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * @author Cristina González
 */
public class GetContentDashboardItemsCsvMVCResourceCommandTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BrowserSnifferUtil browserSnifferUtil = new BrowserSnifferUtil();

		browserSnifferUtil.setBrowserSniffer(new BrowserSnifferImpl());

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@Before
	public void setUp() {
		_setUpLanguageUtil();
	}

	@Test
	public void testCsvServeResource() throws Exception {
		ContentDashboardItem contentDashboardItem = _getContentDashboardItem(
			"assetCategory", "assetTag", "className", 12345L);

		_initGetContentDashboardItemsCsvMVCResourceCommand(
			contentDashboardItem);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(WebKeys.LOCALE, LocaleUtil.US);

		InfoItemReference infoItemReference =
			contentDashboardItem.getInfoItemReference();

		mockLiferayResourceRequest.addParameter(
			"className", infoItemReference.getClassName());
		mockLiferayResourceRequest.addParameter(
			"classPK", String.valueOf(infoItemReference.getClassPK()));

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_getContentDashboardItemsCsvMVCResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		byte[] bytes = byteArrayOutputStream.toByteArray();

		Assert.assertTrue(bytes.length != 0);
	}

	private ContentDashboardItem _getContentDashboardItem(
		String assetCategoryTitle, String assetTagName, String className,
		long classPK) {

		return new ContentDashboardItem() {

			@Override
			public List<AssetCategory> getAssetCategories() {
				AssetCategory assetCategory = mock(AssetCategory.class);

				when(
					assetCategory.getTitle(Mockito.any(Locale.class))
				).thenReturn(
					assetCategoryTitle
				);

				return Collections.singletonList(assetCategory);
			}

			@Override
			public List<AssetCategory> getAssetCategories(long vocabularyId) {
				return Collections.emptyList();
			}

			@Override
			public List<AssetTag> getAssetTags() {
				AssetTag assetCategory = mock(AssetTag.class);

				when(
					assetCategory.getName()
				).thenReturn(
					assetTagName
				);

				return Collections.singletonList(assetCategory);
			}

			@Override
			public List<Locale> getAvailableLocales() {
				return Collections.singletonList(LocaleUtil.US);
			}

			@Override
			public List<ContentDashboardItemAction>
				getContentDashboardItemActions(
					HttpServletRequest httpServletRequest,
					ContentDashboardItemAction.Type... types) {

				return Collections.emptyList();
			}

			@Override
			public ContentDashboardItemSubtype
				getContentDashboardItemSubtype() {

				ContentDashboardItemSubtype contentDashboardItemSubtype = mock(
					ContentDashboardItemSubtype.class);

				when(
					contentDashboardItemSubtype.getLabel(
						Mockito.any(Locale.class))
				).thenReturn(
					"subTypeLabel"
				);

				return contentDashboardItemSubtype;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public Map<String, Object> getData(Locale locale) {
				return Collections.emptyMap();
			}

			@Override
			public ContentDashboardItemAction
				getDefaultContentDashboardItemAction(
					HttpServletRequest httpServletRequest) {

				return null;
			}

			@Override
			public Locale getDefaultLocale() {
				return LocaleUtil.US;
			}

			@Override
			public String getDescription(Locale locale) {
				return "Web Content description";
			}

			@Override
			public Object getDisplayFieldValue(
				String fieldName, Locale locale) {

				if (Objects.equals(fieldName, "authorProfileImage")) {
					return new WebImage("url");
				}

				return null;
			}

			@Override
			public InfoItemReference getInfoItemReference() {
				return new InfoItemReference(className, classPK);
			}

			@Override
			public Date getModifiedDate() {
				return new Date();
			}

			@Override
			public String getScopeName(Locale locale) {
				return RandomTestUtil.randomString();
			}

			@Override
			public JSONObject getSpecificInformationJSONObject(
				String backURL, LiferayPortletResponse liferayPortletResponse,
				Locale locale, ThemeDisplay themeDisplay) {

				JSONObject jsonObject = new JSONObjectImpl();

				jsonObject.put(
					"creationDate", "Thu Sep 23 12:49:44 GMT 2021"
				).put(
					"description", "My very important description"
				).put(
					"displayDate", "Thu Sep 23 12:47:04 GMT 2021"
				).put(
					"downloadURL", "www.download.url.com/download"
				).put(
					"extension", ".pdf"
				).put(
					"fileName", "MyDocument"
				).put(
					"languagesTranslatedInto", "Spanish and German"
				).put(
					"previewImageURL", "www.previewImage.url.com/previewImage"
				).put(
					"previewURL", "www.previewURL.url.com/previewURL"
				).put(
					"size", "5"
				).put(
					"viewURL", "www.viewURL.url.com/viewURL"
				);

				return jsonObject;
			}

			@Override
			public String getTitle(Locale locale) {
				return "title";
			}

			@Override
			public String getTypeLabel(Locale locale) {
				return "Web Content";
			}

			@Override
			public long getUserId() {
				return 0;
			}

			@Override
			public String getUserName() {
				return "Test Username";
			}

			@Override
			public List<Version> getVersions(Locale locale) {
				return Collections.singletonList(
					new Version("version", "style", "0.1"));
			}

			@Override
			public boolean isViewable(HttpServletRequest httpServletRequest) {
				return true;
			}

		};
	}

	private void _initGetContentDashboardItemsCsvMVCResourceCommand(
		ContentDashboardItem contentDashboardItem) {

		_getContentDashboardItemsCsvMVCResourceCommand =
			new GetContentDashboardItemsCsvMVCResourceCommand();

		ReflectionTestUtil.setFieldValue(
			_getContentDashboardItemsCsvMVCResourceCommand, "_portal",
			new PortalImpl());

		SearchContainer<ContentDashboardItem<?>> searchContainer =
			new SearchContainer<>();

		searchContainer.setResults(
			Collections.singletonList(contentDashboardItem));

		ReflectionTestUtil.setFieldValue(
			_getContentDashboardItemsCsvMVCResourceCommand,
			"_contentDashboardAdminDisplayContext",
			new ContentDashboardAdminDisplayContext(
				null, null, null, null, null, null, null, null, null, null,
				searchContainer));
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = mock(Language.class);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("title"))
		).thenReturn(
			"Title"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("author"))
		).thenReturn(
			"Author"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("type"))
		).thenReturn(
			"Type"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("subtype"))
		).thenReturn(
			"Subtype"
		);

		when(
			language.get(
				Matchers.any(Locale.class),
				Matchers.eq("site-or-asset-library"))
		).thenReturn(
			"Asset Type or Library"
		);
		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("status"))
		).thenReturn(
			"Status"
		);

		when(
			language.get(
				Matchers.any(Locale.class), Matchers.eq("modified-date"))
		).thenReturn(
			"Modified Date"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("description"))
		).thenReturn(
			"Description"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("extension"))
		).thenReturn(
			"Extension"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("file-name"))
		).thenReturn(
			"File Name"
		);

		when(
			language.get(Matchers.any(Locale.class), Matchers.eq("size"))
		).thenReturn(
			"Size"
		);

		when(
			language.get(
				Matchers.any(Locale.class), Matchers.eq("display-date"))
		).thenReturn(
			"Display Date"
		);

		when(
			language.get(
				Matchers.any(Locale.class), Matchers.eq("creation-date"))
		).thenReturn(
			"Creation Date"
		);

		when(
			language.get(
				Matchers.any(Locale.class),
				Matchers.eq("languages-translated-into"))
		).thenReturn(
			"Languages translated into"
		);

		languageUtil.setLanguage(language);
	}

	private GetContentDashboardItemsCsvMVCResourceCommand
		_getContentDashboardItemsCsvMVCResourceCommand;

}