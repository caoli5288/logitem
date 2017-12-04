/*
 * Decompiled with CFR 0_123.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.block.DoubleChest
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.i5mc.logitem;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Main extends JavaPlugin implements Listener {

    private List<ItemInfo> all;
    private MainLogger log;

    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.log = new MainLogger();
        all = getConfig().getMapList("item").stream().map(val -> new ItemInfo(val)).collect(toList());
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void h(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("logitem.admin")) {
            return;
        }
        run(100, () -> {
            if (!p.isOnline()) {
                return;
            }
            ItemProcessor processor = new ItemProcessor(all);
            List<ItemInfo> result = processor.hit(p.getInventory());
            result.forEach(info -> {
                this.log.info("玩家" + p.getName() + "背包物品超限制 " + info);
                String command = info.getCommand();
                if (command == null || command.isEmpty()) {
                    return;
                }
                command = PlaceholderAPI.setPlaceholders(p, command);
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), command);
            });
        });
    }

    void run(int i, Runnable r) {
        this.getServer().getScheduler().runTaskLater(this, r, (long) i);
    }

    @EventHandler
    public void handle(InventoryOpenEvent e) {
        HumanEntity p = e.getPlayer();
        if (!(p instanceof Player) || p.hasPermission("logitem.admin")) {
            return;
        }
        ItemProcessor processor = new ItemProcessor(all);
        List<ItemInfo> result = processor.hit(e.getInventory());
        result.forEach(info -> {
            this.log.info("玩家" + p.getName() + "打开背包超限制 " + info);
            if (e.getInventory().getHolder() instanceof BlockState) {
                Location loc = ((BlockState) e.getInventory().getHolder()).getLocation();
                log.info("" + loc);
            }
            String command = info.getCommand();
            if (command == null || command.isEmpty()) {
                return;
            }
            command = PlaceholderAPI.setPlaceholders((Player) p, command);
            this.getServer().dispatchCommand(this.getServer().getConsoleSender(), command);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void handle(BlockBreakEvent e) {
        BlockState state = e.getBlock().getState();
        if (state instanceof Chest) {
            Inventory inventory = ((Chest) state).getBlockInventory();
            ItemProcessor processor = new ItemProcessor(all);
            List<ItemInfo> result = processor.hit(inventory);
            result.forEach(info -> {
                this.log.info("玩家" + e.getPlayer().getName() + "破坏箱子" + e.getBlock().getLocation());
                log.info("" + info);
                String command = info.getCommand();
                if (command == null || command.isEmpty()) {
                    return;
                }
                command = PlaceholderAPI.setPlaceholders(e.getPlayer(), command);
                this.getServer().dispatchCommand(this.getServer().getConsoleSender(), command);
            });
            if (!result.isEmpty()) e.setCancelled(true);
        }
    }

}

