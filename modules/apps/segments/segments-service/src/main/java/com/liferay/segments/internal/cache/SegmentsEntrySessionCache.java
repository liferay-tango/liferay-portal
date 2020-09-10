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

package com.liferay.segments.internal.cache;

import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.segments.constants.SegmentsWebKeys;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Cristina González
 */
@Component(service = SegmentsEntrySessionCache.class)
public class SegmentsEntrySessionCache {

	public void clear() {
		for (Map.Entry<String, HttpSession> entry : _sessions.entrySet()) {
			HttpSession httpSession = entry.getValue();

			try {
				httpSession.removeAttribute(SegmentsWebKeys.SEGMENTS_ENTRY_IDS);
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Error cleaning the cache " + illegalStateException);
				}
			}
		}

		_sessions.clear();
	}

	public void clear(HttpSession httpSession) {
		if (_sessions.get(httpSession.getId()) == null) {
			return;
		}

		try {
			httpSession.removeAttribute(SegmentsWebKeys.SEGMENTS_ENTRY_IDS);
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isInfoEnabled()) {
				_log.info("Error cleaning the cache " + illegalStateException);
			}
		}

		_sessions.remove(httpSession.getId());
	}

	public long[] getSegmentsEntryIds(long groupId) {
		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession == null) {
			return null;
		}

		Map<Long, long[]> segmentsEntryIds =
			(Map<Long, long[]>)httpSession.getAttribute(
				SegmentsWebKeys.SEGMENTS_ENTRY_IDS);

		if (segmentsEntryIds == null) {
			return null;
		}

		return segmentsEntryIds.get(groupId);
	}

	public void putSegmentsEntryIds(long groupId, long[] segmentsEntryIds) {
		HttpSession httpSession = PortalSessionThreadLocal.getHttpSession();

		if (httpSession == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get HTTP session");
			}

			return;
		}

		Map<Long, long[]> cachedSegmentsEntryIds =
			(Map<Long, long[]>)GetterUtil.getObject(
				httpSession.getAttribute(SegmentsWebKeys.SEGMENTS_ENTRY_IDS),
				new HashMap<>());

		cachedSegmentsEntryIds.put(groupId, segmentsEntryIds);

		httpSession.setAttribute(
			SegmentsWebKeys.SEGMENTS_ENTRY_IDS, cachedSegmentsEntryIds);

		_sessions.putIfAbsent(httpSession.getId(), httpSession);
	}

	@Deactivate
	protected void deactivate() {
		clear();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntrySessionCache.class);

	private final ConcurrentMap<String, HttpSession> _sessions =
		new ConcurrentReferenceValueHashMap<>(
			new ConcurrentHashMap<>(), FinalizeManager.WEAK_REFERENCE_FACTORY);

}