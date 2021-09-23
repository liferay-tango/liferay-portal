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

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;

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

				int cellIndex = _createBasicDataRow(
					0, locale, row, contentDashboardItem);

				cellIndex = _createFileSpecificDataRow(
					cellIndex, locale, resourceResponse, resourceRequest, row,
					themeDisplay, contentDashboardItem);

				_createJournalArticleSpecificDataRow(
					cellIndex, locale, resourceResponse, resourceRequest, row,
					themeDisplay, contentDashboardItem);
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

	private int _createBasicDataHeaderRow(
		Locale locale, int headerRowCellIndex, Row headerRow) {

		Cell titleCell = headerRow.createCell(headerRowCellIndex++);

		titleCell.setCellValue(LanguageUtil.get(locale, "title"));

		Cell authorCell = headerRow.createCell(headerRowCellIndex++);

		authorCell.setCellValue(LanguageUtil.get(locale, "author"));

		Cell typeCell = headerRow.createCell(headerRowCellIndex++);

		typeCell.setCellValue(LanguageUtil.get(locale, "type"));

		Cell subtypeCell = headerRow.createCell(headerRowCellIndex++);

		subtypeCell.setCellValue(LanguageUtil.get(locale, "subtype"));

		Cell assetCell = headerRow.createCell(headerRowCellIndex++);

		assetCell.setCellValue(
			LanguageUtil.get(locale, "site-or-asset-library"));

		Cell statusCell = headerRow.createCell(headerRowCellIndex++);

		statusCell.setCellValue(LanguageUtil.get(locale, "status"));

		Cell modifiedDateCell = headerRow.createCell(headerRowCellIndex++);

		modifiedDateCell.setCellValue(
			LanguageUtil.get(locale, "modified-date"));

		return headerRowCellIndex;
	}

	private int _createBasicDataRow(
		int cellIndex, Locale locale, Row row,
		ContentDashboardItem<?> contentDashboardItem) {

		Cell titleDataCell = row.createCell(cellIndex++);

		titleDataCell.setCellValue(contentDashboardItem.getTitle(locale));

		Cell authorDataCell = row.createCell(cellIndex++);

		authorDataCell.setCellValue(contentDashboardItem.getUserName());

		Cell typeDataCell = row.createCell(cellIndex++);

		typeDataCell.setCellValue(contentDashboardItem.getTypeLabel(locale));

		Cell subtypeDataCell = row.createCell(cellIndex++);

		ContentDashboardItemSubtype contentDashboardItemSubtype =
			contentDashboardItem.getContentDashboardItemSubtype();

		subtypeDataCell.setCellValue(
			contentDashboardItemSubtype.getLabel(locale));

		Cell assetDataCell = row.createCell(cellIndex++);

		assetDataCell.setCellValue(contentDashboardItem.getScopeName(locale));

		List<ContentDashboardItem.Version> versions =
			contentDashboardItem.getVersions(locale);

		Cell statusDataCell = row.createCell(cellIndex++);

		ContentDashboardItem.Version latestVersion = versions.get(0);

		statusDataCell.setCellValue(latestVersion.getLabel());

		//TO DO: Add MultipleVocabularies

		Cell modifiedDateDataCell = row.createCell(cellIndex++);

		Date modifiedDate = contentDashboardItem.getModifiedDate();

		modifiedDateDataCell.setCellValue(modifiedDate.toString());

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

	private int _createFileHeaderRow(
		Row headerRow, int headerRowCellIndex, Locale locale) {

		Cell descriptionCell = headerRow.createCell(headerRowCellIndex++);

		descriptionCell.setCellValue(LanguageUtil.get(locale, "description"));

		Cell extensionCell = headerRow.createCell(headerRowCellIndex++);

		extensionCell.setCellValue(LanguageUtil.get(locale, "extension"));

		Cell fileNameCell = headerRow.createCell(headerRowCellIndex++);

		fileNameCell.setCellValue(LanguageUtil.get(locale, "file-name"));

		Cell sizeCell = headerRow.createCell(headerRowCellIndex++);

		sizeCell.setCellValue(LanguageUtil.get(locale, "size"));

		return headerRowCellIndex;
	}

	private int _createFileSpecificDataRow(
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

		Cell fileDescriptionCell = row.createCell(cellIndex++);

		fileDescriptionCell.setCellValue(fileDescription);

		String extension = fileSpecificDataJSONObject.get(
			"extension"
		).toString();

		Cell fileExtensionCell = row.createCell(cellIndex++);

		fileExtensionCell.setCellValue(extension);

		String fileName = fileSpecificDataJSONObject.get(
			"fileName"
		).toString();

		Cell fileFileNameCell = row.createCell(cellIndex++);

		fileFileNameCell.setCellValue(fileName);

		String size = fileSpecificDataJSONObject.get(
			"size"
		).toString();

		Cell fileSizeCell = row.createCell(cellIndex++);

		fileSizeCell.setCellValue(size);

		return cellIndex;
	}

	private void _createHeaderRow(
		Locale locale, Sheet sheet, Workbook workbook) {

		Row headerRow = sheet.createRow((short)0);

		headerRow.setRowStyle(
			_createCellStyle(true, "Helvetica", (short)11, workbook));

		int headerRowCellIndex = _createBasicDataHeaderRow(
			locale, 0, headerRow);

		headerRowCellIndex = _createFileHeaderRow(
			headerRow, headerRowCellIndex, locale);

		_createJournalArticleHeaderRow(headerRow, headerRowCellIndex, locale);
	}

	private void _createJournalArticleHeaderRow(
		Row headerRow, int headerRowCellIndex, Locale locale) {

		Cell displayDateCell = headerRow.createCell(headerRowCellIndex++);

		displayDateCell.setCellValue(LanguageUtil.get(locale, "display-date"));

		Cell creationDateCell = headerRow.createCell(headerRowCellIndex++);

		creationDateCell.setCellValue(
			LanguageUtil.get(locale, "creation-date"));

		Cell languagesTranslatedCell = headerRow.createCell(headerRowCellIndex);

		languagesTranslatedCell.setCellValue(
			LanguageUtil.get(locale, "languages-translated-into"));
	}

	private void _createJournalArticleSpecificDataRow(
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

		Cell journalDisplayDateCell = row.createCell(cellIndex++);

		journalDisplayDateCell.setCellValue(displayDate);

		String creationDate = journalArticleSpecificDataJSONObject.get(
			"creationDate"
		).toString();

		Cell journalCreationDateCell = row.createCell(cellIndex++);

		journalCreationDateCell.setCellValue(creationDate);

		String languagesTranslated = journalArticleSpecificDataJSONObject.get(
			"languagesTranslatedInto"
		).toString();

		Cell journalLanguagesTranslatedCell = row.createCell(cellIndex);

		journalLanguagesTranslatedCell.setCellValue(languagesTranslated);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetContentDashboardItemsCsvMVCResourceCommand.class);

	@Reference
	private ContentDashboardAdminDisplayContext
		_contentDashboardAdminDisplayContext;

	@Reference
	private Portal _portal;

}