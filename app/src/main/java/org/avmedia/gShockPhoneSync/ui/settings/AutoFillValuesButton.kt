/*
 * Created by Ivo Zivkov (izivkov@gmail.com) on 2022-03-30, 12:06 a.m.
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2022-03-23, 9:38 a.m.
 */

package org.avmedia.gShockPhoneSync.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.alarm_item.view.*
import org.avmedia.gShockPhoneSync.casio.SettingsTransferObject
import org.avmedia.gShockPhoneSync.customComponents.Button
import org.avmedia.gShockPhoneSync.utils.ProgressEvents
import java.util.*


class AutoFillValuesButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener())
        onState()
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    autoFill()
                }
            }
            v?.performClick()
            return false
        }
    }

    private fun autoFill() {
        val settings = AutoConfigurator.configure(context)
        fillLocale(settings)
        fillButtonTone(settings)
        fillLight(settings)
        fillPowerSavingMode(settings)

        updateUI()
    }

    private fun fillPowerSavingMode(settings: SettingsTransferObject) {
        val powerSaveMode = SettingsModel.powerSavingMode as SettingsModel.PowerSavingMode
        powerSaveMode.powerSavingMode = settings.powerSavingMode
    }

    private fun fillLight(settings: SettingsTransferObject) {
        val lightSetting = SettingsModel.light as SettingsModel.Light
        lightSetting.autoLight = settings.autoLight
        if (settings.lightDuration == "2s") {
            lightSetting.duration = SettingsModel.Light.LIGHT_DURATION.TWO_SECONDS
        } else {
            lightSetting.duration = SettingsModel.Light.LIGHT_DURATION.FOUR_SECONDS

        }
    }

    private fun fillButtonTone(settings: SettingsTransferObject) {
        val toneSetting = SettingsModel.buttonSound as SettingsModel.OperationSound
        toneSetting.sound = settings.buttonTone
    }

    private fun fillLocale(settings: SettingsTransferObject) {

        val localeSetting = SettingsModel.locale as SettingsModel.Locale
        when (settings.language) {
            "English" -> localeSetting.dayOfWeekLanguage =
                SettingsModel.Locale.DAY_OF_WEEK_LANGUAGE.ENGLISH
            "Spanish" -> localeSetting.dayOfWeekLanguage =
                SettingsModel.Locale.DAY_OF_WEEK_LANGUAGE.SPANISH
            "French" -> localeSetting.dayOfWeekLanguage =
                SettingsModel.Locale.DAY_OF_WEEK_LANGUAGE.FRENCH
            "German" -> localeSetting.dayOfWeekLanguage =
                SettingsModel.Locale.DAY_OF_WEEK_LANGUAGE.GERMAN
            "Italian" -> localeSetting.dayOfWeekLanguage =
                SettingsModel.Locale.DAY_OF_WEEK_LANGUAGE.ITALIAN
            "Russian" -> localeSetting.dayOfWeekLanguage =
                SettingsModel.Locale.DAY_OF_WEEK_LANGUAGE.RUSSIAN
        }

        if (settings.dateFormat == "DD:MM") {
            localeSetting.dateFormat = SettingsModel.Locale.DATE_FORMAT.DAY_MONTH
        } else {
            localeSetting.dateFormat = SettingsModel.Locale.DATE_FORMAT.MONTH_DAY
        }

        if (settings.timeFormat == "12h") {
            localeSetting.timeFormat = SettingsModel.Locale.TIME_FORMAT.TWELVE_HOURS
        } else {
            localeSetting.timeFormat = SettingsModel.Locale.TIME_FORMAT.TWENTY_FOUR_HOURS
        }
    }

    private fun updateUI() {
        ProgressEvents.onNext(ProgressEvents.Events.NeedToUpdateUI)
    }
}
