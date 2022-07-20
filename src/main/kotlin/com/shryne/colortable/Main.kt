package com.shryne.colortable

import kong.unirest.Unirest
import org.jsoup.Jsoup

fun main() {
    //println(Unirest.get()
    //    .asString().body)

    val html = Jsoup.connect("https://www.bricklink.com/catalogColors.asp?sortBy=N")
        .get()

    val table = html.body().getElementById("id-main-legacy-table")
    val rows = table!!.getElementsByTag("table")[3].getElementsByTag("table").get(1).getElementsByTag("tr")

    rows.forEach {
        val name = it.getElementsByTag("td")[3].getElementsByTag("font").text()
        //val id = it.getElementsByTag("td")[0].getElementsByTag("font")
        //val sale = it.getElementsByTag("td")[7].getElementsByTag("font")
        //println("name: $name, id: $id, for sale: $sale" )
        println(name)
    }
}