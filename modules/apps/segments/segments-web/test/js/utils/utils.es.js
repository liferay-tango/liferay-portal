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

import {
	CONJUNCTIONS,
	PROPERTY_TYPES,
	SUPPORTED_OPERATORS,
	SUPPORTED_PROPERTY_TYPES,
} from '../../../src/main/resources/META-INF/resources/js/utils/constants.es';
import * as Utils from '../../../src/main/resources/META-INF/resources/js/utils/utils.es';
import {mockCriteria, mockCriteriaNested} from '../data';

const GROUP_ID = 'group_1';

describe('utils', () => {
	describe('createNewGroup', () => {
		it('returns a new group object with the passed in items', () => {
			expect(Utils.createNewGroup([])).toEqual({
				conjunctionName: CONJUNCTIONS.AND,
				groupId: GROUP_ID,
				items: [],
			});
		});
	});

	describe('getChildGroupIds', () => {
		it('returns an empty array if there are no child groups', () => {
			expect(Utils.getChildGroupIds(mockCriteria(1))).toEqual([]);
			expect(Utils.getChildGroupIds(mockCriteria(3))).toEqual([]);
		});

		it('returns the child group ids', () => {
			expect(Utils.getChildGroupIds(mockCriteriaNested())).toEqual([
				'group_02',
				'group_03',
				'group_04',
			]);
		});
	});

	describe('getSupportedOperatorsFromType', () => {
		it('returns an array of supported operators with boolean field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.BOOLEAN
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
			]);
		});

		it('returns an array of supported operators with collection field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.COLLECTION
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
				{
					label: 'contains',
					name: 'contains',
				},
				{
					label: 'not-contains',
					name: 'not-contains',
				},
			]);
		});

		it('returns an array of supported operators with date field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.DATE
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
				{
					label: 'greater-than',
					name: 'gt',
				},
				{
					label: 'greater-than-or-equals',
					name: 'ge',
				},
				{
					label: 'less-than',
					name: 'lt',
				},
				{
					label: 'less-than-or-equals',
					name: 'le',
				},
			]);
		});

		it('returns an array of supported operators with date time field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.DATE_TIME
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
				{
					label: 'greater-than',
					name: 'gt',
				},
				{
					label: 'greater-than-or-equals',
					name: 'ge',
				},
				{
					label: 'less-than',
					name: 'lt',
				},
				{
					label: 'less-than-or-equals',
					name: 'le',
				},
			]);
		});

		it('returns an array of supported operators with double field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.DOUBLE
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
				{
					label: 'greater-than',
					name: 'gt',
				},
				{
					label: 'greater-than-or-equals',
					name: 'ge',
				},
				{
					label: 'less-than',
					name: 'lt',
				},
				{
					label: 'less-than-or-equals',
					name: 'le',
				},
			]);
		});

		it('returns an array of supported operators with integer field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.INTEGER
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
				{
					label: 'greater-than',
					name: 'gt',
				},
				{
					label: 'greater-than-or-equals',
					name: 'ge',
				},
				{
					label: 'less-than',
					name: 'lt',
				},
				{
					label: 'less-than-or-equals',
					name: 'le',
				},
			]);
		});

		it('returns an array of supported operators with multiple id field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.MULTIPLE_ID
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
				{
					label: 'in',
					name: 'in',
				},
				{
					label: 'not-in',
					name: 'not-in',
				},
			]);
		});

		it('returns an array of supported operators with single id field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.SINGLE_ID
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},
			]);
		});

		it('returns an array of supported operators with string field', () => {
			const supportedOperators = Utils.getSupportedOperatorsFromType(
				SUPPORTED_OPERATORS,
				SUPPORTED_PROPERTY_TYPES,
				PROPERTY_TYPES.STRING
			);

			expect(supportedOperators).toEqual([
				{
					label: 'equals',
					name: 'eq',
				},
				{
					label: 'not-equals',
					name: 'not-eq',
				},

				{
					label: 'contains',
					name: 'contains',
				},
				{
					label: 'not-contains',
					name: 'not-contains',
				},
			]);
		});
	});

	describe('insertAtIndex', () => {
		it('inserts an item at the beginning', () => {
			expect(Utils.insertAtIndex('c', ['a', 'b'], 0)).toEqual([
				'c',
				'a',
				'b',
			]);
		});

		it('inserts an item at the middle', () => {
			expect(Utils.insertAtIndex('c', ['a', 'b'], 1)).toEqual([
				'a',
				'c',
				'b',
			]);
		});

		it('inserts an item at the end', () => {
			expect(Utils.insertAtIndex('c', ['a', 'b'], 2)).toEqual([
				'a',
				'b',
				'c',
			]);
		});
	});

	describe('objectToFormData', () => {
		it('takes an object of key value pairs and return a form data object with the same values', () => {
			const testData = {
				bar: 'bar',
				foo: 'foo',
			};

			const formData = Utils.objectToFormData(testData);

			expect(formData.get('bar')).toEqual('bar');
			expect(formData.get('foo')).toEqual('foo');
		});
	});

	describe('removeAtIndex', () => {
		it('removes the item at the beginning', () => {
			expect(Utils.removeAtIndex(['a', 'b', 'c'], 0)).toEqual(['b', 'c']);
		});

		it('removes the item at the middle', () => {
			expect(Utils.removeAtIndex(['a', 'b', 'c'], 1)).toEqual(['a', 'c']);
		});

		it('removes the item at the end', () => {
			expect(Utils.removeAtIndex(['a', 'b', 'c'], 2)).toEqual(['a', 'b']);
		});
	});

	describe('replaceAtIndex', () => {
		it('replaces the item at the beginning', () => {
			expect(Utils.replaceAtIndex('x', ['a', 'b', 'c'], 0)).toEqual([
				'x',
				'b',
				'c',
			]);
		});

		it('replaces the item at the middle', () => {
			expect(Utils.replaceAtIndex('x', ['a', 'b', 'c'], 1)).toEqual([
				'a',
				'x',
				'c',
			]);
		});

		it('replaces the item at the end', () => {
			expect(Utils.replaceAtIndex('x', ['a', 'b', 'c'], 2)).toEqual([
				'a',
				'b',
				'x',
			]);
		});
	});

	describe('sub', () => {
		it('returns an array', () => {
			const res = Utils.sub('hello world', [''], false);

			expect(res).toEqual(['hello world']);
		});

		it('returns a string', () => {
			const res = Utils.sub('hello world', ['']);

			expect(res).toEqual('hello world');
		});

		it('returns with a subbed value for {0}', () => {
			const res = Utils.sub('hello {0}', ['world']);

			expect(res).toEqual('hello world');
		});

		it('returns with multiple subbed values', () => {
			const res = Utils.sub('My name is {0} {1}', ['hello', 'world']);

			expect(res).toEqual('My name is hello world');
		});
	});
});
