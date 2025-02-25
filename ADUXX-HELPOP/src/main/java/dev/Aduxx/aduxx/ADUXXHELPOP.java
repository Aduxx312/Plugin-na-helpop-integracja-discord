package dev.Aduxx.aduxx;

import Discord.DiscordBot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class ADUXXHELPOP extends JavaPlugin {

    private static ADUXXHELPOP instance;
    private DiscordBot discordBot;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        String botToken = getConfig().getString("BOT_TOKEN", "MTM0Mzk1MTMyOTMxMTA2NDA2NQ.GbZ5vQ.oQfwW4uM93cBMvPuucvAYoYYc4_Cy1Va3JE1l8");
        String channelId = getConfig().getString("CHANNEL_ID", "1343951073936670784");

        if (botToken.isEmpty() || channelId.isEmpty()) {
            getLogger().severe("BOT_TOKEN lub CHANNEL_ID nie są ustawione w config.yml!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            discordBot = new DiscordBot(botToken, channelId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String activity = getConfig().getString("bot-activity", "W grze twoj twojserwer.pl");
        discordBot.start(activity);

        PluginCommand helpopCommand = this.getCommand("helpop");
        if (helpopCommand != null) {
            helpopCommand.setExecutor(new CommandHelpop(this));
        }
    }

    @Override
    public void onDisable() {
        if (discordBot != null) {
            discordBot.stop();
        }
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public boolean isOnCooldown(UUID playerId) {
        if (cooldowns.containsKey(playerId)) {
            long cooldownTime = getConfig().getInt("cooldown", 10) * 1000L;
            long timeLeft = (cooldowns.get(playerId) + cooldownTime) - System.currentTimeMillis();
            return timeLeft > 0;
        }
        return false;
    }

    public void setCooldown(UUID playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
    }

    public long getTimeLeft(UUID playerId) {
        if (cooldowns.containsKey(playerId)) {
            long cooldownTime = getConfig().getInt("cooldown", 10) * 1000L;
            long timeLeft = (cooldowns.get(playerId) + cooldownTime) - System.currentTimeMillis();
            return timeLeft > 0 ? timeLeft / 1000 : 0;
        }
        return 0;
    }

    public static ADUXXHELPOP getInstance() {
        return instance;
    }
}