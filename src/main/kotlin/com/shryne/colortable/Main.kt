package com.shryne.colortable

import org.jsoup.Jsoup

val alreadyExistingColors = setOf("Black", "Pearl Dark Gray", "Pearl Light Gray",
    "Dark Gray", "Dark Bluish Gray", "Light Gray", "Light Bluish Gray",
    "Very Light Gray", "Metallic Silver", "Flat Silver", "Glow In Dark White",
    "White", "Sand Red", "Red", "Dark Red", "Dark Brown", "Brown", "Dark Orange",
    "Medium Nougat", "Pearl Gold", "Nougat", "Dark Tan", "Coral", "Tan", "Light Nougat",
    "Orange", "Medium Orange", "Bright Light Orange", "Yellow", "Bright Light Yellow",
    "Purple", "Magenta", "Medium Lavender", "Dark Pink", "Bright Pink", "Pink",
    "Lavender", "Dark Purple", "Dark Blue", "Blue", "Dark Azure",
    "Medium Blue", "Sand Blue", "Maersk Blue", "Sky Blue", "Medium Azure",
    "Bright Light Blue", "Aqua", "Light Aqua", "Dark Turquoise", "Dark Green",
    "Green", "Bright Green", "Sand Green", "Olive Green", "Lime", "Medium Lime",
    "Yellowish Green", "Glow In Dark Opaque", "Trans-Black",
    "Glow In Dark Trans", "Trans-Clear", "Trans-Red", "Trans-Neon Orange", "Trans-Orange",
    "Trans-Yellow", "Trans-Neon Yellow", "Trans-Purple",
    "Trans-Dark Pink", "Trans-Dark Blue", "Trans-Medium Blue",
    "Trans-Light Blue", "Trans-Green", "Trans-Bright Green", "Trans-Neon Green",
    "Chrome Gold", "Chrome Silver"
)

fun main() {
    //println(Unirest.get()
    //    .asString().body)

    val html =
        Jsoup.connect("https://www.bricklink.com/catalogColors.asp?sortBy=N")
            .get()

    val table = html.body().getElementById("id-main-legacy-table")
    val rows =
        table!!.getElementsByTag("table")[3].getElementsByTag("table").get(1)
            .getElementsByTag("tr")

    val availableColors = rows
        .map { it.getElementsByTag("td")[3].getElementsByTag("font").text() }
        .toSet()

    println(
        "These colors may be misspelled: \n${
            alreadyExistingColors.filter { it !in availableColors }.toSet()
        }"
    )

    println("These are the missing colors: \n${availableColors - alreadyExistingColors}")
}