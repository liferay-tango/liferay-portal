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

import {useEventListener} from '@liferay/frontend-js-react-web';
import React, {useEffect, useState} from 'react';

import AnalyticsReports from './components/AnalyticsReports';

export default function AnalyticsReportsApp({context, portletNamespace}) {
	const {analyticsReportsDataURL} = context;

	const panelState = async () =>
		await Liferay.Util.Session.get(
			'com.liferay.analytics.reports.web_panelState'
		);

	const [panelIsOpen, setPanelIsOpen] = useState(panelState === 'open');

	const analyticsReportsPanelToggle = document.getElementById(
		`${portletNamespace}analyticsReportsPanelToggleId`
	);

	useEffect(() => {
		const sidenavInstance = Liferay.SideNavigation.instance(
			analyticsReportsPanelToggle
		);

		sidenavInstance.on('open.lexicon.sidenav', () => {
			Liferay.Util.Session.set(
				'com.liferay.analytics.reports.web_panelState',
				'open'
			);
			setPanelIsOpen(true);
		});

		sidenavInstance.on('closed.lexicon.sidenav', () => {
			Liferay.Util.Session.set(
				'com.liferay.analytics.reports.web_panelState',
				'closed'
			);
			setPanelIsOpen(false);
		});

		Liferay.once('screenLoad', () => {
			Liferay.SideNavigation.destroy(analyticsReportsPanelToggle);
		});
	}, [analyticsReportsPanelToggle, portletNamespace]);

	const [fetchInitialData, setFetchInitialData] = useState(false);

	useEventListener(
		'mouseenter',
		() => setFetchInitialData(true),
		{once: true},
		analyticsReportsPanelToggle
	);

	useEventListener(
		'focus',
		() => setFetchInitialData(true),
		{once: true},
		analyticsReportsPanelToggle
	);

	return (
		<AnalyticsReports
			analyticsReportsDataURL={analyticsReportsDataURL}
			fetchInitialData={fetchInitialData}
			panelIsOpen={panelIsOpen}
		/>
	);
}
