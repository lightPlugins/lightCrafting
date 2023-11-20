package de.lightplugins.lightcrafting.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.regex.Matcher;

public class ColorTranslation {

    public TextComponent deserialize(String msg) {

        MiniMessage miniMessage = MiniMessage.miniMessage();
        return (TextComponent) miniMessage.deserialize(msg);
    }

}
