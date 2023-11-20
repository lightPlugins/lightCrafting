package de.lightplugins.lightcrafting.util;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class ColorTranslation {

    public String deserialize(String msg) {

        MiniMessage miniMessage = MiniMessage.miniMessage();
        return miniMessage.deserialize(msg).toString();
    }
}
