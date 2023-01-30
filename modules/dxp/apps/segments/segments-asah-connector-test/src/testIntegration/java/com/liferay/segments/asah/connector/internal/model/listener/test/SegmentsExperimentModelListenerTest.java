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

package com.liferay.segments.asah.connector.internal.model.listener.test;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.SettingsFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.asah.connector.test.util.MockHttpUtil;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class SegmentsExperimentModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		SegmentsExperiment segmentsExperiment1 = _addSegmentsExperiment();

		Object asahSegmentsExperimentProcessor =
			ReflectionTestUtil.getFieldValue(
				_modelListener, "_asahSegmentsExperimentProcessor");

		Object asahFaroBackendClient = ReflectionTestUtil.getFieldValue(
			asahSegmentsExperimentProcessor, "_asahFaroBackendClient");

		ReflectionTestUtil.setFieldValue(
			asahFaroBackendClient, "_http",
			MockHttpUtil.geHttp(
				Collections.singletonMap(
					"/api/1.0/experiments/" +
						segmentsExperiment1.getSegmentsExperimentKey(),
					() -> "{}")));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build(),
						SettingsFactoryUtil.getSettingsFactory())) {

			String description = "A/B test description updated";

			segmentsExperiment1.setDescription(description);

			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment1);

			SegmentsExperiment segmentsExperiment2 =
				_segmentsExperimentLocalService.getSegmentsExperiment(
					segmentsExperiment1.getSegmentsExperimentId());

			Assert.assertEquals(
				description, segmentsExperiment2.getDescription());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				asahFaroBackendClient, "_http", _http);
		}
	}

	@Test
	public void testOnBeforeCreate() throws Exception {
		Object asahSegmentsExperimentProcessor =
			ReflectionTestUtil.getFieldValue(
				_modelListener, "_asahSegmentsExperimentProcessor");

		Object asahFaroBackendClient = ReflectionTestUtil.getFieldValue(
			asahSegmentsExperimentProcessor, "_asahFaroBackendClient");

		String segmentsExperimentKey = "123456789";

		ReflectionTestUtil.setFieldValue(
			asahFaroBackendClient, "_http",
			MockHttpUtil.geHttp(
				Collections.singletonMap(
					"/api/1.0/experiments",
					() -> JSONUtil.put(
						"id", segmentsExperimentKey
					).toString())));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build(),
						SettingsFactoryUtil.getSettingsFactory())) {

			SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

			Assert.assertNotNull(segmentsExperiment);
			Assert.assertEquals(
				segmentsExperimentKey,
				segmentsExperiment.getSegmentsExperimentKey());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				asahFaroBackendClient, "_http", _http);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		SegmentsExperiment segmentsExperiment1 = _addSegmentsExperiment();

		Object asahSegmentsExperimentProcessor =
			ReflectionTestUtil.getFieldValue(
				_modelListener, "_asahSegmentsExperimentProcessor");

		Object asahFaroBackendClient = ReflectionTestUtil.getFieldValue(
			asahSegmentsExperimentProcessor, "_asahFaroBackendClient");

		ReflectionTestUtil.setFieldValue(
			asahFaroBackendClient, "_http",
			MockHttpUtil.geHttp(
				Collections.singletonMap(
					"/api/1.0/experiments/" +
						segmentsExperiment1.getSegmentsExperienceId(),
					() -> "{}")));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build(),
						SettingsFactoryUtil.getSettingsFactory())) {

			_segmentsExperimentLocalService.deleteSegmentsExperiment(
				segmentsExperiment1);

			SegmentsExperiment segmentsExperiment2 =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					segmentsExperiment1.getSegmentsExperimentId());

			Assert.assertNull(segmentsExperiment2);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				asahFaroBackendClient, "_http", _http);
		}
	}

	private SegmentsExperiment _addSegmentsExperiment() throws PortalException {
		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		return _segmentsExperimentLocalService.addSegmentsExperiment(
			defaultSegmentsExperienceId, _portal.getClassNameId(Layout.class),
			_layout.getPlid(), "AB test", "A/B test description",
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK, _serviceContext);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Http _http;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.segments.asah.connector.internal.model.listener.SegmentsExperimentModelListener"
	)
	private ModelListener<SegmentsExperiment> _modelListener;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	private ServiceContext _serviceContext;

}