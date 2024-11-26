package ui;

public class MonsterData {
    private String name;
    private String cls;

    public MonsterData(String name, String cls) {
        this.name = name;
        this.cls = cls;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCls() { return cls; }
    public void setCls(String cls) { this.cls = cls; }    
}
