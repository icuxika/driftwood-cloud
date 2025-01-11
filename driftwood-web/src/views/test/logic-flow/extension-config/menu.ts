import LogicFlow from "@logicflow/core";
import { MenuConfig } from "@logicflow/extension";
import NodeData = LogicFlow.NodeData;
import EdgeData = LogicFlow.EdgeData;

const menuConfig = (): MenuConfig => {
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
                    alert(`
      节点id：${node.id}
      节点类型：${node.type}
      节点坐标：(x: ${node.x}, y: ${node.y})
    `);
                },
            },
        ],
        edgeMenu: [
            {
                text: "属性",
                callback(edge: EdgeData) {
                    const {
                        id,
                        type,
                        startPoint,
                        endPoint,
                        sourceNodeId,
                        targetNodeId,
                    } = edge;
                    alert(`
      边id：${id}
      边类型：${type}
      边起点坐标：(startPoint: [${startPoint.x}, ${startPoint.y}])
      边终点坐标：(endPoint: [${endPoint.x}, ${endPoint.y}])
      源节点id：${sourceNodeId}
      目标节点id：${targetNodeId}
    `);
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
