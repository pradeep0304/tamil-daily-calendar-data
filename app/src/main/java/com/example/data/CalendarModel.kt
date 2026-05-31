package com.example.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

data class CalendarDayInfo(
    val gregorianDate: LocalDate,
    val tamilMonth: String,
    val tamilDate: Int,
    val tithi: String,
    val nakshatram: String,
    val nallaNeramMorning: String,
    val nallaNeramEvening: String,
    val gowriNallaNeramMorning: String,
    val gowriNallaNeramEvening: String,
    val rahuKaalam: String,
    val kuligai: String,
    val yemagandam: String,
    val saniRahu: String,
    val soolam: String,
    val parigaram: String,
    val karanam: String,
    val festivalName: String?,
    val festivalDetail: String?,
    val isGovernmentHoliday: Boolean = false,
    val rasi: String = "ரிஷபம்",
    val isAmavasai: Boolean = false,
    val isPournami: Boolean = false
)

object CalendarModel {

    // Tithis Cycle
    private val tithis = listOf(
        "பிரதமை", "துவிதி", "திருதியை", "சதுர்த்தி", "பஞ்சமி",
        "சஷ்டி", "சப்தமி", "அஷ்டமி", "நவமி", "தசமி",
        "ஏகாதசி", "துவாதசி", "திரயோதசி", "சதுர்தசி", "பௌர்ணமி",
        "பிரதமை (தேய்பிறை)", "துவிதி (தேய்பிறை)", "திருதியை (தேய்பிறை)", "சதுர்த்தி (தேய்பிறை)", "பஞ்சமி (தேய்பிறை)",
        "சஷ்டி (தேய்பிறை)", "சப்தமி (தேய்பிறை)", "அஷ்டமி (தேய்பிறை)", "நவமி (தேய்பிறை)", "தசமி (தேய்பிறை)",
        "ஏகாதசி (தேய்பிறை)", "துவாதசி (தேய்பிறை)", "திரயோதசி (தேய்பிறை)", "சதுர்தசி (தேய்பிறை)", "அமாவாசை"
    )

    // Nakshatrams Cycle
    private val nakshatrams = listOf(
        "அசுவினி", "பரணி", "கார்த்திகை", "ரோகிணி", "மிருகசீரிடம்",
        "திருவாதிரை", "புனர்பூசம்", "பூசம்", "ஆயில்யம்", "மகம்",
        "பூரம்", "உத்திரம்", "அஸ்தம்", "சித்திரை", "சுவாதி",
        "விசாகம்", "அனுஷம்", "கேட்டை", "மூலம்", "பூராடம்",
        "உத்திராடம்", "திருவோணம்", "அவிட்டம்", "சதயம்", "பூரட்டாதி",
        "உத்திரட்டாதி", "ரேவதி"
    )

    // Rasis
    private val rasis = listOf(
        "மேஷம்", "ரிஷபம்", "மிதுனம்", "கடகம்", "சிம்மம்", "கன்னி",
        "துலாம்", "விருச்சிகம்", "தனுசு", "மகரம்", "கும்பம்", "மீனம்"
    )

