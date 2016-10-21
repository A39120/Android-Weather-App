package isel.pdm.trab.openweathermap


import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class GetRequestTests {

    /* Os métodos waitForCompletion(), executeAndPublishResult() as variaveis a eles associadas
        são apenas para testar devido ao GetRequest ser assincrono.
        Para chamar o GetRequest basta utilizar Volley.newRequestQueue(...)
        Nota: Os callbacks utilizados foram para testar
      */
    private lateinit var latch: CountDownLatch
    private var error: AssertionError? = null

    private fun waitForCompletion() {
        try {
            if (latch.await(60, TimeUnit.SECONDS)) {
                if (error != null)
                    throw error as AssertionError
            } else {
                fail("Test harness thread timeout while waiting for completion")
            }
        } catch (_: InterruptedException) {
            fail("Test harness thread was interrupted")
        }

    }

    @Test
    fun test_get_request_current_weather(){
        latch = CountDownLatch(1)
        val url = "http://api.openweathermap.org/data/2.5/weather?q=London&units=metric&lang=pt&appid=c7a548f3e5399a3cebb9b39ad5645120"
        val lat: Double = 51.51
        // Quando for usado no ambito de uma Activity deve ser usado o contexto da mesma. ex newRequestQueue(ActivityContext)
        Volley.newRequestQueue(InstrumentationRegistry.getTargetContext()).add(
                GetRequest(
                        url,
                        { weather ->
                            executeAndPublishResult{assertNotEquals(23.1, weather.coord.latitude)} // Se for usado assertEquals é suposto dar erro
                            executeAndPublishResult{assertEquals(lat, weather.coord.latitude)}
                        },
                        { error -> System.out.println("Error in response?")},
                        CurrentWeatherDto::class.java)
                )
        waitForCompletion()
    }

    private fun executeAndPublishResult(assertions: () -> Unit ) {
        try {
            assertions()
        } catch (error: AssertionError) {
            this.error = error
        } finally {
            latch.countDown()
        }
    }
}