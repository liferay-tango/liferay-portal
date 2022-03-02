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
import React, {useState} from 'react';

import AnalyticsReports from './components/AnalyticsReports';

export default function AnalyticsReportsApp({context, portletNamespace}) {
	const {analyticsReportsDataURL} = context;

	const analyticsReportsPanelToggle = document.getElementById(
		`${portletNamespace}analyticsReportsPanelToggleId`
	);

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
		/>
	);
}