    fun getTamilDayInfo(date: LocalDate): CalendarDayInfo {
        val dayOfWeek = date.dayOfWeek
        val dayOfMonth = date.dayOfMonth
        val month = date.month

        // Dynamic Tamil Month & Day calculation
        val (tamilMonthName, tamilDay) = calculateTamilDate(date)

        // Deterministic cycle based on epoch days for Tithi, Nakshatram, and Rasi
        val epochDays = date.toEpochDay()
        
        // --- Tithi (Moon Phase) Calculation ---
        // Synodic month is ~29.530588 days.
        // We calibrate to a known Full Moon: May 31, 2026 (epoch day 20604).
        // Index 14 in 'tithis' is Pournami.
        val synodicMonth = 29.530588
        val knownFullMoonEpoch = 20604.0 
        var moonAge = (epochDays - knownFullMoonEpoch + (14.5 * synodicMonth / 30.0)) % synodicMonth
        if (moonAge < 0) moonAge += synodicMonth
        
        val tithiIndex = ((moonAge / synodicMonth) * 30).toInt().coerceIn(0, 29)
        val selectedTithi = tithis[tithiIndex]

        // --- Nakshatram (Moon position in Sky) Calculation ---
        // Sidereal month is ~27.321661 days.
        // May 31 2026 is roughly Anuradha (Anusham) - index 16.
        val siderealMonth = 27.321661
        val knownNakshatramEpoch = 20604.0
        var nakshatraAge = (epochDays - knownNakshatramEpoch + (16.5 * siderealMonth / 27.0)) % siderealMonth
        if (nakshatraAge < 0) nakshatraAge += siderealMonth
        
        val nakshatramIndex = ((nakshatraAge / siderealMonth) * 27).toInt().coerceIn(0, 26)
        val selectedNakshatram = nakshatrams[nakshatramIndex]

        val rasiIndex = ((epochDays - 20233L + 1) % 12 + 12) % 12
        val selectedRasi = rasis[rasiIndex.toInt()]

        // Rahu, Yamagandam, Kuligai are deterministic by Day of Week
        val (rahu, yema, kuli) = getAstroTimings(dayOfWeek)
        val nallaNeramMorning = getNallaNeramMorning(dayOfWeek)
        val nallaNeramEvening = getNallaNeramEvening(dayOfWeek)
        val gowriNallaNeramMorning = getGowriNallaNeramMorning(dayOfWeek)
        val gowriNallaNeramEvening = getGowriNallaNeramEvening(dayOfWeek)

        val soolam = getSoolam(dayOfWeek)
        val parigaram = getParigaram(soolam)
        val karanam = getKaranam(dayOfWeek)

        // Festival check
        val (festName, festDetail, isGovHoliday) = getFestival(date, tamilMonthName, tamilDay, selectedTithi)

        return CalendarDayInfo(
            gregorianDate = date,
            tamilMonth = tamilMonthName,
            tamilDate = tamilDay,
            tithi = selectedTithi,
            nakshatram = selectedNakshatram,
            nallaNeramMorning = nallaNeramMorning,
            nallaNeramEvening = nallaNeramEvening,
            gowriNallaNeramMorning = gowriNallaNeramMorning,
            gowriNallaNeramEvening = gowriNallaNeramEvening,
            rahuKaalam = rahu,
            kuligai = kuli,
            yemagandam = yema,
            saniRahu = getSaniRahuSymbol(dayOfWeek),
            soolam = soolam,
            parigaram = parigaram,
            karanam = karanam,
            festivalName = festName,
            festivalDetail = festDetail,
            isGovernmentHoliday = isGovHoliday,
            rasi = selectedRasi,
            isAmavasai = selectedTithi == "அமாவாசை",
            isPournami = selectedTithi == "பௌர்ணமி"
        )
    }

    private fun calculateTamilDate(date: LocalDate): Pair<String, Int> {
        val month = date.month
        val day = date.dayOfMonth

        return when (month) {
            Month.JANUARY -> {
                if (day >= 14) Pair("தை", day - 13)
                else Pair("மார்கழி", day + 16)
            }
            Month.FEBRUARY -> {
                if (day >= 13) Pair("மாசி", day - 12)
                else Pair("தை", day + 18)
            }
            Month.MARCH -> {
                if (day >= 14) Pair("பங்குனி", day - 13)
                else Pair("மாசி", day + 16)
            }
            Month.APRIL -> {
                if (day >= 14) Pair("சித்திரை", day - 13)
                else Pair("பங்குனி", day + 17)
            }
            Month.MAY -> {
                if (day >= 15) Pair("வைகாசி", day - 14)
                else Pair("சித்திரை", day + 17)
            }
            Month.JUNE -> {
                if (day >= 15) Pair("ஆனி", day - 14)
                else Pair("வைகாசி", day + 16)
            }
            Month.JULY -> {
                if (day >= 17) Pair("ஆடி", day - 16)
                else Pair("ஆனி", day + 16)
            }
            Month.AUGUST -> {
                if (day >= 17) Pair("ஆவணி", day - 16)
                else Pair("ஆடி", day + 14)
            }
            Month.SEPTEMBER -> {
                if (day >= 17) Pair("புரட்டாசி", day - 16)
                else Pair("ஆவணி", day + 15)
            }
            Month.OCTOBER -> {
                if (day >= 18) Pair("ஐப்பசி", day - 17)
                else Pair("புரட்டாசி", day + 14)
            }
            Month.NOVEMBER -> {
                if (day >= 17) Pair("கார்த்திகை", day - 16)
                else Pair("ஐப்பசி", day + 13)
            }
            Month.DECEMBER -> {
                if (day >= 16) Pair("மார்கழி", day - 15)
                else Pair("கார்த்திகை", day + 14)
            }
        }
    }

