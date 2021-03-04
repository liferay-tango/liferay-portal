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

export default function EmptyAuditPanel({
	assetsPath,
	showConfigurePageSpeedButton,
}) {
	const defaultIllustration = `${assetsPath}/issues-default.svg`;

	return (
		<div className="text-center">
			<img
				className="c-mb-4 c-mt-5"
				src={defaultIllustration}
				width="120px"
			/>

			<div className="c-mb-2 font-weight-semi-bold">
				<span>
					{Liferay.Language.get(
						"check-issues-that-impact-on-your-page's-accessibility-and-seo"
					)}
				</span>
			</div>

			{showConfigurePageSpeedButton ? (
				<>
					<div className="c-mb-3 text-secondary">
						{Liferay.Language.get(
							'connect-to-pagespeed-to-run-a-page-audit'
						)}
					</div>

					<ClayButton displayType="secondary">
						{Liferay.Language.get('connect-to-pagespeed')}
					</ClayButton>
				</>
			) : (
				<div className="text-secondary">
					<span>
						{Liferay.Language.get(
							'to-run-a-page-audit-connect-with-pagespeed-from-instance-settings-pages-pagespeed'
						)}
					</span>
				</div>
			)}
		</div>
	);
}
