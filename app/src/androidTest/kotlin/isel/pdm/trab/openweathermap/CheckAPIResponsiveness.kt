package isel.pdm.trab.openweathermap

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.core.deps.guava.base.Strings
import android.support.test.runner.AndroidJUnit4
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import isel.pdm.trab.openweathermap.comms.GetRequest
import isel.pdm.trab.openweathermap.models.CurrentWeatherDto
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.fail

/**
 * Instrumentation test, which will execute on an Android device. Used to check whether the
 * accesses to the Remote API are behaving as expected.

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class CheckAPIResponsiveness {

    private lateinit var requestQueue: RequestQueue

    // Synchronization between test harness thread and callbacks thread
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

    private fun executeAndPublishResult(assertions: () -> Unit ) {
        try {
            assertions()
        } catch (error: AssertionError) {
            this.error = error
        } finally {
            latch.countDown()
        }
    }

    @Before
    fun prepare() {
        // Preparing Volley's request queue
        requestQueue = Volley.newRequestQueue(InstrumentationRegistry.getTargetContext())
        requestQueue.cache.clear()
        // Preparing test harness thread synchronization artifacts
        latch = CountDownLatch(1)
        error = null
    }

    @Test
    fun test_checkAPIResponsiveness() {

        requestQueue.add(
            StringRequest(
                Request.Method.GET,
                    WEATHER_TEST,
                { response -> executeAndPublishResult { assertFalse(Strings.isNullOrEmpty(response)) } },
                { error -> executeAndPublishResult { assertNotNull(error.networkResponse) } }
            )
        )

        waitForCompletion()
    }

    @Test
    fun test_successfulResponseParsing() {
        requestQueue.add(
                GetRequest(
                        WEATHER_TEST,
                    { weather -> executeAndPublishResult { assertNotNull(weather) } },
                    { error -> executeAndPublishResult { fail() } },
                        CurrentWeatherDto::class.java
                )
        )
        waitForCompletion()
    }

}
