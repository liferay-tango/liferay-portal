/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import ClayButton from '@clayui/button';
import React from 'react';

export default function EmptyLayoutAuditPanel({showButton}) {
	return (
		<div className="text-center">
			<img
				alt="" // TODO: Add alt for the image
				className="c-my-5"
				src={''} // TODO: Change image
				width="120px"
			/>

			<div className="c-mb-2 font-weight-semi-bold">
				<span>
					Check issues that impact on your page&apos;s accessibility
					and SEO.
				</span>
			</div>

			{showButton ? (
				<>
					<div className="c-mb-3 text-secondary">
						Connect to PageSpeed to run a Page Audit.
					</div>

					<ClayButton displayType="secondary">
						Connect to PageSpeed
					</ClayButton>
				</>
			) : (
				<div className="text-secondary">
					<span>
						To run a Page Audit connect with Page Speed from
						Instance settings &#62; Pages &#62; PageSpeed.
					</span>
				</div>
			)}
		</div>
	);
}
