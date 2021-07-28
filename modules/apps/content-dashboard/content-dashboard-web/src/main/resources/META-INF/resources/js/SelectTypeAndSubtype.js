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

import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import React, {useMemo, useState} from 'react';
import {Treeview} from 'frontend-js-components-web';

function visit(nodes, callback) {
	nodes.forEach((node) => {
		callback(node);

		if (node.children) {
			visit(node.children, callback);
		}
	});
}

function getFilter(filterQuery) {
	if (!filterQuery) {
		return null;
	}

	const filterQueryLowerCase = filterQuery.toLowerCase();

	return (node) =>
		!node.vocabulary &&
		node.name.toLowerCase().indexOf(filterQueryLowerCase) !== -1;
}

const handleSelectionChange = (selectedNodes) => {
    // TODO
};

function SelectTypeAndSubtype() {
    /* Mocked values */
    const namespace = "mynamespace";
    const nodes = [
        { "expanded":true,
          "children":[
             {"children":[],"icon":"simple-circle","name":"Basic Web Content","id":"39768"},
             {"children":[],"icon":"simple-circle","name":"Clara Web Content","id":"39769"},
             {"children":[],"icon":"simple-circle","name":"XXX Web Content","id":"39767"}
         ],
         "icon":"folder",
         "name":"Web Content",
         "id":"0"},
         { "expanded":false,
          "children":[
             {"children":[],"icon":"simple-circle","name":"Subtype of document1","id":"49768"},
             {"children":[],"icon":"simple-circle","name":"Subtype of document2","id":"49769"},
             {"children":[],"icon":"simple-circle","name":"Subtype of document","id":"49767"}
         ],
         "icon":"folder",
         "name":"Document",
         "id":"1"}
         ]
    /* End mocked values */

    const [filterQuery, setFilterQuery] = useState('');

    const initialSelectedNodeIds = useMemo(() => {
		const selectedNodes = [];

		visit(nodes, (node) => {
			if (node.selected) {
				selectedNodes.push(node.id);
			}
		});

		return selectedNodes;
	}, [nodes]);

    return (
    <div className="select-type-and-subtype">
        <form
            className="mb-3 mt-3"
            onSubmit={(event) => event.preventDefault()}
            role="search"
        >
            <ClayLayout.ContainerFluid className="d-flex">
                <div className="input-group">
                    <div className="input-group-item">
                        <input
                            className="form-control h-100 input-group-inset input-group-inset-after"
                            onChange={(event) =>
                                setFilterQuery(event.target.value)
                            }
                            placeholder={Liferay.Language.get('search')}
                            type="text"
                        />

                        <div className="input-group-inset-item input-group-inset-item-after pr-3">
                            <ClayIcon symbol="search" />
                        </div>
                    </div>
                </div>
            </ClayLayout.ContainerFluid>
        </form>

        <form name={`${namespace}selectSelectTypeAndSubtypeFm`}>
            <ClayLayout.ContainerFluid containerElement="fieldset">
                <div
                    className="type-tree"
                    id={`${namespace}typeContainer`}
                >
                    {nodes.length > 0 ? (
                        <Treeview
                            NodeComponent={Treeview.Card}
                            filter={getFilter(filterQuery)}
                            initialSelectedNodeIds={initialSelectedNodeIds}
                            multiSelection={true}
                            nodes={nodes}
                            onSelectedNodesChange={handleSelectionChange}
                        />
                    ) : (
                        <div className="border-0 pt-0 sheet taglib-empty-result-message">
                            <div className="taglib-empty-result-message-header"></div>
                            <div className="sheet-text text-center">
                                {Liferay.Language.get(
                                    'no-types-were-found'
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </ClayLayout.ContainerFluid>
        </form>
    </div>
    )
}

export default SelectTypeAndSubtype;