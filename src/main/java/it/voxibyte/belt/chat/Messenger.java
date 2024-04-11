package it.voxibyte.belt.chat;

import it.voxibyte.belt.i18n.Language;

public class Messenger {
    private static Language language;

    public static void init() {
        Messenger.language = Language.getInstance();
    }

    public static Message send(String content) {
        return new Message(language, content);
    }

}
