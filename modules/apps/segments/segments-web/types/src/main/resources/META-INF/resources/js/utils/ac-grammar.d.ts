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

import {CriteriaItem} from '../../types/Criteria';
import {DateValue} from '../../types/Date';

/**
 * Recursively traverses the criteria object to build an Ac Grammar filter query
 * string. Properties is required to parse the correctly with or without quotes
 * and formatting the query differently for certain types like collection.
 * @returns An AC grammar query string built from the criteria object.
 */
declare function buildEventQueryString(
	criteria:
		| {
				items: Array<
					CriteriaItem & {
						assetId: string;
						day?: {
							operatorName: string;
							value: DateValue;
						};
						operatorNot?: boolean;
					}
				>;
		  }
		| undefined
): string;
export {buildEventQueryString};
