package adapters;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(value != null ? value.toMinutes() : null);
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        return Duration.ofMinutes(Long.parseLong(in.nextString()));
    }
}
