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

import {useEffect, useState} from 'react';

import {
	ANALYTICS_REPORTS_OPEN_PANEL_VALUE,
	ANALYTICS_REPORTS_PANEL_ID,
} from '../constants';

const setInitialOpenPanelState = async (stateCallback) => {
	const _panelState = await Liferay.Util.Session.get(
		ANALYTICS_REPORTS_PANEL_ID
	);

	stateCallback(_panelState === ANALYTICS_REPORTS_OPEN_PANEL_VALUE);
};

export default function useInitialPanelState() {
	const [isPanelStateOpen, setIsPanelStateOpen] = useState(false);

	useEffect(() => {
		setInitialOpenPanelState(setIsPanelStateOpen);
	}, []);

	return [isPanelStateOpen];
}
