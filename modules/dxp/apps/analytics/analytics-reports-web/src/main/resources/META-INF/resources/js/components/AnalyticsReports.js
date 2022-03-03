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

import ClayAlert from '@clayui/alert';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useReducer} from 'react';

import {ChartStateContextProvider} from '../context/ChartStateContext';
import ConnectionContext from '../context/ConnectionContext';
import {StoreContextProvider} from '../context/StoreContext';
import {dataReducer, initialState} from '../context/dataReducer';
import Navigation from './Navigation';

import '../../css/analytics-reports-app.scss';

export default function AnalyticsReports({
	analyticsReportsDataURL,
	hoverOrFocusEventTriggered,
	isPanelStateOpen,
}) {
	const isMounted = useIsMounted();

	const [state, dispatch] = useReducer(dataReducer, initialState);

	const safeDispatch = useCallback(
		(action) => {
			if (isMounted()) {
				dispatch(action);
			}
		},
		[isMounted]
	);

	const getData = useCallback(
		(fetchURL, timeSpanKey, timeSpanOffset) => {
			safeDispatch({type: 'LOAD_DATA'});

			const body =
				!timeSpanOffset && !!timeSpanKey
					? {timeSpanKey, timeSpanOffset}
					: {};

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
						error: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'SET_ERROR',
					});
				});
		},
		[safeDispatch]
	);

	useEffect(() => {
		if (hoverOrFocusEventTriggered && !state.data) {
			getData(analyticsReportsDataURL);
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [hoverOrFocusEventTriggered]);

	useEffect(() => {
		if (isPanelStateOpen && !state.data && !state.loading) {
			getData(analyticsReportsDataURL);
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [isPanelStateOpen]);

	const handleSelectedLanguageClick = useCallback(
		(url, timeSpanKey, timeSpanOffset) => {
			getData(url, timeSpanKey, timeSpanOffset);
		},
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);

	return state.loading ? (
		<ClayLoadingIndicator small />
	) : state.error ? (
		<ClayAlert displayType="danger" variant="stripe">
			{state.error}
		</ClayAlert>
	) : (
		state.data && (
			<ConnectionContext.Provider
				value={{
					validAnalyticsConnection:
						state.data.validAnalyticsConnection,
				}}
			>
				<StoreContextProvider
					value={{
						endpoints: {...state.data.endpoints},
						languageTag: state.data.languageTag,
						namespace: state.data.namespace,
						page: state.data.page,
						publishedToday: state.data.publishedToday,
					}}
				>
					<ChartStateContextProvider
						publishDate={state.data.publishDate}
						timeRange={state.data.timeRange}
						timeSpanKey={state.data.timeSpanKey}
					>
						<div className="analytics-reports-app">
							<Navigation
								author={state.data.author}
								canonicalURL={state.data.canonicalURL}
								onSelectedLanguageClick={
									handleSelectedLanguageClick
								}
								pagePublishDate={state.data.publishDate}
								pageTitle={state.data.title}
								timeSpanOptions={state.data.timeSpans}
								viewURLs={state.data.viewURLs}
							/>
						</div>
					</ChartStateContextProvider>
				</StoreContextProvider>
			</ConnectionContext.Provider>
		)
	);
}

AnalyticsReports.propTypes = {
	analyticsReportsDataURL: PropTypes.string.isRequired,
	hoverOrFocusEventTriggered: PropTypes.bool.isRequired,
	isPanelStateOpen: PropTypes.bool.isRequired,
};
