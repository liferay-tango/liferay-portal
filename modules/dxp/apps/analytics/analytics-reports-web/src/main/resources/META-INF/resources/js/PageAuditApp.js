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
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useMemo, useState} from 'react';

import EmptyPageAudit from './components/EmptyPageAudit';

const MOCK_DATA = {
	assetsPath: '/o/analytics-reports-web/assets',
	canonicalURL: 'http://localhost:8080/en/web/guest/w/basic-content-page',
	showConfigurePageSpeedButton: false,
	title: 'Basic Content Page',
	validPageSpeedConnection: false,
	viewURLs: [
		{
			default: true,
			languageId: 'en-US',
			selected: true,
			viewURL: 'http://example.com/en-us',
		},
		{
			default: false,
			languageId: 'es-ES',
			selected: false,
			viewURL: 'http://example.com/es-es',
		},
	],
};

const {
	assetsPath,
	canonicalURL,
	showConfigurePageSpeedButton,
	title,
	validPageSpeedConnection,
	viewURLs,
} = MOCK_DATA;

const noop = () => {};

export default function () {
	const [active, setActive] = useState(false);

	const selectedLanguage = useMemo(() => {
		return (
			viewURLs.find(({selected}) => selected).languageId ||
			viewURLs.find(({default: isDefault}) => isDefault).languageId
		);
	}, []);

	return (
		<>
			<ClayLayout.ContentRow>
				<ClayLayout.ContentCol>
					<div className="inline-item-before">
						<ClayLayout.ContentRow>
							<ClayLayout.ContentCol>
								<ClayDropDown
									active={active}
									hasLeftSymbols
									onActiveChange={setActive}
									trigger={
										<ClayButton
											className="btn-monospaced"
											displayType="secondary"
											small
										>
											<ClayIcon
												symbol={selectedLanguage.toLowerCase()}
											/>
											<span
												className="d-block font-weight-normal"
												style={{fontSize: '9px'}}
											>
												{selectedLanguage}
											</span>
										</ClayButton>
									}
								>
									<ClayDropDown.ItemList>
										{Object.values(viewURLs).map(
											(language, index) => (
												<ClayDropDown.Item
													active={
														language.selected &&
														language.languageId
													}
													key={index}
													onClick={noop}
													symbolLeft={language.languageId.toLowerCase()}
												>
													<ClayLayout.ContentRow>
														<ClayLayout.ContentCol
															expand
														>
															<span>
																{
																	language.languageId
																}
															</span>
														</ClayLayout.ContentCol>
														<ClayLayout.ContentCol>
															{language.default && (
																<ClayLabel displayType="primary">
																	{Liferay.Language.get(
																		'default'
																	)}
																</ClayLabel>
															)}
														</ClayLayout.ContentCol>
													</ClayLayout.ContentRow>
												</ClayDropDown.Item>
											)
										)}
									</ClayDropDown.ItemList>
								</ClayDropDown>
							</ClayLayout.ContentCol>
						</ClayLayout.ContentRow>
					</div>
				</ClayLayout.ContentCol>
				<ClayLayout.ContentCol expand>
					<ClayLayout.ContentRow>
						<ClayTooltipProvider>
							<span className="font-weight-semi-bold text-truncate-inline">
								<span
									className="text-truncate"
									data-tooltip-align="bottom"
									title={title}
								>
									{title}
								</span>
							</span>
						</ClayTooltipProvider>
					</ClayLayout.ContentRow>

					<ClayLayout.ContentRow>
						<ClayTooltipProvider>
							<span className="text-secondary text-truncate-inline text-truncate-reverse">
								<span
									className="text-truncate"
									data-tooltip-align="bottom"
									title={canonicalURL}
								>
									{canonicalURL}
								</span>
							</span>
						</ClayTooltipProvider>
					</ClayLayout.ContentRow>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{validPageSpeedConnection ? (
				<></>
			) : (
				<EmptyPageAudit
					assetsPath={assetsPath}
					showConfigurePageSpeedButton={showConfigurePageSpeedButton}
				/>
			)}
		</>
	);
}
