<template>
  <div class="app">
    <h1>IoT Benchmark Dashboard</h1>

    <div class="tabs">
      <button v-for="tab in tabs" :key="tab" @click="currentTab = tab" :class="{ active: currentTab === tab }">
        {{ tab }}
      </button>
    </div>

    <div v-if="currentTab === 'Simulation'">
      <SimulationPage
          :config="config"
          :status="status"
          @update-config="updateConfig"
          @send-config="sendConfig"
      />

      <div class="simulation-controls">
        <button @click="toggleSimulation">
          {{ status === 'running' ? 'Stop Simulation' : 'Start Simulation' }}
        </button>
        <p>Status: {{ status }}</p>
      </div>
    </div>

    <div v-else-if="currentTab === 'Performances'">
      <p>Performances tab content (à compléter)</p>
    </div>

    <div v-else-if="currentTab === 'Dashboards'">
      <p>Dashboards tab content (à compléter)</p>
    </div>

    <div v-else-if="currentTab === 'Query'">
      <p>Query tab content (à compléter)</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SimulationPage from './components/SimulationPage.vue'

const tabs = ['Simulation', 'Performances', 'Dashboards', 'Query']
const currentTab = ref('Simulation')

const config = ref({
  dbType: 'QUESTDB',
  clearTablesFlag: false,
  retentionWindowMillis: 1000000000,
  rate: 3,
  rateRandomess: 0.8,
  url: 'http://sp-service:8080/api/injection/data',
  nbrSmartMeters: 5000,
  batch: false,
  batchSize: 10,
  batchRandomness: 0.2
})

function updateConfig(updatedFields) {
  config.value = { ...config.value, ...updatedFields }
}

const status = ref('idle')
const start_url = 'http://localhost:8080/api/simulation/start'
const stop_url = 'http://localhost:8080/api/simulation/stop'
const update_url = 'http://localhost:8080/api/config'

async function toggleSimulation() {
  const isStarting = status.value !== 'running'
  const endpoint = isStarting ? start_url : stop_url

  try {
    const response = await fetch(endpoint, { method: 'GET' })

    if (!response.ok) {
      console.error('HTTP error', response.status)
      status.value = 'error'
      return
    }

    status.value = isStarting ? 'running' : 'idle'
  } catch (err) {
    console.error('Network error:', err)
    status.value = 'error'
  }
}

async function sendConfig() {
  try {
    const response = await fetch(update_url, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(config.value)
    })

    if (!response.ok) {
      console.error('Failed to send config:', response.status)
    } else {
      console.log('Config updated successfully')
    }
  } catch (err) {
    console.error('Error sending config:', err)
  }
}
</script>

<style scoped>
.app {
  padding: 2rem;
  font-family: sans-serif;
}
.tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}
button.active {
  font-weight: bold;
  border-bottom: 2px solid black;
}
.simulation-controls {
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-width: 300px;
}
</style>
