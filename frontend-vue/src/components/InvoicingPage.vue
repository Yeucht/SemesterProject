<template>
  <div class="invoicing">
    <h2>Invoicing</h2>

    <div class="form-inline">
      <label>
        Serial Number (1 – {{ nbrMeters ?? '…' }}):
        <input
            type="number"
            min="1"
            :max="nbrMeters || 1"
            v-model.number="serialNumber"
        />
      </label>

      <label>
        Price (CHF/kWh):
        <input type="number" step="0.0001" v-model.number="priceKwh" />
      </label>

      <label>
        Start Date:
        <input type="datetime-local" v-model="startLocal" />
      </label>

      <label>
        End Date:
        <input type="datetime-local" v-model="endLocal" />
      </label>
    </div>

    <!-- (supprimé l’ancien petit hint global ici) -->

    <div class="actions">
      <button @click="fetchInvoice" :disabled="busy">
        {{ busy ? 'Calcul en cours…' : 'Calculer la facture' }}
      </button>
      <!-- (supprimé l’ancien <span class="meters"> … </span>) -->
    </div>


    <p v-if="error" class="error">⚠️ {{ error }}</p>

    <div v-if="invoiceObj" class="result">
      <h3>Résultat</h3>
      <table>
        <thead>
        <tr>
          <th v-for="(label, key) in keyLabels" :key="key">
            {{ label }}
          </th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td v-for="(label, key) in keyLabels" :key="key">
      <span v-if="key === 'dateStart' || key === 'dateEnd'">
        {{ formatDate(invoiceObj[key]) }}
      </span>
            <span v-else-if="key === 'bill'">
        {{ formatNumber(invoiceObj[key], 2) }} CHF
      </span>
            <span v-else>
        {{ invoiceObj[key] }}
      </span>
          </td>
        </tr>
        </tbody>

      </table>
    </div>



  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

const INVOICE_URL     = 'http://localhost:8080/api/extraction/invoice'
const NBR_METERS_URL  = 'http://localhost:8080/api/extraction/nbrMeters'

const serialNumber = ref(1)
const priceKwh = ref(0.1218)
const nbrMeters = ref(null)        // null => inconnu, sinon nombre
const invoiceObj = ref(null)
const busy = ref(false)
const error = ref('')

const keyLabels = {
  serialNumber: "Serial Number",
  dateStart: "Start Date",
  dateEnd: "End Date",
  totalConsumption: "Consumption (kWh)",
  priceKwh: "Price (CHF/kWh)",
  bill: "Total Cost (CHF)"
}

