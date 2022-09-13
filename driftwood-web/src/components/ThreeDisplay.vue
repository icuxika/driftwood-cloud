<template>
	<div class="container">
		<div class="status-wrapper">
			<div>Loading status: {{ loading }}</div>
			<div>Loading progress: {{ loadingProgress }}</div>
			<div>Selected mesh: {{ selectedMesh?.name }}</div>
			<n-color-picker
				:show-alpha="false"
				:actions="['confirm']"
				@confirm="handleColorConfirm"
			/>
			<n-switch @update:value="handleAutoRotate">
				<template #checked> 开启自动旋转</template>
				<template #unchecked> 关闭自动旋转</template>
			</n-switch>
			<ul>
				<li
					v-for="(mesh, index) in meshList"
					:key="mesh.id"
					@click="selectMesh(index)"
				>
					<span> {{ mesh.id }} {{ mesh.name }} </span>
				</li>
			</ul>
		</div>
		<div class="three-display-wrapper">
			<div ref="threeDisplayDivRef" class="three-display"></div>
			<div>x: {{ x }} y: {{ y }} z: {{ z }}</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import {
	Scene,
	PerspectiveCamera,
	WebGLRenderer,
	Color,
	Clock,
	AnimationMixer,
	AmbientLight,
	DirectionalLight,
	DirectionalLightHelper,
	HemisphereLight,
	HemisphereLightHelper,
	Mesh,
	MeshPhongMaterial,
	DataTexture,
	RGBFormat,
	Object3D,
} from "three";
import { onMounted, reactive, ref, toRefs } from "vue";
import { GLTF, GLTFLoader } from "three/examples/jsm/loaders/GLTFLoader";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls";
import Stats from "three/examples/jsm/libs/stats.module";

let scene: Scene;
let directionalLight: DirectionalLight; // 平行光
let directionalLightHelper: DirectionalLightHelper;
let hemisphereLight: HemisphereLight;
let hemisphereLightHelper: HemisphereLightHelper;
let ambientLight: AmbientLight; // 环境光
let camera: PerspectiveCamera;
let renderer: WebGLRenderer;
let controls: OrbitControls;
let loader: GLTFLoader;
let stats: Stats;

const threeDisplayDivRef = ref<HTMLDivElement | null>(null);

// 相机默认坐标
const defaultCameraPos = {
	x: 0,
	y: 1,
	z: -1,
};

let cameraPos = reactive(defaultCameraPos);
let { x, y, z } = toRefs(cameraPos);

const loading = ref(true);
const loadingProgress = ref(0);
const meshList = ref<Mesh[]>([]);
const selectedMesh = ref<Mesh>();

const initScene = () => {
	scene = new Scene();
	scene.background = new Color(0xe8eaed);
	renderer = new WebGLRenderer({ antialias: true });
	renderer.physicallyCorrectLights = true;
	renderer.shadowMap.enabled = true;
	if (threeDisplayDivRef.value) {
		renderer.setSize(
			threeDisplayDivRef.value.offsetWidth,
			threeDisplayDivRef.value.offsetHeight
		);
		threeDisplayDivRef.value.appendChild(renderer.domElement);
	}
};

const initLight = () => {
	directionalLight = new DirectionalLight(0xffffff, 5);
	directionalLight.castShadow = true;
	directionalLight.position.set(-4, 8, 4);
	directionalLightHelper = new DirectionalLightHelper(
		directionalLight,
		5,
		0xff0000
	);
	hemisphereLight = new HemisphereLight(0xffffff, 0xffffff, 5);
	hemisphereLight.position.set(0, 8, 0);
	hemisphereLightHelper = new HemisphereLightHelper(hemisphereLight, 5);
	scene.add(directionalLight, hemisphereLight);
	ambientLight = new AmbientLight(0xffffff, 5);
	ambientLight.position.set(0, 10, 0);
	scene.add(ambientLight);
};

const initCamera = () => {
	const { x, y, z } = defaultCameraPos;
	camera = new PerspectiveCamera(75, innerWidth / innerHeight, 0.1, 1000);
	camera.position.set(x, y, z);
};

const initControls = () => {
	controls = new OrbitControls(camera, renderer.domElement);
	controls.target.set(0, 0.5, 0);
	controls.update();
	controls.enableZoom = true;
	controls.enablePan = true; // 开启右键拖拽
	controls.autoRotate = false; // 是否自动旋转
	controls.addEventListener("change", () => {
		cameraPos.x = camera.position.x;
		cameraPos.y = camera.position.y;
		cameraPos.z = camera.position.z;
	});
};

// 显示FPS、内存占用等
const initStats = () => {
	stats = Stats();
	if (threeDisplayDivRef.value) {
		threeDisplayDivRef.value.appendChild(stats.domElement);
	}
};

const animate = () => {
	requestAnimationFrame(animate);
	stats.update();
	controls.update();
	renderer.render(scene, camera);
};

const onWindowResize = () => {
	if (threeDisplayDivRef.value) {
		let width = threeDisplayDivRef.value.offsetWidth;
		let height = threeDisplayDivRef.value.offsetHeight;
		camera.aspect = width / height;
		camera.updateProjectionMatrix();
		renderer.setSize(width, height);
	}
};

// 加载 GLTF 模型
const loadGLTFL = (path: string, file: string): Promise<GLTF> => {
	loading.value = true;
	loader = new GLTFLoader().setPath(path);
	return new Promise((resolve, reject) => {
		loader.load(
			file,
			(gltf) => {
				resolve(gltf);
			},
			({ loaded, total }) => {
				let load = Math.abs((loaded / total) * 100);
				loadingProgress.value = load;
				if (load == 100) {
					loading.value = false;
				}
			},
			(error) => {
				reject(error);
			}
		);
	});
};

const selectMesh = (index: number) => {
	selectedMesh.value = meshList.value[index];
};

// 更新颜色
const handleColorConfirm = (value: string) => {
	console.log(value);
	let rgb = value.replace(/[rgb]|[(]|[)]|\s/g, "").split(",");
	console.log(rgb);
	if (selectedMesh.value) {
		if (selectedMesh.value.material) {
			let meshPhongMaterial = selectedMesh.value
				.material as MeshPhongMaterial;
			meshPhongMaterial.color = new Color(
				Number(rgb[0]),
				Number(rgb[1]),
				Number(rgb[2])
			);
			meshPhongMaterial.needsUpdate = true;
		}
	}
};

// 是否开启自动旋转
const handleAutoRotate = (value: boolean) => {
	controls.autoRotate = value;
};

const initialize = async () => {
	initScene();
	initLight();
	initCamera();
	initControls();
	initStats();

	const gltf = await loadGLTFL("/models/chair/", "scene.gltf");
	console.log(gltf);
	const modelScene = gltf.scene;

	modelScene.traverse((child: Object3D) => {
		if (child instanceof Mesh) {
			meshList.value.push(child);
		}
	});

	scene.add(modelScene);
	animate();
	window.addEventListener("resize", onWindowResize, false);
	// 确保第一次加载的时候canvas的宽高正确计算
	onWindowResize();
};
onMounted(initialize);
</script>

<style lang="scss" scoped>
.container {
	display: flex;
	flex-direction: row;

	> .status-wrapper {
		min-width: 300px;
	}

	> .three-display-wrapper {
		width: 100%;
		height: 100%;

		> .three-display {
			min-height: 600px;
		}
	}
}
</style>
