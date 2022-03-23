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

package com.liferay.segments.internal.odata.entity;

import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.IntegerEntityField;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * Provides the entity data model for the context that segments users.
 *
 * @author Eduardo García
 */
@Component(
	immediate = true, property = "entity.model.name=" + EventEntityModel.NAME,
	service = EntityModel.class
)
public class EventEntityModel implements EntityModel {

	public static final String NAME = "Event";

	public EventEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new ComplexEntityField(
				"download document and media",
				Arrays.asList(
					new IdEntityField(
						"Downloaded Document & Media", locale -> "documentId",
						String::valueOf),
					new IntegerEntityField("time", String::valueOf),
					new IntegerEntityField("range", String::valueOf))));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}