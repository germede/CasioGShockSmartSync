package org.avmedia.gshockapi.casio

import org.avmedia.gshockapi.utils.Utils

object CasioTimeZone {
    class WorldCity(private val city: String, val index: Int) {
        fun createCasioString(): String {
            return ("1F" + "%02x".format(index) + Utils.toHexStr(city.take(18)).padEnd(40, '0'))
        }
    }

    object TimeZoneHelper {
        fun parseCity(timeZone: String): String {
            return try {
                val city = timeZone.split('/')[1]
                city.uppercase().replace('_', ' ')
            } catch (e: Error) {
                ""
            }
        }
    }

    fun setHomeTime(timeZone: String) {
        val city = TimeZoneHelper.parseCity(timeZone)

        var worldCity = WorldCity(city, 0)
        CasioIO.writeCmd(0xE,
            // "1f004e444a414d454e4100000000000000000000"
            // "1f01544f524f4e544f0000000000000000000000"
            worldCity.createCasioString()
        )
    }
}