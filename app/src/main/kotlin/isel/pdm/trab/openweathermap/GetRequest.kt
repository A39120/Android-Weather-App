package isel.pdm.trab.openweathermap

import android.util.Log

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import isel.pdm.trab.openweathermap.models.MovieDto

import java.io.IOException

class GetRequest(url: String, success: (MovieDto) -> Unit, error: (VolleyError) -> Unit)
: JsonRequest<MovieDto>(Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<MovieDto> {
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        try {
            val movieDto = mapper.readValue(String(response.data), MovieDto::class.java)
            return Response.success(movieDto, null)
        } catch (e: IOException) {
            e.printStackTrace()
            return Response.error(VolleyError())
        }
    }
}