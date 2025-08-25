<template>
  <div class="app">
    <div class="page-inner">
      <h1>NoSQL DataBases Benchmark Dashboard</h1>

      <div class="tabs">
        <button v-for="tab in tabs" :key="tab" @click="currentTab = tab" :class="{ active: currentTab === tab }">
          {{ tab }}
        </button>
      </div>

      <div v-if="currentTab === 'Simulation'">
        <div v-if="loading">Loading config…</div>
        <template v-else>
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
            <p class="status" :data-status="status">Status: {{ status }}</p>
          </div>
        </template>
      </div>

      <div v-else-if="currentTab === 'Performances'">
        <PerformancePage />
      </div>

      <div v-else-if="currentTab === 'Performances2'">
        <PerformancePage2 />
      </div>

      <div v-else-if="currentTab === 'Dashboards'">
        <p>Dashboards tab content (à compléter)</p>
      </div>

      <div v-else-if="currentTab === 'Query'">
        <p>Query tab content (à compléter)</p>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import SimulationPage from './components/SimulationPage.vue'
import PerformancePage from "./components/PerformancePage.vue";
import PerformancePage2 from "./components/PerformancePage2.vue";

const tabs = ['Simulation', 'Performances', 'Performances2', 'Dashboards', 'Query']
const currentTab = ref('Simulation')

const config = ref({
  dbType: 'QUESTDB',
  clearTablesFlag: false,
  retentionWindowMillis: 1000000000,
  rate: 3,
  rateRandomness: 0.8,
  url: 'http://sp-service:8080/api/injection/data',
  nbrSmartMeters: 5000,
  batch: false,
  batchSize: 10,
  batchRandomness: 0.2
})

const loading = ref(true)

function updateConfig(updatedFields) {
  config.value = { ...config.value, ...updatedFields }
}

const status = ref('idle')

const start_url   = 'http://localhost:8080/api/simulation/start'
const stop_url    = 'http://localhost:8080/api/simulation/stop'
const config_url  = 'http://localhost:8080/api/config'
const running_url = 'http://localhost:8080/api/simulation/running'

async function fetchConfig() {
  try {
    const res = await fetch(config_url, { method: 'GET' })
    if (!res.ok) throw new Error(`GET /config ${res.status}`)
    const body = await res.json()
    if (body && typeof body === 'object') {
      config.value = { ...config.value, ...body }
    }
  } catch (e) {
    console.error('Failed to fetch config:', e)
  } finally {
    loading.value = false
  }
}

// lit "running: true/false" et met à jour status
async function fetchRunning() {
  try {
    const res = await fetch(running_url, { method: 'GET' })
    if (!res.ok) throw new Error(`GET /simulation/running ${res.status}`)
    const text = await res.text()
    const isRunning = /running:\s*true/i.test(text)
    status.value = isRunning ? 'running' : 'idle'
  } catch (e) {
    console.error('Failed to fetch running state:', e)
    // en cas d’erreur on ne casse pas l’UI, on garde le status courant
  }
}

async function toggleSimulation() {
  const isStarting = status.value !== 'running'
  const endpoint = isStarting ? start_url : stop_url

  try {
    const response = await fetch(endpoint, {method: 'GET'})
    if (!response.ok) {
      console.error('HTTP error', response.status)
      status.value = 'error'
      return
    }
    // on relit l’état réel côté serveur (au cas où l’action est async)
    await fetchRunning()
  } catch (err) {
    console.error('Network error:', err)
    status.value = 'error'
  }
}

async function sendConfig() {
  try {
    const response = await fetch(config_url, {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
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

let runningInterval = null

onMounted(async () => {
  await Promise.all([fetchConfig(), fetchRunning()])
  // poll léger pour garder l’état bouton/disable synchro
  runningInterval = setInterval(fetchRunning, 5000)
})

onUnmounted(() => {
  if (runningInterval) clearInterval(runningInterval)
})
</script>

<style scoped>
/* Page plus wide, alignée à gauche */
.app {
  width: 100vw;
  padding: 1.5rem 2rem;
  font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, Noto Sans, Arial, "Apple Color Emoji", "Segoe UI Emoji";
}

/* Contenu large mais lisible, aligné à gauche (pas centré) */
.page-inner {
  width: clamp(1000px, 95vw, 1600px);
}

/* Titre plus sobre */
h1 {
  margin: 0 0 1rem 0;
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.01em;
}

/* Tabs plus grandes, left-aligned, look moderne */
.tabs {
  display: flex;
  gap: 0.5rem;
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 0.5rem;
  margin-bottom: 1rem;
}

.tabs button {
  appearance: none;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  color: #111827;
  padding: 0.6rem 1rem;
  font-size: 1rem;
  line-height: 1;
  border-radius: 10px;
  cursor: pointer;
  transition: background 140ms ease, border-color 140ms ease, transform 60ms ease;
}

.tabs button:hover {
  background: #eef2f7;
  border-color: #d1d5db;
}

.tabs button.active {
  background: #111827;
  color: #ffffff;
  border-color: #111827;
}

/* Bloc des contrôles simulation, aligné à gauche et mieux espacé */
.simulation-controls {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.simulation-controls > button {
  appearance: none;
  border: 1px solid #111827;
  background: #111827;
  color: #fff;
  padding: 0.55rem 0.9rem;
  font-size: 0.95rem;
  border-radius: 10px;
  cursor: pointer;
}
.simulation-controls > button:hover {
  filter: brightness(1.08);
}
.simulation-controls > button:active {
  transform: translateY(1px);
}

/* Petit badge de status coloré */
.status {
  margin-left: 0.25rem;
  font-weight: 600;
}
.status[data-status="running"] { color: #059669; } /* green-600 */
.status[data-status="idle"]    { color: #2563eb; } /* blue-600  */
.status[data-status="error"]   { color: #dc2626; } /* red-600   */
</style>

