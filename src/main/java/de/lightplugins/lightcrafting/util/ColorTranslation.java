package de.lightplugins.lightcrafting.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import java.util.regex.Pattern;

public class ColorTranslation {

    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public String deserialize(String msg) {

        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(msg).toString();
    }
}
