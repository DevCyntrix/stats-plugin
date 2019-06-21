package nl.lolmewn.stats.storage;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import nl.lolmewn.stats.stat.Stat;
import nl.lolmewn.stats.stat.StatManager;

import java.io.IOException;

public class StatTypeAdapter extends TypeAdapter<Stat> {
    @Override
    public void write(JsonWriter out, Stat value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public Stat read(JsonReader in) throws IOException {
        String name = in.nextString();
        return StatManager.getInstance().getStat(name).orElse(null);
    }
}
