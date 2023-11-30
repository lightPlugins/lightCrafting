package de.lightplugins.lightcrafting.commands.master;

import de.lightplugins.lightcrafting.enums.MessagePath;
import de.lightplugins.lightcrafting.enums.PermissionPath;
import de.lightplugins.lightcrafting.main.LightCrafting;
import de.lightplugins.lightcrafting.util.SubCommand;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reloads the configurations";
    }

    @Override
    public String getSyntax() {
        return "/lcrafting reload";
    }

    @Override
    public boolean perform(Player player, String[] args) throws ExecutionException, InterruptedException {

        if(!player.hasPermission(PermissionPath.RELOAD.getPerm())) {
            LightCrafting.util.sendMessage(player, MessagePath.NO_PERMISSION.getPath());
            LightCrafting.util.playSuccessSound(player, false);
            return false;
        }

        if(args.length != 1) {
            LightCrafting.util.sendMessage(player, MessagePath.WRONG_COMMAND.getPath()
                    .replace("#syntax#", "/lcrafting reload"));
            LightCrafting.util.playSuccessSound(player, false);
            return false;
        }

        LightCrafting.armorsmith.reloadConfig("armorsmith.yml");
        LightCrafting.blacksmith.reloadConfig("blacksmith.yml");
        LightCrafting.carpentry.reloadConfig("carpentry.yml");
        LightCrafting.cooking.reloadConfig("cooking.yml");
        LightCrafting.elementsmith.reloadConfig("elementsmith.yml");
        LightCrafting.loom.reloadConfig("loom.yml");
        LightCrafting.melting.reloadConfig("armorsmith.yml");

        LightCrafting.settings.reloadConfig("settings.yml");
        LightCrafting.messages.reloadConfig("messages.yml");

        LightCrafting.util.sendMessage(player, MessagePath.RELOAD.getPath());
        LightCrafting.util.playSuccessSound(player, true);



        return false;
    }
}
