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

import {Sidebar, SidebarPanelInfoView} from '@liferay/content-dashboard-web';
import React from 'react';

const testProps = {
	className: 'com.liferay.journal.model.JournalArticle',
	classPK: '44017',
	clipboard: {},
	createDate: '2022-05-09T15:40:19.897',
	description: '',
	languageTag: 'en-US',
	modifiedDate: '2022-05-09T15:40:20.092',
	preview: {},
	propTypes: [],
	specificFields: {
		'display-date': {
			title: 'Display Date',
			type: 'Date',
			value: '2022-05-09T15:40:00',
		},
		'expiration-date': {
			title: 'Expiration Date',
			type: 'String',
		},
		'review-date': {
			title: 'Review Date',
			type: 'String',
		},
	},
	subType: 'Basic Web Content',
	subscribe: {
		icon: 'bell-on',
		label: 'Subscribe',
		url:
			'http://localhost:8080/group/guest/~/control_panel/manage?p_p_id=com_liferay_journal_web_portlet_JournalPortlet&p_p_lifecycle=1&_com_liferay_journal_web_portlet_JournalPortlet_javax.portlet.action=%2Fjournal%2Fsubscribe_article&_com_liferay_journal_web_portlet_JournalPortlet_redirect=%2Fgroup%2Fguest%2F%7E%2Fcontrol_panel%2Fmanage%3Fp_p_id%3Dcom_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet%26p_p_lifecycle%3D0%26p_p_state%3Dmaximized%26p_v_l_s_g_id%3D40522%26p_p_auth%3D1HgMyvXb&_com_liferay_journal_web_portlet_JournalPortlet_articleId=44017&p_auth=I3HwMOAB&p_p_auth=xQUNFGix',
	},
	tags: [],
	title: 'My first web content',
	type: 'Web Content Article',
	user: {name: 'Test Test', userId: '40528'},
	versions: [
		{
			statusLabel: 'Approved',
			statusStyle: 'success',
			version: '1.0',
		},
	],
	viewURLs: [
		{
			default: true,
			languageId: 'en-US',
		},
	],
	vocabularies: {},
};

export default function () {
	return (
		<Sidebar>
			<SidebarPanelInfoView {...testProps} />
		</Sidebar>
	);
}
