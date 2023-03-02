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

package com.liferay.segments.asah.connector.internal.criteria.contributor;

import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.segments.asah.connector.internal.criteria.mapper.SegmentsCriteriaJSONObjectMapperImpl;
import com.liferay.segments.asah.connector.internal.odata.entity.EventEntityModel;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.criteria.mapper.SegmentsCriteriaJSONObjectMapper;
import com.liferay.segments.field.Field;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Cristina González
 */
@Component(immediate = true, service = EventSegmentsCriteriaContributor.class)
public class EventSegmentsCriteriaContributor
	implements SegmentsCriteriaContributor {

	public static final String KEY = "event";

	@Override
	public JSONObject getCriteriaJSONObject(Criteria criteria)
		throws Exception {

		SegmentsCriteriaJSONObjectMapper segmentsCriteriaJSONObjectMapper =
			new SegmentsCriteriaJSONObjectMapperImpl();

		return segmentsCriteriaJSONObjectMapper.toJSONObject(criteria, this);
	}

	@Override
	public EntityModel getEntityModel() {
		return _entityModel;
	}

	@Override
	public String getEntityName() {
		return EventEntityModel.NAME;
	}

	@Override
	public List<Field> getFields(PortletRequest portletRequest) {
		return Arrays.asList(
			new Field(
				"commentPosted",
				_language.get(
					_portal.getLocale(portletRequest), "commented-on-blog"),
				"event", null,
				_getSelectEntity(portletRequest, AssetType.BLOGS_ENTRY)),
			new Field(
				"blogViewed",
				_language.get(_portal.getLocale(portletRequest), "viewed-blog"),
				"event", null,
				_getSelectEntity(portletRequest, AssetType.BLOGS_ENTRY)),
			new Field(
				"documentDownloaded",
				_language.get(
					_portal.getLocale(portletRequest),
					"downloaded-document-and-media"),
				"event", null,
				_getSelectEntity(portletRequest, AssetType.FILE_ENTRY)),
			new Field(
				"documentPreviewed",
				_language.get(
					_portal.getLocale(portletRequest),
					"viewed-document-and-media"),
				"event", null,
				_getSelectEntity(portletRequest, AssetType.FILE_ENTRY)),
			new Field(
				"formSubmitted",
				_language.get(
					_portal.getLocale(portletRequest), "submitted-form"),
				"event"),
			new Field(
				"formViewed",
				_language.get(_portal.getLocale(portletRequest), "viewed-form"),
				"event"),
			new Field(
				"pageViewed",
				_language.get(_portal.getLocale(portletRequest), "viewed-page"),
				"event", null,
				_getSelectEntity(portletRequest, AssetType.LAYOUT)),
			new Field(
				"webContentViewed",
				_language.get(
					_portal.getLocale(portletRequest), "viewed-web-content"),
				"event", null,
				_getSelectEntity(portletRequest, AssetType.JOURNAL_ARTICLE)));
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Criteria.Type getType() {
		return Criteria.Type.ANALYTICS;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (FeatureFlagManagerUtil.isEnabled("LPS-171722")) {
			_serviceRegistration = bundleContext.registerService(
				SegmentsCriteriaContributor.class, this,
				HashMapDictionaryBuilder.<String, Object>put(
					"segments.criteria.contributor.key",
					EventSegmentsCriteriaContributor.KEY
				).put(
					"segments.criteria.contributor.model.class.name", "*"
				).build());
		}
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private Field.SelectEntity _getSelectEntity(
		PortletRequest portletRequest, AssetType assetType) {

		try {
			ItemSelectorCriterion itemSelectorCriterion;

			if ((assetType == AssetType.BLOGS_ENTRY) ||
				(assetType == AssetType.FILE_ENTRY) ||
				(assetType == AssetType.JOURNAL_ARTICLE)) {

				InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
					new InfoItemItemSelectorCriterion();

				infoItemItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new InfoItemItemSelectorReturnType());
				infoItemItemSelectorCriterion.setItemType(
					assetType.getClassName());
				infoItemItemSelectorCriterion.setMultiSelection(true);

				itemSelectorCriterion = infoItemItemSelectorCriterion;
			}
			else if (assetType == AssetType.LAYOUT) {
				LayoutItemSelectorCriterion layoutItemSelectorCriterion =
					new LayoutItemSelectorCriterion();

				layoutItemSelectorCriterion.setShowHiddenPages(true);
				layoutItemSelectorCriterion.setShowPrivatePages(true);
				layoutItemSelectorCriterion.setShowPublicPages(true);
				layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					Collections.<ItemSelectorReturnType>singletonList(
						new UUIDItemSelectorReturnType()));
				layoutItemSelectorCriterion.setMultiSelection(true);

				itemSelectorCriterion = layoutItemSelectorCriterion;
			}
			else {
				throw new IllegalArgumentException(
					"Invalid assetType:" + assetType);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			String title = _resourceActions.getModelResource(
				themeDisplay.getLocale(), assetType.getClassName());

			return new Field.SelectEntity(
				"selectEntity",
				_language.format(themeDisplay.getLocale(), "select-x", title),
				PortletURLBuilder.create(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							portletRequest),
						"selectEntity", itemSelectorCriterion)
				).buildString(),
				true);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get select entity", exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EventSegmentsCriteriaContributor.class);

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(entity.model.name=" + EventEntityModel.NAME + ")"
	)
	private volatile EntityModel _entityModel;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActions _resourceActions;

	private volatile ServiceRegistration<SegmentsCriteriaContributor>
		_serviceRegistration;

	private static enum AssetType {

		BLOGS_ENTRY("com.liferay.blogs.model.BlogsEntry"),
		DDM_FORM(DDMForm.class.getName()),
		FILE_ENTRY(FileEntry.class.getName()),
		JOURNAL_ARTICLE("com.liferay.journal.model.JournalArticle"),
		LAYOUT(Layout.class.getName());

		public String getClassName() {
			return _className;
		}

		private AssetType(String className) {
			_className = className;
		}

		private final String _className;

	}

}