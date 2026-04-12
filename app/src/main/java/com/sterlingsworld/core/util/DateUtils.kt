package com.sterlingsworld.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE

/** Returns today's date as "yyyy-MM-dd" in the device's local time zone. */
fun localDateStamp(): String = LocalDate.now().format(DATE_FORMATTER)
