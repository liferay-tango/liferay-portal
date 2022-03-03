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

import PropTypes from 'prop-types';
import {useEffect} from 'react';

const ANALYTICS_REPORTS_CLOSED_PANEL_VALUE = 'closed';
const ANALYTICS_REPORTS_OPEN_PANEL_VALUE = 'open';
const ANALYTICS_REPORTS_PANEL_ID =
	'com.liferay.analytics.reports.web_panelState';

export default function useSidenavState(element, portletNamespace) {
	useEffect(() => {
		const sidenavInstance = Liferay.SideNavigation.instance(element);

		sidenavInstance.on('open.lexicon.sidenav', () => {
			Liferay.Util.Session.set(
				ANALYTICS_REPORTS_PANEL_ID,
				ANALYTICS_REPORTS_OPEN_PANEL_VALUE
			);
		});

		sidenavInstance.on('closed.lexicon.sidenav', () => {
			Liferay.Util.Session.set(
				ANALYTICS_REPORTS_PANEL_ID,
				ANALYTICS_REPORTS_CLOSED_PANEL_VALUE
			);
		});

		Liferay.once('screenLoad', () => {
			Liferay.SideNavigation.destroy(element);
		});
	}, [element, portletNamespace]);
}

useSidenavState.propTypes = {
	element: PropTypes.instanceOf(Element).isRequired,
	portletNamespace: PropTypes.string.isRequired,
};
