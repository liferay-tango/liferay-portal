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
import {ClayPaginationWithBasicItems} from '@clayui/pagination';
import {fetch, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import formatDate from './utils/formatDate';

const VersionsContent = ({
	getItemVersionsURL,
	languageTag = 'en',
	namespace,
	onError,
}) => {
	const [currentPage, setCurrentPage] = useState(1);
	const [loading, setLoading] = useState(false);
	const [totalPages, setTotalPages] = useState(0);
	const [versions, setVersions] = useState([]);

	useEffect(() => {
		setLoading(true);
		fetch(`${getItemVersionsURL}&${namespace}pageNumber=${currentPage}`)
			.then((response) => {
				response.json().then((data) => {
					setVersions(data.versions);
					setTotalPages(data.totalPages);
					setLoading(false);
				});
			})
			.catch((error) => {
				if (onError) {
					onError();
				}
				if (process.env.NODE_ENV === 'development') {
					console.error('Failed to fetch versions: ', error);
				}
			});
	}, [currentPage, getItemVersionsURL, namespace, onError]);

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
						</li>
					))}
				</ul>
			)}
			{totalPages > 1 && (
				<ClayPaginationWithBasicItems
					activePage={currentPage}
					onActiveChange={setCurrentPage}
					totalPages={totalPages}
				/>
			)}
		</>
	);
};

VersionsContent.defaultProps = {
	languageTag: 'en-US',
};

VersionsContent.propTypes = {
	getItemVersionsURL: PropTypes.string.isRequired,
	namespace: PropTypes.string.isRequired,
};

export default VersionsContent;
