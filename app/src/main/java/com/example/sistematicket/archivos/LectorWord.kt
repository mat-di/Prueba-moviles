package com.example.sistematicket.archivos

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.util.zip.ZipFile

object LectorWord {

    fun extraerParrafos(archivo: File): List<String> {
        ZipFile(archivo).use { zip ->
            val entrada = zip.getEntry("word/document.xml") ?: return emptyList()
            zip.getInputStream(entrada).use { flujo ->
                val parser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(flujo, "UTF-8")

                val parrafos = mutableListOf<String>()
                val actual = StringBuilder()
                var dentroDeTexto = false
                var dentroDeRun = false

                var evento = parser.eventType
                while (evento != XmlPullParser.END_DOCUMENT) {
                    when (evento) {
                        XmlPullParser.START_TAG -> when (parser.name) {
                            "w:r" -> dentroDeRun = true
                            "w:t" -> dentroDeTexto = true
                            "w:tab" -> if (dentroDeRun) actual.append('\t')
                            "w:br" -> if (dentroDeRun) actual.append('\n')
                        }
                        XmlPullParser.TEXT -> if (dentroDeTexto) actual.append(parser.text)
                        XmlPullParser.END_TAG -> when (parser.name) {
                            "w:r" -> dentroDeRun = false
                            "w:t" -> dentroDeTexto = false
                            "w:p" -> {
                                parrafos.add(actual.toString())
                                actual.clear()
                            }
                        }
                    }
                    evento = parser.next()
                }
                return parrafos
            }
        }
    }
}
