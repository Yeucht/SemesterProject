<template>
  <div class="simu">
    <div class="header">
      <h2>Simulation</h2>
      <div class="actions">
        <button @click="$emit('send-config')" :disabled="status==='running'">Update Config</button>
        <button @click="$emit('toggle')" :data-status="status">
          {{ status==='running' ? 'Stop Simulation' : 'Start Simulation' }}
        </button>
        <span class="status" :data-status="status">Status: {{ status }}</span>
      </div>
    </div>

    <BlockDiagram :config="config" @update="v => $emit('update-config', v)" />
  </div>
</template>

<script setup>
import BlockDiagram from './BlockDiagram.vue'
defineProps({ config: Object, status: String })
defineEmits(['update-config','send-config','toggle'])
</script>

<style scoped>
.simu{ display:grid; gap:1rem }
.header{ display:flex; align-items:center; justify-content:space-between }
.actions{ display:flex; gap:.5rem; align-items:center }
.actions button{
  appearance:none; border:1px solid #111827; background:#111827; color:#fff;
  padding:.5rem .8rem; border-radius:10px; cursor:pointer;
}
.actions button[data-status="running"]{ background:#b91c1c; border-color:#b91c1c }
.status[data-status="running"]{ color:#059669 }
.status[data-status="idle"]{ color:#2563eb }
.status[data-status="error"]{ color:#dc2626 }
</style>
