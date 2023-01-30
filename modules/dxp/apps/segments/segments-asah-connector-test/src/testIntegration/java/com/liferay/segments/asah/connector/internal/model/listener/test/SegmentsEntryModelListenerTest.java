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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.SettingsFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.asah.connector.test.util.MockHttpUtil;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelService;

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
public class SegmentsEntryModelListenerTest {

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
		SegmentsEntry segmentsEntry1 =
			_segmentsEntryLocalService.addSegmentsEntry(
				null,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, true, null, User.class.getName(), _serviceContext);

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry1.getSegmentsEntryId(),
				_portal.getClassNameId(Layout.class), _layout.getPlid(),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), "Segment experience 1"),
				true, new UnicodeProperties(true), _serviceContext);

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience1.getSegmentsExperienceId(),
				_portal.getClassNameId(Layout.class), _layout.getPlid(),
				"AB test", "A/B test description",
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK, _serviceContext);

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
						segmentsExperiment.getSegmentsExperienceId(),
					() -> "{}")));

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), 0,
				_portal.getClassNameId(Layout.class), _layout.getPlid(),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), "Variant 1"),
				false, new UnicodeProperties(true), _serviceContext);

		_segmentsExperimentRelService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience2.getSegmentsExperienceId(), _serviceContext);

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

			String name = "Segments entry modified";

			segmentsEntry1.setNameMap(
				Collections.singletonMap(LocaleUtil.getSiteDefault(), name));

			_segmentsEntryLocalService.updateSegmentsEntry(segmentsEntry1);

			SegmentsEntry segmentsEntry2 =
				_segmentsEntryLocalService.getSegmentsEntry(
					segmentsEntry1.getSegmentsEntryId());

			Assert.assertEquals(
				name, segmentsEntry2.getName(LocaleUtil.getSiteDefault()));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				asahFaroBackendClient, "_http", _http);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Http _http;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.segments.asah.connector.internal.model.listener.SegmentsEntryModelListener"
	)
	private ModelListener<SegmentsEntry> _modelListener;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Inject
	private SegmentsExperimentRelService _segmentsExperimentRelService;

	private ServiceContext _serviceContext;

}