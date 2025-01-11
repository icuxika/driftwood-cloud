<template>
    <div>
        <n-button type="primary" @click="downloadXml">下载XML</n-button>
        <n-upload :custom-request="uploadXml" :show-file-list="false">
            <n-button>上传XML</n-button>
        </n-upload>
        <div class="container" ref="container"></div>
    </div>
</template>

<script setup lang="ts">
import LogicFlow from "@logicflow/core";
import "@logicflow/core/lib/style/index.css";
import {
    BPMNAdapter,
    BpmnElement,
    BpmnXmlAdapter,
    Control,
    DndPanel,
    InsertNodeInPolyline,
    Menu,
    MiniMap,
    SelectionSelect,
} from "@logicflow/extension";
import "@logicflow/extension/lib/style/index.css";
import { UploadCustomRequestOptions } from "naive-ui";
import { onMounted, useTemplateRef } from "vue";
import { addControlItem } from "./extension-config/control";
import { patternItems } from "./extension-config/dnd-panel";
import { menuConfig } from "./extension-config/menu";

const container = useTemplateRef("container");
let lf: LogicFlow;

const download = (filename: string, text: string) => {
    const element = document.createElement("a");
    element.setAttribute(
        "href",
        "data:text/plain;charset=utf-8," + encodeURIComponent(text)
    );
    element.setAttribute("download", filename);
    element.style.display = "none";
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
};
const downloadXml = () => {
    const data = lf.getGraphData() as string;
    download("logic-flow.xml", data);
};

const uploadXml = async ({ file }: UploadCustomRequestOptions) => {
    const reader = new FileReader();
    reader.onload = (event: ProgressEvent<FileReader>) => {
        if (event.target) {
            const xml = event.target.result as string;
            if (lf.adapterIn) {
                console.log(lf.adapterIn(xml));
                lf.renderRawData(lf.adapterIn(xml));
            }
        }
    };
    reader.readAsText(file.file as File);
};

onMounted(() => {
    if (container.value) {
        lf = new LogicFlow({
            container: container.value,
            grid: true,
            plugins: [
                Control,
                Menu,
                DndPanel,
                MiniMap,
                BpmnElement,
                BpmnXmlAdapter,
                InsertNodeInPolyline,
                SelectionSelect,
                BPMNAdapter,
            ],
        });
        addControlItem(lf.extension.control as Control);
        (lf.extension.dndPanel as DndPanel).setPatternItems(patternItems(lf));
        (lf.extension.menu as Menu).addMenuConfig(menuConfig());
        lf.render({});
    }
});
</script>
<style lang="scss" scoped>
.container {
    height: 600px;
    :deep(i.lf-control-navigation) {
        background-image: url("data:image/jpeg;base64,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");
    }
}
</style>
