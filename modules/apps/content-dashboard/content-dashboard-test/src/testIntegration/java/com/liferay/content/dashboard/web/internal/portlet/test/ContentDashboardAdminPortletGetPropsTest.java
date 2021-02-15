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

package com.liferay.content.dashboard.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class ContentDashboardAdminPortletGetPropsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetPropsWithAssetCategoriesSortedByKey() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-1",
			assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-2",
			assetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {assetCategory2.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {assetVocabulary.getName()},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"key", String.valueOf(assetCategory1.getCategoryId())
					).put(
						"name", "category-1"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					),
					JSONUtil.put(
						"key", String.valueOf(assetCategory2.getCategoryId())
					).put(
						"name", "category-2"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithChildAssetCategoriesSortedByKey()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory1 =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category-1", childAssetVocabulary.getVocabularyId(),
				serviceContext);
		AssetCategory childAssetCategory2 =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category-2", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory1.getCategoryId(),
						childAssetCategory2.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory2.getCategoryId()
					}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory1.getCategoryId())
							).put(
								"name", "child-category-1"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory2.getCategoryId())
							).put(
								"name", "child-category-2"
							).put(
								"value", 2L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithChildNoneAssetCategory() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {assetCategory.getCategoryId()}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key", "none"
							).put(
								"name", "No child-vocabulary Specified"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithChildNoneAssetCategoryAndNoneAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-1",
			assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-2",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory1.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {childAssetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key", "none"
							).put(
								"name", "No child-vocabulary Specified"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory1.getCategoryId())
					).put(
						"name", "category-1"
					).put(
						"value", 2L
					).put(
						"vocabularyName", "vocabulary"
					),
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 0L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key", "none"
							).put(
								"name", "No child-vocabulary Specified"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory2.getCategoryId())
					).put(
						"name", "category-2"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					),
					JSONUtil.put(
						"categories",
						JSONUtil.put(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", "none"
					).put(
						"name", "No vocabulary Specified"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteAssetVocabulary(
				childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithEmptyChildAssetCategoriesSortedByKey()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-1",
			assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category-2",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory1 =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category-1", childAssetVocabulary.getVocabularyId(),
				serviceContext);
		AssetCategory childAssetCategory2 =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category-2", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory1.getCategoryId(),
						childAssetCategory2.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory2.getCategoryId(),
						childAssetCategory1.getCategoryId()
					}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory1.getCategoryId())
							).put(
								"name", "child-category-1"
							).put(
								"value", 0L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory2.getCategoryId())
							).put(
								"name", "child-category-2"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory1.getCategoryId())
					).put(
						"name", "category-1"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					),
					JSONUtil.put(
						"categories",
						JSONUtil.putAll(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory1.getCategoryId())
							).put(
								"name", "child-category-1"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							),
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory2.getCategoryId())
							).put(
								"name", "child-category-2"
							).put(
								"value", 0L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory2.getCategoryId())
					).put(
						"name", "category-2"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingCategorizedJournalArticle()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		_assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		_assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			"child-category", childAssetVocabulary.getVocabularyId(),
			serviceContext);

		try {
			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				String.valueOf(JSONFactoryUtil.createJSONArray()),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteAssetVocabulary(
				childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingCategorizedJournalArticleWithAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		_assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {childAssetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.put(
					JSONUtil.put(
						"key",
						String.valueOf(childAssetCategory.getCategoryId())
					).put(
						"name", "child-category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "child-vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteVocabulary(childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingCategorizedJournalArticleWithChildAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		_assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(),
			childAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {assetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.put(
					JSONUtil.put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteAssetVocabulary(
				childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingChildAssetVocabularies()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);
		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				String.valueOf(JSONFactoryUtil.createJSONArray()),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteAssetVocabulary(
				childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithMissingChildAssetVocabulary() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {assetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.put(
					JSONUtil.put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteAssetVocabulary(
				childAssetVocabulary);
		}
	}

	@Test
	public void testGetPropsWithNoneAssetCategory() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"vocabulary", serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(), "category",
			assetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-vocabulary", serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				"child-category", childAssetVocabulary.getVocabularyId(),
				serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				_getServiceContext(
					TestPropsValues.getUserId(), _group.getGroupId(),
					new long[] {childAssetCategory.getCategoryId()}));

			Map<String, Object> data = _getData(
				_getMockLiferayPortletRenderRequest(
					new String[] {
						assetVocabulary.getName(),
						childAssetVocabulary.getName()
					},
					LocaleUtil.getSiteDefault()));

			Map<String, Object> props = (Map<String, Object>)data.get("props");

			Assert.assertNotNull(props);

			JSONArray vocabulariesJSONArray = (JSONArray)props.get(
				"vocabularies");

			Assert.assertEquals(
				JSONUtil.putAll(
					JSONUtil.put(
						"categories",
						JSONUtil.put(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", String.valueOf(assetCategory.getCategoryId())
					).put(
						"name", "category"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).put(
					JSONUtil.put(
						"categories",
						JSONUtil.put(
							JSONUtil.put(
								"key",
								String.valueOf(
									childAssetCategory.getCategoryId())
							).put(
								"name", "child-category"
							).put(
								"value", 1L
							).put(
								"vocabularyName", "child-vocabulary"
							))
					).put(
						"key", "none"
					).put(
						"name", "No vocabulary Specified"
					).put(
						"value", 1L
					).put(
						"vocabularyName", "vocabulary"
					)
				).toString(),
				vocabulariesJSONArray.toString());
		}
		finally {
			_assetVocabularyLocalService.deleteAssetVocabulary(assetVocabulary);
			_assetVocabularyLocalService.deleteAssetVocabulary(
				childAssetVocabulary);
		}
	}

	private Map<String, Object> _getData(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"CONTENT_DASHBOARD_ADMIN_DISPLAY_CONTEXT"),
			"getData", new Class<?>[0]);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			String[] assetVocabularyNames, Locale locale)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			ProxyUtil.newProxyInstance(
				PortletContext.class.getClassLoader(),
				new Class<?>[] {PortletContext.class},
				(PortletContextProxy, portletContextMethod,
				 portletContextArgs) -> {

					if (Objects.equals(
							portletContextMethod.getName(),
							"getRequestDispatcher") &&
						Objects.equals(portletContextArgs[0], path)) {

						return ProxyUtil.newProxyInstance(
							PortletRequestDispatcher.class.getClassLoader(),
							new Class<?>[] {PortletRequestDispatcher.class},
							(portletRequestDispatcherProxy,
							 portletRequestDispatcherMethod,
							 portletRequestDispatcherArgs) -> null);
					}

					throw new UnsupportedOperationException();
				}));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(locale));

		PortletPreferences portletPreferences =
			mockLiferayPortletRenderRequest.getPreferences();

		portletPreferences.setValues(
			"assetVocabularyNames", assetVocabularyNames);

		return mockLiferayPortletRenderRequest;
	}

	private ServiceContext _getServiceContext(
			long userId, long groupId, long[] assetCategoryIds)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay(Locale locale) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(locale);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setUser(_company.getDefaultUser());

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.web.internal.portlet.ContentDashboardAdminPortlet"
	)
	private Portlet _portlet;

}