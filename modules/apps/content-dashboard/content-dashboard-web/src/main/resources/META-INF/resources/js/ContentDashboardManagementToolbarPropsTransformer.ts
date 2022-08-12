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

// FAKE

const _getfakeData = (item: any): any => {
	const options: any = {
		selectAssetCategory: {
			data: {
				itemValueKey: 'categoryId',
				selectEventName: 'selectedAssetCategory',
				selectItemURL: item.data.selectAssetCategoryURL,
				urlParamName: 'assetCategoryId',
			},
			multiple: true,
			size: 'md',
		},
		selectAssetTag: {
			data: {
				itemValueKey: 'value',
				selectEventName: 'selectedAssetTag',
				selectItemURL: item.data.selectTagURL,
				urlParamName: 'assetTagId',
			},
			multiple: true,
			size: 'lg',
		},
		selectAuthor: {
			data: {
				itemValueKey: 'id',
				selectEventName: 'selectedAuthorItem',
				selectItemURL: item.data.selectAuthorURL,
				urlParamName: 'authorIds',
			},
			multiple: true,
			size: 'lg',
		},
		selectContentDashboardItemSubtype: {
			data: {
				itemValueKey: false,
				selectEventName: 'selectedContentDashboardItemSubtype',
				selectItemURL: item.data.selectContentDashboardItemSubtypeURL,
				urlParamName: 'contentDashboardItemSubtypePayload',
			},
			multiple: true,
			size: 'md',
		},
		selectFileExtension: {
			data: {
				itemValueKey: false,
				selectEventName: 'selectedFileExtension',
				selectItemURL: item.data.selectFileExtensionURL,
				urlParamName: 'fileExtension',
			},
			multiple: true,
			size: 'md',
		},
		selectScope: {
			data: {
				itemValueKey: 'groupid',
				selectEventName: 'selectedScopeIdItem',
				selectItemURL: item.data.selectScopeURL,
				urlParamName: 'scopeId',
			},
			multiple: false,
			size: 'lg',
		},
	};

	return {
		...item,
		data: {
			...item.data,
			...options[item.data.action].data,
		},
		multiple: options[item.data.action].multiple,
		size: options[item.data.action].size,
	};
};

// /FAKE

const DEFAULT_VALUES: IDefaultValues = {
	buttonAddLabel: Liferay.Language.get('select'),
	iframeBodyCssClass: '',
	modalHeight: '70vh',
};

/**
 * Returns true if the specified value is an object. Not arrays, native events or functions.
 * @param {?} value Variable to test.
 * @return {boolean} Whether variable is an object.
 */
const _isObjectStrict = (value: unknown): boolean =>
	typeof value === 'object' &&
	!Array.isArray(value) &&
	value !== null &&
	!Object.prototype.hasOwnProperty.call(value, 'currentTarget');

/**
 * Returns URL with proper search params.
 */
const _getRedirectURLWithParams = ({
	data,
	portletNamespace,
	selection,
}: IParams) => {
	const {itemValueKey, redirectURL, urlParamName} = data;

	return [selection]
		.reduce((acc, val) => acc.concat(val), []) // replace with flat()
		.reduce(
			(acc: string, item: any) =>
				addParams(
					`${portletNamespace}${urlParamName}=${
						itemValueKey ? item[itemValueKey] : JSON.stringify(item)
					}`,
					acc
				),
			redirectURL
		);
};

const _handleOnSelect = ({data, portletNamespace, selection}: IParams) => {
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

export default function propsTransformer({
	portletNamespace,
	...otherProps
}: IPropsTransformerProps) {
	return {
		...otherProps,
		onFilterDropdownItemClick(
			_event: Event,
			{item: originalItem}: {item: Item}
		) {

			// FAKE

			const item = _getfakeData(originalItem);

			// /FAKE

			const {data, multiple, size} = item;

			const {dialogTitle, selectEventName, selectItemURL} = data;

			openSelectionModal({
				buttonAddLabel: DEFAULT_VALUES.buttonAddLabel,
				height: DEFAULT_VALUES.modalHeight,
				iframeBodyCssClass: DEFAULT_VALUES.iframeBodyCssClass,
				multiple,
				onSelect: (selection: any[]) =>
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

interface IPropsTransformerProps {
	otherProps: unknown;
	portletNamespace: string;
}

interface Item {
	data: ItemData;
	multiple: boolean;
	size: string;
}

interface ItemData {
	itemValueKey: string;
	redirectURL: string;
	selectEventName: string;
	selectItemURL: string;
	urlParamName: string;
}

interface IParams {
	data: ItemData;
	portletNamespace: string;
	selection: any[];
}

interface IDefaultValues {
	buttonAddLabel: string;
	iframeBodyCssClass: string;
	modalHeight: string;
}
