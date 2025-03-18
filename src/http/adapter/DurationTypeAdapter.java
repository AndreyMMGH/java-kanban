package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, Duration duration) throws IOException {
        long minutesDuration = duration.toMinutes();
        jsonWriter.value(minutesDuration);
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        long minutesDuration = jsonReader.nextLong();
        return Duration.ofMinutes(minutesDuration);
    }
}

