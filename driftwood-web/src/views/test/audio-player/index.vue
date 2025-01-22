<template>
    <div class="container">
        <audio
            ref="audioRef"
            controls
            src="/download/VillageSurroundedByGreen.mp3"
        ></audio>
        <canvas ref="canvasRef" width="1000px" height="600px"></canvas>
    </div>
</template>
<script setup lang="ts">
import { onMounted, onUnmounted, useTemplateRef } from "vue";

const audioRef = useTemplateRef("audioRef");
const canvasRef = useTemplateRef("canvasRef");
let isInit = false;
let dataArray: Uint8Array, analyser: AnalyserNode;
let animationFrameId: number;

const draw = () => {
    animationFrameId = requestAnimationFrame(draw);
    if (canvasRef.value) {
        const { width, height } = canvasRef.value;
        const ctx = canvasRef.value.getContext("2d");
        if (ctx) {
            ctx.clearRect(0, 0, width, height);
            if (!isInit) {
                return;
            }
            analyser.getByteFrequencyData(dataArray);
            const len = dataArray.length / 4;
            const barWidth = width / len / 2;
            ctx.fillStyle = "dodgerblue";
            for (let i = 0; i < len; i++) {
                const data = dataArray[i];
                const barHeight = (data / 255) * height;
                const x1 = i * barWidth + width / 2;
                const x2 = width / 2 - (i + 1) * barWidth;
                const y = height - barHeight;
                ctx.fillRect(x1, y, barWidth - 2, barHeight);
                ctx.fillRect(x2, y, barWidth - 2, barHeight);
            }
        }
    }
};

onMounted(() => {
    if (audioRef.value) {
        audioRef.value.onplay = () => {
            if (isInit) {
                return;
            }
            const audioCtx = new AudioContext();
            const source = audioCtx.createMediaElementSource(
                audioRef.value as HTMLMediaElement
            );
            analyser = audioCtx.createAnalyser();
            analyser.fftSize = 512;
            dataArray = new Uint8Array(512 / 2);
            source.connect(analyser);
            analyser.connect(audioCtx.destination);
            isInit = true;
            draw();
        };

        audioRef.value.onpause = () => {};
        audioRef.value.onended = () => {
            cancelAnimationFrame(animationFrameId);
        };
    }
});

onUnmounted(() => {
    cancelAnimationFrame(animationFrameId);
});
</script>
<style lang="scss" scoped>
.container {
    display: flex;
    flex-direction: column;
    align-items: center;

    canvas {
        border: 2px solid rgba(0, 0, 0, 0.5);
    }
}
</style>
