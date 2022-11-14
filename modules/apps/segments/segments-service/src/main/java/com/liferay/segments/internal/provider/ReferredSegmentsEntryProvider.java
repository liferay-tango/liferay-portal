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

package com.liferay.segments.internal.provider;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.context.Context;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.odata.matcher.ODataMatcher;
import com.liferay.segments.odata.retriever.ODataRetriever;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.entry.provider.order:Integer=200",
		"segments.entry.provider.source=" + SegmentsEntryConstants.SOURCE_REFERRED
	},
	service = SegmentsEntryProvider.class
)
public class ReferredSegmentsEntryProvider
	extends BaseSegmentsEntryProvider implements SegmentsEntryProvider {

	@Activate
	protected void activate(BundleContext bundleContext) {
		serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<ODataRetriever<BaseModel<?>>>)(Class<?>)ODataRetriever.class,
			"model.class.name");
	}

	@Deactivate
	protected void deactivate() {
		serviceTrackerMap.close();
	}

	@Override
	protected String getSource() {
		return SegmentsEntryConstants.SOURCE_REFERRED;
	}

	@Override
	protected boolean isMember(
		String className, long classPK, Context context,
		SegmentsEntry segmentsEntry, long[] segmentsEntryIds) {

		Criteria criteria = segmentsEntry.getCriteriaObj();

		if ((criteria == null) || MapUtil.isEmpty(criteria.getCriteria())) {
			return false;
		}

		Criteria.Conjunction referredConjunction = getConjunction(
			segmentsEntry, Criteria.Type.REFERRED);
		String referredFilterString = getFilterString(
			segmentsEntry, Criteria.Type.REFERRED);

		boolean member = super.isMember(
			className, classPK, context, segmentsEntry, segmentsEntryIds);

		if (Validator.isNull(referredFilterString) ||
			(member && referredConjunction.equals(Criteria.Conjunction.OR)) ||
			(!member && referredConjunction.equals(Criteria.Conjunction.AND))) {

			return member;
		}

		List<String> referredSegmentsEntryIds = _getReferredSegmentsEntryIds(
			referredFilterString);

		List<Long> userMemberSegmentsEntryIds = new ArrayList<>();

		try {
			for (String segmentEntryId : referredSegmentsEntryIds) {
				SegmentsEntry segmentsEntry1 =
					_segmentsEntryLocalService.getSegmentsEntry(
						Long.valueOf(segmentEntryId));

				if (super.isMember(
						className, classPK, context, segmentsEntry1,
						segmentsEntryIds)) {

					userMemberSegmentsEntryIds.add(
						segmentsEntry1.getSegmentsEntryId());
				}
			}

			return _segmentsEntryODataMatcher.matches(
				referredFilterString,
				HashMapBuilder.put(
					"segmentsEntryIds",
					StringUtil.merge(userMemberSegmentsEntryIds, ",")
				).build());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return member;
	}

	private List<String> _getReferredSegmentsEntryIds(
		String referredFilterString) {

		List<String> segmentsEntryIds = new ArrayList<>();

		Matcher matcher = _pattern.matcher(referredFilterString);

		while (matcher.find()) {
			String match = matcher.group();

			match = match.replaceAll("'", "");

			segmentsEntryIds.add(match);
		}

		return segmentsEntryIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReferredSegmentsEntryProvider.class);

	private static final Pattern _pattern = Pattern.compile("'(\\d+)'");

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference(
		target = "(target.class.name=com.liferay.segments.model.SegmentsEntry)"
	)
	private ODataMatcher<Map<String, String>> _segmentsEntryODataMatcher;

}