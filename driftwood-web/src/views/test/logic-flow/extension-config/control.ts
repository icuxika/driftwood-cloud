import LogicFlow from "@logicflow/core";
import { Control, MiniMap } from "@logicflow/extension";

type MethodFirstParameterType<C, M extends keyof C> = C[M] extends (
    ...args: any[]
) => any
    ? C[M] extends (args0: infer P, ...args: any) => any
        ? P
        : never
    : never;

type addItemFirstParameterType = MethodFirstParameterType<Control, "addItem">;

const miniMapItem: addItemFirstParameterType = {
    key: "mini-map",
    // vue文件css部分主动设置background-image
    iconClass: "lf-control-navigation",
    title: "",
    text: "导航",
    onMouseEnter: (lf: LogicFlow, ev: MouseEvent) => {
        const position = lf.getPointByClient(ev.x, ev.y);
        (lf.extension.miniMap as MiniMap).show(
            position.domOverlayPosition.x - 120,
            position.domOverlayPosition.y + 35
        );
    },
    onClick: (lf: LogicFlow, ev: MouseEvent) => {
        const position = lf.getPointByClient(ev.x, ev.y);
        (lf.extension.miniMap as MiniMap).show(
            position.domOverlayPosition.x - 120,
            position.domOverlayPosition.y + 35
        );
    },
};

export { miniMapItem };
