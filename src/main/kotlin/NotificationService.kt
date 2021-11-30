import net.jini.core.entry.Entry
import net.jini.space.JavaSpace
import net.jini.space.JavaSpace05
import java.rmi.RemoteException

enum class SensorType {
    UNKNOWN,
    TEMPERATURE,
    HUMIDITY,
    VELOCITY,
}

interface NotificationService {

    fun notifySensorWarning(type: SensorType, name: String, message: String)

    fun terminate()

}


class TupleSpaceNotificationService(private val timeout: Long = 60 * 1000) : NotificationService {

    private val lookup = Lookup(JavaSpace::class.java)

    override fun terminate() {
        lookup.terminate();
    }

    fun readEntriesByType(type: SensorType): ArrayList<SensorEntry> {
        val space = (lookup.service as JavaSpace05);
        val template = SensorEntry()
        template.type = type;

        val match = space.contents(arrayListOf(template).toMutableList(), null, timeout, timeout);
        val result = arrayListOf<SensorEntry>();
        try {
            var cur = match.next();
            while (cur != null) {
                result.add(match.next() as SensorEntry);
                cur = match.next();
            }
        } catch (e: Exception) {
        }

        return result
    }

    override fun notifySensorWarning(type: SensorType, name: String, message: String) {
        val entry = SensorEntry()
        entry.type = type
        entry.name = name
        entry.message = message

        try {
            val space = (lookup.service as JavaSpace);
            space.write(entry, null, timeout);
        } catch (e: RemoteException) {
            e.printStackTrace(System.err)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace(System.err)
        }
    }

}