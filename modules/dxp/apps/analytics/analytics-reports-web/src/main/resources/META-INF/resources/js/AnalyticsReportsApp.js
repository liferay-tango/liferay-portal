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
import useInitialPanelState from './hooks/useInitialPanelState';
export default function AnalyticsReportsApp({context, portletNamespace}) {
	const {analyticsReportsDataURL} = context;

	const [
		hoverOrFocusEventTriggered,
		setHoverOrFocusEventTriggered,
	] = useState(false);

	const analyticsReportsPanelToggle = document.getElementById(
		`${portletNamespace}analyticsReportsPanelToggleId`
	);

	const [isPanelStateOpen] = useInitialPanelState();

	useEventListener(
		'mouseenter',
		() => setHoverOrFocusEventTriggered(true),
		{once: true},
		analyticsReportsPanelToggle
	);

	useEventListener(
		'focus',
		() => setHoverOrFocusEventTriggered(true),
		{once: true},
		analyticsReportsPanelToggle
	);

	return (
		<AnalyticsReports
			analyticsReportsDataURL={analyticsReportsDataURL}
			hoverOrFocusEventTriggered={hoverOrFocusEventTriggered}
			isPanelStateOpen={isPanelStateOpen}
		/>
	);
}

AnalyticsReportsApp.propTypes = {
	context: PropTypes.shape({
		analyticsReportsDataURL: PropTypes.string.isRequired,
	}).isRequired,
	portletNamespace: PropTypes.string.isRequired,
};
