package me.gushel.sentodiscordbot.DiscordListeners;

import com.gufli.kingdomcraft.api.KingdomCraft;
import com.gufli.kingdomcraft.api.KingdomCraftProvider;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import me.gushel.sentodiscordbot.SentoDiscordBot;
import me.gushel.sentodiscordbot.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class KingdomInfo extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        KingdomCraft kdc = KingdomCraftProvider.get();
        if (!(event.getName().equals("kingdom-info"))) return;
        event.deferReply().queue();
        if (!Util.canUseCommands(event.getMember())) {
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreatorN("link-command", "not-verified").build()).build()).queue();
            return;
        }
        Kingdom kingdom = kdc.getKingdom(event.getOption(config.getString("Bot.kingdom-info-command.argument.name")).getAsString());
        if (kingdom == null){
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreatorN("kingdom-info-command", "kingdom-not-found").build()).build()).queue();
            return;
        }
        EmbedBuilder eb = Util.embedCreatorN("kingdom-info-command", "info");
        eb.setDescription(Util.replaceFieldPlaceholdersKingdom(kingdom,config.getString("Bot.kingdom-info-command.embeds.info.description")));
        for (String fieldString : config.getStringList("Bot.kingdom-info-command.embeds.info.fields")){
            String[] fieldSplit = Util.replaceFieldPlaceholdersKingdom(kingdom,fieldString).split(":fieldValue:",2);
            eb.addField(Util.replaceFieldPlaceholdersKingdom(kingdom,fieldSplit[0]),Util.replaceFieldPlaceholdersKingdom(kingdom,fieldSplit[1]),config.getBoolean("Bot.kingdom-info-command.embeds.info.inline-fields"));
        }
        event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
    }
}

