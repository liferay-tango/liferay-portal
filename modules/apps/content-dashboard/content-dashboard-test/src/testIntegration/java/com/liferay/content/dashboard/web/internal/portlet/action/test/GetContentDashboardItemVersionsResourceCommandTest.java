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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import javax.portlet.ResourceRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class GetContentDashboardItemVersionsResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0,
			"Test Site");
	}

	@Test
	public void testGetContentDashboardItemVersions() throws Exception {
		FileEntry fileEntry = _addRandomFileEntry();

		int totalFileEntryVersions = 11;

		for (int i = 0; i < totalFileEntryVersions; i++) {
			_generateNewFileEntryVersion(fileEntry);
		}

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayPortletResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"className", FileEntry.class.getName());
		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(fileEntry.getFileEntryId()));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getContentDashboardItemVersionsJSONObject",
			new Class<?>[] {ResourceRequest.class}, mockLiferayResourceRequest);

		Assert.assertNotNull(jsonObject);

		JSONArray versionsJSONArray = jsonObject.getJSONArray("versions");

		Assert.assertNotNull(versionsJSONArray);

		Assert.assertEquals(
			versionsJSONArray.toString(), 10, versionsJSONArray.length());
	}

	@Test
	public void testGetContentDashboardItemVersionsForNonversionableContent()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayPortletResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"className", BlogsEntry.class.getName());
		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(blogsEntry.getEntryId()));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getContentDashboardItemVersionsJSONObject",
			new Class<?>[] {ResourceRequest.class}, mockLiferayResourceRequest);

		Assert.assertNotNull(jsonObject);

		Assert.assertEquals(0, jsonObject.length());
	}

	@Test
	public void testGetContentDashboardItemVersionsWithLimit()
		throws Exception {

		FileEntry fileEntry = _addRandomFileEntry();

		int maxDisplayVersions = 5;

		int totalFileEntryVersions = 11;

		for (int i = 0; i < totalFileEntryVersions; i++) {
			_generateNewFileEntryVersion(fileEntry);
		}

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayPortletResourceRequest();

		mockLiferayResourceRequest.setParameter(
			"className", FileEntry.class.getName());
		mockLiferayResourceRequest.setParameter(
			"classPK", String.valueOf(fileEntry.getFileEntryId()));
		mockLiferayResourceRequest.setParameter(
			"maxDisplayVersions", String.valueOf(maxDisplayVersions));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcResourceCommand, "_getContentDashboardItemVersionsJSONObject",
			new Class<?>[] {ResourceRequest.class}, mockLiferayResourceRequest);

		Assert.assertNotNull(jsonObject);

		JSONArray versionsJSONArray = jsonObject.getJSONArray("versions");

		Assert.assertNotNull(versionsJSONArray);

		Assert.assertEquals(
			versionsJSONArray.toString(), maxDisplayVersions,
			versionsJSONArray.length());
	}

	private FileEntry _addRandomFileEntry() throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			TestDataConstants.TEST_BYTE_ARRAY, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private FileEntry _generateNewFileEntryVersion(FileEntry fileEntry)
		throws Exception {

		return DLAppServiceUtil.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			fileEntry.getMimeType(), fileEntry.getTitle(), StringPool.BLANK,
			fileEntry.getDescription(), RandomTestUtil.randomString(),
			DLVersionNumberIncrease.MINOR, TestDataConstants.TEST_BYTE_ARRAY,
			fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private MockLiferayResourceRequest _getMockLiferayPortletResourceRequest()
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayResourceRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "mvc.command.name=/content_dashboard/get_content_dashboard_item_versions"
	)
	private MVCResourceCommand _mvcResourceCommand;

}