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

import ClayBadge from '@clayui/badge';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

export default function Collapse({children, label, open = false, total}) {
	const [isOpen, setIsOpen] = useState(open);
	const collapseIcon = isOpen ? 'angle-down-small' : 'angle-right-small';
	const collapseIconClassName = isOpen ? 'open' : 'closed';

	useEffect(() => {
		setIsOpen(open);
	}, [open]);

	const handleClick = () => {
		setIsOpen(!isOpen);
	};

	let displayValue = total;

	if (total > 100) {
		displayValue = '+100';
	}

	return (
		<div className="panel-group panel-group-flush">
			<a
				aria-expanded={isOpen}
				className={classNames('collapse-icon', 'sheet-subtitle', {
					collapsed: !isOpen,
				})}
				onClick={handleClick}
			>
				<span className="c-inner ellipsis" tabIndex="-1">
					<ClayLayout.ContentRow>
						<ClayLayout.ContentCol
							className="align-self-center"
							expand
						>
							{label}
						</ClayLayout.ContentCol>
						<ClayLayout.ContentCol>
							<ClayBadge
								displayType="secondary"
								label={displayValue}
							/>
						</ClayLayout.ContentCol>
						<span
							className={`collapse-icon-${collapseIconClassName}`}
						>
							<ClayIcon
								key={collapseIcon}
								symbol={collapseIcon}
							/>
						</span>
					</ClayLayout.ContentRow>
				</span>
			</a>

			<div>{isOpen && children}</div>
		</div>
	);
}

Collapse.propTypes = {
	children: PropTypes.node.isRequired,
	label: PropTypes.string.isRequired,
	open: PropTypes.bool,
	total: PropTypes.number.isRequired,
};
