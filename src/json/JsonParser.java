package json;

import java.util.*;

public class JsonParser {
    public static final JsonParser INSTANCE = new JsonParser();
    static boolean isDigit(char ch) {
        return ch <= '9' && ch >= '0';
    }
    static boolean isHex(char ch) {
        return isDigit(ch) || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
    }
    static boolean isLowerAlpha(char ch) {
        return ch >= 'a' && ch <= 'z';
    }
    static boolean isValidNum(String str, int start, int end) {
        if (start >= end || end > str.length()) return false;
        int index = start;
        if (str.charAt(index) == '-') {
            index++;
            if (index >= end) return false;
        }
        if (str.charAt(index) == '0') {
            index++;
            if (index >= end) return true;
        }
        else if (isDigit(str.charAt(index))) {
            while (isDigit(str.charAt(index))) {
                index++;
                if (index >= end) return true;
            }
        }
        else return false;

        if (str.charAt(index) == '.') {
            index++;
            if (index >= end) return false;
            if (!isDigit(str.charAt(index))) return false;
            while (isDigit(str.charAt(index))) {
                index++;
                if (index >= end) return true;
            }
        }
        if (str.charAt(index) == 'E' || str.charAt(index) == 'e') {
            index++;
            if (index >= end) return false;
            if (str.charAt(index) == '+' || str.charAt(index) == '-') {
                index++;
                if (index >= end) return false;
            }
            if (!isDigit(str.charAt(index))) return false;
            while (isDigit(str.charAt(index))) {
                index++;
                if (index >= end) return true;
            }
            return false;
        }
        else return false;
    }

    static void cycleCheckRecurse(Set<JsonElement> record, JsonElement element) {
        if (record.contains(element)) throw new IllegalArgumentException("Cyclic JSON element");
        record.add(element);
        if (element instanceof JsonArray arr) {
            arr.array().forEach(elem -> cycleCheckRecurse(record, elem));
        }
        else if (element instanceof JsonObject obj) {
            obj.values().forEach(elem -> cycleCheckRecurse(record, elem));
        }
    }

    static String escape(String str) {
        StringBuilder escaped = new StringBuilder();
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\b':
                    escaped.append("\\b");
                    continue;
                case '\f':
                    escaped.append("\\f");
                    continue;
                case '\n':
                    escaped.append("\\n");
                    continue;
                case '\r':
                    escaped.append("\\r");
                    continue;
                case '\t':
                    escaped.append("\\t");
                    continue;
                case '"':
                    escaped.append("\\\"");
                    continue;
                case '\\':
                    escaped.append("\\\\");
                    continue;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }

    private int current = 0;
    private String string = "";

    public JsonElement parse(String str) {
        current = 0;
        string = str;
        JsonElement parsed = parseElement();
        skipSpaces();
        if (!end()) throw new IllegalArgumentException("Leftover string in JSON");
        return parsed;
    }

    private static boolean isSpace(char ch) {
        return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
    }

    private static boolean isReserved(char ch) {
        return ch == '\b' || ch == '\f' || ch == '\n' || ch == '\r' || ch == '\t';
    }

    private boolean end() {
        return current >= string.length();
    }

    private char getChar() {
        return string.charAt(current);
    }

    private void skipSpaces() {
        while (!end() && isSpace(getChar())) {
            current++;
        }
    }

    private void expect(char c) {
        if (getChar() != c) throw new RuntimeException("Expected char '" + c + "'");
    }

    private void checkEnd() {
        if (end()) throw new IllegalArgumentException("Unexpected end of string");
    }

    private JsonElement parseElement() {
        skipSpaces();
        checkEnd();
        char ch = getChar();
        if (ch == '{') return parseObject();
        if (ch == '[') return parseArray();
        if (ch == '\"') return parseString();
        if (ch == '-' || isDigit(ch)) return parseNumber();
        return parseLiteral();
    }

