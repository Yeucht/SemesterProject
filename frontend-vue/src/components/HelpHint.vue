<template>
  <span class="help-hint" @mouseenter="open=true" @mouseleave="open=false">
    <button
        type="button"
        class="help-hint__btn"
        :aria-label="ariaLabel || text"
        @focus="open=true"
        @blur="open=false"
    >
      <!-- (?) -->
      <svg viewBox="0 0 20 20" class="help-hint__icon" aria-hidden="true">
        <circle cx="10" cy="10" r="9" />
        <text x="10" y="14" text-anchor="middle" font-weight="700">?</text>
      </svg>
    </button>

    <span
        v-show="open"
        class="help-hint__bubble"
        role="tooltip"
        :style="{ maxWidth }"
    >
      {{ text }}
      <i class="help-hint__arrow" aria-hidden="true"></i>
    </span>
  </span>
</template>

<script setup>
import { ref } from 'vue'

const open = ref(false)
defineProps({
  text: String,
  maxWidth: { type: [String, Number], default: '260px' },
  asHtml: { type: Boolean, default: false },
})
</script>

<style scoped>
.help-hint{ position:relative; display:inline-flex; }
.help-hint__btn{
  display:inline-grid; place-items:center;
  width:18px; height:18px; border:0; border-radius:50%;
  cursor:help; padding:0; background:transparent;
}
.help-hint__icon{
  width:18px; height:18px;
  fill:#e2e8f0; /* fond du rond */
  stroke:#1f2937; stroke-width:1.2;
}
.help-hint__icon text{ font: 700 12px/1 'Inter', system-ui; fill:#1f2937; }

.help-hint__bubble{
  position:absolute; z-index:50; top:-8px; left:24px;
  transform: translateY(-50%);
  background:#0f172a; color:#f8fafc;
  border:1px solid #0b1220; border-radius:8px;
  padding:.45rem .55rem; font-size:.85rem; line-height:1.25;
  box-shadow:0 8px 24px rgba(0,0,0,.2);
  display:inline-block;
  width: max-content;
  min-width: 140px;
  white-space: normal;
  overflow-wrap: break-word;
  word-break: normal;
}
.help-hint__arrow{
  position:absolute; top:50%; left:-6px; width:10px; height:10px;
  background:#0f172a; border-left:1px solid #0b1220; border-top:1px solid #0b1220;
  transform: translateY(-50%) rotate(45deg);
}
.help-hint__btn:focus-visible{
  outline: none;
  box-shadow:0 0 0 3px rgba(37,99,235,.26);
  border-radius:50%;
}

.help-hint__content{
  white-space:pre-line;
}
</style>

