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

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayList from '@clayui/list';
import ClayPanel from '@clayui/panel';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import {StoreStateContext} from '../context/StoreContext';

const MAX_NUMBER_OF_SHOWN_ELEMENTS = 100;

export default function IssueDetail() {
	const {selectedIssue} = useContext(StoreStateContext);

	return (
		<div className="c-p-3">
			<ClayPanel.Group className="panel-group-flush panel-group-sm">
				<HtmlPanel
					content={selectedIssue.description}
					title={Liferay.Language.get('description')}
				/>
				<HtmlPanel
					content={selectedIssue.tips}
					title={Liferay.Language.get('tips')}
				/>
				<FailingElementsPanel
					failingElements={selectedIssue.failingElements}
				/>
			</ClayPanel.Group>
		</div>
	);
}

const HtmlPanel = ({content, title}) => (
	<ClayPanel
		collapsable
		collapseClassNames="c-mb-4 c-mt-3"
		displayTitle={title}
		displayType="unstyled"
		showCollapseIcon={true}
	>
		<ClayPanel.Body>
			<div
				className="text-secondary"
				dangerouslySetInnerHTML={{
					__html: content,
				}}
			></div>
		</ClayPanel.Body>
	</ClayPanel>
);

HtmlPanel.propTypes = {
	content: PropTypes.string.isRequired,
	title: PropTypes.string.isRequired,
};

const FailingElementsPanel = ({failingElements}) => {
	const [shownElements, setShownElements] = useState(10);
	const [showAlert, setShowAlert] = useState(
		failingElements.length > MAX_NUMBER_OF_SHOWN_ELEMENTS
	);

	const totalElements = Math.min(
		failingElements.length,
		MAX_NUMBER_OF_SHOWN_ELEMENTS
	);

	const onViewMore = () => {
		const newShownElements = shownElements + 10;

		setShownElements(
			newShownElements < totalElements ? newShownElements : totalElements
		);
	};

	return (
		<ClayPanel
			collapsable
			collapseClassNames="c-mb-4 c-mt-3"
			defaultExpanded
			displayTitle={Liferay.Language.get('failing-elements')}
			displayType="unstyled"
			showCollapseIcon={true}
		>
			<ClayPanel.Body>
				{showAlert && (
					<ClayAlert
						displayType="info"
						onClose={() => setShowAlert(false)}
					>
						{Liferay.Util.sub(
							Liferay.Language.get(
								'showing-up-to-x-elements-to-fix'
							),
							MAX_NUMBER_OF_SHOWN_ELEMENTS
						)}
					</ClayAlert>
				)}

				<ClayList>
					{failingElements
						.filter((element, index) => index < shownElements)
						.map((element) => (
							<ClayList.Item
								className="border-0 p-0"
								flex
								key={element.key}
							>
								<ClayList.ItemField className="mb-3 p-0" expand>
									<ClayList.ItemTitle>
										{element.title}
									</ClayList.ItemTitle>
									<ClayList.ItemText>
										<code>{element.content}</code>
									</ClayList.ItemText>
								</ClayList.ItemField>
							</ClayList.Item>
						))}
				</ClayList>

				{shownElements < totalElements && (
					<ClayButton displayType="secondary" onClick={onViewMore}>
						{Liferay.Language.get('view-more')}
					</ClayButton>
				)}
			</ClayPanel.Body>
		</ClayPanel>
	);
};
