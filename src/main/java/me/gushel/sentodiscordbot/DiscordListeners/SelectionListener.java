package me.gushel.sentodiscordbot.DiscordListeners;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import me.gushel.sentodiscordbot.SentoDiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.concurrent.ExecutionException;

public class SelectionListener extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {
        if (!event.getSelectionMenu().getId().equals("menu:ranks")) return;
        KingdomCraft kdc = KingdomCraftProvider.get();
        event.deferReply().queue();
        String value = event.getSelectedOptions().get(0).getValue();
        String[] rankTarget = value.split(":");
        String rankName = rankTarget[0];
        String target = rankTarget[1];
        try {
            User targetK = kdc.getUser(target).get();
            String oldRank = targetK.getRank().getName();
            Rank rankK = targetK.getKingdom().getRank(rankName);
            if (rankK.getLevel() < targetK.getRank().getLevel()) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.successfully.title").replace("%username%", targetK.getName()).replace("%currentRank%", targetK.getRank().getName()), null);
                eb.setColor(new Color(config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.successfully.color.r"), config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.successfully.color.g"), config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.successfully.color.b")));
                eb.setDescription(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.successfully.description").replace("%username%", targetK.getName()).replace("%newRank%", rankName).replace("%oldRank%", oldRank).replace("%kingdom%", targetK.getKingdom().getName()));
                if (config.getBoolean("Bot.special-rank.commands.kingdom-promote.embeds.successfully.footer.enabled"))
                    eb.setFooter(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.successfully.footer.message"));
                if (config.getBoolean("Bot.special-rank.commands.kingdom-promote.embeds.successfully.thumbnail.enabled"))
                    eb.setThumbnail(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.successfully.thumbnail.image-link"));
                event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
                Bukkit.getScheduler().runTaskAsynchronously(SentoDiscordBot.getInstance(), () -> {
                    targetK.setRank(rankK);
                    kdc.saveAsync(targetK);
                });
            }
            else{
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.demoted.title").replace("%username%", targetK.getName()).replace("%currentRank%", targetK.getRank().getName()), null);
                eb.setColor(new Color(config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.demoted.color.r"), config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.demoted.color.g"), config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.demoted.color.b")));
                eb.setDescription(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.demoted.description").replace("%username%", targetK.getName()).replace("%newRank%", rankName).replace("%oldRank%", oldRank).replace("%kingdom%", targetK.getKingdom().getName()));
                if (config.getBoolean("Bot.special-rank.commands.kingdom-promote.embeds.demoted.footer.enabled"))
                    eb.setFooter(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.demoted.footer.message"));
                if (config.getBoolean("Bot.special-rank.commands.kingdom-promote.embeds.demoted.thumbnail.enabled"))
                    eb.setThumbnail(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.demoted.thumbnail.image-link"));
                event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
                Bukkit.getScheduler().runTaskAsynchronously(SentoDiscordBot.getInstance(), () -> {
                    targetK.setRank(rankK);
                    kdc.saveAsync(targetK);
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
