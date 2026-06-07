package com.example.myapplication.core.util

fun Int.formatVnd(): String = "%,d\u0111".format(this).replace(',', '.')
