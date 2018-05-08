import java.text.SimpleDateFormat

import java.util.Calendar

val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

val currentDate: java.util.Date = new java.util.Date()
System.out.println(dateFormat.format(currentDate))

// convert date to calendar
val c: Calendar = Calendar.getInstance

//
//val today = dateFormat.format()

//today