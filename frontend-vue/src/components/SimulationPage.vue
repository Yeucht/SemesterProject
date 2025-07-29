<template>
  <div class="simulation-settings">
    <h2>Simulation Settings</h2>

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
        <input type="number" step = 0.2 v-model.number="localConfig.rate" @input="emitUpdate" /> inserts/sec
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

    <div class="update-config">
      <button @click="emit('send-config')" :disabled="status === 'running'">
        Update Config
      </button>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
const props = defineProps({
  config: Object,
  status: String
})
const emit = defineEmits(['update-config', 'send-config'])

const dbTypes = ['IOTDB', 'INFLUXDB', 'QUESTDB']
const localConfig = reactive({ ...props.config })

function emitUpdate() {
  emit('update-config', { ...localConfig })
}
</script>

<style scoped>
.simulation-settings {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 600px;
}
label {
  display: block;
  margin-bottom: 0.5rem;
}
section {
  border: 1px solid #ccc;
  padding: 1rem;
  border-radius: 6px;
}
.update-config {
  margin-top: 1rem;
}
</style>
