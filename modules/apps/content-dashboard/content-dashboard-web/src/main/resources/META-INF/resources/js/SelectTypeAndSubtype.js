/* eslint-disable @liferay/liferay/no-abbreviations */
/* eslint-disable sort-keys */
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
import React, {useEffect, useState} from 'react';

import TreeFilter from './components/TreeFilter';
import {nodeTreeArrayMapper} from './utils/tree-utils';

const SelectTypeAndSubtype = ({
	contentDashboardItemTypes,
	itemSelectorSaveEvent,
	portletNamespace,
}) => {
	const [mockData, setMockData] = useState([]);

	const mocks = {
		xs:
			'http://localhost:8080/documents/20123/0/contentDashboardItemTypes_xs_25-25.json',
		sm:
			'http://localhost:8080/documents/20123/0/contentDashboardItemTypes_sm_25-150.json',
		md:
			'http://localhost:8080/documents/20123/0/contentDashboardItemTypes_md_60-60.json',
		lg:
			'http://localhost:8080/documents/20123/0/contentDashboardItemTypes_lg_25-500.json',
		xl:
			'http://localhost:8080/documents/20123/0/contentDashboardItemTypes_xl_250-125.json',
		xxl:
			'http://localhost:8080/documents/20123/0/contentDashboardItemTypes_xxl_550-400.json',
	};

	const SELECTED_MOCK = 'xxl';

	useEffect(() => {
		const fetchMockData = async () => {
			// eslint-disable-next-line @liferay/portal/no-global-fetch
			let response = await fetch(mocks[SELECTED_MOCK]);
			response = await response.json();

			const concatData = contentDashboardItemTypes.concat(
				response.map((parent) => {
					return {
						...parent,
						itemSubtypes: parent.itemSubtypes.map(
							(child, index) => {
								return {
									...child,
									label: child.label + ' ' + index,
								};
							}
						),
					};
				})
			);

			setMockData(concatData);
		};

		fetchMockData();
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const nodes = nodeTreeArrayMapper({
		childrenPropertyKey: 'itemSubtypes',
		namePropertyKey: 'label',
		nodeArray: mockData,
	});

	// prevent the component to render during the fetch of the mock data

	if (!nodes.length) {
		return null;
	}

	return (
		<TreeFilter
			childrenPropertyKey="itemSubtypes"
			itemSelectorSaveEvent={itemSelectorSaveEvent}
			mandatoryFieldsForFiltering={['className', 'classPK']}
			namePropertyKey="label"
			nodes={nodes}
			portletNamespace={portletNamespace}
		/>
	);
};

SelectTypeAndSubtype.propTypes = {
	contentDashboardItemTypes: PropTypes.array.isRequired,
	itemSelectorSaveEvent: PropTypes.string.isRequired,
	portletNamespace: PropTypes.string.isRequired,
};

export default SelectTypeAndSubtype;
