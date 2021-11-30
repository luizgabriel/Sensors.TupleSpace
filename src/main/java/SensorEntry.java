import net.jini.core.entry.Entry;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;

public class SensorEntry implements Entry {
    public String name;
    public SensorType type;
    public Integer value;
    public Integer min;
    public Integer max;
    public Date createdAt;

    public SensorEntry() {
    }

    public SensorEntry(String name, SensorType type, int value, int min, int max, Date createdAt) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.min = min;
        this.max = max;
        this.createdAt = createdAt;
    }

    @NotNull
    public String getUnit() {
        switch (type) {
            default:
            case UNKNOWN:
                return "#";
            case TEMPERATURE:
                return "°C";
            case HUMIDITY:
                return "g/Kg";
            case VELOCITY:
                return "m/s";
        }
    }

    @NotNull
    public SensorState getState() {
        if (value > max) {
            return SensorState.ABOVE_MAXIMUM;
        } else if (value < min) {
            return SensorState.BELLOW_MINIMUM;
        } else {
            return SensorState.NORMAL;
        }
    }

    @Override
    public String toString() {
        return "SensorEntry{" + "name='" + name + '\'' + ", type=" + type + ", value=" + value + ", min=" + min + ", max=" + max + ", createdAt=" + createdAt + '}';
    }

    public static SensorEntry create(String name, SensorType type, int value, int min, int max) {
        return new SensorEntry(name, type, value, min, max, Date.from(Instant.now()));
    }
}
