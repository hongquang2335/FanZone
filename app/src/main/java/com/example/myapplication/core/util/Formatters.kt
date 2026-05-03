package com.example.myapplication.core.util

fun formatVnd(price: Int): String = "%,d\u0111".format(price).replace(',', '.')

