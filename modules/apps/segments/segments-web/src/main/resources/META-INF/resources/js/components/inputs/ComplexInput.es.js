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
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import propTypes from 'prop-types';
import React from 'react';

class ComplexInput extends React.Component {
	static propTypes = {
		disabled: propTypes.bool,
		onChange: propTypes.func.isRequired,
		options: propTypes.array,
		value: propTypes.oneOfType([propTypes.string, propTypes.number]),
	};

	static defaultProps = {
		options: [],
	};

	_handleIntegerChange = (event) => {
		const value = parseInt(event.target.value, 10);

		if (!isNaN(value)) {
			this.props.onChange({value: value.toString()});
		}
	};

	render() {
		const {disabled, value} = this.props;
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

		return (
			<div className="mx-0" style={{flexGrow: 1}}>
				<div className="align-items-center d-flex mb-2">
					<ClaySelectWithOption
						className="criterion-input form-control operator-input"
						options={firstOptions}
					/>

					<span className="criterion-string">
						Downloaded Document & Media
					</span>

					<ClayForm.Group className="mb-0">
						<ClayInput.Group>
							<ClayInput.GroupItem prepend>
								<ClayInput placeholder="" type="text" />
							</ClayInput.GroupItem>

							<ClayInput.GroupItem append shrink>
								<ClayButton
									displayType="secondary"
									type="button"
								>
									Select
								</ClayButton>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayForm.Group>
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
						onChange={this._handleIntegerChange}
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
}

export default ComplexInput;
