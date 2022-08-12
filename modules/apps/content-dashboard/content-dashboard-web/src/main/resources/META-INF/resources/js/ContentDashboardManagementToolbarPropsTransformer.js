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

import {addParams, navigate, openSelectionModal} from 'frontend-js-web';

const DEFAULT_VALUES = {
	buttonAddLabel: Liferay.Language.get('select'),
	iframeBodyCssClass: '',
	modalHeight: '70vh',
};

/**
 * Returns true if the specified value is an object. Not arrays, native events or functions.
 * @param {?} value Variable to test.
 * @return {boolean} Whether variable is an object.
 */
const _isObjectStrict = (value) =>
	typeof value === 'object' &&
	!Array.isArray(value) &&
	value !== null &&
	!Object.hasOwn(value, 'currentTarget');

/**
 * Returns URL with proper search params.
 */
const _getRedirectURLWithParams = ({data, portletNamespace, selection}) => {
	const {itemValueKey, redirectURL, urlParamName} = data;

	return [selection].flat().reduce((acc, item) => {
		return addParams(
			`${portletNamespace}${urlParamName}=${
				itemValueKey ? item[itemValueKey] : JSON.stringify(item)
			}`,
			acc
		);
	}, redirectURL);
};

const _handleOnSelect = ({data, portletNamespace, selection}) => {
	if (_isObjectStrict(selection)) {
		selection = Object.values(selection).filter((item) => !item.unchecked);
	}

	navigate(
		_getRedirectURLWithParams({
			data,
			portletNamespace,
			selection,
		})
	);
};

export default function propsTransformer({portletNamespace, ...otherProps}) {
	return {
		...otherProps,
		onFilterDropdownItemClick(_event, {item}) {
			const {data, multiple, size} = item;

			const {dialogTitle, selectEventName, selectItemURL} = data;

			openSelectionModal({
				buttonAddLabel: DEFAULT_VALUES.buttonAddLabel,
				height: DEFAULT_VALUES.modalHeight,
				iframeBodyCssClass: DEFAULT_VALUES.iframeBodyCssClass,
				multiple,
				onSelect: (selection) =>
					_handleOnSelect({
						data,
						portletNamespace,
						selection,
					}),
				selectEventName: portletNamespace + selectEventName,
				size,
				title: dialogTitle,
				url: selectItemURL,
			});
		},
	};
}
