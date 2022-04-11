<template>
  <n-space>
    <Draggable
        :list="tabList" item-key="id"
        @start="dragging = true"
        @end="dragging =false"
        class="draggable-container"
        tag="transition-group"
        :component-data="{tag: 'div', name: 'flip-list', type: 'transition'}"
    >
      <template #item="{element}">
        <div
            class="draggable-container-item"
            :class="{'draggable-container-item-active': element.id === 1}"
            @click.stop="goto(element)"
        >
          <span>{{element.title}}</span>
          <n-icon size="16" v-if="!element.fixed" @click.stop="closeTab(element)">
            <CloseIcon />
          </n-icon>
        </div>
      </template>
    </Draggable>
  </n-space>
</template>

<script setup lang="ts">
import Draggable from "vuedraggable";
import {CloseOutline as CloseIcon} from "@vicons/ionicons5";
import {ref} from "vue";

interface TabItem {
  id: number;
  title: string;
  fixed: boolean;
}
const tabList = ref<TabItem[]>([
	{
		id: 1,
		title: "首页",
		fixed: true
	},
	{
		id: 2,
		title: "用户管理",
		fixed: false
	},
	{
		id: 3,
		title: "角色管理",
		fixed: false
	},
]);
const dragging = ref(false);
const goto = (item: TabItem) => {
	console.log("goto: ", JSON.stringify(item));
};
const closeTab = (item: TabItem) => {
	console.log(item);
};
</script>

<style lang="scss" scoped>
.n-space {
  background: #e9ebec;
  padding: 8px;

  & .draggable-container {
    display: flex;

    & .draggable-container-item {
      display: flex;
      align-items: center;
      background: white;
      color: black;
      margin-right: 8px;
      height: 32px;
      padding: 4px 8px;
      border-radius: 4px;
      cursor: pointer;
    }

    & .draggable-container-item-active {
      color: dodgerblue;
    }
  }
}

.flip-list-move {
  transition: transform 0.25s;
}

.no-move {
  transition: transform 0s;
}
</style>
