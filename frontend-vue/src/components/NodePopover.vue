<template>
  <div
      class="popover"
      :style="{ left: x + 'px', top: y + 'px', minWidth }"
      @click.stop
  >
    <header class="popover__header">
      <strong>{{ title }}</strong>
      <button class="icon" @click="$emit('close')">✕</button>
    </header>
    <div class="popover__body">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  x: { type: Number, required: true },
  y: { type: Number, required: true },
  minWidth: { type: String, default: '380px' },
})
defineEmits(['close'])
</script>

<style scoped>
.popover{
  position:absolute; z-index:20;
  background:#e2e8f0;               /* gris clair du popover */
  border:1px solid #cbd5e1;
  border-radius:14px;
  box-shadow:0 12px 30px rgba(0,0,0,.14);
  padding:.75rem;
}
.popover, .popover *{
  color:#0f172a;
  font-size:0.95rem;
  text-align:left;
}
.popover__header{
  display:flex; align-items:center; justify-content:space-between;
  padding:.25rem .35rem .5rem;
  border-bottom:1px solid #dbe3ea;
}
.popover__body{
  padding:.65rem .35rem .35rem;
  display:grid; gap:.75rem;
}

/* le contenu du <slot> vient du parent → on cible via :deep() */
.popover :deep(label){
  display:flex; align-items:center; gap:.6rem; justify-content:flex-start;
}

/* ✅ Inputs lisibles (fond blanc) même si fournis par le parent */
.popover :deep(input[type="number"]),
.popover :deep(input[type="text"]),
.popover :deep(select),
.popover :deep(textarea){
  background:#ffffff;
  color:#0f172a;
  border:1px solid #d1d5db;
  border-radius:8px;
  padding:.35rem .5rem;
  font-variant-numeric: tabular-nums;
}

.popover :deep(input[type="number"]:focus),
.popover :deep(input[type="text"]:focus),
.popover :deep(select:focus),
.popover :deep(textarea:focus){
  outline: none;
  border-color:#2563eb;
  box-shadow:0 0 0 3px rgba(37,99,235,.18);
  background:#ffffff;
}

.toggle-center{ justify-content:center !important; text-align:center !important }
.icon{ border:0; background:transparent; cursor:pointer; font-size:14px }
</style>
