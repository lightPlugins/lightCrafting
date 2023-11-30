package de.lightplugins.lightcrafting.commands;

import de.lightplugins.lightcrafting.commands.master.ReloadCommand;
import de.lightplugins.lightcrafting.main.LightCrafting;
import de.lightplugins.lightcrafting.util.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MainCommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }


    public LightCrafting plugin;
    public MainCommandManager(LightCrafting plugin) {
        this.plugin = plugin;
        subCommands.add(new ReloadCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                for(int i = 0; i < subCommands.size(); i++) {
                    if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {

                        try {
                            if(getSubCommands().get(i).perform(player, args)) {

                            }

                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException("Error on Main command execution", e);
                        }
                    }
                }
            }
        }

        return false;
    }

}
