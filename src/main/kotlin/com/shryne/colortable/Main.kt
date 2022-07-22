package com.shryne.colortable

import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kong.unirest.ObjectMapper
import kong.unirest.Unirest
import org.jsoup.Jsoup
import java.io.File
import java.io.FileWriter
import kotlin.system.exitProcess

val alreadyExistingColors = listOf(/*"Black", */"Pearl Dark Gray", "Pearl Light Gray",
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
).map { Color(it) }.toSet()

val parts = listOf(
    Item(name = "Tile 1 x 2 with Groove", id = "17084")
)

fun main() {
    val html =
        Jsoup.connect("https://www.bricklink.com/catalogColors.asp?sortBy=N")
            .get()

    val table = html.body().getElementById("id-main-legacy-table")
    //println(table!!.getElementsByTag("table")[3].getElementsByTag("table")[1])
    val rows = table!!.getElementsByTag("table")[3].getElementsByTag("table")[1]
        .getElementsByTag("tr")

    val availableColors = HashSet<Color>()
    rows
        .forEach {
            if (it.getElementsByTag("td")[0].getElementsByTag("font").text().toIntOrNull() != null) {
                availableColors.add(
                    Color(
                        name = it.getElementsByTag("td")[3].getElementsByTag("font")
                            .text(),
                        id = it.getElementsByTag("td")[0].getElementsByTag("font")
                            .text().toInt(),
                        selling = it.getElementsByTag("td")[3].getElementsByTag(
                            "font"
                        ).text().toIntOrNull() ?: 0
                    )
                )
            }
        }

    println(
        "These colors may be misspelled: \n${
            alreadyExistingColors.filter { it !in availableColors }.toSet()
        }"
    )

    val missingColors = availableColors - alreadyExistingColors
    println("These are the missing colors: \n${missingColors.joinToString { "\n$it" }}")

    Unirest.config().enableCookieManagement(false)
    Unirest.config().objectMapper = object : ObjectMapper {
        val mapper = com.fasterxml.jackson.databind.ObjectMapper().registerModule(
            KotlinModule.Builder().build()
        )

        override fun <T : Any?> readValue(
            value: String?,
            valueType: Class<T>?
        ): T? {
            if (value == "") {
                return null
            }
            return mapper.readValue(value, valueType)
        }

        override fun writeValue(value: Any?): String =
            mapper.writeValueAsString(value)
    }


    var callsMade = 0
    //FileWriter(filename).use { it.write(text) }
    val checkedColors = File("checkedColors").run {
        if (exists()) {
            readLines().map {
                CheckedColor.from(it)
            }.toMutableSet()
        } else {
            mutableSetOf()
        }
    }
    println("These colors have already been checked: ${checkedColors}")

    (missingColors.filterNot { c ->
        checkedColors.any { it.color == c }
    }).forEach { color ->
        for (e in parts) {
            Thread.sleep(200)
            val items = Unirest
                .get(url(color.id, e.id))
                .headers(
                    mapOf(
                        "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:102.0) Gecko/20100101 Firefox/102.0",
                        "Upgrade-Insecure-Requests" to "1",
                        "HOST" to "www.bricklink.com",
                        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
                        "Accept-Language" to "en-GB,en;q=0.5",
                        "Connection" to "keep-alive",
                        "Cookie" to "AWSALB=8kcw0W3HpbOXPK8mWaG2svYtEMraQEUD3yCutAkXFOGtSwP7Iac/98bwt/xxtjvHswRFNp9cGs3SGXT7dnNh35EPY6E4ktq/I5jZlCq7rtJDoU7ZnU4rYLW5D1k8; AWSALBCORS=8kcw0W3HpbOXPK8mWaG2svYtEMraQEUD3yCutAkXFOGtSwP7Iac/98bwt/xxtjvHswRFNp9cGs3SGXT7dnNh35EPY6E4ktq/I5jZlCq7rtJDoU7ZnU4rYLW5D1k8; BLNEWSESSIONID=0FED924538E0998444FB9CF2B06CBCA0; cartBuyerID=-1291621328; blckMID=1821d05df7700000-2e053adb354d5357; blckSessionStarted=1; _pk_id.12.23af=eea0541f0e96e78f.1658344301.; blckCookieSetting=CHK; ASPSESSIONIDAACCARCR=LKGAIMMDIHCIBHOPMCFADLLO; ASPSESSIONIDACCAARCR=HHLNBFHAJPAKBJBIEBLJPDNA; ASPSESSIONIDAABCDQDR=GBNMBFHANFPIEAMCFKBECALJ; ASPSESSIONIDACABCQCQ=KHONEFHAICAHOLDBKJACFLFI",
                        "Sec-Fetch-Dest" to "document",
                        "Sec-Fetch-Mode" to "navigate",
                        "Sec-Fetch-Site" to "none",
                        "Sec-Fetch-User" to "?1",
                        "TE" to "trailers"
                    )
                )
                .asObject(Offers::class.java)
                .ifFailure {
                    File("checkedColors").delete()
                    FileWriter("checkedColors").use { fileWriter ->
                        checkedColors.forEach { it.writeTo(fileWriter) }
                    }
                    println("Checked colors are: $checkedColors.")
                    exitProcess(0)
                }
                .body
            callsMade++
            if (items == null || items.offers.isEmpty()) {
                continue
            } else {
                val offeredBy = items.offers.find { it.sellersCountry == "Germany" }?.run {
                    "for ${this.price} from Germany." to this
                } ?: "for ${items.offers.first().price} from ${items.offers.first().sellersCountry}" to items.offers.first()
                println("${color.name} available ${offeredBy.first}")
                checkedColors.add(
                    CheckedColor(color, offeredBy.second.price, offeredBy.first, e)
                )
                break
            }
        }
        if (checkedColors.any { it.color == color }) {
            checkedColors.add(
                CheckedColor(color, "NONE", "NONE", Item("NONE", "NONE"))
            )
        }
    }

    //println(items)
}

fun url(colorId: Int, itemId: String) =
    "https://www.bricklink.com/ajax/clone/catalogifs.ajax?itemid=${itemId}&color=${colorId}&ss=DE&cond=N"

class Color(val name: String, val id: Int = 0, val selling: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "$name, id=$id, selling=$selling"
    }
}

data class CheckedColor(val color: Color, val price: String, val offeredBy: String, val part: Item) {
    fun writeTo(fileWriter: FileWriter) {
        fileWriter.write(color.name)
        fileWriter.write(";")
        fileWriter.write(price)
        fileWriter.write(";")
        fileWriter.write(part.name)
        fileWriter.write(";")
        fileWriter.write(part.id)
        fileWriter.write(";")
        fileWriter.write(offeredBy)
        fileWriter.write("\n")
    }

    companion object {
        fun from(line: String): CheckedColor {
            val split = line.split(";")
            return CheckedColor(
                Color(split[0]), split[1], split[4], Item(split[2], split[3])
            )
        }
    }
}