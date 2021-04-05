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

package com.liferay.layout.reports.web.internal.data.provider;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.apache.http.HttpStatus;

/**
 * @author Cristina González
 */
public class LayoutReportsDataProvider {

	public LayoutReportsDataProvider(String apiKey, Http http) {
		if (http == null) {
			throw new IllegalArgumentException("Http is null");
		}

		_apiKey = apiKey;
		_http = http;
	}

	public List<LayoutReportsIssue> getLayoutReportsIssues(String url)
		throws LayoutReportsDataProviderException {

		JSONObject jsonObject = _getResponseJSONObject(url);

		return Arrays.asList(
			new LayoutReportsIssue(
				"accessibility",
				Stream.of(
					_ACCESSIBILITY_ISSUES
				).mapToLong(
					accessibilityIssue -> _count(accessibilityIssue, jsonObject)
				).sum()),
			new LayoutReportsIssue(
				"seo",
				Stream.of(
					_SEO_ISSUES
				).mapToLong(
					accessibilityIssue -> _count(accessibilityIssue, jsonObject)
				).sum()));
	}

	public boolean isValidConnection() {
		return Validator.isNotNull(_apiKey);
	}

	public static class LayoutReportsDataProviderException
		extends PortalException {

		public LayoutReportsDataProviderException(Exception exception) {
			super(exception);
		}

		public LayoutReportsDataProviderException(String message) {
			super(message);
		}

	}

	public static class LayoutReportsIssue {

		public LayoutReportsIssue(String key, long total) {
			_key = key;
			_total = total;
		}

		public JSONObject toJSONObject(ResourceBundle resourceBundle) {
			return JSONUtil.put(
				"key", _key
			).put(
				"title", ResourceBundleUtil.getString(resourceBundle, _key)
			).put(
				"total", _total
			);
		}

		private final String _key;
		private final long _total;

	}

	private int _count(String issue, JSONObject jsonObject) {
		JSONObject issueJSONObject = JSONUtil.getValueAsJSONObject(
			jsonObject, "JSONObject/lighthouseResult", "JSONObject/audits",
			"JSONObject/" + issue);

		int score = issueJSONObject.getInt("score", -1);

		int issuesCount = (score == 0) ? 1 : 0;

		if (!issueJSONObject.has("details")) {
			return issuesCount;
		}

		JSONArray itemsJSONArray = JSONUtil.getValueAsJSONArray(
			issueJSONObject, "JSONObject/details", "JSONArray/items");

		return Math.max(issuesCount, itemsJSONArray.length());
	}

	private String _getGooglePageSpeedURL(String apiKey, String url) {
		return StringBundler.concat(
			"https://pagespeedonline.googleapis.com/pagespeedonline/v5",
			"/runPagespeed?category=accessibility&category=best-practices",
			"&category=seo&key=", apiKey, "&url=", url);
	}

	private JSONObject _getResponseJSONObject(String url)
		throws LayoutReportsDataProviderException {

		if (!isValidConnection()) {
			throw new LayoutReportsDataProviderException(
				"You need to configure a valid API KEY before attend to " +
					"request data from the service");
		}

		Http.Options options = new Http.Options();

		options.addHeader("Accept", "application/json");

		options.setLocation(_getGooglePageSpeedURL(_apiKey, url));
		options.setTimeout(30000);

		try {
			String response = _http.URLtoString(options);

			Http.Response httpResponse = options.getResponse();

			if (httpResponse.getResponseCode() != HttpStatus.SC_OK) {
				throw new LayoutReportsDataProviderException(
					StringBundler.concat(
						"Unexpected response status ",
						httpResponse.getResponseCode(),
						" with response message: ", response));
			}

			return JSONFactoryUtil.createJSONObject(response);
		}
		catch (IOException | JSONException exception) {
			throw new LayoutReportsDataProviderException(exception);
		}
	}

	private static final String[] _ACCESSIBILITY_ISSUES = {
		"color-contrast", "image-alt", "input-image-alt", "video-caption"
	};

	private static final String[] _SEO_ISSUES = {
		"canonical", "crawlable-anchors", "document-title", "font-size",
		"hreflang", "image-aspect-ratio", "is-crawlable", "link-text",
		"meta-description", "tap-targets"
	};

	private final String _apiKey;
	private final Http _http;

}