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

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classnames from 'classnames';
import React, {useState} from 'react';

const DownloadCSVButton = () => {
	const [loading, setLoading] = useState(false);

	const buttonTextKey = loading
		? Liferay.Language.get('generating-csv')
		: Liferay.Language.get('csv');

	const handleClick = () => {
		setLoading(true);
	};

	const handleCancel = () => {
		setLoading(false);
	};

	return (
		<>
			<ClayTooltipProvider>
				<ClayButton
					borderless
					className={classnames('download-csv-button', {
						'download-csv-button--loading': loading,
					})}
					data-tooltip-align="top"
					disabled={loading}
					displayType="secondary"
					onClick={handleClick}
					title={Liferay.Language.get(
						'download-your-data-in-a-csv-file'
					)}
				>
					<span className="inline-item inline-item-before">
						{loading ? (
							<ClayLoadingIndicator small />
						) : (
							<ClayIcon symbol="download" />
						)}
					</span>
					{buttonTextKey}
				</ClayButton>
			</ClayTooltipProvider>
			{loading && (
				<ClayTooltipProvider>
					<ClayButtonWithIcon
						borderless
						className="ml-2"
						data-tooltip-align="top"
						displayType="secondary"
						onClick={handleCancel}
						symbol="times-circle"
						title={Liferay.Language.get('cancel-csv')}
					/>
				</ClayTooltipProvider>
			)}
		</>
	);
};

export default DownloadCSVButton;
