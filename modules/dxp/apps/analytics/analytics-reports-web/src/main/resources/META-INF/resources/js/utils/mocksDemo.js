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

function hash(string) {
	var value = 0;
	if (string.length == 0) {
		return value;
	}

	for (let i = 0; i < string.length; i++) {
		var charCode = string.charCodeAt(i);
		value = (value << 7) - value + charCode;
		value = value & value;
	}

	return value;
}

export const mockTimeRange = {
	endDate: '2020-08-10',
	startDate: '2020-08-16',
};

export const mockPagePublishDate = 'Fri Aug 10 08:17:57 GMT 2020';

export const generateRandomNumber = (canonicalURL) => {
	const MODULUS = 2 ** 32;
	const MULTIPLIER = 1664525;
	const INCREMENT = 1013904223;
	const hashVaue = '1' + hash(canonicalURL);
	let seed = parseInt(hashVaue, 8);

	function getNumber() {
		seed = (MULTIPLIER * seed + INCREMENT) % MODULUS;

		return seed;
	}

	function getRandom(multiplier = 100000) {
		return Math.floor(multiplier * (getNumber() / MODULUS));
	}

	const valueViews = getRandom(100000);

	const valueReads = getRandom(10000);

	function getMockTotalReads() {
		return Promise.resolve(getRandom(10000));
	}

	function getMockTotalViews() {
		return Promise.resolve(getRandom(100000));
	}

	const getMockHistoricalViews7Days = () =>
		Promise.resolve({
			analyticsReportsHistoricalViews: {
				histogram: [
					{
						key: '2020-08-10T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-11T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-12T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-13T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-14T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-15T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-16T00:00:00',
						value: getRandom(100000),
					},
				],
				value: valueViews,
			},
		});

	const getMockHistoricalReads7Days = () =>
		Promise.resolve({
			analyticsReportsHistoricalReads: {
				histogram: [
					{
						key: '2020-08-10T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-11T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-12T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-13T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-14T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-15T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-16T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
				],
				value: valueReads,
			},
		});

	const getMockHistoricalViews30Days = () =>
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
						value: getRandom(10000),
					},
					{
						key: '2020-08-11T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-12T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-13T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-14T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-15T00:00:00',
						value: getRandom(10000),
					},
					{
						key: '2020-08-16T00:00:00',
						value: getRandom(100000),
					},
				],
				value: valueViews,
			},
		});

	const getMockHistoricalReads30Days = () =>
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
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-11T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-12T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-13T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-14T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-15T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
					{
						key: '2020-08-16T00:00:00',
						value: Math.floor(getRandom(1000) / 2),
					},
				],
				value: valueReads,
			},
		});

	function getMockTrafficSources() {
		return Promise.resolve([
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
				value: getRandom(10000),
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
				value: getRandom(1000),
			},
		]);
	}

	return {
		getMockHistoricalReads7Days,
		getMockHistoricalReads30Days,
		getMockHistoricalViews7Days,
		getMockHistoricalViews30Days,
		getMockTotalReads,
		getMockTotalViews,
		getMockTrafficSources,
	};
};
