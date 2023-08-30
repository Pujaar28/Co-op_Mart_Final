package com.pujaad.coopmart.view.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import java.util.Calendar


class DateDialog {
    companion object {
        fun createDialogWithoutDateField(ctx: Context, listener: DatePickerDialog.OnDateSetListener): DatePickerDialog {
            val date = Calendar.getInstance()
            val dpd = DatePickerDialog(ctx, listener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
            try {
                val datePickerDialogFields = dpd.javaClass.declaredFields
                for (datePickerDialogField in datePickerDialogFields) {
                    if (datePickerDialogField.name == "mDatePicker") {
                        datePickerDialogField.isAccessible = true
                        val datePicker = datePickerDialogField[dpd] as DatePicker
                        val datePickerFields = datePickerDialogField.type.declaredFields
                        for (datePickerField in datePickerFields) {

                            if ("mDaySpinner" == datePickerField.name) {
                                datePickerField.isAccessible = true
                                val dayPicker = datePickerField[datePicker]
                                (dayPicker as View).visibility = View.GONE
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return dpd
        }

        fun createTimePickerDialog(
            ctx: Context,
            listener: TimePickerDialog.OnTimeSetListener
        ): TimePickerDialog {
            val date = Calendar.getInstance()
            return TimePickerDialog(ctx, listener, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true)
        }
    }
}