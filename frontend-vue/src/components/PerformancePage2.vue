<template>
  <div class="performance-page2">
    <h2>System Performance</h2>

    <div class="toolbar">
      <label>
        Dashboard:
        <select v-model="selectedUid">
          <option v-for="d in dashboards" :key="d.uid" :value="d.uid">{{ d.title }}</option>
        </select>
      </label>

      <label>
        Instance (var):
        <input v-model="instanceVar" placeholder="All" />
      </label>

      <label v-if="showPool">
        Pool (var):
        <input v-model="poolVar" placeholder="All" />
      </label>

      <label>
        Default range:
        <select v-model="defaultRange">
          <option value="now-5m">Last 5m</option>
          <option value="now-1h">Last 1h</option>
          <option value="now-6h">Last 6h</option>
          <option value="now-24h">Last 24h</option>
        </select>
      </label>

      <label>
        Refresh:
        <select v-model="refreshEvery">
          <option value="5s">5s</option>
          <option value="10s">10s</option>
          <option value="30s">30s</option>
          <option value="1m">1m</option>
          <option value="">Off</option>
        </select>
      </label>

      <button @click="bump">Reload</button>
    </div>

    <div class="dashboard-frame-wrap">
      <iframe
          :key="selectedUid + '-' + cacheBust"
          :src="dashboardUrl"
          class="dashboard-frame"
          loading="lazy"
          frameborder="0"
          allow="fullscreen"
      ></iframe>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const grafanaBase = 'http://localhost:3000' // adapte si besoin
const dashboards = [
  { uid: 'sp-overview', title: 'Overview' },
  { uid: 'sp-jvm', title: 'JVM' },
  { uid: 'sp-db', title: 'DB (Hikari)' },
  { uid: 'sp-http', title: 'HTTP' },
  { uid: 'sp-host', title: 'Host/System' }
]

const selectedUid = ref(dashboards[0].uid)
const instanceVar = ref('All') // sera passé en ?var-instance=...
const poolVar = ref('All')     // ?var-pool=...
const defaultRange = ref('now-1h')
const refreshEvery = ref('5s')
const cacheBust = ref(Date.now())

const showPool = computed(() => selectedUid.value === 'sp-db') // le var pool n’est utile que pour DB

function bump() {
  cacheBust.value = Date.now()
}

const dashboardUrl = computed(() => {
  // slug: peut être n’importe quoi; Grafana ne s’en sert que pour l’URL lisible
  const slug = {
    'sp-overview': 'sp-overview',
    'sp-jvm': 'sp-jvm',
    'sp-db': 'sp-db',
    'sp-http': 'sp-http',
    'sp-host': 'sp-host'
  }[selectedUid.value] || 'sp'

  const params = new URLSearchParams({
    orgId: '1',
    from: defaultRange.value,
    to: 'now'
  })

  if (refreshEvery.value) params.set('refresh', refreshEvery.value)
  if (instanceVar.value) params.set('var-instance', instanceVar.value)
  if (showPool.value && poolVar.value) params.set('var-pool', poolVar.value)

  // on embarque le dashboard complet (pas d-solo) pour garder zoom/timepicker/variables
  params.set('kiosk', '1') // masque le menu gauche + topbar
  return `${grafanaBase}/d/${selectedUid.value}/${slug}?${params.toString()}&_=${cacheBust.value}`
})
</script>

<style scoped>
.performance-page { max-width: 1600px; margin: 0 auto; }
.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: .75rem;
  align-items: center;
  margin-bottom: 1rem;
}
.toolbar label { display: flex; gap: .5rem; align-items: center; }
.dashboard-frame-wrap {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}
.dashboard-frame {
  width: 100%;
  height: 900px; /* ajuste selon la hauteur de tes dashboards */
  position: relative;
  z-index: 1;
  pointer-events: auto;
}
</style>
