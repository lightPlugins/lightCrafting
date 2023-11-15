package de.lightplugins.lightcrafting.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelSystem {
    private Map<UUID, PlayerLevelData> playerDataMap = new HashMap<>();

    public void gainExp(UUID playerUUID, int gainXP) {
        PlayerLevelData playerData = getPlayerData(playerUUID);

        if(getPlayerLevel(playerUUID) <= 250) {
            playerData.gainExperience(gainXP);
        }
    }

    public int getPlayerLevel(UUID playerUUID) {
        PlayerLevelData playerData = getPlayerData(playerUUID);
        return playerData.getLevel();
    }

    public int getPlayerExperience(UUID playerUUID) {
        PlayerLevelData playerData = getPlayerData(playerUUID);
        return playerData.getExperience();
    }

    public int getExperienceToNextLevel(UUID playerUUID) {
        PlayerLevelData playerData = getPlayerData(playerUUID);
        return playerData.calculateExperienceRequired() - playerData.getExperience();
    }

    private PlayerLevelData getPlayerData(UUID playerUUID) {
        return playerDataMap.computeIfAbsent(playerUUID, uuid -> new PlayerLevelData());
    }

    private static class PlayerLevelData {
        private int playerLevel;
        private int experiencePoints;

        public PlayerLevelData() {
            this.playerLevel = 1;
            this.experiencePoints = 0;
        }

        public void gainExperience(int xp) {
            this.experiencePoints += xp;

            while (this.experiencePoints >= calculateExperienceRequired()) {
                this.experiencePoints -= calculateExperienceRequired();


                if(playerLevel >= 250) {
                    playerLevel = 250;
                } else {
                    levelUp();
                }

            }
        }

        private void levelUp() {
            this.playerLevel++;
        }

        private int calculateExperienceRequired() {
            // Hier nehmen wir an, dass für jeden Levelaufstieg die erforderlichen XP exponentiell ansteigen.
            int baseXP = 100;

            // Begrenze den exponentiellen Anstieg auf Level 250
            int maxLevel = 250;

            int clampedLevel = Math.min(playerLevel, maxLevel);
            return baseXP * (clampedLevel * clampedLevel);
        }

        public int getLevel() {
            return playerLevel;
        }

        public int getExperience() {
            return experiencePoints;
        }
    }
}
