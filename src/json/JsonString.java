package json;

public class JsonString implements JsonElement {
    private final String string;
    public JsonString(String str) {
        string = str;
    }

    public String stringValue() { return string; }

    @Override
    public String serialize(int level) {
        return "\"" + JsonParser.escape(string) + "\"";
    }
}
