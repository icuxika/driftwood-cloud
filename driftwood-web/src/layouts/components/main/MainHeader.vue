<template>
	<n-space>
		<Draggable
			class="draggable-container"
			tag="transition-group"
			:component-data="{
				tag: 'div',
				name: !dragging ? 'flip-list' : null,
			}"
			v-model="tabList"
			v-bind="dragOptions"
			item-key="id"
			@start="dragging = true"
			@end="dragging = false"
		>
			<template #item="{ element }">
				<div
					class="draggable-container-item"
					:class="{
						'draggable-container-item-active': element.id === 1,
					}"
					@click.stop="goto(element)"
				>
					<span>{{ element.title }}</span>
					<n-icon
						size="16"
						v-if="!element.fixed"
						@click.stop="closeTab(element)"
					>
						<CloseIcon />
					</n-icon>
				</div>
			</template>
		</Draggable>
	</n-space>
</template>

<script setup lang="ts">
import Draggable from "vuedraggable";
import { CloseOutline as CloseIcon } from "@vicons/ionicons5";
import { ref } from "vue";

interface TabItem {
	id: number;
	title: string;
	fixed: boolean;
}

const tabList = ref<TabItem[]>([
	{
		id: 1,
		title: "首页",
		fixed: true,
	},
	{
		id: 2,
		title: "客户端管理",
		fixed: false,
	},
	{
		id: 3,
		title: "用户管理",
		fixed: false,
	},
	{
		id: 4,
		title: "角色管理",
		fixed: false,
	},
	{
		id: 5,
		title: "权限管理",
		fixed: false,
	},
	{
		id: 6,
		title: "菜单管理",
		fixed: false,
	},
]);

// 传递给vue.draggable.next(https://github.com/SortableJS/vue.draggable.next)依赖的Sortable(https://github.com/SortableJS/Sortable)的参数
const dragOptions = {
	group: "description",
	animation: 250,
	disabled: false,
	ghostClass: "ghost",
};

// 是否处于拖拽，此判断影响是否给<transition-group>指定name属性，目前来自于vue.draggable.next的示例(https://github.com/SortableJS/vue.draggable.next/blob/master/example/components/transition-example-2.vue)
// 实际测试，不使用列表的移动过渡(.flip-list-move),底层的Sortable似乎已经提供应有的效果了，待后期测试
const dragging = ref(false);
const goto = (item: TabItem) => {
	console.log("goto: ", JSON.stringify(item));
};

// 关闭标签
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
	transition: transform 0.2s ease;
}

.ghost {
	opacity: 0.5;
}
</style>
