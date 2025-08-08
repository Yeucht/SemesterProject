<template>
  <div class="simulation-settings">
    <h2>Simulation Settings</h2>

    <div class="settings-grid">
      <section>
        <h3>Database choice</h3>
        <label v-for="type in dbTypes" :key="type">
          <input type="radio" :value="type" v-model="localConfig.dbType" @change="emitUpdate" />
          {{ type }}
        </label>
      </section>

      <section>
        <h3>Data insertion</h3>
        <label>
          <input type="checkbox" v-model="localConfig.clearTablesFlag" @change="emitUpdate" />
          Clear Data before
        </label>
        <label>
          Insert rate:
          <input type="number" step="0.2" v-model.number="localConfig.rate" @input="emitUpdate" /> inserts/sec
        </label>
        <label>
          Rate randomness:
          <input type="number" step="0.1" v-model.number="localConfig.rateRandomess" @input="emitUpdate" />
        </label>
        <label>
          Number of Smart Meters:
          <input type="number" v-model.number="localConfig.nbrSmartMeters" @input="emitUpdate" />
        </label>
      </section>

      <section>
        <h3>Batch Mode</h3>
        <label>
          <input type="checkbox" v-model="localConfig.batch" @change="emitUpdate" />
          Enable batching
        </label>
        <div v-if="localConfig.batch">
          <label>
            Batch size:
            <input type="number" v-model.number="localConfig.batchSize" @input="emitUpdate" />
          </label>
          <label>
            Batch randomness:
            <input type="number" step="0.1" v-model.number="localConfig.batchRandomness" @input="emitUpdate" />
          </label>
        </div>
      </section>

      <section>
        <h3>Retention window</h3>
        <label>
          Length (ms):
          <input type="number" v-model.number="localConfig.retentionWindowMillis" @input="emitUpdate" />
        </label>
      </section>
    </div>

    <div class="update-config">
      <button @click="emit('send-config')" :disabled="status === 'running'">
        Update Config
      </button>
    </div>
  </div>
</template>


<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  config: Object,
  status: String
})
const emit = defineEmits(['update-config', 'send-config'])

const dbTypes = ['IOTDB', 'INFLUXDB', 'QUESTDB']
const localConfig = reactive({ ...props.config })

watch(
    () => props.config,
    (v) => {
      if (v && typeof v === 'object') {
        Object.assign(localConfig, v)
      }
    },
    { deep: false, immediate: true }
)

function emitUpdate() {
  emit('update-config', { ...localConfig })
}
</script>

<style scoped>
.settings-grid {
  display: grid;
  gap: 1rem;
  grid-template-columns: 1fr;
}

@media (min-width: 1100px) {
  .settings-grid {
    grid-template-columns: 1fr 1fr;
  }
}

section {
  border: 1px solid #444;
  padding: 1rem;
  border-radius: 10px;
  background: #98a1d6; /* fond clair */
  color: #000;
}

section h3 {
  margin-top: 0;
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

label {
  display: flex;
  align-items: center;
  justify-content: flex-start; /* aligne tout à gauche */
  gap: 0.5rem;
  margin-bottom: 0.6rem;
  font-size: 0.9rem;
}

/* taille uniforme pour les champs texte/number */
label input[type="number"],
label input[type="text"] {
  width: 6rem;
  padding: 0.3rem 0.4rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}

/* Ticks (checkbox, radio) à gauche, texte à droite */
label input[type="checkbox"],
label input[type="radio"] {
  order: 0; /* input en premier */
}


</style>

