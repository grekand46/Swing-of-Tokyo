package ui;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import json.*;

public class Config {
    private static Map<String, Config> cache = new HashMap<>();
    private static final String path = "out/app-data/collection";
    static {
        File dir = new File(path);
        File[] battles = dir.listFiles();
        for (File battle : battles) {
            String id = Util.fileNameNoExtension(battle.getName());
            JsonObject obj = null;
            try {
                obj = (JsonObject) JsonParser.INSTANCE.parse(Files.readString(battle.toPath()));
            } catch (ClassCastException e) {
                battle.delete();
                continue;
            } catch (IOException e) {
                System.out.println("IO exception");
                continue;
            }
            if (!isValid(obj)) {
                battle.delete();
                continue;
            }
            cache.put(id, new Config(id, obj));
        }
    }

    public static Config fromExisting(String id) {
        return cache.get(id);
    }

    public static Config create() {
        String id = Util.randomAlphaNum(6);
        while (cache.containsKey(id)) {
            id = Util.randomAlphaNum(6);
        }
        JsonObject obj = new JsonObject();
        obj.set("games", new JsonNumber(1));
        obj.set("botNames", new JsonArray());
        obj.set("botClasses", new JsonArray());
        obj.set("pause", JsonLiteral.FALSE);
        return new Config(id, obj);
    }

    public void delete() {
        File file = new File(path + "/" + id + ".json");
        file.delete();
        cache.remove(id);
    }

    private static boolean isValid(JsonObject data) {
        if (data.get("games") instanceof JsonNumber games) {
            int v = games.intValue();
            if (v < 1 || v > 1000) return false;
        }
        else return false;
        int len1 = -1;
        int len2 = -1;
        if (data.get("botNames") instanceof JsonArray names) {
            for (JsonElement str : names.array()) {
                if (!(str instanceof JsonString)) return false;
            }
            len1 = names.array().size();
        }
        else return false;
        if (data.get("botClasses") instanceof JsonArray classes) {
            for (JsonElement str : classes.array()) {
                if (!(str instanceof JsonString)) return false;
            }
            len2 = names.array().size();
        }
        else return false;
        if (len1 != len2) return false;
        if (data.get("pause") instanceof JsonLiteral pause) {
            if (pause == JsonLiteral.NULL) return false;
        }
        else return false;

        return true;

    }

    private String id;
    private int games;
    private boolean pause;
    private List<String> names = new ArrayList<>();
    private List<String> classes = new ArrayList<>();

    private Config(String id, JsonObject data) {
        this.id = id;
        games = data.get(JsonNumber.class, "games").intValue();
        pause = data.get("pause") == JsonLiteral.TRUE;
        for (JsonString elem : data.get(JsonArray.class, "botNames").array(JsonString.class)) {
            names.add(elem.stringValue());
        }
        for (JsonString elem : data.get(JsonArray.class, "botClasses").array(JsonString.class)) {
            classes.add(elem.stringValue());
        }
    }

    public void setGames(int n) {
        games = Math.max(1, Math.min(n, 1000));
    }
    public int getGames() {
        return games;
    }

    public void setPause(boolean b) {
        pause = b;
    }
    public boolean getPause() {
        return pause;
    }

    public void addMonster() {
        
    }
    public void editMonster(String name, String cls) {

    }


    public void save() {
        File file = new File(path + "/" + id + ".json");
        JsonObject obj = new JsonObject();
        obj.set("games", new JsonNumber(games));
        obj.set("botNames", new JsonArray(names.stream().map(JsonString::new).toList()));
        obj.set("botClasses", new JsonArray(classes.stream().map(JsonString::new).toList()));
        obj.set("pause", pause ? JsonLiteral.TRUE : JsonLiteral.FALSE);
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.write(obj.serialize(0));
            writer.close();
        }
        catch (IOException e) {
            System.out.println("IO err");
        }
    }
}
