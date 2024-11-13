package json;

import java.util.*;
import java.util.stream.Collectors;

public class JsonArray implements JsonElement {
    private final List<JsonElement> array = new ArrayList<>();

    public JsonArray(JsonElement... children) {
        for (JsonElement jsonElement : children) {
            array.add(jsonElement);
        }
    }
    public JsonArray(List<? extends JsonElement> children) {
        for (JsonElement jsonElement : children) {
            array.add(jsonElement);
        }
    }

    public List<JsonElement> array() { return array; } 
    public <T extends JsonElement> List<T> array(Class<T> cls) {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) array;
        return result;
    }

    @Override
    public String serialize(int level) {
        if (array.size() == 0) return "[]";
        cycleCheck();
        String list = array.stream()
            .map(elem -> elem.serialize(level))
            .collect(Collectors.joining(", "));
        return "[" + list + "]";
    }

    private void cycleCheck() {
        Set<JsonElement> record = new HashSet<>();
        JsonParser.cycleCheckRecurse(record, this);
    }
}
