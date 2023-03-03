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

package com.liferay.dynamic.data.mapping.web.internal.item.selector;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceNameComparator;
import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "item.selector.view.order:Integer=200",
	service = ItemSelectorView.class
)
public class DDMFormInstanceItemSelectorView
	implements InfoItemSelectorView,
			   ItemSelectorView<InfoItemItemSelectorCriterion> {

	@Override
	public String getClassName() {
		return DDMFormInstance.class.getName();
	}

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "forms");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, infoItemItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new DDMFormInstanceSelectorViewDescriptor(
				(HttpServletRequest)servletRequest,
				infoItemItemSelectorCriterion, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoItemItemSelectorReturnType());

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class DDMFormInstanceItemDescriptor
		implements ItemSelectorViewDescriptor.ItemDescriptor {

		public DDMFormInstanceItemDescriptor(
			DDMFormInstance ddmFormInstance,
			HttpServletRequest httpServletRequest) {

			_ddmFormInstance = ddmFormInstance;
			_httpServletRequest = httpServletRequest;

			_resourceBundle = ResourceBundleUtil.getBundle(
				httpServletRequest.getLocale(), getClass());
		}

		@Override
		public String getIcon() {
			return "forms";
		}

		@Override
		public String getImageURL() {
			return null;
		}

		@Override
		public Date getModifiedDate() {
			return _ddmFormInstance.getModifiedDate();
		}

		@Override
		public String getPayload() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return JSONUtil.put(
				"className", DDMFormInstance.class.getName()
			).put(
				"classNameId",
				_portal.getClassNameId(DDMFormInstance.class.getName())
			).put(
				"classPK", _ddmFormInstance.getFormInstanceId()
			).put(
				"title",
				LocalizationUtil.getLocalization(
					_ddmFormInstance.getName(), themeDisplay.getLanguageId())
			).put(
				"type",
				ResourceActionsUtil.getModelResource(
					themeDisplay.getLocale(), DDMFormInstance.class.getName())
			).toString();
		}

		@Override
		public String getSubtitle(Locale locale) {
			Date modifiedDate = _ddmFormInstance.getModifiedDate();

			String modifiedDateDescription = _language.getTimeDescription(
				locale, System.currentTimeMillis() - modifiedDate.getTime(),
				true);

			return _language.format(
				locale, "x-ago-by-x",
				new Object[] {
					modifiedDateDescription,
					HtmlUtil.escape(_ddmFormInstance.getUserName())
				});
		}

		@Override
		public String getTitle(Locale locale) {
			return LocalizationUtil.getLocalization(
				_ddmFormInstance.getName(), locale.getLanguage());
		}

		@Override
		public long getUserId() {
			return _ddmFormInstance.getUserId();
		}

		@Override
		public String getUserName() {
			return _ddmFormInstance.getUserName();
		}

		private final DDMFormInstance _ddmFormInstance;
		private HttpServletRequest _httpServletRequest;
		private final ResourceBundle _resourceBundle;

	}

	private class DDMFormInstanceSelectorViewDescriptor
		implements ItemSelectorViewDescriptor<DDMFormInstance> {

		public DDMFormInstanceSelectorViewDescriptor(
			HttpServletRequest httpServletRequest,
			InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
			PortletURL portletURL) {

			_httpServletRequest = httpServletRequest;
			_infoItemItemSelectorCriterion = infoItemItemSelectorCriterion;
			_portletURL = portletURL;
		}

		@Override
		public ItemDescriptor getItemDescriptor(
			DDMFormInstance ddmFormInstance) {

			return new DDMFormInstanceItemDescriptor(
				ddmFormInstance, _httpServletRequest);
		}

		@Override
		public ItemSelectorReturnType getItemSelectorReturnType() {
			return new InfoItemItemSelectorReturnType();
		}

		public String getOrderByCol() {
			if (Validator.isNotNull(_orderByCol)) {
				return _orderByCol;
			}

			_orderByCol = SearchOrderByUtil.getOrderByCol(
				_httpServletRequest,
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
				"selector-order-by-type", "modified-date");

			return _orderByCol;
		}

		@Override
		public String[] getOrderByKeys() {
			return new String[] {"name", "modified-date"};
		}

		public String getOrderByType() {
			if (Validator.isNotNull(_orderByType)) {
				return _orderByType;
			}

			_orderByType = SearchOrderByUtil.getOrderByType(
				_httpServletRequest,
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
				"selector-order-by-type", "asc");

			return _orderByType;
		}

		@Override
		public SearchContainer<DDMFormInstance> getSearchContainer() {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			SearchContainer<DDMFormInstance> entriesSearchContainer =
				new SearchContainer<>(
					(PortletRequest)_httpServletRequest.getAttribute(
						JavaConstants.JAVAX_PORTLET_REQUEST),
					_portletURL, null, "no-entries-were-found");

			entriesSearchContainer.setOrderByCol(getOrderByCol());
			entriesSearchContainer.setOrderByComparator(
				_getDDMFormInstanceOrderByComparator(
					getOrderByCol(), getOrderByType()));
			entriesSearchContainer.setOrderByType(getOrderByType());
			entriesSearchContainer.setResultsAndTotal(
				() -> _ddmFormInstanceService.search(
					themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
					StringPool.BLANK, entriesSearchContainer.getStart(),
					entriesSearchContainer.getEnd(),
					entriesSearchContainer.getOrderByComparator()),
				_ddmFormInstanceService.searchCount(
					themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
					StringPool.BLANK));

			return entriesSearchContainer;
		}

		@Override
		public boolean isMultipleSelection() {
			return _infoItemItemSelectorCriterion.isMultiSelection();
		}

		private OrderByComparator<DDMFormInstance>
			_getDDMFormInstanceOrderByComparator(
				String orderByCol, String orderByType) {

			boolean orderByAsc = false;

			if (orderByType.equals("asc")) {
				orderByAsc = true;
			}

			OrderByComparator<DDMFormInstance> orderByComparator = null;

			if (orderByCol.equals("modified-date")) {
				orderByComparator = new DDMFormInstanceModifiedDateComparator(
					orderByAsc);
			}
			else if (orderByCol.equals("name")) {
				orderByComparator = new DDMFormInstanceNameComparator(
					orderByAsc);
			}

			return orderByComparator;
		}

		private HttpServletRequest _httpServletRequest;
		private final InfoItemItemSelectorCriterion
			_infoItemItemSelectorCriterion;
		private String _orderByCol;
		private String _orderByType;
		private final PortletURL _portletURL;

	}

}