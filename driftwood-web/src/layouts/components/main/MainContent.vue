<template>
	<router-view v-slot="{ Component }">
		<template v-if="Component">
			<transition mode="out-in">
				<keep-alive>
					<suspense>
						<component :is="Component"></component>
						<template #fallback>
							<div>Loading...</div>
						</template>
					</suspense>
				</keep-alive>
			</transition>
		</template>
	</router-view>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped></style>
