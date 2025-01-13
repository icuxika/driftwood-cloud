import LogicFlow from "@logicflow/core";
import { MenuConfig } from "@logicflow/extension";
import NodeData = LogicFlow.NodeData;
import EdgeData = LogicFlow.EdgeData;

type MenuHandleFunctions = {
    handleNodeProperty?: (node: NodeData) => void;
    handleEdgeProperty?: (edge: EdgeData) => void;
};

const menuConfig = (menuHandleFunctions: MenuHandleFunctions): MenuConfig => {
    return {
        nodeMenu: [
            {
                text: "分享",
                callback() {
                    alert("分享成功！");
                },
            },
            {
                text: "属性",
                callback(node: NodeData) {
                    if (menuHandleFunctions.handleNodeProperty) {
                        menuHandleFunctions.handleNodeProperty(node);
                    }
                },
            },
        ],
        edgeMenu: [
            {
                text: "属性",
                callback(edge: EdgeData) {
                    if (menuHandleFunctions.handleEdgeProperty) {
                        menuHandleFunctions.handleEdgeProperty(edge);
                    }
                },
            },
        ],
        graphMenu: [
            {
                text: "分享",
                callback() {
                    alert("分享成功！");
                },
            },
        ],
    };
};

export { menuConfig };
