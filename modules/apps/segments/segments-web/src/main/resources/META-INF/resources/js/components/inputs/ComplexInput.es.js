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

import ClayButton from '@clayui/button';
import {ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';

// import propTypes from 'prop-types';

import React, {useState} from 'react';

import SelectEntityInput from '../inputs/SelectEntityInput.es';

function ComplexInput(disabled, onChange, options, value) {

	// static propTypes = {
	// 	disabled: propTypes.bool,
	// 	onChange: propTypes.func.isRequired,
	// 	options: propTypes.array,
	// 	value: propTypes.oneOfType([propTypes.string, propTypes.number]),
	// };

	// static defaultProps = {
	// 	options: [],
	// };

	const [visible, setVisible] = useState(false);
	const {observer, onClose} = useModal({
		onClose: () => setVisible(false),
	});

	const _handleIntegerChange = (event) => {
		const value = parseInt(event.target.value, 10);

		if (!isNaN(value)) {
			onChange({value: value.toString()});
		}
	};

	const firstOptions = [
		{
			label: 'has',
			value: 'has',
		},
		{
			label: 'has not',
			value: 'has not',
		},
	];
	const secondOptions = [
		{
			label: 'at least',
			value: 'at least',
		},
		{
			label: 'at most',
			value: 'at most',
		},
	];

	const thirdOptions = [
		{
			label: 'since',
			value: 'since',
		},
		{
			label: 'after',
			value: 'after',
		},
		{
			label: 'before',
			value: 'before',
		},
		{
			label: 'between',
			value: 'between',
		},
		{
			label: 'ever',
			value: 'ever',
		},
		{
			label: 'on',
			value: 'on',
		},
	];

	const fourthOptions = [
		{
			label: 'last 24 hours',
			value: '24',
		},
		{
			label: 'yesterday',
			value: 'yesterday',
		},
		{
			label: 'last 7 days',
			value: '7',
		},
		{
			label: 'last 28 days',
			value: '28',
		},
		{
			label: 'last 30 days',
			value: '30',
		},
		{
			label: 'last 90 days',
			value: '90',
		},
	];

	const selectProps = {
		disabled: false,
		displayValue: '',
		id: 'selectEntity',
		multiple: true,
		onChange: () => {},
		options: [],
		renderEmptyValueErrors: false,
		selectEntity: {
			id: 'selectEntity',
			multiple: true,
			title: 'Select Documents and Media',
		},
		title: 'Select User',
		uri:
			'http://tango.com:8080/group/guest/~/control_panel/manage/-/select/user/selectEntity?_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_0_json=%7B%22desiredItemSelectorReturnTypes%22%3A%22uuid%22%7D&_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_checkedUserIdsEnabled=true&p_p_auth=uqRyKhAn',
	};

	return (
		<div className="mx-0" style={{flexGrow: 1}}>
			{visible && (
				<ClayModal
					observer={observer}
					size="lg"

					// spritemap={spritemap}

					// status="info"

				>
					<ClayModal.Header>
						Downloaded Document & Media
					</ClayModal.Header>

					<ClayModal.Body>
						<p>Select the documents and media</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={<ClayButton onClick={onClose}>Save</ClayButton>}
					/>
				</ClayModal>
			)}

			<div className="align-items-center d-flex mb-2">
				<ClaySelectWithOption
					className="criterion-input form-control operator-input"
					options={firstOptions}
				/>

				<span className="criterion-string">
					Downloaded Document & Media
				</span>

				<SelectEntityInput {...selectProps}></SelectEntityInput>
			</div>

			<div className="align-items-center d-flex">
				<ClaySelectWithOption
					className="criterion-input form-control operator-input"
					options={secondOptions}
				/>

				<input
					className="criterion-input form-control"
					data-testid="integer-number"
					disabled={disabled}
					onChange={_handleIntegerChange}
					type="number"
					value={value || 1}
				/>

				<span className="criterion-string">time(s)</span>

				<ClaySelectWithOption
					className="criterion-input form-control operator-input"
					options={thirdOptions}
				/>

				<ClaySelectWithOption
					className="criterion-input form-control operator-input"
					options={fourthOptions}
				/>
			</div>
		</div>
	);
}

export default ComplexInput;
