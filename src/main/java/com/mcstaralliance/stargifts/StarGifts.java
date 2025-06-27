package com.mcstaralliance.stargifts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class StarGifts extends JavaPlugin {

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("gifts")) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此指令。");
            return true;
        }
        Player player = (Player) sender;
        if (!player.isOp() && !player.hasPermission("stargifts.give")) {
            player.sendMessage(ChatColor.RED + "你没有权限使用此指令。");
            return true;
        }
        if (args.length != 1 || !(args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("random"))) {
            player.sendMessage(ChatColor.YELLOW + "用法: /gifts <all|random>");
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "你手上没有物品。");
            return true;
        }
        if (args[0].equalsIgnoreCase("all")) {
            int amount = item.getAmount();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(player)) continue;
                ItemStack giveItem = item.clone();
                giveItem.setAmount(amount);
                p.getInventory().addItem(giveItem);
            }
            player.sendMessage(ChatColor.GREEN + "已将手中物品发放给全服玩家，每人 " + amount + " 个。");
            Bukkit.broadcastMessage(ChatColor.GOLD + "[StarGifts] " + ChatColor.AQUA + player.getName() + " 给全服玩家发放了 " + amount + " 个 " + item.getType().name() + "!");
        } else if (args[0].equalsIgnoreCase("random")) {
            List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(player)).collect(Collectors.toList());
            if (players.isEmpty()) {
                player.sendMessage(ChatColor.RED + "没有其他在线玩家。");
                return true;
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "[StarGifts] " + ChatColor.AQUA + player.getName() + " 正在抽奖，3秒后将随机送出 " + item.getType().name() + "!");
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Player winner = players.get(new Random().nextInt(players.size()));
                winner.getInventory().addItem(item.clone());
                winner.sendMessage(ChatColor.GREEN + "你被抽中获得了 " + item.getType().name() + "!");
                Bukkit.broadcastMessage(ChatColor.GOLD + "[StarGifts] " + ChatColor.AQUA + winner.getName() + " 被抽中获得了 " + item.getType().name() + "!");
            }, 60L); // 60 tick = 3秒
            player.sendMessage(ChatColor.GREEN + "抽奖已开始，3秒后开奖。");
        }
        return true;
    }
}
