package json;

public enum JsonLiteral implements JsonElement {
    TRUE("true"),
    FALSE("false"),
    NULL("null");

    private String text;
    private JsonLiteral(String text) {
        this.text = text;
    }

    @Override
    public String serialize(int level) {
        return text;
    }
}
