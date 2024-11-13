package json;

public class JsonNumber implements JsonElement {
    private final String numAsString;
    JsonNumber(String verifiedString, Object dummy) {
        numAsString = verifiedString;
    }
    public JsonNumber(String str) {
        if (!JsonParser.isValidNum(str, 0, str.length()))
            throw new IllegalArgumentException("Invalid JSON number");
        numAsString = str;
    }
    public JsonNumber(int n) {
        numAsString = Integer.toString(n);
    }
    public JsonNumber(long n) {
        numAsString = Long.toString(n);
    }
    public JsonNumber(double x) {
        numAsString = Double.toString(x);
    }

    @Override
    public String serialize(int level) {
        return numAsString;
    }

    public double doubleValue() {
        return Double.parseDouble(numAsString);
    }

    public int intValue() {
        return (int) doubleValue();
    }

    public long longValue() {
        return (long) doubleValue();
    }
}
