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
import {runScriptsInElement} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useMemo, useRef} from 'react';

const Share = ({onShareClick}) => {
	const contentMemoized = useMemo(
		() => `<script>${onShareClick}</script>`,
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[]
	);
	const elRef = useRef(null);

	const handleShareClick = () => {
		if (elRef.current) {
			runScriptsInElement(elRef.current);
		}
	};

	return (
		<>
			<span
				dangerouslySetInnerHTML={{__html: contentMemoized}}
				ref={elRef}
			/>
			<ClayButton displayType="secondary" onClick={handleShareClick}>
				{Liferay.Language.get('share')}
			</ClayButton>
		</>
	);
};

Share.propTypes = {
	classNameId: PropTypes.string.isRequired,
	classPK: PropTypes.string.isRequired,
	title: PropTypes.string,
};

export default Share;
