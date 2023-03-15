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

import {Property} from '../../types/Criteria';
import {buildEventQueryString} from './ac-grammar';
import {Conjunction, PROPERTY_GROUPS, PropertyKey} from './constants';
import {buildQueryString} from './odata';
interface BaseContributor {
	conjunctionId: Conjunction;
	conjunctionInputId: string;
	criteriaMap: Record<string, unknown>;
	entityName: string;
	inputId: string;
	modelLabel: string;
	properties: Property[];
	query: string;
}
interface EventContributor extends BaseContributor {
	initialQuery?: Parameters<typeof buildEventQueryString>[0];
	propertyKey: typeof PROPERTY_GROUPS.EVENT;
}
interface UnknownContributor extends BaseContributor {
	initialQuery?: Parameters<typeof buildQueryString>[0][0];
	propertyKey: unknown;
}
declare type Contributor = EventContributor | UnknownContributor;
interface PropertyGroup {
	entityName: string;
	name: string;
	properties: Property[];
	propertyKey: string;
}

/**
 * Produces a list of Contributors from a list of initialContributors and a list
 * of propertyGroups.
 */
export declare function initialContributorsToContributors(
	initialContributors: Contributor[],
	propertyGroups: PropertyGroup[]
): {
	conjunctionId: Conjunction;
	conjunctionInputId: string;
	criteriaMap:
		| import('../../types/Criteria').CriteriaItem
		| import('../../types/Criteria').Criteria
		| {
				items: import('../../types/Criteria').CriteriaItem[];
		  }
		| null;
	entityName: string | undefined;
	inputId: string;
	modelLabel: string | undefined;
	properties: Property[] | undefined;
	propertyKey: unknown;
	query: string;
}[];

/**
 * Applies a criteria change to a contributor from a list in both the
 * criteriaMap and query properties.
 */
export declare function applyCriteriaChangeToContributors(
	contributors: Contributor[],
	change: {
		criteriaChange: Contributor['initialQuery'];
		propertyKey: PropertyKey;
	}
): (
	| EventContributor
	| UnknownContributor
	| {
			criteriaMap:
				| import('../../types/Criteria').CriteriaItem
				| import('../../types/Criteria').Criteria
				| {
						items: import('../../types/Criteria').CriteriaItem[];
				  }
				| null
				| undefined;
			query: string;
			initialQuery?:
				| {
						items: import('../../types/Criteria').CriteriaItem[];
				  }
				| undefined;
			propertyKey: 'event';
			conjunctionId: Conjunction;
			conjunctionInputId: string;
			entityName: string;
			inputId: string;
			modelLabel: string;
			properties: Property[];
	  }
	| {
			criteriaMap:
				| import('../../types/Criteria').CriteriaItem
				| import('../../types/Criteria').Criteria
				| {
						items: import('../../types/Criteria').CriteriaItem[];
				  }
				| null
				| undefined;
			query: string;
			initialQuery?:
				| import('../../types/Criteria').CriteriaItem
				| import('../../types/Criteria').Criteria
				| null
				| undefined;
			propertyKey: unknown;
			conjunctionId: Conjunction;
			conjunctionInputId: string;
			entityName: string;
			inputId: string;
			modelLabel: string;
			properties: Property[];
	  }
)[];

/**
 * Applies a conjunction change to the whole array of contributors.
 */
export declare function applyConjunctionChangeToContributor(
	contributors: Contributor[],
	conjunctionName: Conjunction
): Contributor[];
export {};
