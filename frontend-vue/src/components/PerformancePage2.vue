

<template>
  <div class="performance-page2">
    <h2>System Performance</h2>

    <div class="grafana-grid">
      <!-- dashboard complet sur 2 colonnes -->
      <iframe
          class="panel-frame full-span"
          src="http://localhost:3000/d/iot-benchmark-dashboard/iot-benchmark?orgId=1&kiosk&refresh=5s"
          frameborder="0"
          allow="fullscreen"
      ></iframe>

      <!-- panels en 2 colonnes en-dessous -->
      <iframe
          v-for="panel in grafanaPanels"
          :key="panel.id"
          :src="panelUrl(panel.id)"
          class="panel-frame"
          frameborder="0"
          allow="fullscreen"
          loading="lazy"
      ></iframe>
    </div>
  </div>
</template>


<script setup>
function panelUrl(id) {
  return `http://localhost:3000/d-solo/iot-benchmark-dashboard/iot-benchmark?orgId=1&panelId=${id}&from=now-5m&to=now&refresh=5s`
}
</script>

<style scoped>
.performance-page2 {
  padding: 1rem;
}
.grafana-panels {
  display: grid;
  gap: 1rem;
}
</style>


<style scoped>
.performance-page2 { max-width: 1600px; }
.grafana-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: 1fr;
}
@media (min-width: 1100px) {
  .grafana-grid {
    grid-template-columns: 1fr 1fr;
  }
}
.panel-frame {
  width: 100%;
  height: 340px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #fff;
  position: relative;
  z-index: 1;
  pointer-events: auto;
}
.full-span {
  grid-column: 1 / -1;   /* s’étend sur les 2 colonnes */
  height: 780px;         /* plus grand pour le dashboard complet */
}
</style>