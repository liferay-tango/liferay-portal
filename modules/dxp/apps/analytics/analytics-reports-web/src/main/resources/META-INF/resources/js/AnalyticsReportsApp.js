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
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import AnalyticsReports from './components/AnalyticsReports';
import useSidenavState from './hooks/useSidenavState';

export default function AnalyticsReportsApp(props) {
	const {analyticsReportsDataURL, isPanelStateOpen, portletNamespace} = props;

	const [eventTriggered, setEventTriggered] = useState(false);

	const analyticsReportsPanelToggle = document.getElementById(
		`${portletNamespace}analyticsReportsPanelToggleId`
	);

	useSidenavState(analyticsReportsPanelToggle, portletNamespace);

	useEventListener(
		'mouseenter',
		() => setEventTriggered(true),
		{once: true},
		analyticsReportsPanelToggle
	);

	useEventListener(
		'focus',
		() => setEventTriggered(true),
		{once: true},
		analyticsReportsPanelToggle
	);

	return (
		<AnalyticsReports
			analyticsReportsDataURL={analyticsReportsDataURL}
			eventTriggered={eventTriggered}
			isPanelStateOpen={isPanelStateOpen}
		/>
	);
}

AnalyticsReportsApp.propTypes = {
	analyticsReportsDataURL: PropTypes.string.isRequired,
	isPanelStateOpen: PropTypes.bool.isRequired,
	portletNamespace: PropTypes.string.isRequired,
};
