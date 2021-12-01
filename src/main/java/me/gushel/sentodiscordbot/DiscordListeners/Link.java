package me.gushel.sentodiscordbot.DiscordListeners;

import me.gushel.sentodiscordbot.MySQL.MySQL;
import me.gushel.sentodiscordbot.SentoDiscordBot;
import me.gushel.sentodiscordbot.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Link extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("link")) return;
        event.deferReply().queue();
        if (event.getOption(config.getString("Bot.link-command.argument.name")) != null) {
            String linkCode = event.getOption(config.getString("Bot.link-command.argument.name")).getAsString().replaceAll("[/\\D+/g]", "");
            if (!(SentoDiscordBot.notVerifiedUsers.containsValue(linkCode))){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(config.getString("Bot.link-command.embeds.invalid-code.title"), null);
                eb.setColor(new Color(config.getInt("Bot.link-command.embeds.invalid-code.color.r"), config.getInt("Bot.link-command.embeds.invalid-code.color.g"), config.getInt("Bot.link-command.embeds.invalid-code.color.b")));
                eb.setDescription(config.getString("Bot.link-command.embeds.invalid-code.description"));
                if (config.getBoolean("Bot.link-command.embeds.invalid-code.footer.enabled")) eb.setFooter(config.getString("Bot.link-command.embeds.invalid-code.footer.message"));
                if (config.getBoolean("Bot.link-command.embeds.invalid-code.thumbnail.enabled")) eb.setThumbnail(config.getString("Bot.link-command.embeds.invalid-code.thumbnail.image-link"));
                event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
                return;
            }
            if (MySQL.getVerifyCodeFromDiscordID(event.getUser().getId()) != null){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(config.getString("Bot.link-command.embeds.already-verified.title"), null);
                eb.setColor(new Color(config.getInt("Bot.link-command.embeds.already-verified.color.r"), config.getInt("Bot.link-command.embeds.already-verified.color.g"), config.getInt("Bot.link-command.embeds.already-verified.color.b")));
                eb.setDescription(config.getString("Bot.link-command.embeds.already-verified.description"));
                if (config.getBoolean("Bot.link-command.embeds.already-verified.footer.enabled")) eb.setFooter(config.getString("Bot.link-command.embeds.already-verified.footer.message"));
                if (config.getBoolean("Bot.link-command.embeds.already-verified.thumbnail.enabled")) eb.setThumbnail(config.getString("Bot.link-command.embeds.already-verified.thumbnail.image-link"));
                event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
                return;
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(config.getString("Bot.link-command.embeds.verified.title"), null);
            eb.setColor(new Color(config.getInt("Bot.link-command.embeds.verified.color.r"), config.getInt("Bot.link-command.embeds.verified.color.g"), config.getInt("Bot.link-command.embeds.verified.color.b")));
            eb.setDescription(config.getString("Bot.link-command.embeds.verified.description"));
            if (config.getBoolean("Bot.link-command.embeds.verified.footer.enabled")) eb.setFooter(config.getString("Bot.link-command.embeds.verified.footer.message"));
            if (config.getBoolean("Bot.link-command.embeds.verified.thumbnail.enabled")) eb.setThumbnail(config.getString("Bot.link-command.embeds.verified.thumbnail.image-link"));
            event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
            MySQL.addUserToDatabase(Util.getKey(SentoDiscordBot.notVerifiedUsers,linkCode),event.getUser().getId(),linkCode);
            SentoDiscordBot.notVerifiedUsers.remove(Util.getKey(SentoDiscordBot.notVerifiedUsers,linkCode));
            event.getGuild().addRoleToMember(event.getMember(),event.getGuild().getRoleById(config.getString("Bot.link-command.linked-role"))).queue();
        }
    }

}
