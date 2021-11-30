import net.jini.core.entry.Entry;

public class SensorEntry implements Entry {
    public SensorType type;
    public String name;
    public String message;

    public SensorEntry() {
    }

    @Override
    public String toString() {
        return "SensorEntry{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
