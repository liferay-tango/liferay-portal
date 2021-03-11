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
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useState} from 'react';

import EmptyLayoutReportsApp from './components/EmptyLayoutReportsApp';

export default function ({context}) {
	const {
		assetsPath,
		canonicalURLs,
		defaultLanguageId,
		showButton,
		validConnection,
	} = context;

	const [selectedLanguageId, setSelectedLanguageId] = useState(
		defaultLanguageId
	);
	const [active, setActive] = useState(false);

	const selectedCanonicalURL = canonicalURLs.find(
		(canonicalURL) => canonicalURL.languageId === selectedLanguageId
	);

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
												symbol={selectedCanonicalURL.languageId.toLowerCase()}
											/>
											<span
												className="d-block font-weight-normal"
												style={{fontSize: '9px'}}
											>
												{
													selectedCanonicalURL.languageId
												}
											</span>
										</ClayButton>
									}
								>
									<ClayDropDown.ItemList>
										{Object.values(canonicalURLs).map(
											(canonicalURL, index) => (
												<ClayDropDown.Item
													active={
														selectedCanonicalURL.languageId ===
														canonicalURL.languageId
													}
													key={index}
													onActiveChange={setActive}
													onClick={event => {
														setSelectedLanguageId(canonicalURL.languageId)
													}}
													symbolLeft={canonicalURL.languageId.toLowerCase()}
												>
													<ClayLayout.ContentRow>
														<ClayLayout.ContentCol
															expand
														>
															<span>
																{
																	canonicalURL.languageId
																}
															</span>
														</ClayLayout.ContentCol>
														<ClayLayout.ContentCol>
															{canonicalURL.default && (
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
									title={selectedCanonicalURL.title}
								>
									{selectedCanonicalURL.title}
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
									title={selectedCanonicalURL.canonicalURL}
								>
									{selectedCanonicalURL.canonicalURL}
								</span>
							</span>
						</ClayTooltipProvider>
					</ClayLayout.ContentRow>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>

			{validConnection ? (
				<></>
			) : (
				<EmptyLayoutReportsApp
					assetsPath={assetsPath}
					showButton={showButton}
				/>
			)}
		</>
	);
}
