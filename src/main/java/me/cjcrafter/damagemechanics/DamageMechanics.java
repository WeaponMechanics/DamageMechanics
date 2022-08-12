package me.cjcrafter.damagemechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class DamageMechanics extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        saveDefaultConfig();
        reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Reloaded DamageMechanics");

        double health = getConfig().getDouble("Set_Player_Health_Attribute", -1.0);

        // Do nothing if the feature is disabled
        if (health == -1.0)
            return true;

        for (Player player : Bukkit.getOnlinePlayers()) {

            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            boolean active = isEnabled(player.getWorld().getName());
            attribute.setBaseValue(active ? health : 20.0);
        }

        return true;
    }

    public boolean isEnabled(String worldName) {
        List<?> list = getConfig().getList("Active_Worlds", Collections.emptyList());
        if (list.isEmpty())
            return true;

        return list.contains(worldName);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        double health = getConfig().getDouble("Set_Player_Health_Attribute", -1.0);

        // Do nothing if the feature is disabled
        if (health == -1.0)
            return;

        Player player = event.getPlayer();
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        boolean active = isEnabled(player.getWorld().getName());
        attribute.setBaseValue(active ? health : 20.0);
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        double health = getConfig().getDouble("Set_Player_Health_Attribute", -1.0);

        // Do nothing if the feature is disabled
        if (health == -1.0)
            return;

        Player player = event.getPlayer();
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        boolean active = isEnabled(event.getTo().getWorld().getName());
        attribute.setBaseValue(active ? health : 20.0);
    }

    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        double modifier = getConfig().getDouble("Damage_Modifiers." + event.getCause(), 1.0);
        event.setDamage(event.getDamage() * modifier);
    }

    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHeal(EntityRegainHealthEvent event) {
        double modifier = getConfig().getDouble("Heal_Modifiers." + event.getRegainReason(), 1.0);
        event.setAmount(event.getAmount() * modifier);
    }
}
