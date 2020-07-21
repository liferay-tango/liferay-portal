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

package com.liferay.segments.odata.retriever.test;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.expando.util.test.ExpandoTestUtil;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;
import com.liferay.segments.odata.retriever.ODataRetriever;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
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
public class OrganizationODataRetrieverCustomFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_expandoTable = ExpandoTestUtil.addTable(
			PortalUtil.getClassNameId(Organization.class), "CUSTOM_FIELDS");

		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			StringBundler.concat(
				"(&(model.class.name=com.liferay.portal.kernel.model.",
				"Organization)(objectClass=", ODataRetriever.class.getName(),
				"))"));

		_serviceTracker = registry.trackServices(filter);

		_serviceTracker.open();
	}

	@After
	public void tearDown() {
		_serviceTracker.close();
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndBooleanKeywordType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.BOOLEAN,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, Boolean.TRUE,
			String.valueOf(Boolean.TRUE), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndBooleanTextType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.BOOLEAN,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, Boolean.TRUE,
			String.valueOf(Boolean.TRUE), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndDateKeywordType()
		throws Exception {

		Date date = new Date();

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.DATE,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, date,
			ISO8601Utils.format(date), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndDateTextType()
		throws Exception {

		Date date = new Date();

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.DATE, ExpandoColumnConstants.INDEX_TYPE_TEXT,
			date, ISO8601Utils.format(date), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndDoubleKeywordType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.DOUBLE,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, 3.0D,
			String.valueOf(3.0D), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndDoubleTextType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.DOUBLE,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, 3.0D, String.valueOf(3.0D),
			LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndFloatKeywordType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.FLOAT,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, 3.0F,
			String.valueOf(3.0F), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndFloatTextType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.FLOAT,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, 3.0F, String.valueOf(3.0F),
			LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndIntegerKeywordType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.INTEGER,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, 3, String.valueOf(3),
			LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndIntegerTextType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.INTEGER,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, 3, String.valueOf(3),
			LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndLocalizedStringKeywordType()
		throws Exception {

		Locale esLocale = LocaleUtil.fromLanguageId("es_ES");

		Map<Locale, String> columnValueMap = HashMapBuilder.put(
			esLocale, RandomTestUtil.randomString()
		).put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		Serializable columnValue = (Serializable)columnValueMap;

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.STRING_LOCALIZED,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, columnValue,
			"'" + columnValueMap.get(esLocale) + "'", esLocale, 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndLocalizedStringTextType()
		throws Exception {

		Locale esLocale = LocaleUtil.fromLanguageId("es_ES");

		Map<Locale, String> columnValueMap = HashMapBuilder.put(
			esLocale, "Hola Mundo!"
		).put(
			LocaleUtil.getDefault(), "Hello World!"
		).build();

		Serializable columnValue = (Serializable)columnValueMap;

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.STRING_LOCALIZED,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, columnValue,
			"'" + columnValueMap.get(esLocale) + "'", esLocale, 0, false);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndLongKeywordType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.LONG,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, 3L, String.valueOf(3L),
			LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndLongTextType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.LONG,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, 3L, String.valueOf(3L),
			LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndShortKeywordType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.SHORT,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, (short)3,
			String.valueOf((short)3), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndShortTextType()
		throws Exception {

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.SHORT,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, (short)3,
			String.valueOf((short)3), LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndStringKeywordType()
		throws Exception {

		String columnValue = RandomTestUtil.randomString();

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.STRING,
			ExpandoColumnConstants.INDEX_TYPE_KEYWORD, columnValue,
			"'" + columnValue + "'", LocaleUtil.getDefault(), 1, true);
	}

	@Test
	public void testGetOrganizationsFilterByCustomFieldWithEqualsAndStringTextType()
		throws Exception {

		String columnValue = "Hello World!";

		testGetOrganizationsFilterByCustomFieldWithEquals(
			ExpandoColumnConstants.STRING,
			ExpandoColumnConstants.INDEX_TYPE_TEXT, columnValue,
			"'" + columnValue + "'", LocaleUtil.getDefault(), 0, false);
	}

	protected void testGetOrganizationsFilterByCustomFieldWithEquals(
			int expandoColumnType, int expandoColumnIndexType,
			Serializable value, String filterValue, Locale locale,
			int expectedCount, boolean assertOrganization)
		throws Exception {

		ExpandoColumn expandoColumn = _addExpandoColumn(
			_expandoTable, RandomTestUtil.randomString(), expandoColumnType,
			expandoColumnIndexType);

		Organization organization1 = _addOrganization(
			expandoColumn.getName(), value);

		_organizations.add(organization1);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizations.add(organization2);

		String filterString = String.format(
			"(customField/%s eq %s)", _encodeName(expandoColumn), filterValue);

		int count = _getODataRetriever().getResultsCount(
			TestPropsValues.getCompanyId(), filterString, locale);

		Assert.assertEquals(expectedCount, count);

		if (assertOrganization) {
			List<Organization> organizations = _getODataRetriever().getResults(
				TestPropsValues.getCompanyId(), filterString, locale, 0, 1);

			Assert.assertEquals(organization1, organizations.get(0));
		}
	}

	private ExpandoColumn _addExpandoColumn(
			ExpandoTable expandoTable, String columnName, int columnType,
			int indexType)
		throws Exception {

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, columnName, columnType);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE, String.valueOf(indexType));

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private Organization _addOrganization(
			String columnName, Serializable columnValue)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				columnName, columnValue
			).build());

		return OrganizationLocalServiceUtil.addOrganization(
			TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(),
			_organizationLocalService.getTypes()[0], 0, 0,
			ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, StringPool.BLANK,
			false, serviceContext);
	}

	private String _encodeName(ExpandoColumn expandoColumn) throws Exception {
		return ReflectionTestUtil.invoke(
			_getExpandoColumnModelListener(), "_encodeName",
			new Class<?>[] {ExpandoColumn.class}, expandoColumn);
	}

	private ModelListener<ExpandoColumn> _getExpandoColumnModelListener()
		throws Exception {

		Registry registry = RegistryUtil.getRegistry();

		Collection<ModelListener<ExpandoColumn>> collection =
			registry.getServices(
				(Class<ModelListener<ExpandoColumn>>)
					(Class<?>)ModelListener.class,
				"(component.name=com.liferay.segments.internal.model." +
					"listener.OrganizationExpandoColumnModelListener)");

		Iterator<ModelListener<ExpandoColumn>> iterator = collection.iterator();

		return iterator.next();
	}

	private ODataRetriever<Organization> _getODataRetriever() {
		return _serviceTracker.getService();
	}

	@Inject
	private static ExpandoColumnLocalService _expandoColumnLocalService;

	private static ServiceTracker
		<ODataRetriever<Organization>, ODataRetriever<Organization>>
			_serviceTracker;

	@DeleteAfterTestRun
	private ExpandoTable _expandoTable;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

}