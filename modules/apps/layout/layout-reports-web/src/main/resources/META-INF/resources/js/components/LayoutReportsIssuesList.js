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

import PropTypes from 'prop-types';

import Collapse from './Collapse';

export default function LayoutReportsIssuesList({layoutReportsIssues}) {
	return (
		<div className="c-my-3">
			{layoutReportsIssues.map(({key, title, total}) => {
				return (
					<Collapse key={key} label={title} total={total}>
						<></>
					</Collapse>
				);
			})}
		</div>
	);
}

LayoutReportsIssuesList.propTypes = {
	canonicalURLs: PropTypes.arrayOf(
		PropTypes.shape({
			canonicalURL: PropTypes.string.isRequired,
			languageId: PropTypes.string.isRequired,
			title: PropTypes.string.isRequired,
		})
	),
	defaultLanguageId: PropTypes.string.isRequired,
};
