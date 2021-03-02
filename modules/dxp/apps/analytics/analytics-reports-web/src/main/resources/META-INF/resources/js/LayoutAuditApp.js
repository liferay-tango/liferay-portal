/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import ClayLayout from '@clayui/layout';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';

import EmptyLayoutAuditPanel from './components/EmptyLayoutAuditPanel';
import Flags from './components/Flags';

const MOCK_DATA = {
	canonicalURL:
		'http://localhost:8080/en/web/guest/w/alice-s-adventures-in-wonderland',
	defaultLanguage: 'en-US',
	showButton: true, // TODO: Check naming to display or not the button (permission to go to System Settings or Instance Settings)
	title: "Alice's Adventures in Wonderland",
	validConnection: false, // TODO: Check naming to display or not the list of issues (with / without API KEY configured)
	viewURLs: [
		{
			default: true,
			languageId: 'en-US',
			selected: true,
			viewURL:
				'http://localhost:8080/en/web/guest/w/alice-s-adventures-in-wonderland?p_p_id=com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=%2Fanalytics_reports%2Fget_data&p_p_cacheability=cacheLevelPage&assetEntryId=41314&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_languageId=en_US&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_classNameId=20134&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_classPK=41308&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_redirect=%2Fweb%2Fguest%2Fbooks',
		},
		{
			default: false,
			languageId: 'es-ES',
			selected: false,
			viewURL:
				'http://localhost:8080/en/web/guest/w/alice-s-adventures-in-wonderland?p_p_id=com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=%2Fanalytics_reports%2Fget_data&p_p_cacheability=cacheLevelPage&assetEntryId=41314&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_languageId=es_ES&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_classNameId=20134&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_classPK=41308&_com_liferay_analytics_reports_web_internal_portlet_AnalyticsReportsPortlet_redirect=%2Fweb%2Fguest%2Fbooks',
		},
	],
};

const {
	canonicalURL,
	defaultLanguage,
	title,
	validConnection,
	viewURLs,
} = MOCK_DATA;

const noop = () => {};

export default function () {
	return (
		<>
			<ClayLayout.ContentRow>
				<ClayLayout.ContentCol>
					<div className="inline-item-before">
						<ClayLayout.ContentRow>
							<ClayLayout.ContentCol>
								<Flags
									defaultLanguage={defaultLanguage}
									onSelectedLanguageClick={noop}
									viewURLs={viewURLs}
								/>
							</ClayLayout.ContentCol>
						</ClayLayout.ContentRow>
					</div>
				</ClayLayout.ContentCol>
				<ClayLayout.ContentCol expand>
					<ClayLayout.ContentRow>
						<ClayTooltipProvider>
							<span className="font-weight-semi-bold text-truncate-inline">
								<span
									className="text-truncate"
									data-tooltip-align="bottom"
									title={title}
								>
									{title}
								</span>
							</span>
						</ClayTooltipProvider>
					</ClayLayout.ContentRow>

					<ClayLayout.ContentRow>
						<ClayTooltipProvider>
							<span className="text-secondary text-truncate-inline text-truncate-reverse">
								<span
									className="text-truncate"
									data-tooltip-align="bottom"
									title={canonicalURL}
								>
									{canonicalURL}
								</span>
							</span>
						</ClayTooltipProvider>
					</ClayLayout.ContentRow>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{validConnection ? <></> : <EmptyLayoutAuditPanel />}
		</>
	);
}
