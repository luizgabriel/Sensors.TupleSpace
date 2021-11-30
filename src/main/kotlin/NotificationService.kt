import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.jini.core.lookup.ServiceTemplate
import net.jini.discovery.DiscoveryEvent
import net.jini.discovery.DiscoveryListener
import net.jini.discovery.LookupDiscovery
import net.jini.space.JavaSpace
import net.jini.space.JavaSpace05
import java.rmi.Remote
import java.rmi.RemoteException
import java.time.Instant
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

enum class SensorType {
    UNKNOWN,
    TEMPERATURE,
    HUMIDITY,
    VELOCITY,
}

enum class SensorState {
    NORMAL,
    BELLOW_MINIMUM,
    ABOVE_MAXIMUM,
}


interface NotificationService {

    suspend fun notify(entry: SensorEntry)

    suspend fun getAvailableTopics(): Set<String>

    suspend fun getAllNotificationsByTopic(topic: String): List<SensorEntry>

}


class TupleSpaceNotificationService(private val space: JavaSpace05, private val timeout: Long = 60 * 1000) : NotificationService {

    companion object {
        suspend fun lookupForSpace(timeout: Long = 60 * 1000): TupleSpaceNotificationService? {
            val serviceTemplate = ServiceTemplate(null, arrayOf(JavaSpace::class.java), null)
            val startSignal = CountDownLatch(1)
            val discovery = LookupDiscovery(LookupDiscovery.ALL_GROUPS)
            var service: JavaSpace05? = null;

            discovery.addDiscoveryListener(object : DiscoveryListener {
                override fun discovered(p0: DiscoveryEvent?) {
                    p0?.registrars?.forEach {
                        try {
                            val result = it.lookup(serviceTemplate) as JavaSpace05?
                            if (result != null) {
                                service = result
                                startSignal.countDown()
                            }
                        } catch (e: RemoteException) {
                            e.printStackTrace(System.err)
                        }
                    }
                }

                override fun discarded(p0: DiscoveryEvent?) {
                }
            })

            return try {
                withContext(Dispatchers.IO) {
                    startSignal.await(timeout, TimeUnit.MILLISECONDS)
                }

                TupleSpaceNotificationService(service!!, timeout)
            } catch (e: InterruptedException) {
                null
            }
        }
    }

    private fun readEntries(template: SensorEntry): List<SensorEntry> {
        val match = space.contents(arrayListOf(template).toMutableList(), null, timeout, timeout)
        val result = arrayListOf<SensorEntry>()
        try {
            var cur = match.next()
            while (cur != null) {
                result.add(cur as SensorEntry)
                cur = match.next()
            }
        } catch (e: Exception) {
            e.printStackTrace(System.err)
        }

        return result
    }

    override suspend fun getAvailableTopics(): Set<String> {
        return readEntries(SensorEntry()).groupBy { it.name }.keys
    }

    override suspend fun getAllNotificationsByTopic(topic: String): List<SensorEntry> {
        val template = SensorEntry()
        template.name = topic
        return readEntries(template)
    }

    override suspend fun notify(entry: SensorEntry) {
        try {
            space.write(entry, null, timeout);
        } catch (e: Exception) {
            e.printStackTrace(System.err)
        }
    }

}