    private JsonString parseString() {
        expect('\"');
        current++;
        checkEnd();
        StringBuilder result = new StringBuilder();
        while (getChar() != '\"') {
            char c = getChar();
            if (isReserved(c)) throw new IllegalArgumentException("Reserved character encountered");
            if (c == '\\') {
                current++;
                checkEnd();
                char escape = getChar();
                switch (escape) {
                    case '\"':
                        result.append('\"');
                        break;
                    case '\\':
                        result.append('\\');
                        break;
                    case '/':
                        result.append('/');
                        break;
                    case 'b':
                        result.append('\b');
                        break;
                    case 'f':
                        result.append('\f');
                        break;
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    case 'u': {
                        current += 4;
                        checkEnd();
                        String hex = string.substring(current - 3, current + 1);
                        for (char hexDigit : hex.toCharArray()) 
                            if (!isHex(hexDigit)) throw new IllegalArgumentException("Invalid unicode escape");
                        result.append((char) Integer.parseInt(hex, 16));
                        break;
                    }
                    default: throw new IllegalArgumentException("Unknown escape");
                }
            }
            else result.append(c);
            current++;
            checkEnd();
        }
        current++;
        return new JsonString(result.toString());
    }

    private JsonLiteral parseLiteral() {
        int saved = current;
        while (isLowerAlpha(getChar())) {
            current++;
            if (current >= string.length()) break;
        }
        String literal = string.substring(saved, current);
        if (literal.equals("true")) return JsonLiteral.TRUE;
        if (literal.equals("false")) return JsonLiteral.FALSE;
        if (literal.equals("null")) return JsonLiteral.NULL;
        throw new IllegalArgumentException("Bad literal: \"" + literal + "\"");
    }

    private void bad(String str) {
        throw new IllegalArgumentException("Bad JSON " + str);
    }

    private JsonNumber parseNumber() {
        int saved = current;
        if (getChar() == '-') {
            current++;
            checkEnd();
        }
        if (getChar() == '0') {
            current++;
            if (end()) return new JsonNumber(string.substring(saved, current), null);
        }
        else if (isDigit(getChar())) {
            while (isDigit(getChar())) {
                current++;
                if (end()) return new JsonNumber(string.substring(saved, current), null);
            }
        }
        else bad("number");;

        if (getChar() == '.') {
            current++;
            if (end() || !isDigit(getChar())) bad("number");
            while (isDigit(getChar())) {
                current++;
                if (end()) return new JsonNumber(string.substring(saved, current), null);
            }
        }
        if (getChar() == 'E' || getChar() == 'e') {
            current++;
            if (end()) bad("number");
            if (getChar() == '+' || getChar() == '-') {
                current++;
                if (end()) bad("number");
            }
            if (!isDigit(getChar())) bad("number");
            while (isDigit(getChar())) {
                current++;
                if (end()) return new JsonNumber(string.substring(saved, current), null);
            }
            return new JsonNumber(string.substring(saved, current), null);
        }
        else return new JsonNumber(string.substring(saved, current), null);
    }

    private JsonArray parseArray() {
        expect('[');
        current++;
        if (end()) bad("array");
        List<JsonElement> elements = new ArrayList<>();
        skipSpaces();
        if (end()) bad("array");
        if (getChar() == ']') {
            current++;
            return new JsonArray(new JsonElement[0]);
        }
        while (true) {
            skipSpaces();
            elements.add(parseElement());
            skipSpaces();
            if (getChar() == ',') {
                current++;
                if (end()) bad("array");
                continue;
            }
            if (getChar() == ']') {
                current++;
                return new JsonArray(elements.toArray(JsonElement[]::new));
            }
            bad("array");
        }
    }

    private JsonObject parseObject() {
        expect('{');
        current++;
        if (end()) bad("object");
        JsonObject result = new JsonObject();
        skipSpaces();
        if (end()) bad("object");
        if (getChar() == '}') {
            current++;
            return result;
        }
        while (true) {
            skipSpaces();
            String key = parseString().stringValue();
            skipSpaces();
            expect(':');
            current++;
            if (end()) bad("object");
            skipSpaces();
            JsonElement val = parseElement();
            skipSpaces();
            result.set(key, val);
            if (getChar() == ',') {
                current++;
                if (end()) bad("array");
                continue;
            }
            if (getChar() == '}') {
                current++;
                return result;
            }
            bad("object");
        }
    }


}