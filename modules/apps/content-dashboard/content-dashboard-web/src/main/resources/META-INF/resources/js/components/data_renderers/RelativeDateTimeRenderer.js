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

import PropType from 'prop-types';
import React from 'react';

function RelativeDateTimeRenderer({value}) {
	const DAY = 86400;
	const DATE_UNITS = {
		year: DAY * 365,
		// eslint-disable-next-line sort-keys
		month: DAY * 30,
		// eslint-disable-next-line sort-keys
		day: DAY,
		hour: 3600,
		minute: 60,
		second: 1,
	};

	const locale = themeDisplay.getLanguageId().replace('_', '-');

	if (!value) {
		return null;
	}

	const getSecondsDiff = (timestamp) => {
		return (Date.now() - timestamp) / 1000;
	};
	const getUnitAndValueDate = (secondsElapsed) => {
		for (const [unit, secondsInUnit] of Object.entries(DATE_UNITS)) {
			if (secondsElapsed >= secondsInUnit || unit === 'second') {
				const value = Math.floor(secondsElapsed / secondsInUnit) * -1;

				// eslint-disable-next-line sort-keys
				return {value, unit};
			}
		}
	};

	const getTimeAgo = (timestamp) => {
		const rtf = new Intl.RelativeTimeFormat(locale);

		const secondsElapsed = getSecondsDiff(timestamp);
		const {unit, value} = getUnitAndValueDate(secondsElapsed);

		return rtf.format(value, unit);
	};

	const formattedDate = getTimeAgo(new Date(value));

	return <span className="text-capitalize">{formattedDate}</span>;
}

RelativeDateTimeRenderer.propTypes = {
	options: PropType.shape({
		format: PropType.object,
	}),
	value: PropType.string.isRequired,
};

export default RelativeDateTimeRenderer;
