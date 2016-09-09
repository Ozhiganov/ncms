package com.softmotions.ncms.mtt.tp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.google.inject.Singleton
import com.softmotions.commons.cont.KVOptions
import com.softmotions.commons.lifecycle.Start
import com.softmotions.commons.re.RegexpHelper
import com.softmotions.kotlin.toDays
import com.softmotions.ncms.events.NcmsEventBus
import com.softmotions.web.cookie
import com.softmotions.web.decodeValue
import com.softmotions.web.setEncodedValue
import com.softmotions.weboot.mb.MBDAOSupport
import org.apache.commons.lang3.StringUtils
import org.apache.ibatis.session.SqlSession
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * @author Adamansky Anton (adamansky@gmail.com)
 */
@Singleton
@JvmSuppressWildcards
open class MttTpService
@Inject
constructor(val sess: SqlSession,
            val mapper: ObjectMapper,
            val ebus: NcmsEventBus) : MBDAOSupport(MttTpRS::class.java, sess) {

    companion object {

        private val log = LoggerFactory.getLogger(MttTpService::class.java)

        private val MATCHED_TPS_COOKIE_KEY = "_tps";

        private val MATCHED_TPS_REQ_KEY = MttTpRS::class.java.name + "_tps"
    }

    // Parameter name => parameter value => TpSlot
    private val pmap: MutableMap<String, MutableMap<String, TpSlot>> = HashMap()

    // TpSlot id => TpSlot
    private val imap: MutableMap<Long, TpSlot> = HashMap()

    private val lock = ReentrantReadWriteLock()


    init {
        ebus.register(this)
    }

    //
    // 0,=43434,983,9289,32,44
    // name1=pvalue1
    // name2=pvalue2
    //
    internal class InjectedTps(cval: String = "") {

        internal var modified = false

        private val tpmap: MutableMap<String, MutableCollection<String>>

        init {

            val kvo = KVOptions(cval)
            for ((k, v) in kvo) {
                k as String
                v as String
                if (k == "0,") {
                    kvo[k] = StringUtils.split(v, ',').toMutableSet()
                } else {
                    kvo[k] = mutableListOf(v)
                }
            }
            tpmap = kvo as MutableMap<String, MutableCollection<String>>
        }

        internal fun syncTParams(tp: TpSlot, pmap: Map<String, Array<String>>) {
            for (pn in tp.tParams) {
                val pv = pmap[pn]
                if (pv != null && pv.size > 0) { //todo review
                    val parr = tpmap.getOrPut(pn, {
                        modified = true
                        mutableListOf(pv[0])
                    }) as MutableList<String>
                    if (parr.firstOrNull() != pv[0]) {
                        modified = true
                        parr[0] = pv[0]
                    }
                }
            }
        }

        internal fun addTp(tp: TpSlot, pmap: Map<String, Array<String>>) {
            if (!tp.enabled) {
                return
            }
            val sid = tp.id.toString()
            val sids = tpmap.getOrPut("0,", {
                modified = true
                ArrayList(8)
            })
            if (!sids.contains(sid)) {
                modified = true
                sids += sid
            }
            syncTParams(tp, pmap)
        }

        internal operator fun contains(tp: TpSlot): Boolean {
            val sids = tpmap["0,"] ?: return false
            return tp.id.toString() in sids
        }

        internal fun writeTo(req: HttpServletRequest, resp: HttpServletResponse) {
            if (tpmap.isEmpty()) {
                return
            }
            req.setAttribute(MATCHED_TPS_REQ_KEY, this)
            val amap = KVOptions()
            for ((k, v) in tpmap) {
                amap.put(k, v.joinToString(","))
            }
            val cookie = Cookie(MATCHED_TPS_COOKIE_KEY, null)
            if (log.isDebugEnabled) {
                log.debug("Set cookie {}={}", MATCHED_TPS_COOKIE_KEY, amap.toString())
            }
            cookie.setEncodedValue(amap.toString())
            cookie.maxAge = 1.toDays().toSeconds().toInt()
            resp.addCookie(cookie)
        }

        override fun toString(): String {
            return "InjectedTps(modified=${modified}, tpmap=${tpmap})"
        }
    }

    private fun loadTrackingPixels(req: HttpServletRequest): InjectedTps {
        val tps = (req.getAttribute(MATCHED_TPS_REQ_KEY) as InjectedTps?)
                ?: InjectedTps(req.cookie(MATCHED_TPS_COOKIE_KEY)?.decodeValue() ?: "")
        if (log.isDebugEnabled) {
            log.debug("loadTrackingPixels={}", tps)
        }
        return tps
    }

    fun injectTrackingPixels(req: HttpServletRequest, resp: HttpServletResponse) {

        val tps = loadTrackingPixels(req)
        val rpMap = req.parameterMap

        lock.read {

            for ((pn, pvs) in rpMap) {
                val pnl = pn.toLowerCase()

                // Matching against a raw string parameters
                pmap[pnl]?.let {
                    for (pv in pvs) {
                        it[pv]?.let {
                            if (log.isDebugEnabled) {
                                log.debug("Tps matched {}={}, tps={}", pnl, pv, it)
                            }
                            tps.addTp(it, rpMap)
                        }
                    }
                }

                // Matching against a regexps
                for (pv in pvs) {
                    for (it in imap.values) {
                        val re = it.rParams[pnl]
                        if (re == null || !it.enabled) {
                            continue
                        }
                        if (it in tps) {
                            tps.syncTParams(it, rpMap)
                            continue
                        }
                        if (re.matches(pv)) {
                            if (log.isDebugEnabled) {
                                log.debug("Tps matched re={} {}={}, tps={}", re, pnl, pv, it)
                            }
                            tps.addTp(it, rpMap)
                        }
                    }
                }
            }
        }

        if (tps.modified) {
            tps.writeTo(req, resp)
        }
    }

    private fun activateTp(tp: MttTp) {
        val ns = TpSlot(tp, mapper)
        if (log.isDebugEnabled) {
            log.debug("activateTp={}", tp)
        }
        lock.write {
            imap[ns.id]?.let {
                tpDeleted(it.id)
            }
            imap[ns.id] = ns
            for ((pn, pv) in ns.sParams) {
                pmap.getOrPut(pn, {
                    HashMap<String, TpSlot>()
                }).getOrPut(pv, {
                    ns
                })
            }
        }
    }

    private fun activateTp(id: Long) {
        val tp: MttTp? = selectOne("selectTpById", id)
        if (tp != null) {
            activateTp(tp)
        }
    }

    @Subscribe
    fun tpDeleted(ev: MttTpDeletedEvent) {
        tpDeleted(ev.tpId)
    }

    fun tpDeleted(id: Long) {
        lock.write {
            imap.remove(id)?.let {
                for (m in pmap.values) {
                    m.keys.toTypedArray().forEach {
                        val slot = m[it]
                        if (slot!!.id == id) {
                            if (log.isDebugEnabled) {
                                log.debug("tpDeleted={}", it)
                            }
                            m.remove(it)
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    fun tpUpdated(ev: MttTpUpdatedEvent) = activateTp(ev.tpId)

    @Start(order = 100)
    fun start() {
        for (tp in select<MttTp>("selectAll")) {
            activateTp(tp)
        }
    }

    internal class TpSlot(val tp: MttTp, mapper: ObjectMapper) {

        internal val id: Long
            get() = tp.id

        internal val enabled: Boolean
            get() = tp.isEnabled

        internal val spec: ObjectNode

        // Parameter name => raw parameter string value
        internal val sParams = HashMap<String, String>()

        // Parameter name => parameter regexp value
        internal val rParams = HashMap<String, Regex>()

        // Transferred parameters what will be saved for this TP
        // in the client's cookie
        internal val tParams = HashSet<String>()

        init {

            spec = mapper.readTree(tp.spec) as ObjectNode
            // {"params":"utm_source=yandex","url":"http://sm.ru?","jscode":"eew\nwqwqwwq\n\n\n\n\n"}
            for ((pn, pv) in KVOptions(spec.path("params").asText())) {
                pn as String
                pv as String
                if (StringUtils.containsAny("?*{}", pv)) {
                    rParams[pn.toLowerCase()] = Regex("^" + RegexpHelper.convertGlobToRegEx(pv) + "$")
                } else {
                    sParams[pn.toLowerCase()] = pv
                }
            }
            spec.path("tparams").asText().split(',').filter {
                it.isNotBlank()
            }.forEach {
                val pname = it.trim().toLowerCase()
                if (!pname.startsWith("0,")) {
                    tParams += pname
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            other as TpSlot
            if (tp != other.tp) return false
            return true
        }

        override fun hashCode(): Int {
            return tp.hashCode()
        }

        override fun toString(): String {
            return "TpSlot(tp=$tp)"
        }
    }
}