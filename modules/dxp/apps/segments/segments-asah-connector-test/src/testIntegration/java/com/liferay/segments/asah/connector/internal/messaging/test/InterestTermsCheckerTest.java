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

package com.liferay.segments.asah.connector.internal.messaging.test;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.settings.SettingsFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.asah.connector.test.util.MockHttpUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class InterestTermsCheckerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = TestPropsValues.getUser();

		Bundle bundle = FrameworkUtil.getBundle(InterestTermsCheckerTest.class);

		_serviceTracker = new ServiceTracker<>(
			bundle.getBundleContext(),
			FrameworkUtil.createFilter(
				"(component.name=com.liferay.segments.asah.connector." +
					"internal.messaging.InterestTermsChecker)"),
			null);

		_serviceTracker.open();

		_interestTermsChecker = _serviceTracker.getService();

		Assert.assertNotNull(_interestTermsChecker);
	}

	@After
	public void tearDown() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	@Test
	public void testCheckIndividualSegments() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId", "123456789"
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

			Object asahFaroBackendClient = ReflectionTestUtil.getFieldValue(
				_interestTermsChecker, "_asahFaroBackendClient");

			ReflectionTestUtil.setFieldValue(
				asahFaroBackendClient, "_http",
				MockHttpUtil.geHttp(
					HashMapBuilder.
						<String, UnsafeSupplier<String, Exception>>put(
							"/api/1.0/interests/terms/" + _user.getUserId(),
							() -> JSONUtil.put(
								"_embedded",
								JSONUtil.put(
									"interest-topics",
									JSONUtil.putAll(
										JSONUtil.put(
											"terms",
											JSONUtil.putAll(
												JSONUtil.put(
													"keyword", "term1")))))
							).put(
								"page",
								JSONUtil.put(
									"number", 0
								).put(
									"size", 100
								).put(
									"totalElements", 1
								).put(
									"totalPages", 1
								)
							).put(
								"total", 0
							).toString()
						).build()));

			ReflectionTestUtil.invoke(
				_interestTermsChecker, "checkInterestTerms",
				new Class<?>[] {long.class, String.class}, _user.getCompanyId(),
				String.valueOf(_user.getUserId()));

			PortalCache<String, String[]> portalCache =
				(PortalCache<String, String[]>)_multiVMPool.getPortalCache(
					"com.liferay.segments.asah.connector.internal.cache." +
						"AsahInterestTermCache");

			String[] terms = portalCache.get("segments-" + _user.getUserId());

			Assert.assertArrayEquals(new String[] {"term1"}, terms);
		}
	}

	private Object _interestTermsChecker;

	@Inject
	private MultiVMPool _multiVMPool;

	private ServiceTracker<Object, Object> _serviceTracker;
	private User _user;

}