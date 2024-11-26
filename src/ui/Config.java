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

    public static List<Config> all() {
        List<Config> res = new ArrayList<>();
        res.addAll(cache.values());
        return res;
    }

    public static Config fromExisting(String id) {
        return cache.get(id);
    }

    public static Config create(String name) {
        String id = Util.randomAlphaNum(6);
        while (cache.containsKey(id)) {
            id = Util.randomAlphaNum(6);
        }
        JsonObject obj = new JsonObject();
        obj.set("name", new JsonString(name));
        obj.set("games", new JsonNumber(1));
        obj.set("botNames", new JsonArray());
        obj.set("botClasses", new JsonArray());
        obj.set("pause", JsonLiteral.FALSE);
        Config res = new Config(id, obj);
        cache.put(id, res);
        return res;
    }

    public static void saveAll() {
        for (Config cfg : cache.values()) {
            cfg.save();
        }
    }

    public void delete() {
        File file = new File(path + "/" + id + ".json");
        file.delete();
        cache.remove(id);
    }

    private static boolean isValid(JsonObject data) {
        if (!(data.get("name") instanceof JsonString)) return false;
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
    private String battleName;
    private int games;
    private boolean pause;
    private List<MonsterData> monsters = new ArrayList<>();

    private Config(String id, JsonObject data) {
        this.id = id;
        battleName = data.get(JsonString.class, "name").stringValue();
        games = data.get(JsonNumber.class, "games").intValue();
        pause = data.get("pause") == JsonLiteral.TRUE;
        List<JsonString> names = data.get(JsonArray.class, "botNames").array(JsonString.class);
        List<JsonString> classes = data.get(JsonArray.class, "botClasses").array(JsonString.class);
        for (int i = 0; i < names.size(); i++) {
            monsters.add(new MonsterData(names.get(i).stringValue(), classes.get(i).stringValue()));
            nameSet.add(names.get(i).stringValue());
        }
    }

    public void setName(String str) {
        battleName = str;
    }

    public String getName() {
        return battleName;
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

    private Set<String> nameSet = new HashSet<>();

    public MonsterData addMonster() {
        MonsterData toAdd = new MonsterData(getDefaultName(), "PlayerNaive");
        addMonster(toAdd);
        return toAdd;
    }

    public void addMonster(MonsterData md) {
        nameSet.add(md.getName());
        monsters.add(md);
    }

    public void removeMonster(MonsterData md) {
        if (monsters.remove(md)) nameSet.remove(md.getName());
    }

    public List<MonsterData> getMonsters() {
        return monsters;
    }

    public String getDefaultName() {
        int i = 0;
        while (true) {
            String req = "Random Monster " + i;
            if (!nameSet.contains(req)) return req;
            i++;
        }
    }

    public void save() {
        File file = new File(path + "/" + id + ".json");
        JsonObject obj = new JsonObject();
        obj.set("name", new JsonString(battleName));
        obj.set("games", new JsonNumber(games));
        obj.set("botNames", new JsonArray(
            monsters.stream()
                .map(m -> new JsonString(m.getName()))
                .toList()
        ));
        obj.set("botClasses", new JsonArray(
            monsters.stream()
                .map(m -> new JsonString(m.getCls()))
                .toList()
        ));
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
