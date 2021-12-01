package me.gushel.sentodiscordbot.DiscordListeners;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.User;
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

import java.util.concurrent.ExecutionException;

public class Kingdom extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        KingdomCraft kdc = KingdomCraftProvider.get();
        if (!(event.getName().equals("kingdom"))) return;
        event.deferReply().queue();
        if (!Util.canUseCommands(event.getMember())) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreatorN("link-command", "not-verified").build()).build()).queue();
            return;
        }
        String playerToCheck = event.getOption(config.getString("Bot.kingdom-command.argument.name")).getAsString();
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerToCheck);
        if (!target.hasPlayedBefore()) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreatorN("kingdom-command", "player-not-found").build()).build()).queue();
            return;
        }
        try {
            User targetK = kdc.getUser(target.getName()).get();
            EmbedBuilder eb = Util.embedCreatorN("kingdom-command", "info");
            eb.setDescription(Util.replaceFieldPlaceholders(target,config.getString("Bot.kingdom-command.embeds.info.description")));
            for (String fieldString : config.getStringList("Bot.kingdom-command.embeds.info.fields")){
                if (targetK.getKingdom() != null){
                    String[] fieldSplit = Util.replaceFieldPlaceholdersK(target,targetK,fieldString).split(":fieldValue:",2);
                    eb.addField(Util.replaceFieldPlaceholdersK(target,targetK,fieldSplit[0]),Util.replaceFieldPlaceholdersK(target,targetK,fieldSplit[1]),config.getBoolean("Bot.kingdom-command.embeds.info.inline-fields"));
                }
                if (targetK.getKingdom() == null){
                    System.out.println(fieldString);
                    if (!fieldString.contains("%kingdom")){
                        String[] fieldSplit = Util.replaceFieldPlaceholders(target,fieldString).split(":fieldValue:",2);
                        eb.addField(Util.replaceFieldPlaceholders(target,fieldSplit[0]),Util.replaceFieldPlaceholders(target,fieldSplit[1]),config.getBoolean("Bot.kingdom-command.embeds.info.inline-fields"));
                    }
                }
            }
            event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
