package org.avmedia.gshockapi.apiIO

import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import org.avmedia.gshockapi.Alarm
import org.avmedia.gshockapi.ble.Connection
import org.avmedia.gshockapi.casio.AlarmDecoder
import org.avmedia.gshockapi.casio.BluetoothWatch
import org.avmedia.gshockapi.utils.Utils
import org.json.JSONObject
import timber.log.Timber
import java.util.ArrayList

object AlarmsIO {

    suspend fun request(): ArrayList<Alarm> {
        return ApiIO.request("GET_ALARMS", ::getAlarms) as ArrayList<Alarm>
    }

    private suspend fun getAlarms(key: String): ArrayList<Alarm> {
        Connection.sendMessage("{ action: '$key'}")

        Alarm.clear()

        var deferredResult = CompletableDeferred<ArrayList<Alarm>>()
        ApiIO.resultQueue.enqueue(
            ResultQueue.KeyedResult(
                key, deferredResult as CompletableDeferred<Any>
            )
        )

        ApiIO.subscribe("ALARMS") { keyedData ->
            val data = keyedData.getString("value")
            val key = "GET_ALARMS"

            fun fromJson(jsonStr: String) {
                val gson = Gson()
                val alarmArr = gson.fromJson(jsonStr, Array<Alarm>::class.java)
                Alarm.alarms.addAll(alarmArr)
            }

            fromJson(data)

            if (Alarm.alarms.size > 1) {
                ApiIO.resultQueue.dequeue(key)?.complete(Alarm.alarms)
            }
        }
        return deferredResult.await()
    }

    fun set(alarms: ArrayList<Alarm>) {
        if (alarms.isEmpty()) {
            Timber.d("Alarm model not initialised! Cannot set alarm")
            return
        }

        @Synchronized
        fun toJson(): String {
            val gson = Gson()
            return gson.toJson(alarms)
        }

        // remove from cache
        ApiIO.cache.remove("GET_ALARMS")

        Connection.sendMessage("{action: \"SET_ALARMS\", value: ${toJson()} }")
    }

    fun toJson (data:String): JSONObject {
        return JSONObject().put(
            "ALARMS",
            JSONObject().put("value", AlarmDecoder.toJson(data).get("ALARMS"))
                .put("key", "GET_ALARMS")
        )
    }
}