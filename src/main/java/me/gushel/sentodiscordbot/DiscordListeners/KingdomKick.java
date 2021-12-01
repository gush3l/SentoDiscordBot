package me.gushel.sentodiscordbot.DiscordListeners;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KingdomKick  extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        KingdomCraft kdc = KingdomCraftProvider.get();
        if (!(event.getName().equals("kingdom-kick"))) return;
        event.deferReply().queue();
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
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-kick","failed").build()).build()).queue();
            return;
        }
        String playerToKickS = event.getOption(config.getString("Bot.special-rank.commands.kingdom-kick.argument.name")).getAsString();
        String playerExecutingUUID = MySQL.getUUIDfromDiscordID(event.getUser().getId());
        OfflinePlayer playerExecuting = Bukkit.getOfflinePlayer(UUID.fromString(playerExecutingUUID));
        OfflinePlayer playerToKick = Bukkit.getOfflinePlayer(playerToKickS);
        if (!playerToKick.hasPlayedBefore()) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-kick","player-not-found").build()).build()).queue();
            return;
        }
        if (!SentoDiscordBot.getPermissions().playerHas(null, playerExecuting, config.getString("Minecraft.kick-command.perm-to-check"))) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-kick","no-permission").build()).build()).queue();
            return;
        }
        try {
            User target = kdc.getUser(playerToKick.getName()).get();
            User playerExecutingK = kdc.getUser(playerExecuting.getName()).get();
            if (target.getKingdom() != playerExecutingK.getKingdom()) {
                event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-kick","not-same-kingdom").build()).build()).queue();
                return;
            }
            if (target.getRank().getLevel() > playerExecutingK.getRank().getLevel()) {
                event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreator("kingdom-kick","rank-level-low").build()).build()).queue();
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskAsynchronously(SentoDiscordBot.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    User target = kdc.getUser(playerToKick.getName()).get();
                    target.setKingdom(null);
                    kdc.saveAsync(target);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        String kingdomName = null;
        try {
            User target = kdc.getUser(playerToKick.getName()).get();
            kingdomName = target.getKingdom().getName();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(config.getString("Bot.special-rank.commands.kingdom-kick.embeds.successfully.title"), null);
        eb.setColor(new Color(config.getInt("Bot.special-rank.commands.kingdom-kick.embeds.successfully.color.r"), config.getInt("Bot.special-rank.commands.kingdom-kick.embeds.successfully.color.g"), config.getInt("Bot.special-rank.commands.kingdom-kick.embeds.successfully.color.b")));
        eb.setDescription(config.getString("Bot.special-rank.commands.kingdom-kick.embeds.successfully.description").replace("%username%", playerToKick.getName()).replace("%kingdom%", kingdomName));
        if (config.getBoolean("Bot.special-rank.commands.kingdom-kick.embeds.successfully.footer.enabled"))
            eb.setFooter(config.getString("Bot.special-rank.commands.kingdom-kick.embeds.successfully.footer.message"));
        if (config.getBoolean("Bot.special-rank.commands.kingdom-kick.embeds.successfully.thumbnail.enabled"))
            eb.setThumbnail(config.getString("Bot.special-rank.commands.kingdom-kick.embeds.successfully.thumbnail.image-link"));
        event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
    }

}
