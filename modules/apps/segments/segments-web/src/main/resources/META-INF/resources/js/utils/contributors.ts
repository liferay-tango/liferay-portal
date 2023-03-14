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
import {
	CONJUNCTIONS,
	Conjunction,
	PROPERTY_GROUPS,
	PropertyKey,
	SUPPORTED_CONJUNCTIONS,
} from './constants';
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

type Contributor = EventContributor | UnknownContributor;

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
export function initialContributorsToContributors(
	initialContributors: Contributor[],
	propertyGroups: PropertyGroup[]
) {
	const DEFAULT_CONTRIBUTOR = {conjunctionId: CONJUNCTIONS.AND};
	const {conjunctionId: initialConjunction} =
		initialContributors.find((c) => c.conjunctionId) || DEFAULT_CONTRIBUTOR;

	return initialContributors.map((initialContributor) => {
		const propertyGroup =
			propertyGroups &&
			propertyGroups.find(
				(propertyGroup) =>
					initialContributor.propertyKey === propertyGroup.propertyKey
			);

		let query = '';

		if (initialContributor.initialQuery) {
			query =
				initialContributor.propertyKey === PROPERTY_GROUPS.EVENT
					? buildEventQueryString(
							(initialContributor as EventContributor)
								.initialQuery!
					  )
					: buildQueryString(
							[
								(initialContributor as UnknownContributor)
									.initialQuery!,
							],
							initialContributor.conjunctionId ||
								initialConjunction,
							propertyGroup ? propertyGroup.properties : []
					  );
		}

		return {
			conjunctionId:
				initialContributor.conjunctionId || initialConjunction,
			conjunctionInputId: initialContributor.conjunctionInputId,
			criteriaMap: initialContributor.initialQuery || null,
			entityName: propertyGroup && propertyGroup.entityName,
			inputId: initialContributor.inputId,
			modelLabel: propertyGroup && propertyGroup.name,
			properties: propertyGroup && propertyGroup.properties,
			propertyKey: initialContributor.propertyKey,
			query,
		};
	});
}

/**
 * Applies a criteria change to a contributor from a list in both the
 * criteriaMap and query properties.
 */
export function applyCriteriaChangeToContributors(
	contributors: Contributor[],
	change: {
		criteriaChange: Contributor['initialQuery'];
		propertyKey: PropertyKey;
	}
) {
	return contributors.map((contributor) => {
		const {conjunctionId, properties, propertyKey} = contributor;

		return change.propertyKey === propertyKey
			? {
					...contributor,
					criteriaMap: change.criteriaChange,
					query:
						propertyKey === PROPERTY_GROUPS.EVENT
							? buildEventQueryString(
									(change.criteriaChange as EventContributor['initialQuery'])!
							  )
							: buildQueryString(
									[
										(change.criteriaChange as UnknownContributor['initialQuery'])!,
									],
									conjunctionId,
									properties
							  ),
			  }
			: contributor;
	});
}

/**
 * Applies a conjunction change to the whole array of contributors.
 */
export function applyConjunctionChangeToContributor(
	contributors: Contributor[],
	conjunctionName: Conjunction
) {
	const conjunctionIndex = SUPPORTED_CONJUNCTIONS.findIndex(
		(item) => item.name === conjunctionName
	);

	if (conjunctionIndex === -1) {
		return contributors;
	}

	const nextContributors = contributors.map((contributor) => ({
		...contributor,
		conjunctionId: conjunctionName,
	}));

	return nextContributors;
}
