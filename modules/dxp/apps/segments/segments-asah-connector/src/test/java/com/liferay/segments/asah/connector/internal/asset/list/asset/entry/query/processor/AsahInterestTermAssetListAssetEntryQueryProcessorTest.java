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

package com.liferay.segments.asah.connector.internal.asset.list.asset.entry.query.processor;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.segments.asah.connector.internal.provider.AsahInterestTermProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author David Arques
 */
@RunWith(MockitoJUnitRunner.class)
public class AsahInterestTermAssetListAssetEntryQueryProcessorTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_asahInterestTermAssetListAssetEntryQueryProcessor,
			"_asahInterestTermProvider", _asahInterestTermProvider);
	}

	@Test
	public void testProcessAssetEntryQuery() {
		String userId = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();

		String[] interestTerms = {"sport", "america"};

		Mockito.when(
			_asahInterestTermProvider.getInterestTerms(companyId, userId)
		).thenReturn(
			interestTerms
		);

		UnicodeProperties unicodeProperties = new UnicodeProperties();

		unicodeProperties.setProperty(
			"enableContentRecommendation", String.valueOf(Boolean.TRUE));

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_asahInterestTermAssetListAssetEntryQueryProcessor.
			processAssetEntryQuery(
				companyId, userId, unicodeProperties, assetEntryQuery);

		Assert.assertArrayEquals(
			interestTerms, assetEntryQuery.getAnyKeywords());
	}

	@Test
	public void testProcessAssetEntryQueryWithDisableContentRecommendation() {
		UnicodeProperties unicodeProperties = new UnicodeProperties();

		unicodeProperties.setProperty(
			"enableContentRecommendation", String.valueOf(Boolean.FALSE));

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_asahInterestTermAssetListAssetEntryQueryProcessor.
			processAssetEntryQuery(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
				unicodeProperties, assetEntryQuery);

		Assert.assertArrayEquals(
			new String[0], assetEntryQuery.getAnyKeywords());
	}

	@Test
	public void testProcessAssetEntryQueryWithEmptyAcClientUserId() {
		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_asahInterestTermAssetListAssetEntryQueryProcessor.
			processAssetEntryQuery(
				RandomTestUtil.randomLong(), StringPool.BLANK,
				new UnicodeProperties(), assetEntryQuery);

		Assert.assertArrayEquals(
			new String[0], assetEntryQuery.getAnyKeywords());
	}

	@Test
	public void testProcessAssetEntryQueryWithEmptyInterestTerms() {
		String userId = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_asahInterestTermProvider.getInterestTerms(companyId, userId)
		).thenReturn(
			new String[0]
		);

		UnicodeProperties unicodeProperties = new UnicodeProperties();

		unicodeProperties.setProperty(
			"enableContentRecommendation", String.valueOf(Boolean.TRUE));

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_asahInterestTermAssetListAssetEntryQueryProcessor.
			processAssetEntryQuery(
				companyId, userId, unicodeProperties, assetEntryQuery);

		Assert.assertArrayEquals(
			new String[0], assetEntryQuery.getAnyKeywords());
	}

	@Test
	public void testProcessAssetEntryQueryWithNullAcClientUserId() {
		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_asahInterestTermAssetListAssetEntryQueryProcessor.
			processAssetEntryQuery(
				RandomTestUtil.randomLong(), null, new UnicodeProperties(),
				assetEntryQuery);

		Assert.assertArrayEquals(
			new String[0], assetEntryQuery.getAnyKeywords());
	}

	private final AsahInterestTermAssetListAssetEntryQueryProcessor
		_asahInterestTermAssetListAssetEntryQueryProcessor =
			new AsahInterestTermAssetListAssetEntryQueryProcessor();

	@Mock
	private AsahInterestTermProvider _asahInterestTermProvider;

}