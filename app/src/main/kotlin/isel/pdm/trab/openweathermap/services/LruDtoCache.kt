package isel.pdm.trab.openweathermap.services

import java.util.LinkedHashMap


class LruDtoCache<String, B>(private val maxEntries: Int) :
        LinkedHashMap<String, B>(maxEntries, 0.75f, true){

    override fun removeEldestEntry(eldest: Map.Entry<String, B>): Boolean {
        return super.size > maxEntries
    }
}
