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

import java.util.HashMap;

public class KingdomList  extends ListenerAdapter {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!(event.getName().equals("kingdom-list"))) return;
        event.deferReply().queue();
        KingdomCraft kdc = KingdomCraftProvider.get();
        HashMap<String,Integer> sortedKingdoms = new HashMap<>();
        for (Kingdom kingdom : kdc.getKingdoms()){
            if (Util.getOnOffPlayers(kingdom.getMembers(),true).size() > 0) {
                sortedKingdoms.put(kingdom.getName(), Util.getOnOffPlayers(kingdom.getMembers(), true).size());
            }
        }
        if (sortedKingdoms.isEmpty()){
            event.getHook().editOriginal(new MessageBuilder().setEmbed(Util.embedCreatorN("kingdom-list-command", "kingdoms-not-found").build()).build()).queue();
            return;
        }
        sortedKingdoms = Util.sortByValueK(sortedKingdoms);
        int maxKingdoms = config.getInt("Bot.kingdom-list-command.max-to-display");
        int c = 0;
        String kingdomsListDesc = config.getString("Bot.kingdom-list-command.embeds.info.description")+System.lineSeparator();
        for (String kingdomName : sortedKingdoms.keySet()){
            if (c>maxKingdoms) return;
            kingdomsListDesc=kingdomsListDesc+config.getString("Bot.kingdom-list-command.embeds.info.kingdoms-list")
                    .replace("%kingdomName%",kingdomName)
                    .replace("%kingdomOnline%",String.valueOf(sortedKingdoms.get(kingdomName)))+System.lineSeparator();
        }
        EmbedBuilder eb = Util.embedCreatorN("kingdom-info-command", "info");
        eb.setDescription(kingdomsListDesc);
        event.getHook().editOriginal(new MessageBuilder().setEmbed(eb.build()).build()).queue();
    }

}
