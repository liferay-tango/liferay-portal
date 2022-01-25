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

package com.liferay.content.dashboard.web.internal.portlet.action.test;

/**
 * @author Yurena Cabrera
 */
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.content.dashboard.web.test.util.ContentDashboardTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Yurena Cabrera
 */
@RunWith(Arquillian.class)
public class GetContentDashboardItemInfoMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);
	}

	@Test
	public void testServeResourceJournalArticle() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		JSONObject jsonObject = _serveResource(
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			_group.getGroupId());

		Assert.assertEquals(
			JournalArticle.class.getName(), jsonObject.getString("className"));
		Assert.assertEquals(
			String.valueOf(journalArticle.getResourcePrimKey()),
			jsonObject.getString("classPK"));
		Assert.assertEquals("en-US", jsonObject.getString("languageTag"));

		JSONObject specificFieldsJSONObject = jsonObject.getJSONObject(
			"specificFields");

		Assert.assertNotNull(specificFieldsJSONObject.getString("downloadURL"));

		JSONArray versionsJSONArray = jsonObject.getJSONArray("versions");

		Assert.assertEquals(1, versionsJSONArray.length());

		JSONObject versionJSONObject = versionsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			"Approved", versionJSONObject.getString("statusLabel"));
		Assert.assertEquals(
			"success", versionJSONObject.getString("statusStyle"));
		Assert.assertEquals("1.0", versionJSONObject.getString("version"));

		JSONObject vocabulariesJSONObject = jsonObject.getJSONObject(
			"vocabularies");

		Assert.assertEquals(1, vocabulariesJSONObject.length());

		String vocabularyId = String.valueOf(assetVocabulary.getVocabularyId());

		JSONObject vocabularyInfoJSONObject =
			(JSONObject)vocabulariesJSONObject.get(vocabularyId);

		Assert.assertTrue(vocabularyInfoJSONObject.getBoolean("isPublic"));

		String vocabularyName = vocabularyInfoJSONObject.get(
			"vocabularyName"
		).toString();

		String assetVocabularyName = assetVocabulary.getName();

		Assert.assertEquals(
			vocabularyName.toLowerCase(LocaleUtil.ROOT),
			assetVocabularyName.toLowerCase(LocaleUtil.ROOT));

		JSONArray categoriesJSONArray = (JSONArray)vocabularyInfoJSONObject.get(
			"categories");

		Assert.assertEquals(2, categoriesJSONArray.length());

		Assert.assertTrue(
			categoriesJSONArray.toString(
			).contains(
				assetCategory1.getName()
			));
		Assert.assertTrue(
			categoriesJSONArray.toString(
			).contains(
				assetCategory2.getName()
			));
	}

	private FileEntry _addFileEntry() throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			"FileName.pdf", RandomTestUtil.randomString(), new byte[0], null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private JournalArticle _addJournalArticle(long[] assetCategories)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				assetCategories);

		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	private JSONObject _serveResource(
			String className, long classPK, long groupId)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"groupId", String.valueOf(groupId));
		mockLiferayResourceRequest.setParameter("className", className);
		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(classPK));

		ThemeDisplay themeDisplay = ContentDashboardTestUtil.getThemeDisplay(
			_group);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			_getContentDashboardItemInfoMVCResourceCommand.serveResource(
				mockLiferayResourceRequest, mockLiferayResourceResponse);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONObject(
			new String(byteArrayOutputStream.toByteArray()));
	}

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject(
		filter = "mvc.command.name=/content_dashboard/get_content_dashboard_item_info"
	)
	private MVCResourceCommand _getContentDashboardItemInfoMVCResourceCommand;

	@DeleteAfterTestRun
	private Group _group;

}