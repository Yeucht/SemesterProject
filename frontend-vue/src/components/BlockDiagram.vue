<template>
  <div class="diagram" @click="closePop()">
    <svg viewBox="0 0 1200 600" class="svg">
      <defs>
        <!-- arrows -->
        <marker id="arrowHead" markerWidth="12" markerHeight="8" refX="10" refY="4" orient="auto" markerUnits="strokeWidth">
          <polygon points="0,0 12,4 0,8" fill="#1f2937" />
        </marker>
        <!-- text style -->
        <style>
          .t-title{ font: 700 14px/1 'Inter', system-ui; fill:#111827 }
          .t-sub{ font: 12px/1 'Inter', system-ui; fill:#475569 }
        </style>
      </defs>

      <!-- === IMAGES Smart meters === -->
      <!-- HES Smart Meters -->
      <image
          class="hot img"
          :href="imgHesMeters"
          :x="imgHesMetersRect.x" :y="imgHesMetersRect.y"
          :width="imgHesMetersRect.w" :height="imgHesMetersRect.h"
          :style="{ opacity: config.hesFlag ? 1 : 0.55 }"
          @click.stop="open('hesMeters', $event)"
          preserveAspectRatio="xMidYMid meet"
      />
      <!-- Individual Smart Meters -->
      <image
          class="hot img"
          :href="imgMeters"
          :x="imgMetersRect.x" :y="imgMetersRect.y"
          :width="imgMetersRect.w" :height="imgMetersRect.h"
          :style="{ opacity: config.meterFlag ? 1 : 0.55 }"
          @click.stop="open('meters', $event)"
          preserveAspectRatio="xMidYMid meet"
      />

      <!-- === BLOCS === -->

      <!-- HES  -->
      <g class="hot" @click.stop="open('hes', $event)">
        <rect
            class="hitbox"
            :x="hesRect.x" :y="hesRect.y-28"
            :width="hesRect.w" :height="hesRect.h+44"
        />
        <text class="t-title" :x="centerX(hesRect)" :y="hesRect.y+20" text-anchor="middle" font-weight="700">
          Head-End Systems ({{ config.nbrHES }})
        </text>
        <image
            class="img"
            :href="imgServers"
            :x="hesRect.x" :y="hesRect.y"
            :width="hesRect.w" :height="hesRect.h"
            preserveAspectRatio="xMidYMid meet"
            :style="{ opacity: config.hesFlag ? 1 : 0.55 }"
        />
      </g>

      <!-- MDMS -->
      <g @click.stop="open('mdms', $event)" class="hot">
        <rect class="box-mdms" :x="mdmsRect.x" :y="mdmsRect.y" :width="mdmsRect.w" :height="mdmsRect.h" rx="16"/>
        <text class="t-title" :x="centerX(mdmsRect)" :y="mdmsRect.y+28" text-anchor="middle" font-weight="700">
          Meter Data Management System
        </text>
      </g>

      <!-- DB -->
      <g class="hot" @click.stop="open('db', $event)">
        <rect
            class="hitbox"
            :x="dbRect.x" :y="dbRect.y-28"
            :width="dbRect.w" :height="dbRect.h+40"
        />
        <image
            class="img"
            :href="imgDb"
            :x="dbRect.x" :y="dbRect.y"
            :width="dbRect.w" :height="dbRect.h"
            preserveAspectRatio="xMidYMid meet"
        />
        <text class="t-title" :x="centerX(dbRect)" :y="dbRect.y" text-anchor="middle" font-weight="700">Database</text>
      </g>

      <!-- LEGEND/labels images-->
      <text class="t-title" :x="centerX(imgMetersRect)" :y="imgMetersRect.y-10" text-anchor="middle" font-weight="700">
        Individual Smart Meters ({{ config.nbrSmartMeters }})
      </text>

      <text class="t-title" :x="centerX(imgHesMetersRect)" :y="imgHesMetersRect.y-10" text-anchor="middle" font-weight="700">
        HES Smart Meters (~{{ config.nbrMetersPerHES }} / HES)
      </text>

      <!-- === FLÈCHES === -->
      <!-- meters indiv -> MDMS -->
      <path class="arrow" :d="edgeRightToLeft(imgMetersRect, mdmsRect)" marker-end="url(#arrowHead)"
            :style="{ opacity: config.meterFlag ? 1 : 0.55 }"/>
      <!-- HES meters -> HES -->
      <path class="arrow" :d="edgeRightToLeft(imgHesMetersRect, hesRect)" marker-end="url(#arrowHead)"
            :style="{ opacity: config.hesFlag ? 1 : 0.55 }"/>
      <!-- HES -> MDMS -->
      <path class="arrow" :d="edgeBottomToTop(hesRect, mdmsRect)" marker-end="url(#arrowHead)"
            :style="{ opacity: config.hesFlag ? 1 : 0.55 }"/>
      <!-- MDMS -> DB -->
      <path class="arrow" :d="edgeRightToLeft(mdmsRect, dbRect)" marker-end="url(#arrowHead)"/>
    </svg>

    <!-- === POPOVERS === -->
    <NodePopover
        v-if="openKey==='meters'"
        title="Individual Smart Meters"
        :x="pop.x" :y="pop.y"
        @close="closePop"
    >
      <label class="toggle-center">
        <input type="checkbox" v-model="local.meterFlag" @change="emitUpdate" />
        Activate
      </label>

      <fieldset :disabled="!local.meterFlag">
        <label>
          Number of Smart Meters
          <input
              type="number" min="1" step="100"
              v-model.number="local.nbrSmartMeters"
              @input="emitUpdate"
              :style="autoWidth(local.nbrSmartMeters)"
          />
        </label>

        <label>
          Send rate
          <input
              type="number" min="0" step="1"
              v-model.number="local.meterRate"
              @input="emitUpdate"
              :style="autoWidth(local.meterRate)"
          /> /h
        </label>

        <label>
          Rate randomness (0...1)
          <HelpHint
              text="The time interval between two packets will be drawn from U[(1-x)*t, (1+x)*t], where t is the interval associated with Send Rate."
          />
          <input
              type="number" min="0" max="1" step="0.05"
              v-model.number="local.meterRateRandomness"
              @input="emitUpdate"
              :style="autoWidth(local.meterRateRandomness)"
          />
        </label>

        <label>
          Probe rate
          <input
              type="number" min="0" step="1"
              v-model.number="local.probeRate"
              @input="emitUpdate"
              :style="autoWidth(local.probeRate)"
          /> /h
        </label>
      </fieldset>
    </NodePopover>

    <NodePopover
        v-if="openKey==='hesMeters'"
        title="HES-side Smart Meters"
        :x="pop.x" :y="pop.y"
        @close="closePop"
    >
      <fieldset :disabled="!local.hesFlag">
        <label>
          Average number per HES
          <input
              type="number" min="1" step="1"
              v-model.number="local.nbrMetersPerHES"
              @input="emitUpdate"
              :style="autoWidth(local.nbrMetersPerHES)"
          />
        </label>

        <label>
          Associated randomness
          <HelpHint
              text="U[(1-x)*N, (1+x)*N] where N is the number of meters per HES."
          />
          <input
              type="number" min="0" max="1" step="0.05"
              v-model.number="local.nbrMetersPerHESRandomness"
              @input="emitUpdate"
              :style="autoWidth(local.nbrMetersPerHESRandomness)"
          />
        </label>

        <br>
        <label>
          Send rate
          <input
              type="number" min="0" step="1"
              v-model.number="local.hesMeterRate"
              @input="emitUpdate"
              :style="autoWidth(local.hesMeterRate)"
          /> /h
        </label>

        <label>
          Probe rate (>= Send rate)
          <input
              type="number" min="0" step="1"
              v-model.number="local.hesProbeRate"
              @input="emitUpdate"
              :style="autoWidth(local.hesProbeRate)"
          /> /h
        </label>

        <label>
          Rate randomness (0...1)
          <HelpHint
              text="The time interval between two packets sent to the HES will be drawn from U[(1-x)*t, (1+x)*t], where t is the interval associated with Send Rate."
          />
          <input
              type="number" min="0" max="1" step="0.05"
              v-model.number="local.hesMeterRateRandomness"
              @input="emitUpdate"
              :style="autoWidth(local.hesMeterRateRandomness)"
          />
        </label>
      </fieldset>
      <small v-if="!local.hesFlag" style="opacity:.8">Enable HES to edit these settings.</small>
    </NodePopover>

    <NodePopover
        v-if="openKey==='hes'"
        title="Head-End Systems"
        :x="pop.x" :y="pop.y"
        @close="closePop"
    >
      <label class="toggle-center">
        <input type="checkbox" v-model="local.hesFlag" @change="emitUpdate" />
        Activate
      </label>

      <fieldset :disabled="!local.hesFlag">
        <label>
          # HES
          <input
              type="number" min="1" step="10"
              v-model.number="local.nbrHES"
              @input="emitUpdate"
              :style="autoWidth(local.nbrHES)"
          />
        </label>

        <label>
          HES send rate
          <input
              type="number" min="1" step="1"
              v-model.number="local.hesRate"
              @input="emitUpdate"
              :style="autoWidth(local.hesRate)"
          /> /day
        </label>

        <label>
          HES synchronized
          <input type="checkbox" v-model="local.hesSynchronized" @change="emitUpdate">
        </label>

        <label>
          Schedule randomness (0..100)
          <HelpHint
              text="0 means perfectly split within the time interval associated with HES Send Rate. 100 means randomly drawn over the interval (uniform). Values inbetween are setting a linear interpolation."
              max-width="400px"
          />
          <input
              type="number" min="0" max="100" step="1"
              v-model.number="local.hesRateRandomness"
              @input="emitUpdate"
              :style="autoWidth(local.hesRateRandomness)"
              :disabled="local.hesSynchronized"
              :title="local.hesSynchronized ? 'Disabled when synchronized is ON' : ''"
          />
        </label>
      </fieldset>
    </NodePopover>

    <NodePopover
        v-if="openKey==='mdms'"
        title="Meter Data Management System"
        :x="pop.x" :y="pop.y"
        @close="closePop"
    >
      <label class="toggle-center">
        <input type="checkbox" v-model="local.mdmsBatch" @change="emitUpdate" />
        Activate buffer
      </label>

      <label>
        Buffer size (rows)
        <HelpHint
            text="Number of rows in the app-side buffer. Note that QuestDB has a time buffer of 1hour that can be disabled in QuestDBInjection class"
        />
        <input
            type="number" min="0" step="10"
            v-model.number="local.mdmsBatchSize"
            @input="emitUpdate"
            :style="autoWidth(local.mdmsBatchSize)"
            :disabled="!local.mdmsBatch"
        />
      </label>
    </NodePopover>

    <NodePopover v-if="openKey==='db'" title="Database" :x="pop.x" :y="pop.y" minWidth="240px" @close="closePop">
      <label>Type
        <select v-model="local.dbType" @change="emitUpdate">
          <option value="IOTDB">IOTDB</option>
          <option value="QUESTDB">QUESTDB</option>
        </select>
      </label>
      <label><input type="checkbox" v-model="local.clearTablesFlag" @change="emitUpdate"> Clear tables before simulation</label>
    </NodePopover>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import NodePopover from './NodePopover.vue'
import imgMeters from '../assets/smartmeters.png?url'
import imgHesMeters from '../assets/smartmeters.png?url'
import imgDb from '../assets/database.png?url'
import imgServers from '../assets/servers.png?url'
import HelpHint from './HelpHint.vue'

const props = defineProps({ config: { type: Object, required: true }})
const emit = defineEmits(['update'])

const local = reactive({ ...props.config })
watch(() => props.config, v => Object.assign(local, v || {}))
function emitUpdate(){ emit('update', { ...local }) }

/*positions*/
const imgHesMetersRect = reactive({ x: 60,  y: 100,  w: 220, h: 120 })
const imgMetersRect     = reactive({ x: 60,  y: 355, w: 220, h: 120 })
const hesRect           = reactive({ x: 460, y: 60,  w: 330, h: 200 })
const mdmsRect          = reactive({ x: 460, y: 330, w: 330, h: 170 })
const dbRect            = reactive({ x: 980, y: 335,  w: 160, h: 160 })

/*helpers */
const centerX = r => r.x + r.w/2
const centerY = r => r.y + r.h/2

function edgeRightToLeft(from, to){
  const x1 = from.x + from.w
  const y1 = centerY(from)
  const x2 = to.x - 5
  const y2 = centerY(to)
  return `M ${x1} ${y1} L ${x2} ${y2}`
}
function edgeBottomToTop(from, to){
  const x1 = centerX(from)
  const y1 = from.y - 40 + from.h
  const x2 = centerX(to)
  const y2 = to.y - 5
  return `M ${x1} ${y1} L ${x2} ${y2}`
}

/*popovers*/
const openKey = ref(null)
const pop = reactive({ x:0, y:0 })
function open(key, ev){
  openKey.value = key
  const wrap = ev.currentTarget.closest('.diagram')
  const r = wrap.getBoundingClientRect()
  pop.x = ev.clientX - r.left + 8
  pop.y = ev.clientY - r.top + 8
}
function closePop(){ openKey.value = null }

/*Auto width on inputs */
function autoWidth(v, min = 5, max = 14) {
  const s = String(v ?? '');
  const len = s.replace('-', '').length || 1;
  const w = Math.min(max, Math.max(min, len + 2));
  return { width: `${w}ch` };
}
</script>

<style scoped>
.diagram{ position:relative; background:#f8fafc; border:1px solid #e5e7eb; border-radius:16px; padding:.5rem }
.svg{ width:100%; height:auto; display:block }

.box{ fill:#f1f5f9; stroke:#1f2937; stroke-width:1.2 }
.box-mdms{ fill:#e2e8f0; stroke:#1f2937; stroke-width:1.2 }
.box--off{ opacity:.55 }

.arrow{ fill:none; stroke:#1f2937; stroke-width:2; pointer-events:none } /* ⬅︎ ne bloque pas les clics */
.img{ filter: drop-shadow(0 2px 6px rgba(0,0,0,.08)); }
.hot{ cursor:pointer }
.svg .hitbox{ fill:transparent; pointer-events:all }      /* ⬅︎ zone cliquable étendue */
</style>
