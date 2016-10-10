package isel.pdm.trab.openweathermap.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class whose instances represent movie information obtained from the remote API.
 */
data class MovieDto(val adult: Boolean, val title: String, val id: Long) {
    private constructor() : this(false, "", 0)
}
