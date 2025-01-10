<template>
    <div class="container">
        <CarbonEmissionSourceTable
            :previous="previous"
            :submit="submit"
            :category-list="categoryList"
        />
    </div>
</template>
<script setup lang="ts">
import {
    DataItem,
    ParameterCategoryCalculationVO,
    data,
} from "@/views/test/render-table/data";
import { onMounted, ref } from "vue";
import { CarbonEmissionSourceTable } from "./carbon-emission-source-table";
const previous = (e: MouseEvent) => {};
const submit = async (dataItems: DataItem[]) => {
    console.log(dataItems);
};
const categoryList = ref<ParameterCategoryCalculationVO[]>([]);
onMounted(async () => {
    const parameterVO = data;
    console.log(parameterVO);
    if (parameterVO) {
        categoryList.value = parameterVO.categoryList;
    }
});
</script>
<style lang="scss" scoped>
.container {
    display: flex;
    justify-content: center;
}
</style>
