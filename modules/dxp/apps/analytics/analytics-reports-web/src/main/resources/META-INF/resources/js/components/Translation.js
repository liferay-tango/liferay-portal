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

import ClayLayout from '@clayui/layout';
import PropTypes from 'prop-types';
import React from 'react';

import {useChartState} from '../context/ChartStateContext';
import Flags from './Flags';
export default function Translation({
	defaultLanguage,
	onSelectedLanguageClick,
	viewURLs,
}) {
	const chartState = useChartState();

	const handleSelectedLanguageClick = (viewURL) => {
		onSelectedLanguageClick(
			viewURL,
			chartState.timeSpanKey,
			chartState.timeSpanOffset
		);
	};

	return (
		<ClayLayout.ContentRow>
			<ClayLayout.ContentCol expand>
				<h5>{Liferay.Language.get('languages-translated-into')}</h5>
				<span className="text-secondary">
					{Liferay.Language.get(
						'select-language-to-view-its-metrics'
					)}
				</span>
			</ClayLayout.ContentCol>
			<ClayLayout.ContentCol>
				<Flags
					defaultLanguage={defaultLanguage}
					onSelectedLanguageClick={handleSelectedLanguageClick}
					viewURLs={viewURLs}
				/>
			</ClayLayout.ContentCol>
		</ClayLayout.ContentRow>
	);
}

Translation.propTypes = {
	defaultLanguage: PropTypes.string.isRequired,
	onSelectedLanguageClick: PropTypes.func.isRequired,
	viewURLs: PropTypes.arrayOf(
		PropTypes.shape({
			default: PropTypes.bool.isRequired,
			languageId: PropTypes.string.isRequired,
			selected: PropTypes.bool.isRequired,
			viewURL: PropTypes.string.isRequired,
		})
	).isRequired,
};
