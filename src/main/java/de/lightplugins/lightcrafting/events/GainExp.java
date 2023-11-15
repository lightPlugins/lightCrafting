package de.lightplugins.lightcrafting.events;

import de.lightplugins.lightcrafting.util.LevelSystem;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GainExp implements Listener {

    LevelSystem levelSystem = new LevelSystem();


    /**
     *  Für Itemsadder custom Bäume !!! TODO:!
     */

    @EventHandler
    public void itemsAdderCustomTrees(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType().equals(Material.NOTE_BLOCK)) {
            // Überprüfe die Blockdaten, um sicherzustellen, dass es sich um einen NoteBlock handelt
            BlockData blockData = block.getBlockData();
            if (blockData instanceof NoteBlock) {
                NoteBlock noteBlock = (NoteBlock) blockData;

                if (noteBlock.getInstrument().equals(Instrument.BELL)) {
                    // Erstelle die Note "2" ohne die Oktave anzugeben
                    Note noteTwo = new Note(2);

                    if (noteBlock.getNote().equals(noteTwo)) {
                        event.setCancelled(true);
                        player.sendMessage("Das darfst du noch nicht abbauen");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        int oldLevel = levelSystem.getPlayerLevel(player.getUniqueId());

        int gainedEXP = 10;

        if(block.getType().equals(Material.DIAMOND_BLOCK)) {
            gainedEXP += 400000000;
        }

        if(block.getType().equals(Material.GOLD_BLOCK)) {
            gainedEXP += 5000000;
        }

        levelSystem.gainExp(player.getUniqueId(), gainedEXP);

        int newLevel = levelSystem.getPlayerLevel(player.getUniqueId());
        if(newLevel >= 250) {
            player.sendMessage("§4Du hast das MAX Level erreicht! §c" + newLevel );
            return;
        }
        int requiredExp = levelSystem.getExperienceToNextLevel(player.getUniqueId());
        player.sendMessage("Du hast §c" + gainedEXP + " §fEXP bekommen. Du benötigst noch §c" + requiredExp + "§f für das nächste Level. Dein aktuelles Level ist §c" + newLevel);
        if(oldLevel != newLevel) {
            player.sendMessage("Glückwunsch, du bist jetzt Level §c" + newLevel);
        }
    }
}
