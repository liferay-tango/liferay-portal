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
import React, {useMemo, useRef, useState} from 'react';
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

function SelectTypeAndSubtype() {
    /* Mocked values, should be props */
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
         ];
    const itemSelectorSaveEvent = "_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_selectedAssetType";
    /* End mocked values */

    const [filterQuery, setFilterQuery] = useState('');

    const selectedNodesRef = useRef(null);

    const initialSelectedNodeIds = useMemo(() => {
		const selectedNodes = [];

		visit(nodes, (node) => {
			if (node.selected) {
				selectedNodes.push(node.id);
			}
		});

		return selectedNodes;
	}, [nodes]);

    const handleSelectionChange = (selectedNodes) => {
        const data = {};

        // // Mark newly selected nodes as selected.
        visit(nodes, (node) => {
            if (selectedNodes.has(node.id)) {
                data[node.id] = {
                    subtypeId: node.children ? 0 : node.id,
                    nodePath: node.nodePath,
                    value: node.name,
                    typeId: node.children ? node.id : 0,
                };
            }
        });

        // Mark unselected nodes as unchecked.
        if (selectedNodesRef.current) {
            Object.entries(selectedNodesRef.current).forEach(([id, node]) => {
                if (!selectedNodes.has(id)) {
                    data[id] = {
                        ...node,
                        unchecked: true,
                    };
                }
            });
        }

        selectedNodesRef.current = data;

        const openerWindow = Liferay.Util.getOpener();

        openerWindow.Liferay.fire(itemSelectorSaveEvent, {data});
    };

    return (
    <div className="select-type-and-subtype">
        <form
            className="mb-4 pb-3 pt-3 select-type-and-subtype-filter"
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
                            inheritSelection={true}
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