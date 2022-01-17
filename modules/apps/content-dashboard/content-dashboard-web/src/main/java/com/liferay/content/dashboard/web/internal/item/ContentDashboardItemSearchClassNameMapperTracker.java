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

package com.liferay.content.dashboard.web.internal.item;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.GenericUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemSearchClassNameMapperTracker.class)
public class ContentDashboardItemSearchClassNameMapperTracker {

	public String getContentDashboardItemClassName(String searchClassName) {
		Set<String> keys = _serviceTrackerMap.keySet();

		Stream<String> stream = keys.stream();

		return stream.filter(
			key -> {
				ContentDashboardItemSearchClassNameMapper<?>
					contentDashboardItemSearchClassNameMapper =
						_serviceTrackerMap.getService(key);

				return searchClassName.equals(
					contentDashboardItemSearchClassNameMapper.
						getSearchClassName());
			}
		).findFirst(
		).orElse(
			searchClassName
		);
	}

	public String getContentDashboardItemSearchClassName(String className) {
		return Optional.ofNullable(
			_serviceTrackerMap.getService(className)
		).map(
			ContentDashboardItemSearchClassNameMapper::getSearchClassName
		).orElse(
			className
		);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap =
			(ServiceTrackerMap)ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ContentDashboardItemSearchClassNameMapper.class,
				null,
				ServiceReferenceMapperFactory.create(
					bundleContext,
					(contentDashboardItemSearchClassNameMapper, emitter) ->
						emitter.emit(
							GenericUtil.getGenericClassName(
								contentDashboardItemSearchClassNameMapper))));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private volatile ServiceTrackerMap
		<String, ContentDashboardItemSearchClassNameMapper<?>>
			_serviceTrackerMap;

}