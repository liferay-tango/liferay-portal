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
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;

import java.io.ByteArrayOutputStream;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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

		int headerRowIndex = 0;

		Row headerRow = sheet.createRow((short)0);

		headerRow.setRowStyle(
			createCellStyle(workbook, true, "Helvetica", (short)11));

		Locale locale = _portal.getLocale(resourceRequest);

		Cell titleCell = headerRow.createCell(headerRowIndex++);

		titleCell.setCellValue(LanguageUtil.get(locale, "title"));

		Cell authorCell = headerRow.createCell(headerRowIndex++);

		authorCell.setCellValue(LanguageUtil.get(locale, "author"));

		Cell typeCell = headerRow.createCell(headerRowIndex++);

		typeCell.setCellValue(LanguageUtil.get(locale, "type"));

		Cell subtypeCell = headerRow.createCell(headerRowIndex++);

		subtypeCell.setCellValue(LanguageUtil.get(locale, "subtype"));

		Cell assetCell = headerRow.createCell(headerRowIndex++);

		assetCell.setCellValue(
			LanguageUtil.get(locale, "site-or-asset-library"));

		Cell statusCell = headerRow.createCell(headerRowIndex++);

		statusCell.setCellValue(LanguageUtil.get(locale, "status"));

		Cell modifiedDateCell = headerRow.createCell(headerRowIndex);

		modifiedDateCell.setCellValue(
			LanguageUtil.get(locale, "modified-date"));

		resourceResponse.setContentType("application/vnd.ms-excel");
		resourceResponse.setProperty(
			"Content-Disposition",
			"attachment; filename=\"ContentDashboardItemsData.xls\"");

		Object searchContainer =
			_contentDashboardAdminDisplayContext.getSearchContainer();

		List<ContentDashboardItem<?>> items = searchContainer.getResults();

		try {
			CellStyle cellStyle = createCellStyle(
				workbook, true, "Helvetica", (short)14);

			for (ContentDashboardItem<?> contentDashboardItem : items) {
				int cellIndex = 0;

				Row row = sheet.createRow((short)1);

				row.setRowStyle(cellStyle);

				Cell titleDataCell = row.createCell(cellIndex++);

				titleDataCell.setCellValue(
					contentDashboardItem.getTitle(locale));

				Cell authorDataCell = row.createCell(cellIndex++);

				authorDataCell.setCellValue(contentDashboardItem.getUserName());

				Cell typeDataCell = row.createCell(cellIndex++);

				typeDataCell.setCellValue(
					contentDashboardItem.getTypeLabel(locale));

				Cell subtypeDataCell = row.createCell(cellIndex++);

				ContentDashboardItemSubtype contentDashboardItemSubtype =
					contentDashboardItem.getContentDashboardItemSubtype();

				subtypeDataCell.setCellValue(
					contentDashboardItemSubtype.getLabel(locale));

				Cell assetDataCell = row.createCell(cellIndex++);

				assetDataCell.setCellValue(
					contentDashboardItem.getScopeName(locale));

				List<ContentDashboardItem.Version> versions =
					contentDashboardItem.getVersions(locale);

				Cell statusDataCell = row.createCell(cellIndex++);

				ContentDashboardItem.Version latestVersion = versions.get(0);

				statusDataCell.setCellValue(latestVersion.getLabel());

				Cell modifiedDateDataCell = row.createCell(cellIndex);

				Date modifiedDate = contentDashboardItem.getModifiedDate();

				modifiedDateDataCell.setCellValue(modifiedDate.toString());
			}

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			workbook.write(byteArrayOutputStream);

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


	private static final Log _log = LogFactoryUtil.getLog(
		GetContentDashboardItemsCsvMVCResourceCommand.class);

	@Reference
	private ContentDashboardAdminDisplayContext
		_contentDashboardAdminDisplayContext;

	@Reference
	private Portal _portal;

}