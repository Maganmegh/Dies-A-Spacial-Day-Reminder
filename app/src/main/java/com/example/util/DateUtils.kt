package com.example.util

import com.example.model.EventEntity
import com.example.model.Language
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

object DateUtils {

    private val MONTH_NAMES_EN = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val MONTH_NAMES_HI = arrayOf(
        "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून",
        "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर"
    )

    private val MONTH_SHORT_EN = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    private val MONTH_SHORT_HI = arrayOf(
        "जन", "फ़र", "मार्च", "अप्रै", "मई", "जून",
        "जुल", "अग", "सितं", "अक्टू", "नव", "दिसं"
    )

    fun getMonthName(month: Int, language: Language = Language.ENGLISH): String {
        val index = (month - 1).coerceIn(0, 11)
        return if (language == Language.HINDI) MONTH_NAMES_HI[index] else MONTH_NAMES_EN[index]
    }

    fun getMonthShort(month: Int, language: Language = Language.ENGLISH): String {
        val index = (month - 1).coerceIn(0, 11)
        return if (language == Language.HINDI) MONTH_SHORT_HI[index] else MONTH_SHORT_EN[index]
    }

    fun getNextOccurrence(day: Int, month: Int, today: LocalDate = LocalDate.now()): LocalDate {
        val currentYear = today.year
        val validDay = adjustForMonth(day, month, currentYear)
        val thisYearDate = LocalDate.of(currentYear, month, validDay)

        return if (!thisYearDate.isBefore(today)) {
            thisYearDate
        } else {
            val nextYear = currentYear + 1
            val nextYearValidDay = adjustForMonth(day, month, nextYear)
            LocalDate.of(nextYear, month, nextYearValidDay)
        }
    }

    private fun adjustForMonth(day: Int, month: Int, year: Int): Int {
        val m = Month.of(month)
        val maxDays = m.length(LocalDate.of(year, 1, 1).isLeapYear)
        return day.coerceIn(1, maxDays)
    }

    fun getDaysUntilNext(day: Int, month: Int, today: LocalDate = LocalDate.now()): Long {
        val nextDate = getNextOccurrence(day, month, today)
        return ChronoUnit.DAYS.between(today, nextDate)
    }

    fun isToday(day: Int, month: Int, today: LocalDate = LocalDate.now()): Boolean {
        return today.dayOfMonth == day && today.monthValue == month
    }

    fun isTomorrow(day: Int, month: Int, today: LocalDate = LocalDate.now()): Boolean {
        val tomorrow = today.plusDays(1)
        return tomorrow.dayOfMonth == day && tomorrow.monthValue == month
    }

    fun getCountdownLabel(day: Int, month: Int, language: Language = Language.ENGLISH): String {
        val today = LocalDate.now()
        val days = getDaysUntilNext(day, month, today)
        return when {
            days == 0L -> if (language == Language.HINDI) "आज है! 🎉" else "TODAY! 🎉"
            days == 1L -> if (language == Language.HINDI) "कल है" else "TOMORROW"
            else -> if (language == Language.HINDI) "$days दिन बाकी" else "$days DAYS LEFT"
        }
    }

    fun getAgeInfo(event: EventEntity, today: LocalDate = LocalDate.now()): AgeCalculation? {
        val birthYear = event.birthYear ?: return null
        val nextOccurrence = getNextOccurrence(event.dateDay, event.dateMonth, today)
        val turningAge = nextOccurrence.year - birthYear
        val currentAge = if (today.isBefore(LocalDate.of(today.year, event.dateMonth, event.dateDay))) {
            turningAge - 1
        } else {
            turningAge
        }
        return AgeCalculation(currentAge = currentAge, turningAge = turningAge)
    }

    fun formatDate(day: Int, month: Int, year: Int?, language: Language = Language.ENGLISH): String {
        val monthStr = getMonthShort(month, language)
        return if (year != null) {
            "$day $monthStr $year"
        } else {
            "$day $monthStr"
        }
    }

    fun getZodiacSign(day: Int, month: Int, language: Language = Language.ENGLISH): String {
        return when (month) {
            1 -> if (day < 20) zodiac("Capricorn", "मकर", language) else zodiac("Aquarius", "कुंभ", language)
            2 -> if (day < 19) zodiac("Aquarius", "कुंभ", language) else zodiac("Pisces", "मीन", language)
            3 -> if (day < 21) zodiac("Pisces", "मीन", language) else zodiac("Aries", "मेष", language)
            4 -> if (day < 20) zodiac("Aries", "मेष", language) else zodiac("Taurus", "वृषभ", language)
            5 -> if (day < 21) zodiac("Taurus", "वृषभ", language) else zodiac("Gemini", "मिथुन", language)
            6 -> if (day < 21) zodiac("Gemini", "मिथुन", language) else zodiac("Cancer", "कर्क", language)
            7 -> if (day < 23) zodiac("Cancer", "कर्क", language) else zodiac("Leo", "सिंह", language)
            8 -> if (day < 23) zodiac("Leo", "सिंह", language) else zodiac("Virgo", "कन्या", language)
            9 -> if (day < 23) zodiac("Virgo", "कन्या", language) else zodiac("Libra", "तुला", language)
            10 -> if (day < 23) zodiac("Libra", "तुला", language) else zodiac("Scorpio", "वृश्चिक", language)
            11 -> if (day < 22) zodiac("Scorpio", "वृश्चिक", language) else zodiac("Sagittarius", "धनु", language)
            12 -> if (day < 22) zodiac("Sagittarius", "धनु", language) else zodiac("Capricorn", "मकर", language)
            else -> ""
        }
    }

    private fun zodiac(en: String, hi: String, language: Language): String {
        return if (language == Language.HINDI) hi else en
    }
}

data class AgeCalculation(
    val currentAge: Int,
    val turningAge: Int
)