    private fun getAstroTimings(day: DayOfWeek): Triple<String, String, String> {
        // Returns Triple(Rahu, Yamagandam, Kuligai)
        return when (day) {
            DayOfWeek.SUNDAY -> Triple("04:30 - 06:00", "12:00 - 01:30", "03:00 - 04:30")
            DayOfWeek.MONDAY -> Triple("07:30 - 09:00", "10:30 - 12:00", "01:30 - 03:00")
            DayOfWeek.TUESDAY -> Triple("03:00 - 04:30", "09:00 - 10:30", "12:00 - 01:30")
            DayOfWeek.WEDNESDAY -> Triple("12:00 - 01:30", "07:30 - 09:00", "10:30 - 12:00")
            DayOfWeek.THURSDAY -> Triple("01:30 - 03:00", "06:00 - 07:30", "09:00 - 10:30")
            DayOfWeek.FRIDAY -> Triple("10:30 - 12:00", "03:00 - 04:30", "07:30 - 09:00")
            DayOfWeek.SATURDAY -> Triple("09:00 - 10:30", "01:30 - 03:00", "06:00 - 07:30")
        }
    }

    private fun getNallaNeramMorning(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "07:30 - 08:30"
            DayOfWeek.MONDAY -> "06:30 - 07:30"
            DayOfWeek.TUESDAY -> "07:30 - 08:30"
            DayOfWeek.WEDNESDAY -> "09:15 - 10:15"
            DayOfWeek.THURSDAY -> "10:30 - 11:30"
            DayOfWeek.FRIDAY -> "09:30 - 10:30"
            DayOfWeek.SATURDAY -> "07:30 - 08:30"
        }
    }

    private fun getNallaNeramEvening(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "03:30 - 04:30"
            DayOfWeek.MONDAY -> "04:30 - 05:30"
            DayOfWeek.TUESDAY -> "04:30 - 05:30"
            DayOfWeek.WEDNESDAY -> "04:45 - 05:45"
            DayOfWeek.THURSDAY -> "-"
            DayOfWeek.FRIDAY -> "04:30 - 05:30"
            DayOfWeek.SATURDAY -> "04:30 - 05:30"
        }
    }

    private fun getGowriNallaNeramMorning(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "02:00 - 03:00"
            DayOfWeek.MONDAY -> "09:30 - 10:30"
            DayOfWeek.TUESDAY -> "10:30 - 11:30"
            DayOfWeek.WEDNESDAY -> "12:00 - 01:00"
            DayOfWeek.THURSDAY -> "12:00 - 01:00"
            DayOfWeek.FRIDAY -> "12:00 - 01:00"
            DayOfWeek.SATURDAY -> "10:30 - 11:30"
        }
    }

    private fun getGowriNallaNeramEvening(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "07:30 - 08:30"
            DayOfWeek.MONDAY -> "07:30 - 08:30"
            DayOfWeek.TUESDAY -> "10:30 - 11:30"
            DayOfWeek.WEDNESDAY -> "09:30 - 10:30"
            DayOfWeek.THURSDAY -> "07:30 - 08:30"
            DayOfWeek.FRIDAY -> "09:30 - 10:30"
            DayOfWeek.SATURDAY -> "07:30 - 08:30"
        }
    }

    private fun getSoolam(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.MONDAY, DayOfWeek.SATURDAY -> "கிழக்கு"
            DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> "வடக்கு"
            DayOfWeek.THURSDAY -> "தெற்கு"
            DayOfWeek.FRIDAY, DayOfWeek.SUNDAY -> "மேற்கு"
        }
    }

    private fun getParigaram(soolam: String): String {
        return when (soolam) {
            "கிழக்கு" -> "தயிர்"
            "வடக்கு" -> "பால்"
            "தெற்கு" -> "எண்ணெய்"
            "மேற்கு" -> "வெல்லம்"
            else -> "தண்ணீர்"
        }
    }

    private fun getKaranam(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "10:30 - 12 (சகுனி)"
            DayOfWeek.MONDAY -> "09:00 - 10:30 (சிங்கம்)"
            DayOfWeek.TUESDAY -> "12:30 - 02:00 (புலி)"
            DayOfWeek.WEDNESDAY -> "11:00 - 12:30 (பன்றி)"
            DayOfWeek.THURSDAY -> "08:30 - 10:00 (கழுதை)"
            DayOfWeek.FRIDAY -> "14:00 - 15:30 (யானை)"
            DayOfWeek.SATURDAY -> "15:30 - 17:00 (காளை)"
        }
    }

    private fun getSaniRahuSymbol(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY, DayOfWeek.SATURDAY -> "மிது-சுக்"
            else -> "கட-புதன்"
        }
    }

    private fun getFestival(date: LocalDate, tamilMonth: String, tamilDay: Int, selectedTithi: String): Triple<String?, String?, Boolean> {
        // High fidelity festival checker
        // Returns Triple(Festival Name, Festival Detail, Is Government Holiday)

        // Gregorian dates check (fixed holidays)
        if (date.month == Month.JANUARY && date.dayOfMonth == 26) {
            return Triple("குடியரசு தினம் (Republic Day)", "இந்திய அரசியலமைப்பின் வரலாற்று சிறப்புமிக்க நாள்", true)
        }
        if (date.month == Month.AUGUST && date.dayOfMonth == 15) {
            return Triple("சுதந்திர தினம் (Independence Day)", "இந்திய நாட்டின் விடுதலைத் திருநாள்", true)
        }
        if (date.month == Month.OCTOBER && date.dayOfMonth == 2) {
            return Triple("காந்தி ஜெயந்தி (Gandhi Jayanthi)", "தேசப்பிதா மகாத்மா காந்தியின் பிறந்தநாள்", true)
        }
        if (date.month == Month.DECEMBER && date.dayOfMonth == 25) {
            return Triple("கிறிஸ்துமஸ் (Christmas)", "இயேசு கிறிஸ்து பிறந்த தின பெருவிழா", true)
        }

        // Tamil calendar based checks (traditional festivals)
        when (tamilMonth) {
            "தை" -> {
                if (tamilDay == 1) {
                    return Triple("தைப்பொங்கல் (Pongal)", "உழவர் திருநாள், சூரிய பகவானுக்கு நன்றி செலுத்தும் மங்கல விழா", true)
                }
                if (tamilDay == 2) {
                    return Triple("மாட்டுப் பொங்கல் (Mattu Pongal)", "உழவுக்கு உறுதுணையாய் நின்ற கால்நடைகளை போற்றும் திருநாள்", false)
                }
                if (tamilDay == 3) {
                    return Triple("காணும் பொங்கல் (Kaanum Pongal)", "உற்றார் உறவினர்களை கண்டு மகிழ்ந்து விளையாடும் திருநாள்", false)
                }
                if (tamilDay == 15) {
                    return Triple("தைப்பூசம் (Thaipusam)", "முருகப்பெருமானுக்கு உகந்த தைப்பூசம் திருவிழா", true)
                }
            }
            "மாசி" -> {
                if (tamilDay == 10) {
                    return Triple("மாசி மகம் (Masi Magam)", "புண்ணிய தீர்த்தங்களில் புனித நீராடி இறைவனை வழிபடும் திருநாள்", false)
                }
                if (tamilDay == 24) {
                    return Triple("மகா சிவராத்திரி (Maha Shivaratri)", "இறைவன் சிவபெருமான் அருள் புரியும் புண்ணிய இரவு விரத நாள்", true)
                }
            }
            "பங்குனி" -> {
                if (tamilDay == 15) {
                    return Triple("பங்குனி உத்திரம் (Panguni Uthiram)", "தெய்வீக திருமணங்கள் நடைபெற்ற மங்கலமான நாள்", false)
                }
            }
            "சித்திரை" -> {
                if (tamilDay == 1) {
                    return Triple("தமிழ்ப் புத்தாண்டு (Tamil New Year)", "சித்திரை வருடப்பிறப்பு, புதிய பஞ்சாங்கம் வாசிக்கும் நன்னாள்", true)
                }
                if (tamilDay == 27) {
                    return Triple("சித்திரைத் திருவிழா (Chithirai Festival)", "மதுரை மீனாட்சி சொக்கநாதர் திருக்கல்யாணப் பெருவிழா", false)
                }
            }
            "வைகாசி" -> {
                if (tamilDay == 11) {
                    return Triple("வைகாசி ஏகாதசி", "ஸ்ரீ ரங்கநாதர் உற்சவம், பகவத் ஸ்மரணம் மற்றும் விரதம் இருக்க உகந்த நாள்", false)
                }
                if (tamilDay == 15) {
                    return Triple("வைகாசி விசாகம் (Vaikasi Visakam)", "முருக பெருமானின் அவதாரத் திருநாள் விரத வழிபாடுகள்", false)
                }
            }
            "ஆனி" -> {
                if (tamilDay == 15) {
                    return Triple("ஆனி உத்திரம் (Aani Uthiram)", "சிதம்பரம் நடராஜ பெருமானின் திருமஞ்சனப் பெருவிழா", false)
                }
            }
            "ஆடி" -> {
                if (tamilDay == 18) {
                    return Triple("ஆடிப் பெருக்கு (Aadi Perukku)", "காவிரி நதிக்கரைகளில் நீர் பெருக்கிற்கு நன்றி கூறும் மங்கல விழா", false)
                }
                if (tamilDay == 15) {
                    return Triple("ஆடி கிருத்திகை (Aadi Krithigai)", "முருகப்பெருமானுக்கு உகந்த ஆடி கிருத்திகை விரத நாள்", false)
                }
            }
            "ஆவணி" -> {
                if (tamilDay == 11) {
                    return Triple("ஆவணி அவிட்டம் (Avani Avittam)", "வைதீக சடங்குகள் மற்றும் பூணூல் அணியும் புண்ணிய தினம்", false)
                }
                if (tamilDay == 15) {
                    return Triple("விநாயகர் சதுர்த்தி (Ganesha Chaturthi)", "முழுமுதற் கடவுள் விநாயகர் பெருமான் பிறந்த போற்றி விழா", true)
                }
            }
            "புரட்டாசி" -> {
                if (tamilDay == 10) {
                    return Triple("புரட்டாசி சனிக்கிழமை (Purattasi Saturday)", "திருப்பதி வெங்கடாசலபதிக்கு உகந்த விரத சனிக்கிழமை", false)
                }
            }
            "ஐப்பசி" -> {
                if (tamilDay == 13) {
                    return Triple("ஆயுத பூஜை (Ayudha Puja)", "கல்வி, தொழில்களுக்குரிய உபகரணங்களை வைத்து பூஜிக்கும் நன்னாள்", true)
                }
                if (tamilDay == 14) {
                    // Match Diwali
                    return Triple("தீபாவளி (Deepavali)", "நரகாசூரனை வென்று இருள் நீங்கி ஒளி வீசும் நன்னாள்", true)
                }
            }
            "கார்த்திகை" -> {
                if (tamilDay == 15) {
                    return Triple("கார்த்திகை தீபம் (Karthigai Deepam)", "திருவண்ணாமலையில் மகா தீபம் மற்றும் இல்லங்களில் தீப ஒளி விழா", false)
                }
            }
            "மார்கழி" -> {
                if (tamilDay == 11) {
                    return Triple("வைகுண்ட ஏகாதசி (Vaikunda Ekadasi)", "சொர்க்கவாசல் திறப்பு மற்றும் திருமால் ஆராதனை திருநாள்", false)
                }
                if (tamilDay == 30) {
                    return Triple("போகிப் பண்டிகை (Bhogi Festival)", "பழையன கழித்து புதியன புகுத்தும் தைப்பிறப்புக்கு முந்தைய நாள்", false)
                }
            }
        }

        // Fallbacks for general lunar days (Tithis) that are traditionally auspicious etc.
        if (selectedTithi == "ஏகாதசி" || selectedTithi == "ஏகாதசி (தேய்பிறை)") {
            return Triple("ஏகாதசி விரதம் (Ekadasi Vrat)", "மாதாந்திர விஷ்ணு ஆராதனை மற்றும் உண்ணாநோன்பு இருக்கும் நன்னாள்", false)
        }
        if (selectedTithi == "சதுர்த்தி") {
            return Triple("சங்கடஹர சதுர்த்தி", "விநாயகப் பெருமானை வழிபட்டு இடர்களைப் போக்கும் சதுர்த்தி விரதம்", false)
        }
        if (selectedTithi == "சஷ்டி" || selectedTithi == "சஷ்டி (தேய்பிறை)") {
            return Triple("சஷ்டி விரதம் (Sashti Vrat)", "பாலமுருகனை வேண்டி உண்ணா நோன்பு உகந்த சஷ்டி தினம்", false)
        }
        if (selectedTithi == "பௌர்ணமி") {
            return Triple("பௌர்ணமி கிரிவலம் (Pournami)", "முழு நிலவு நாளில் திருத்தலங்களில் கிரிவலம் மற்றும் அம்பாள் ஆராதனை", false)
        }
        if (selectedTithi == "அமாவாசை") {
            return Triple("அமாவாசை தரிசனம் (Amavasai)", "முன்னோர்களுக்கு தர்பணம் மற்றும் வழிபாடுகள் செய்யும் புண்ணிய தினம்", false)
        }

        return Triple(null, null, false)
    }

    // Generator helper for any specific Month/Year to render lists nicely
    fun getFestivalsForYear(year: Int): List<CalendarDayInfo> {
        val list = mutableListOf<CalendarDayInfo>()
        var current = LocalDate.of(year, 1, 1)
        val end = LocalDate.of(year, 12, 31)
        while (!current.isAfter(end)) {
            val info = getTamilDayInfo(current)
            if (info.festivalName != null) {
                list.add(info)
            }
            current = current.plusDays(1)
        }
        return list
    }
}
