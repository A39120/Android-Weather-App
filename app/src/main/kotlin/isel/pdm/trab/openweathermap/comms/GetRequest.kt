package isel.pdm.trab.openweathermap.comms

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.IOException

/**
 * Generic implementation of a custom HTTP GET request.
 *
 * @param url: The URL that is used to make the HTTP request
 * @param success: The callback in case of a HTTP GET request made a successful response
 * @param error: The VolleyError callback in case of an error
 * @param dtoType: The concrete type of DTO contained in the payload of the API response
 */
class GetRequest<DTO>(url: String,
                      success: (DTO) -> Unit,
                      error: (VolleyError) -> Unit,
                      private val dtoType: Class<DTO>)

: JsonRequest<DTO>(Method.GET, url, "", success, error) {

    companion object {
        val mapper: ObjectMapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<DTO> {

        try {
            // TODO: Refine to handle error responses
            val dto = mapper.readValue(response.data, dtoType)
            return Response.success(dto, null)
        } catch (e: IOException) {
            e.printStackTrace()
            return Response.error(VolleyError())
        }
    }
}