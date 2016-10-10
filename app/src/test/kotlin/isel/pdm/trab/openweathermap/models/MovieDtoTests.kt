package isel.pdm.trab.openweathermap.models

import isel.pdm.trab.openweathermap.models.MovieDto
import org.junit.Test
import kotlin.test.assertEquals

class MovieDtoTests {

    @Test
    @Throws(Exception::class)
    fun instantiation_producesCorrectObject() {
        val TITLE = "Star Wars"
        val ID = 10L
        val movie = MovieDto(adult = false, title = TITLE, id = ID)

        assertEquals(false, movie.adult)
        assertEquals(TITLE, movie.title)
        assertEquals(ID, movie.id)
    }
}