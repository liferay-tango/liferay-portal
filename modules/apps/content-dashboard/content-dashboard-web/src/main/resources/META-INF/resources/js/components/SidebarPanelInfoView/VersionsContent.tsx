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

import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {fetch, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import VersionActions, {IAction} from './VersionActions';
import formatDate from './utils/formatDate';

const useIsFirstRender = (): boolean => {
	const isFirstRef = useRef(true);

	if (isFirstRef.current) {
		isFirstRef.current = false;

		return true;
	}

	return isFirstRef.current;
};

const VersionsContent = ({
	getItemVersionsURL,
	languageTag = 'en',
	onError,
}: IProps) => {
	const [loading, setLoading] = useState(false);
	const [versions, setVersions] = useState([] as IVersion[]);
	const isFirst: boolean = useIsFirstRender();
	const getVersionsData = useCallback(async (): Promise<void> => {
		try {
			setLoading(true);
			const response: Response = await fetch(getItemVersionsURL);

			if (!response.ok) {
				throw new Error(`Failed to fetch ${getItemVersionsURL}`);
			}

			const {versions}: IData = await response.json();
			setVersions(versions);
		}
		catch (error: unknown) {
			onError();

			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}
		}
		finally {
			setLoading(false);
		}
	}, [getItemVersionsURL, onError]);

	useEffect((): void => {

		// prevent the initial fetch when the tab is inactive

		if (isFirst) {
			return;
		}
		getVersionsData();
	}, [getVersionsData, isFirst]);

	return (
		<>
			{loading ? (
				<div className="align-items-center d-flex loading-indicator-wrapper">
					<ClayLoadingIndicator small />
				</div>
			) : (
				<ul className="list-group sidebar-list-group">
					{versions.map((version) => (
						<li
							className="list-group-item list-group-item-flex"
							key={version.version}
						>
							<ClayLayout.ContentCol expand>
								<div className="list-group-title">
									{Liferay.Language.get('version') + ' '}

									{version.version}
								</div>

								<div className="list-group-subtitle">
									{sub(Liferay.Language.get('x-by-x'), [
										formatDate(
											version.createDate,
											languageTag
										),
										version.userName,
									])}
								</div>

								<div className="list-group-subtext">
									{version.changeLog
										? version.changeLog
										: Liferay.Language.get('no-change-log')}
								</div>
							</ClayLayout.ContentCol>

							<VersionActions
								actions={[
									{
										action: 'expire',
										actionLabel: 'Expire',
										actionURL:
											// eslint-disable-next-line @liferay/portal/no-localhost-reference
											'http://localhost:8080/group/guest/~/control_panel/manage?p_p_id=com_liferay_journal_web_portlet_JournalPortlet&p_p_lifecycle=1&p_p_state=maximized&p_p_mode=view&_com_liferay_journal_web_portlet_JournalPortlet_javax.portlet.action=%2Fjournal%2Fexpire_articles&_com_liferay_journal_web_portlet_JournalPortlet_redirect=%2Fgroup%2Fguest%2F%7E%2Fcontrol_panel%2Fmanage%3Fp_p_id%3Dcom_liferay_journal_web_portlet_JournalPortlet%26p_p_lifecycle%3D0%26p_p_state%3Dmaximized%26p_p_mode%3Dview%26_com_liferay_journal_web_portlet_JournalPortlet_mvcPath%3D%252Fview_article_history.jsp%26_com_liferay_journal_web_portlet_JournalPortlet_redirect%3D%252Fgroup%252Fguest%252F%257E%252Fcontrol_panel%252Fmanage%253Fp_p_id%253Dcom_liferay_journal_web_portlet_JournalPortlet%2526p_p_lifecycle%253D0%2526p_p_state%253Dmaximized%2526p_p_mode%253Dview%2526p_p_auth%253D4aUqY2iX%2526wkrh___tabs1%253Dproperties%26_com_liferay_journal_web_portlet_JournalPortlet_backURL%3D%252Fgroup%252Fguest%252F%257E%252Fcontrol_panel%252Fmanage%253Fp_p_id%253Dcom_liferay_journal_web_portlet_JournalPortlet%2526p_p_lifecycle%253D0%2526p_p_state%253Dmaximized%2526p_p_mode%253Dview%2526p_p_auth%253D4aUqY2iX%2526wkrh___tabs1%253Dproperties%26_com_liferay_journal_web_portlet_JournalPortlet_articleId%3D43815%26p_p_auth%3D4aUqY2iX&_com_liferay_journal_web_portlet_JournalPortlet_articleId=43815_version_1.8&_com_liferay_journal_web_portlet_JournalPortlet_groupId=20121&p_p_auth=4aUqY2iX',
										icon: 'time',
										title: 'Expire',
									},
								]}
							/>
						</li>
					))}
				</ul>
			)}
		</>
	);
};

interface IData {
	versions: IVersion[];
}

interface IProps {
	getItemVersionsURL: string;
	languageTag?: string;
	onError: () => void;
	versionActions: IAction[];
}

interface IVersion {
	changeLog?: string;
	createDate: string;
	statusLabel: string;
	statusStyle: string;
	userName: string;
	version: string;
}

export default VersionsContent;
