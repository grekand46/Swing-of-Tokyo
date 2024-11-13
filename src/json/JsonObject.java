package json;

import java.util.*;
import java.util.stream.Collectors;

public class JsonObject implements JsonElement {
    private Map<String, JsonElement> map = new HashMap<>();

    public JsonObject() {}
    public JsonObject(String str) {

    }

    public void set(String key, JsonElement val) {
        map.put(JsonParser.escape(key), val);
    }

    public JsonElement get(String key) {
        return map.get(JsonParser.escape(key));
    }

    public <T extends JsonElement> T get(Class<T> cls, String key) {
        return cls.cast(map.get(JsonParser.escape(key)));
    }

    List<JsonElement> values() {
        return new ArrayList<>(map.values());
    }

    @Override
    public String serialize(int level) {
        if (map.isEmpty()) return "{}";

        cycleCheck();
        String paddingUnit = "    ";
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < level; i++) padding.append(paddingUnit);

        String expanded = map.keySet().stream()
            .map(key -> padding + paddingUnit + '\"' + key + '\"' + ": " + map.get(key).serialize(level + 1))
            .collect(Collectors.joining(", "));
        return "{\n" + expanded + "\n" + padding + "}";
    }

    private void cycleCheck() {
        Set<JsonElement> record = new HashSet<>();
        JsonParser.cycleCheckRecurse(record, this);
    }
}
