package com.kef.qzdeath;

import com.rngservers.graves.Graves;
import com.rngservers.graves.inventory.GraveInventory;
import com.rngservers.graves.manager.DataManager;
import com.rngservers.graves.manager.GraveManager;
import com.rngservers.graves.manager.MessageManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

public class Events implements Listener {
    Qzdeath main;
    HashMap<String,String> deathWorld=new HashMap<String,String>();
    public Events(Qzdeath mainclass) {
        main=mainclass;
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // onDeath set player to spectate, create a variable that they are dead, get their death location
        // store all of this in the config
        Player player=event.getEntity();
        player.setGameMode(GameMode.SPECTATOR);
        Location deathLocal=player.getLocation();
        int x=(int)Math.floor(deathLocal.getX());
        int y=(int)Math.floor(deathLocal.getY());
        int z=(int)Math.floor(deathLocal.getZ());
        String worldName=deathLocal.getWorld().getName();
        main.getConfig().set(player.getName()+".x",x);
        main.getConfig().set(player.getName()+".y",y);
        main.getConfig().set(player.getName()+".z",z);
        main.getConfig().set(player.getName()+".worldName",worldName);
        main.saveConfig();
        deathWorld.put(player.getName(),worldName);
        player.sendMessage(ChatColor.DARK_RED+"You died and became a ghost! Find your tomb at "+x+" "+y+" "+z+" in the "+deathLocal.getWorld().getEnvironment()+" realm.");
        player.sendMessage(ChatColor.GOLD +"Write /qze to return without visiting your location of death.");
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        //respawn in correct dimension
        Player player=event.getPlayer();
        World deathDim=Bukkit.getWorld(deathWorld.get(player.getName()));
        if(deathDim.getEnvironment().equals(World.Environment.NETHER)) {
            event.setRespawnLocation(new Location(Bukkit.getWorld(main.getConfig().getString("nether")),0,100,0));
            deathWorld.remove(player.getName());
        }
        if(deathDim.getEnvironment().equals(World.Environment.THE_END)) {
            event.setRespawnLocation(new Location(Bukkit.getWorld(main.getConfig().getString("the_end")), 0, 100, 0));
            deathWorld.remove(player.getName());
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        //when they get to da grave
        if(main.getConfig().contains(event.getPlayer().getName())) {
            Player player=event.getPlayer();
            Location currentLocal=player.getLocation();
            //check if location of player matches death coordinates
            int x=main.getConfig().getInt(player.getName()+".x");
            int y=main.getConfig().getInt(player.getName()+".y");
            int z=main.getConfig().getInt(player.getName()+".z");
            String worldName=main.getConfig().getString(player.getName()+".worldName");
                if(!main.getConfig().contains("radius")) {
                    main.getConfig().set("radius",3);
                    main.saveConfig();
                }
            int radius=main.getConfig().getInt("radius");
            Location deathLocal=new Location(Bukkit.getWorld(worldName),(double)x,(double)y,(double)z);
            if(deathLocal.getWorld()!=currentLocal.getWorld()) {
                player.teleport(new Location(deathLocal.getWorld(),0,100,0));
            }
            if(deathLocal.distance(currentLocal)<=radius) {
                GraveManager gm=new GraveManager(Graves.getPlugin(), new DataManager(new Graves()), new MessageManager(new Graves()));
                Location newLocal=gm.getTeleportLocation(player,deathLocal);
                player.teleport(newLocal);
                player.setGameMode(GameMode.SURVIVAL);
                main.getConfig().set(player.getName(),null);
                main.saveConfig();
            }
        }
    }
}
