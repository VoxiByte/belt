package it.voxibyte.belt.chat;

public class Placeholder {
    private final String key;
    private final String value;

    public Placeholder(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static Placeholder valorize(String key, String value) {
        return new Placeholder(key, value);
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
