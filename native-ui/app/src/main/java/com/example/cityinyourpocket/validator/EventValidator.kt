package com.example.cityinyourpocket.validator

import com.example.cityinyourpocket.model.Event

class EventValidator(private val toBeValidated: Event) {

    fun eventNameIsOk(): Boolean{
        return toBeValidated.name.isNotEmpty()
    }

    fun startDateIsValid(): Boolean{
        /*
        Start date is valid if it is in the format
          yyyy-mm-dd

        * */
        val regex1 = Regex("^[1-9][0-9]{3}-[0-9][1-9]-[0-9][1-9]$")
        val regex3 = Regex("^[1-9][0-9]{3}-[0-9][1-9]-[1-9][0-9]\$")
        val regex2 = Regex("^[1-9][0-9]{3}-[1-9][0-9]-[1-9][0-9]\$")
        val regex4 = Regex("^[1-9][0-9]{3}-[1-9][0-9]-[0-9][1-9]\$")
        return regex1.matches(toBeValidated.startDate)
                || regex2.matches(toBeValidated.startDate)
                || regex3.matches(toBeValidated.startDate)
                || regex4.matches(toBeValidated.startDate)
    }

    fun endDateIsValid(): Boolean{
        val regex1 = Regex("^[1-9][0-9]{3}-[0-9][1-9]-[0-9][1-9]$")
        val regex3 = Regex("^[1-9][0-9]{3}-[0-9][1-9]-[1-9][0-9]\$")
        val regex2 = Regex("^[1-9][0-9]{3}-[1-9][0-9]-[1-9][0-9]\$")
        val regex4 = Regex("^[1-9][0-9]{3}-[1-9][0-9]-[0-9][1-9]\$")
        return regex1.matches(toBeValidated.endDate)
                || regex2.matches(toBeValidated.endDate)
                || regex3.matches(toBeValidated.endDate)
                || regex4.matches(toBeValidated.endDate)
    }

    fun hourIsValid(): Boolean{
        /*
            Time Format HH:MM 12-hour, optional leading 0
            /^(0?[1-9]|1[0-2]):[0-5][0-9]$/
            Time Format HH:MM 12-hour, optional leading 0, Meridiems (AM/PM)
            /((1[0-2]|0?[1-9]):([0-5][0-9]) ?([AaPp][Mm]))/
            Time Format HH:MM 24-hour with leading 0
            /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/
            Time Format HH:MM 24-hour, optional leading 0
            /^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/
            Time Format HH:MM:SS 24-hour
            /(?:[01]\d|2[0123]):(?:[012345]\d):(?:[012345]\d)/
            Credit:
            https://digitalfortress.tech/tricks/top-15-commonly-used-regex/
         */
        val regex1 = Regex("^(0?[1-9]|1[0-2]):[0-5][0-9]\$")
        val regex2 = Regex("((1[0-2]|0?[1-9]):([0-5][0-9]) ?([AaPp][Mm]))")
        val regex3 = Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")
        val regex4 = Regex("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")
        val regex5 = Regex("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")
        return regex1.matches(toBeValidated.openHour) ||
                regex2.matches(toBeValidated.openHour) ||
                regex3.matches(toBeValidated.openHour) ||
                regex4.matches(toBeValidated.openHour) ||
                regex5.matches(toBeValidated.openHour)
    }

    fun datesAreOk(): Boolean{
        return toBeValidated.endDate > toBeValidated.startDate;
    }
}