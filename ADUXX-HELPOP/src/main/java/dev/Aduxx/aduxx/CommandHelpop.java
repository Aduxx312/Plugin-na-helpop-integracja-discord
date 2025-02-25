package dev.Aduxx.aduxx;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;

import java.util.UUID;

public class CommandHelpop implements CommandExecutor {

    private final ADUXXHELPOP plugin;

    public CommandHelpop(ADUXXHELPOP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Mordko nie uzyjesz tej komendy w konsoli!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (plugin.isOnCooldown(playerId)) {
            long timeLeft = plugin.getTimeLeft(playerId);
            String cooldownMessage = plugin.getConfig().getString("cooldown-message")
                    .replace("[TIME]", String.valueOf(timeLeft));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', cooldownMessage));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("helpop-usage")));
            return true;
        }

        String message = String.join(" ", args);
        String playerName = player.getName();

        String embedTitle = plugin.getConfig().getString("embed-title");
        String embedDescription = String.join("\n", plugin.getConfig().getStringList("embed-description"))
                .replace("[PLAYER]", playerName)
                .replace("[MESSAGE]", message);
        String thumbnailUrl = plugin.getConfig().getString("embed-thumbnail-url");
        String embedColor = plugin.getConfig().getString("embed-color");

        plugin.getDiscordBot().sendMessage(embedTitle, embedDescription, thumbnailUrl, embedColor);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("helpop-send")));

        String receiveMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("helpop-receive"))
                .replace("[PLAYER]", playerName)
                .replace("[MESSAGE]", message);

        String permission = plugin.getConfig().getString("helpop-receive-permission");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(permission)) {
                onlinePlayer.sendMessage(receiveMessage);
            }
        }

        plugin.setCooldown(playerId);
        return true;
    }
}