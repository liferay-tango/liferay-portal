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

const MODULUS = 2 ** 32;
const MULTIPLIER = 1664525;
const INCREMENT = 1013904223;
let seed = 1;

function getNumber() {
	seed = (MULTIPLIER * seed + INCREMENT) % MODULUS;

	return seed;
}

export function getRandom(multiplier = 100000) {
	return Math.floor(multiplier * (getNumber() / MODULUS));
}

// --- START MOCKS ---

export const mockTimeRange = {
	endDate: '2020-08-10',
	startDate: '2020-08-16',
};

export const mockPagePublishDate = 'Fri Aug 10 08:17:57 GMT 2020';

export const mockTotalViews = Promise.resolve(getRandom());

export const mockTotalReads = Promise.resolve(getRandom(10000));

const valueViews = getRandom(10000);

const valueReads = getRandom(1000);

const values7DaysViews = [];

for (var i = 0; i <= 7; i++) {
	values7DaysViews[i] = getRandom(100);
}

const values7DaysReads = [];

for (var j = 0; j <= 7; j++) {
	values7DaysReads[j] = getRandom(100);
}

export const mockHistoricalViews7Days = () =>
	Promise.resolve({
		analyticsReportsHistoricalViews: {
			histogram: [
				{
					key: '2020-08-10T00:00:00',
					value: values7DaysViews[0],
				},
				{
					key: '2020-08-11T00:00:00',
					value: values7DaysViews[1],
				},
				{
					key: '2020-08-12T00:00:00',
					value: values7DaysViews[2],
				},
				{
					key: '2020-08-13T00:00:00',
					value: values7DaysViews[3],
				},
				{
					key: '2020-08-14T00:00:00',
					value: values7DaysViews[4],
				},
				{
					key: '2020-08-15T00:00:00',
					value: values7DaysViews[5],
				},
				{
					key: '2020-08-16T00:00:00',
					value: values7DaysViews[6],
				},
			],
			value: valueViews,
		},
	});

export const mockHistoricalReads7Days = () =>
	Promise.resolve({
		analyticsReportsHistoricalReads: {
			histogram: [
				{
					key: '2020-08-10T00:00:00',
					value: values7DaysReads[0],
				},
				{
					key: '2020-08-11T00:00:00',
					value: values7DaysReads[1],
				},
				{
					key: '2020-08-12T00:00:00',
					value: values7DaysReads[2],
				},
				{
					key: '2020-08-13T00:00:00',
					value: values7DaysReads[3],
				},
				{
					key: '2020-08-14T00:00:00',
					value: values7DaysReads[4],
				},
				{
					key: '2020-08-15T00:00:00',
					value: values7DaysReads[5],
				},
				{
					key: '2020-08-16T00:00:00',
					value: values7DaysReads[6],
				},
			],
			value: valueReads,
		},
	});

export const mockHistoricalViews30Days = () =>
	Promise.resolve({
		analyticsReportsHistoricalViews: {
			histogram: [
				{
					key: '2020-08-18T00:00:00',
				},
				{
					key: '2020-08-19T00:00:00',
				},
				{
					key: '2020-08-20T00:00:00',
				},
				{
					key: '2020-08-21T00:00:00',
				},
				{
					key: '2020-08-22T00:00:00',
				},
				{
					key: '2020-08-23T00:00:00',
				},
				{
					key: '2020-08-24T00:00:00',
				},
				{
					key: '2020-08-25T00:00:00',
				},
				{
					key: '2020-08-26T00:00:00',
				},
				{
					key: '2020-08-27T00:00:00',
				},
				{
					key: '2020-08-28T00:00:00',
				},
				{
					key: '2020-08-29T00:00:00',
				},
				{
					key: '2020-08-30T00:00:00',
				},
				{
					key: '2020-08-31T00:00:00',
				},
				{
					key: '2020-08-01T00:00:00',
				},
				{
					key: '2020-08-02T00:00:00',
				},
				{
					key: '2020-08-03T00:00:00',
				},
				{
					key: '2020-08-04T00:00:00',
				},
				{
					key: '2020-08-05T00:00:00',
				},
				{
					key: '2020-08-06T00:00:00',
				},
				{
					key: '2020-08-07T00:00:00',
				},
				{
					key: '2020-08-08T00:00:00',
				},
				{
					key: '2020-08-09T00:00:00',
				},
				{
					key: '2020-08-10T00:00:00',
					value: values7DaysViews[0],
				},
				{
					key: '2020-08-11T00:00:00',
					value: values7DaysViews[1],
				},
				{
					key: '2020-08-12T00:00:00',
					value: values7DaysViews[2],
				},
				{
					key: '2020-08-13T00:00:00',
					value: values7DaysViews[3],
				},
				{
					key: '2020-08-14T00:00:00',
					value: values7DaysViews[4],
				},
				{
					key: '2020-08-15T00:00:00',
					value: values7DaysViews[5],
				},
				{
					key: '2020-08-16T00:00:00',
					value: values7DaysViews[6],
				},
			],
			value: valueViews,
		},
	});

