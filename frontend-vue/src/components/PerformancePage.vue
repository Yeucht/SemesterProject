

<template>
  <div class="performance-page">
    <h2>System Performance</h2>

    <div class="grafana-panels">
      <iframe
          src="http://localhost:3000/d/iot-benchmark-dashboard/iot-benchmark?orgId=1&kiosk&refresh=1s"
          width="100%"
          height="900"
          frameborder="0"
      ></iframe>
    </div>
  </div>
</template>

<script setup>
const grafanaBaseUrl = 'http://localhost:3000'; // ou l'URL Grafana de ton setup
const dashboardUid = 'iot-benchmark-dashboard'; // trouvé dans l'URL de ton dashboard Grafana
const grafanaPanels = [
  { id: 1, title: 'CPU Usage (%)' },
  { id: 2, title: 'Memory Used (bytes)' },
  { id: 3, title: 'Total Inserted Rows' }
];

function generatePanelUrl(panelId) {
  return `${grafanaBaseUrl}/d-solo/${dashboardUid}/iot-performance?orgId=1&panelId=${panelId}&refresh=5s`;
}
</script>

<style scoped>
.performance-page {
  padding: 1rem;
}
.grafana-panels {
  display: grid;
  gap: 1rem;
}
</style>


<style scoped>

</style>