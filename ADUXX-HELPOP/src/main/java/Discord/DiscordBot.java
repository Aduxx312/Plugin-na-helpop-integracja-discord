package Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.Activity;
import java.awt.Color;

public class DiscordBot {

    private final JDA jda;
    private final String channelId;

    public DiscordBot(String botToken, String channelId) throws InterruptedException {
        this.channelId = channelId;
        this.jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ACTIVITY)
                .build();
        jda.awaitReady();
    }

    public void start(String activity) {
        if (jda != null) {
            jda.getPresence().setActivity(Activity.playing(activity));
        }
    }

    public void stop() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public void sendMessage(String title, String description, String thumbnailUrl, String colorHex) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            net.dv8tion.jda.api.EmbedBuilder embedBuilder = new net.dv8tion.jda.api.EmbedBuilder()
                    .setTitle(title)
                    .setDescription(description);

            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                embedBuilder.setThumbnail(thumbnailUrl);
            }

            if (colorHex != null && !colorHex.isEmpty()) {
                try {
                    Color color = Color.decode(colorHex);
                    embedBuilder.setColor(color);
                } catch (IllegalArgumentException e) {
                    System.err.println("Bledny format! " + colorHex);
                }
            }

            channel.sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            System.err.println("Kanał z takim ID: " + channelId + " nie został znaleziony!");
        }
    }
}