export const mockHistoricalReads30Days = () =>
	Promise.resolve({
		analyticsReportsHistoricalReads: {
			histogram: [
				{
					key: '2020-08-18T00:00:00',
				},
				{
					key: '2020-08-19T00:00:00',
				},
				{
					key: '2020-08-20T00:00:00',
				},
				{
					key: '2020-08-21T00:00:00',
				},
				{
					key: '2020-08-22T00:00:00',
				},
				{
					key: '2020-08-23T00:00:00',
				},
				{
					key: '2020-08-24T00:00:00',
				},
				{
					key: '2020-08-25T00:00:00',
				},
				{
					key: '2020-08-26T00:00:00',
				},
				{
					key: '2020-08-27T00:00:00',
				},
				{
					key: '2020-08-28T00:00:00',
				},
				{
					key: '2020-08-29T00:00:00',
				},
				{
					key: '2020-08-30T00:00:00',
				},
				{
					key: '2020-08-31T00:00:00',
				},
				{
					key: '2020-08-01T00:00:00',
				},
				{
					key: '2020-08-02T00:00:00',
				},
				{
					key: '2020-08-03T00:00:00',
				},
				{
					key: '2020-08-04T00:00:00',
				},
				{
					key: '2020-08-05T00:00:00',
				},
				{
					key: '2020-08-06T00:00:00',
				},
				{
					key: '2020-08-07T00:00:00',
				},
				{
					key: '2020-08-08T00:00:00',
				},
				{
					key: '2020-08-09T00:00:00',
				},
				{
					key: '2020-08-10T00:00:00',
					value: values7DaysReads[0],
				},
				{
					key: '2020-08-11T00:00:00',
					value: values7DaysReads[1],
				},
				{
					key: '2020-08-12T00:00:00',
					value: values7DaysReads[2],
				},
				{
					key: '2020-08-13T00:00:00',
					value: values7DaysReads[3],
				},
				{
					key: '2020-08-14T00:00:00',
					value: values7DaysReads[4],
				},
				{
					key: '2020-08-15T00:00:00',
					value: values7DaysReads[5],
				},
				{
					key: '2020-08-16T00:00:00',
					value: values7DaysReads[6],
				},
			],
			value: valueReads,
		},
	});

export const mockTrafficSources = Promise.resolve([
	{
		countryKeywords: [
			{
				countryCode: 'us',
				countryName: 'United States',
				keywords: [
					{
						keyword: 'amazon',
						position: 1,
						searchVolume: getRandom(10000),
						traffic: getRandom(),
					},
					{
						keyword: 'amazon com',
						position: 2,
						searchVolume: getRandom(1000),
						traffic: getRandom(10000),
					},
				],
			},
			{
				countryCode: 'es',
				countryName: 'Spain',
				keywords: [
					{
						keyword: 'amazon',
						position: 1,
						searchVolume: getRandom(10000),
						traffic: getRandom(),
					},
					{
						keyword: 'amazon america',
						position: 2,
						searchVolume: getRandom(1000),
						traffic: getRandom(10000),
					},
				],
			},
		],
		helpMessage:
			'This number refers to the volume of people that find your page through a search engine.',
		name: 'organic',
		share: 80,
		title: 'Organic',
		value: getRandom(),
	},
	{
		countryKeywords: [
			{
				countryCode: 'us',
				countryName: 'United States',
				keywords: [
					{
						keyword: 'amazon',
						position: 1,
						searchVolume: getRandom(1000),
						traffic: getRandom(10000),
					},
					{
						keyword: 'amazon com',
						position: 2,
						searchVolume: getRandom(100),
						traffic: getRandom(1000),
					},
				],
			},
			{
				countryCode: 'es',
				countryName: 'Spain',
				keywords: [],
			},
		],
		helpMessage:
			'This number refers to the volume of people that find your page through paid keywords.',
		name: 'paid',
		share: 20,
		title: 'Paid',
		value: getRandom(10000),
	},
]);

// --- END MOCKS ---
