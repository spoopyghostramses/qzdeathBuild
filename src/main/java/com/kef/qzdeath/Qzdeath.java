package com.kef.qzdeath;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

import static org.bukkit.Bukkit.getWorld;

public final class Qzdeath extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().log(Level.INFO, "QZDeaths has loaded.");
        Events eventclass=new Events(this);
        Bukkit.getPluginManager().registerEvents(eventclass, this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().log(Level.INFO, "QZDeaths shutting down.");
    }

    public Qzdeath() {
        // Constructor and it is public
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("qze")) {
            if(sender instanceof Player) {
                Player player=(Player)sender;
                if(!getConfig().contains(player.getName())) {
                    player.sendMessage(ChatColor.DARK_AQUA+"You are not a ghost!");
                    return true;
                }
                Location bedSpawn=(player.getBedSpawnLocation());
                if(bedSpawn==null) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    }else{
                        player.teleport(player.getBedSpawnLocation());
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(ChatColor.DARK_AQUA+"You have returned to the mortal realm...");
                getConfig().set(player.getName(),null);
                saveConfig();
                return true;
            }
            return true;
        }
        return false;
    }
}
