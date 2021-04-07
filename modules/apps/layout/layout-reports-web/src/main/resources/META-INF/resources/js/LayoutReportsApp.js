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

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useReducer, useState} from 'react';

import BasicInformation from './components/BasicInformation';
import EmptyLayoutReports from './components/EmptyLayoutReports';
import LayoutReportsIssuesList from './components/LayoutReportsIssuesList';

const initialState = {
	data: null,
	error: null,
	loading: false,
};

const dataReducer = (state, action) => {
	switch (action.type) {
		case 'LOAD_DATA':
			return {
				...state,
				loading: true,
			};

		case 'SET_ERROR':
			return {
				...state,
				error: action.error,
				loading: false,
			};

		case 'SET_DATA':
			return {
				data: {
					...action.data,
				},
				error: action.data?.error,
				loading: false,
			};

		default:
			return initialState;
	}
};

export default function ({context}) {
	const {
		assetsPath,
		canonicalURLs,
		configureGooglePageSpeedURL,
		defaultLanguageId,
		validConnection,
	} = context;

	const isMounted = useIsMounted();

	const [state, dispatch] = useReducer(dataReducer, initialState);

	const safeDispatch = (action) => {
		if (isMounted()) {
			dispatch(action);
		}
	};

	const getLayoutReportsIssues = (fetchURL, canonicalURL, groupId) => {
		safeDispatch({type: 'LOAD_DATA'});

		const body = {canonicalURL, groupId};

		fetch(fetchURL, {
			body,
			method: 'POST',
		})
			.then((response) =>
				response.json().then((data) =>
					safeDispatch({
						data: data.context,
						type: 'SET_DATA',
					})
				)
			)
			.catch(() => {
				safeDispatch({
					error: {
						message: Liferay.Language.get('relaunch'),
						title: Liferay.Language.get('connection-failed'),
					},
					type: 'SET_ERROR',
				});
			});
	};

	const [selectedCanonicalURL, setSelectedCanonicalURL] = useState(
		canonicalURLs.find(({languageId}) => languageId === defaultLanguageId)
	);

	const [selectedLanguage, setSelectedLanguage] = useState(defaultLanguageId);

	useEffect(() => {
		getLayoutReportsIssues(
			selectedCanonicalURL.layoutReportsIssuesURL,
			selectedCanonicalURL.canonicalURL,
			20125
		);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [
		canonicalURLs,
		selectedLanguage,
		selectedCanonicalURL.canonicalURL,
		selectedCanonicalURL.layoutReportsIssuesURL,
	]);

	const handleLanguageChange = (canonicalURL) => {
		setSelectedCanonicalURL(canonicalURL);
		setSelectedLanguage(canonicalURL.languageId);
	};

	const handleRelaunch = () =>
		getLayoutReportsIssues(
			selectedCanonicalURL.layoutReportsIssuesURL,
			selectedCanonicalURL.canonicalURL,
			20125
		);

	return (
		<>
			<BasicInformation
				canonicalURLs={canonicalURLs}
				defaultLanguageId={defaultLanguageId}
				onLanguageChange={handleLanguageChange}
				selectedCanonicalURL={selectedCanonicalURL}
			/>

			{validConnection ? (
				state.loading ? (
					<ClayLoadingIndicator small />
				) : (
					<>
						{state.error && (
							<ClayAlert
								className="c-mb-3 c-mt-4"
								displayType="danger"
								title={state.error.title}
							>
								{state.error.message}
							</ClayAlert>
						)}

						<ClayButton
							displayType="secondary"
							onClick={handleRelaunch}
						>
							{Liferay.Language.get('relaunch')}
						</ClayButton>

						{state.data && (
							<LayoutReportsIssuesList
								layoutReportsIssues={
									state.data.layoutReportsIssues
								}
							/>
						)}
					</>
				)
			) : (
				<EmptyLayoutReports
					assetsPath={assetsPath}
					configureGooglePageSpeedURL={configureGooglePageSpeedURL}
				/>
			)}
		</>
	);
}
