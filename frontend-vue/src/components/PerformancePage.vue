<template>
  <div class="performance-page">
    <h2>System Performance</h2>

    <div class="grafana-grid">
      <iframe
          v-for="panel in grafanaPanels"
          :key="panel.id + '-' + refreshTs"
          :src="panelUrl(panel.id)"
          class="panel-frame"
          loading="lazy"
          frameborder="0"
          allow="fullscreen"
      ></iframe>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const grafanaBase = 'http://localhost:3000'
const dashboardUid = 'iot-benchmark-dashboard'
const grafanaPanels = [
  { id: 3, title: 'Inserts History' },
  { id: 4, title: 'CPU History' },
  { id: 5, title: 'RAM History' }
]
const refreshTs = ref(Date.now())

function panelUrl(panelId) {
  // solo panel, fenêtre 5 min par défaut
  return `${grafanaBase}/d-solo/${dashboardUid}/iot-benchmark?orgId=1&panelId=${panelId}&from=now-5m&to=now&refresh=1s&_=${refreshTs.value}`
}
</script>

<style scoped>
.performance-page { max-width: 1600px; }

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

  /* anti “ne répond pas” : s’assure que l’iframe est au-dessus et cliquable */
  position: relative;
  z-index: 1;
  pointer-events: auto;
}
</style>
