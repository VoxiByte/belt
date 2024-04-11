package it.voxibyte.belt.chat;

import it.voxibyte.belt.i18n.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static it.voxibyte.belt.util.ChatUtil.colorize;


public class Message {
    private final String content;
    private final List<Placeholder> placeholders;

    public Message(Language language, String content) {
        this.content = language.getMessage(content) == null ? content : language.getMessage(content);
        this.placeholders = new ArrayList<>(1);
    }

    public Message replacing(Placeholder placeholder) {
        this.placeholders.add(placeholder);
        return this;
    }

    public void to(Collection<? extends CommandSender> targets) {
        for (CommandSender target : targets) {
            to(target);
        }
    }

    public void to(CommandSender target) {
        String message = colorize(content);
        message = replacePlaceholders(message);

        String[] parts = message.split("<br>");

        for(String part : parts) {
            target.sendMessage(part);
        }
    }

    private String replacePlaceholders(String message) {
        StringBuilder sb = new StringBuilder(message); //Use stringbuilder for increased performance
        for (Placeholder placeholder : placeholders) {
            int index;
            String key = placeholder.getKey();
            String value = placeholder.getValue();
            while ((index = sb.indexOf(key)) != -1) {
                sb.replace(index, index + key.length(), value);
            }
        }
        return sb.toString();
    }
}