function formatDate(val) {
  if (!val) return ''
  const d = new Date(val)
  if (isNaN(d)) return val   // au cas où le backend renvoie déjà un string
  return d.toLocaleString('fr-CH', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function formatNumber(val, digits = 2) {
  if (val == null || val === '') return ''
  const n = Number(val)
  if (isNaN(n)) return val
  return n.toFixed(digits)
}


// helpers dates (default: now & now-7j)
function toLocalDatetimeInputValue(d) {
  // retourne "YYYY-MM-DDTHH:mm" (sans secondes, en local)
  const pad = n => String(n).padStart(2, '0')
  const yyyy = d.getFullYear()
  const mm = pad(d.getMonth() + 1)
  const dd = pad(d.getDate())
  const hh = pad(d.getHours()-2)
  const mi = pad(d.getMinutes())
  return `${yyyy}-${mm}-${dd}T${hh}:${mi}`
}

const now = new Date()
const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)

const startLocal = ref(toLocalDatetimeInputValue(sevenDaysAgo))
const endLocal   = ref(toLocalDatetimeInputValue(now))

// Convertit la valeur du <input type="datetime-local"> (locale, sans fuseau)
// en ISO 8601 avec timezone (UTC, suffixe Z), attendu par @DateTimeFormat.ISO.DATE_TIME
function localInputToISOStringZ(localStr) {
  // localStr: "YYYY-MM-DDTHH:mm"
  const [datePart, timePart] = localStr.split('T')
  const [y, m, d] = datePart.split('-').map(Number)
  const [h, min] = timePart.split(':').map(Number)
  const dt = new Date(y, m - 1, d, h + 2, min, 0) // interprété en heure locale
  return dt.toISOString() // => avec Z
}

async function fetchNbrMeters() {
  try {
    console.log('[nbrMeters] GET', NBR_METERS_URL)
    let res = await fetch(NBR_METERS_URL, { method: 'GET' })
        if (res.status === 404) {
          console.warn('[nbrMeters] 404 sur URL principale, tentative fallback', NBR_METERS_URL)
          res = await fetch(NBR_METERS_URL, { method: 'GET' })
        }
    if (!res.ok) throw new Error(`GET /nbrMeters ${res.status}`)
    const txt = await res.text()
    const n = Number(txt)
    if (!Number.isFinite(n) || n < 1) throw new Error('réponse invalide')
    nbrMeters.value = n
    // garde le serialNumber dans [1..n]
    if (serialNumber.value < 1) serialNumber.value = 1
    if (serialNumber.value > n) serialNumber.value = n
  } catch (e) {
    // on ne bloque pas l’UI, mais on informe
    error.value = `Impossible de lire le nombre de compteurs (${e.message}).`
  }
}

async function fetchInvoice() {
  error.value = ''
  invoiceObj.value = null
  busy.value = true
  try {
    const startISO = localInputToISOStringZ(startLocal.value)
    const endISO   = localInputToISOStringZ(endLocal.value)

    const url = new URL(INVOICE_URL)
    url.searchParams.set('start', startISO)
    url.searchParams.set('end', endISO)
    url.searchParams.set('serialNumber', String(serialNumber.value))
    url.searchParams.set('priceKwh', String(priceKwh.value))

    const res = await fetch(url.toString(), { method: 'GET' })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    invoiceObj.value = await res.json()
  } catch (e) {
    error.value = `Échec de la récupération de la facture (${e.message}).`
  } finally {
    busy.value = false
  }
}

// auto-refresh nbrMeters toutes les 30s
let metersInterval = null

onMounted(async () => {
  await fetchNbrMeters()
  metersInterval = setInterval(fetchNbrMeters, 30_000)
})

onUnmounted(() => {
  if (metersInterval) clearInterval(metersInterval)
})

// bornes dynamiques si nbrMeters arrive après coup
watch(nbrMeters, n => {
  if (n && serialNumber.value > n) serialNumber.value = n
})
</script>

<style scoped>
.result {
  margin-top: 1rem;
  overflow-x: auto;
}

.result table {
  border-collapse: collapse;
  width: 100%;
  max-width: 100%;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}

.result th{
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  font-size: 0.9rem;
  color: white
}

.result td {
  padding: 0.6rem 0.75rem;
  border-bottom: 1px solid #e5e7eb;
  text-align: left;
  font-size: 0.9rem;
  color: black
}

.result thead {
  background: #111827;
  color: white;
}

.result tbody tr:last-child td {
  border-bottom: none;
}
.form-inline {
  display: grid;
  grid-template-columns: repeat(4, minmax(180px, 1fr));
  gap: 0.75rem 1rem;
  align-items: end;         /* aligne les inputs sur la ligne */
  margin-bottom: 1rem;
  width: 100%;
}

.form-inline label {
  display: flex;
  flex-direction: column;
  font-size: 0.9rem;
  font-weight: 600;
  color: #ffffff;           /* intitulés en blanc */
}

.form-inline input {
  margin-top: 0.35rem;
  padding: 0.45rem 0.6rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9rem;
  width: 100%;              /* occupe toute la colonne */
}

.form-inline input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 2px #2563eb33;
}


/* Optionnel: quand l’espace se réduit, on passe à 2 colonnes puis 1 */
@media (max-width: 1100px) {
  .form-inline { grid-template-columns: repeat(2, minmax(180px, 1fr)); }
}
@media (max-width: 640px) {
  .form-inline { grid-template-columns: 1fr; }
}


</style>
