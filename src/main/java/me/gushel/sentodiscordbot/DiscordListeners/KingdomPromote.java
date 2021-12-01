package me.gushel.sentodiscordbot.DiscordListeners;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import me.gushel.sentodiscordbot.MySQL.MySQL;
import me.gushel.sentodiscordbot.SentoDiscordBot;
import me.gushel.sentodiscordbot.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KingdomPromote  extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        KingdomCraft kdc = KingdomCraftProvider.get();
        if (!(event.getName().equals("kingdom-promote"))) return;
        event.deferReply().setEphemeral(true).queue();
        if (!Util.canUseCommands(event.getMember())) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(config.getString("Bot.link-command.embeds.not-verified.title"), null);
            eb.setColor(new Color(config.getInt("Bot.link-command.embeds.not-verified.color.r"), config.getInt("Bot.link-command.embeds.not-verified.color.g"), config.getInt("Bot.link-command.embeds.not-verified.color.b")));
            eb.setDescription(config.getString("Bot.link-command.embeds.not-verified.description"));
            if (config.getBoolean("Bot.link-command.embeds.not-verified.footer.enabled"))
                eb.setFooter(config.getString("Bot.link-command.embeds.not-verified.footer.message"));
            if (config.getBoolean("Bot.link-command.embeds.not-verified.thumbnail.enabled"))
                eb.setThumbnail(config.getString("Bot.link-command.embeds.not-verified.thumbnail.image-link"));
            event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
            return;
        }
        if (!Util.canUseSpecialCmds(event.getMember())) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-promote","failed").build()).build()).queue();
            return;
        }
        String playerToPromoteS = event.getOption(config.getString("Bot.special-rank.commands.kingdom-promote.argument.name")).getAsString();
        String playerExecutingUUID = MySQL.getUUIDfromDiscordID(event.getUser().getId());
        OfflinePlayer playerExecuting = Bukkit.getOfflinePlayer(UUID.fromString(playerExecutingUUID));
        OfflinePlayer playerToPromote = Bukkit.getOfflinePlayer(playerToPromoteS);
        if (!playerToPromote.hasPlayedBefore()) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-promote","player-not-found").build()).build()).queue();
            return;
        }
        if (!SentoDiscordBot.getPermissions().playerHas(null,playerExecuting,config.getString("Minecraft.promote-command.perm-to-check"))){
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-promote","no-permission").build()).build()).queue();
            return;
        }
        try {
            User target = kdc.getUser(playerToPromote.getName()).get();
            User playerExecutingK = kdc.getUser(playerExecuting.getName()).get();
            if (target.getKingdom() != playerExecutingK.getKingdom()) {
                event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-promote","not-same-kingdom").build()).build()).queue();
                return;
            }
            if (target.getRank().getLevel() > playerExecutingK.getRank().getLevel()) {
                event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-promote","rank-level-low").build()).build()).queue();
                return;
            }
            Kingdom kingdom = target.getKingdom();
            HashMap<Rank,Integer> ranks = new HashMap<>();
            for (Rank rank : kingdom.getRanks()) {
                if (rank.getLevel() < playerExecutingK.getRank().getLevel() && rank != target.getRank() && !config.getStringList("Bot.special-rank.commands.kingdom-promote.excluded-ranks").contains(rank.getName())){
                    ranks.put(rank,rank.getLevel());
                }
            }
            if (ranks.isEmpty()){
                event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-promote","no-rank-available").build()).build()).queue();
                return;
            }
            HashMap<Rank,Integer> ranksOrder = Util.sortByValue(ranks);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.selection.title").replace("%username%", target.getName()).replace("%currentRank%", target.getRank().getName()), null);
            eb.setColor(new Color(config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.selection.color.r"), config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.selection.color.g"), config.getInt("Bot.special-rank.commands.kingdom-promote.embeds.selection.color.b")));
            eb.setDescription(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.selection.description").replace("%username%", target.getName()).replace("%currentRank%", target.getRank().getName()));
            if (config.getBoolean("Bot.special-rank.commands.kingdom-promote.embeds.selection.footer.enabled"))
                eb.setFooter(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.selection.footer.message"));
            if (config.getBoolean("Bot.special-rank.commands.kingdom-promote.embeds.selection.thumbnail.enabled"))
                eb.setThumbnail(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.selection.thumbnail.image-link"));
            event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).setActionRow(Util.selectionMenuRanksCreator(ranksOrder,target.getName())).queue();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
