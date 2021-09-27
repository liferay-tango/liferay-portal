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

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminDisplayContext;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItem;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemSubtype;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.ByteArrayOutputStream;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yurena Cabrera
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/content_dashboard/get_content_dashboard_items_csv"
	},
	service = MVCResourceCommand.class
)
public class GetContentDashboardItemsCsvMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		Workbook workbook = new HSSFWorkbook();

		Sheet sheet = workbook.createSheet("Content Dashboard Data");

		Locale locale = _portal.getLocale(resourceRequest);

		_createHeaderRow(locale, sheet, workbook);

		SearchContainer<ContentDashboardItem<?>> searchContainer =
			_contentDashboardAdminDisplayContext.getSearchContainer();

		List<ContentDashboardItem<?>> items = searchContainer.getResults();

		try {
			CellStyle cellStyle = _createCellStyle(
				true, "Helvetica", (short)14, workbook);

			for (ContentDashboardItem<?> contentDashboardItem : items) {
				Row row = sheet.createRow((short)1);

				row.setRowStyle(cellStyle);

				HttpServletRequest httpServletRequest =
					_portal.getHttpServletRequest(resourceRequest);

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_createDataRow(
					resourceRequest, resourceResponse, locale,
					contentDashboardItem, row, themeDisplay);
			}

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			workbook.write(byteArrayOutputStream);

			resourceResponse.setContentType("application/vnd.ms-excel");
			resourceResponse.setProperty(
				"Content-Disposition",
				"attachment; filename=\"ContentDashboardItemsData.xls\"");

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				"ContentDashboardItemsData.xlsx",
				byteArrayOutputStream.toByteArray(),
				ContentTypes.APPLICATION_VND_MS_EXCEL);
		}
		catch (Exception exception) {
			_log.error(exception, exception);
		}
	}

	private int _createBasicDataCells(
		int cellIndex, Locale locale, Row row,
		ContentDashboardItem<?> contentDashboardItem) {

		cellIndex = _createCell(
			cellIndex, row, contentDashboardItem.getTitle(locale));
		cellIndex = _createCell(
			cellIndex, row, contentDashboardItem.getUserName());
		cellIndex = _createCell(
			cellIndex, row, contentDashboardItem.getTypeLabel(locale));

		ContentDashboardItemSubtype contentDashboardItemSubtype =
			contentDashboardItem.getContentDashboardItemSubtype();

		cellIndex = _createCell(
			cellIndex, row, contentDashboardItemSubtype.getLabel(locale));

		cellIndex = _createCell(
			cellIndex, row, contentDashboardItem.getScopeName(locale));

		List<ContentDashboardItem.Version> versions =
			contentDashboardItem.getVersions(locale);

		ContentDashboardItem.Version latestVersion = versions.get(0);

		cellIndex = _createCell(cellIndex, row, latestVersion.getLabel());

		List<AssetCategory> categories =
			contentDashboardItem.getAssetCategories();

		Stream<AssetCategory> assetCategoryStream = categories.stream();

		cellIndex = _createCell(
			cellIndex, row,
			StringUtils.joinWith(
				", ",
				assetCategoryStream.map(
					x -> x.getTitle(locale)
				).collect(
					Collectors.toList()
				)));

		List<AssetTag> assetTags = contentDashboardItem.getAssetTags();

		Stream<AssetTag> assetTagsStream = assetTags.stream();

		cellIndex = _createCell(
			cellIndex, row,
			StringUtils.joinWith(
				", ",
				assetTagsStream.map(
					AssetTagModel::getName
				).collect(
					Collectors.toList()
				)));

		Date modifiedDate = contentDashboardItem.getModifiedDate();

		return _createCell(cellIndex, row, modifiedDate.toString());
	}

	private int _createBasicDataHeaderCells(
		Locale locale, int headerRowCellIndex, Row headerRow) {

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "title"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "author"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "type"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "subtype"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "site-or-asset-library"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "status"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "categories"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "tags"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "modified-date"));

		return headerRowCellIndex;
	}

	private int _createCell(int cellIndex, Row row, String value) {
		Cell titleDataCell = row.createCell(cellIndex++);

		titleDataCell.setCellValue(value);

		return cellIndex;
	}

	private CellStyle _createCellStyle(
		boolean bold, String fontName, short heightInPoints,
		Workbook workbook) {

		Font font = workbook.createFont();

		font.setBold(bold);
		font.setFontHeightInPoints(heightInPoints);
		font.setFontName(fontName);

		CellStyle style = workbook.createCellStyle();

		style.setFont(font);

		return style;
	}

	private void _createDataRow(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		Locale locale, ContentDashboardItem<?> contentDashboardItem, Row row,
		ThemeDisplay themeDisplay) {

		int cellIndex = _createBasicDataCells(
			0, locale, row, contentDashboardItem);

		cellIndex = _createFileSpecificDataCells(
			cellIndex, locale, resourceResponse, resourceRequest, row,
			themeDisplay, contentDashboardItem);

		_createJournalArticleSpecificDataCells(
			cellIndex, locale, resourceResponse, resourceRequest, row,
			themeDisplay, contentDashboardItem);
	}

	private int _createFileHeaderCells(
		Row headerRow, int headerRowCellIndex, Locale locale) {

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "description"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "extension"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "file-name"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow, LanguageUtil.get(locale, "size"));

		return headerRowCellIndex;
	}

	private int _createFileSpecificDataCells(
		int cellIndex, Locale locale, ResourceResponse resourceResponse,
		ResourceRequest resourceRequest, Row row, ThemeDisplay themeDisplay,
		ContentDashboardItem<?> contentDashboardItem) {

		JSONObject fileSpecificDataJSONObject =
			contentDashboardItem.getSpecificInformationJSONObject(
				ParamUtil.getString(resourceRequest, "backURL"),
				_portal.getLiferayPortletResponse(resourceResponse), locale,
				themeDisplay);

		if (fileSpecificDataJSONObject == null)

			return cellIndex;

		String fileDescription = fileSpecificDataJSONObject.get(
			"description"
		).toString();

		cellIndex = _createCell(cellIndex, row, fileDescription);

		String extension = fileSpecificDataJSONObject.get(
			"extension"
		).toString();

		cellIndex = _createCell(cellIndex, row, extension);

		String fileName = fileSpecificDataJSONObject.get(
			"fileName"
		).toString();

		cellIndex = _createCell(cellIndex, row, fileName);

		String size = fileSpecificDataJSONObject.get(
			"size"
		).toString();

		return _createCell(cellIndex, row, size);
	}

	private void _createHeaderRow(
		Locale locale, Sheet sheet, Workbook workbook) {

		Row headerRow = sheet.createRow((short)0);

		headerRow.setRowStyle(
			_createCellStyle(true, "Helvetica", (short)11, workbook));

		int headerRowCellIndex = _createBasicDataHeaderCells(
			locale, 0, headerRow);

		headerRowCellIndex = _createFileHeaderCells(
			headerRow, headerRowCellIndex, locale);

		_createJournalArticleHeaderRow(headerRow, headerRowCellIndex, locale);
	}

	private void _createJournalArticleHeaderRow(
		Row headerRow, int headerRowCellIndex, Locale locale) {

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "display-date"));

		headerRowCellIndex = _createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "creation-date"));

		_createCell(
			headerRowCellIndex, headerRow,
			LanguageUtil.get(locale, "languages-translated-into"));
	}

	private void _createJournalArticleSpecificDataCells(
		int cellIndex, Locale locale, ResourceResponse resourceResponse,
		ResourceRequest resourceRequest, Row row, ThemeDisplay themeDisplay,
		ContentDashboardItem<?> contentDashboardItem) {

		JSONObject journalArticleSpecificDataJSONObject =
			contentDashboardItem.getSpecificInformationJSONObject(
				ParamUtil.getString(resourceRequest, "backURL"),
				_portal.getLiferayPortletResponse(resourceResponse), locale,
				themeDisplay);

		if (journalArticleSpecificDataJSONObject == null)

			return;

		String displayDate = journalArticleSpecificDataJSONObject.get(
			"displayDate"
		).toString();

		cellIndex = _createCell(cellIndex, row, displayDate);

		String creationDate = journalArticleSpecificDataJSONObject.get(
			"creationDate"
		).toString();

		cellIndex = _createCell(cellIndex, row, creationDate);

		String languagesTranslated = journalArticleSpecificDataJSONObject.get(
			"languagesTranslatedInto"
		).toString();

		_createCell(cellIndex, row, languagesTranslated);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetContentDashboardItemsCsvMVCResourceCommand.class);

	@Reference
	private ContentDashboardAdminDisplayContext
		_contentDashboardAdminDisplayContext;

	@Reference
	private Portal _portal;

}