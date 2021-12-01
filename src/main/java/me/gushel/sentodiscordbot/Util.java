package me.gushel.sentodiscordbot;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

public class Util {

    public static FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    public static String colorPapi(Player player, String message){
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player,message));
    }

    public static String color(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean canUseSpecialCmds(@NotNull Member user){
        List<Role> userRoles = user.getRoles();
        for (Role role : userRoles){
            if (config.getStringList("Bot.special-rank.discord-role-ids").contains(role.getId())){
                return true;
            }
        }
        return false;
    }

    public static boolean canUseCommands(@NotNull Member user){
        List<Role> userRoles = user.getRoles();
        for (Role role : userRoles){
            if (config.getString("Bot.link-command.linked-role").equals(role.getId())){
                return true;
            }
        }
        return false;
    }

    public static HashMap<Rank,Integer> sortByValue(HashMap<Rank,Integer> hm) {
        List<Map.Entry<Rank,Integer> > list =
                new LinkedList<>(hm.entrySet());
        Collections.sort(list, Map.Entry.comparingByValue());
        HashMap<Rank,Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Rank,Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static String replaceFieldPlaceholdersK(OfflinePlayer offlinePlayer, User kingdomUser, String message){
        Timestamp joinTS = new Timestamp(offlinePlayer.getFirstPlayed());
        Timestamp lastPlayerTS = new Timestamp(offlinePlayer.getLastPlayed());
        String replacedMessage = message.replace("%username%",offlinePlayer.getName());
        replacedMessage = replacedMessage.replace("%serverJoindate%",joinTS.toString());
        replacedMessage = replacedMessage.replace("%lastPlayed%", lastPlayerTS.toString());
        replacedMessage = replacedMessage.replace("%kingdomName%", kingdomUser.getKingdom().getName());
        replacedMessage = replacedMessage.replace("%kingdomJoindate%",Timestamp.from(kingdomUser.getJoinedKingdomAt()).toString());
        replacedMessage = replacedMessage.replace("%kingdomRole%",kingdomUser.getRank().getName());
        replacedMessage = replacedMessage.replace("%kingdomRoleLevel%",String.valueOf(kingdomUser.getRank().getLevel()));
        return replacedMessage;
    }

    public static String makeRanksCommaList(List<Rank> list){
        String commaList = null;
        for (Rank rank : list){
            commaList = commaList + rank.getName() + ", ";
        }
        return commaList;
    }

    public static List<String> getOnOffPlayers(Map<UUID, String> membersM, boolean online){
        List<String> members = new ArrayList<>();
            for (UUID member : membersM.keySet()){
            Player player = Bukkit.getPlayerExact(Bukkit.getOfflinePlayer(member).getName());
            if (player == null && !online){
                members.add(Bukkit.getOfflinePlayer(member).getName());
            }
            if (player != null && online){
                members.add(Bukkit.getOfflinePlayer(member).getName());
            }
        }
        return members;
    }

    public static String replaceFieldPlaceholdersKingdom(Kingdom kingdom, String message){
        String replacedMessage = message.replace("%kingdomName%",kingdom.getName());
        replacedMessage = replacedMessage.replace("%kingdomRoles%",makeRanksCommaList(kingdom.getRanks()));
        replacedMessage = replacedMessage.replace("%kingdomDefaultRole%",kingdom.getDefaultRank().getName());
        if (kingdom.getSpawn() != null){
            replacedMessage = replacedMessage.replace("%kingdomSpawnX%",String.valueOf(kingdom.getSpawn().getX()));
            replacedMessage = replacedMessage.replace("%kingdomSpawnY%",String.valueOf(kingdom.getSpawn().getY()));
            replacedMessage = replacedMessage.replace("%kingdomSpawnZ%",String.valueOf(kingdom.getSpawn().getZ()));
            replacedMessage = replacedMessage.replace("%kingdomSpawnYaw%",String.valueOf(kingdom.getSpawn().getYaw()));
            replacedMessage = replacedMessage.replace("%kingdomSpawnPitch%",String.valueOf(kingdom.getSpawn().getPitch()));
            replacedMessage = replacedMessage.replace("%kingdomSpawnWorldName%",String.valueOf(kingdom.getSpawn().getWorldName()));
        }
        replacedMessage = replacedMessage.replace("%kingdomRolesCount%",String.valueOf(kingdom.getRanks().size()));
        replacedMessage = replacedMessage.replace("%kingdomMemberCount%",String.valueOf(kingdom.getMemberCount()));
        replacedMessage = replacedMessage.replace("%kingdomMaxMembers%",String.valueOf(kingdom.getMaxMembers()));
        replacedMessage = replacedMessage.replace("%kingdomOnlineMembers%",String.join(", ",getOnOffPlayers(kingdom.getMembers(),true)));
        replacedMessage = replacedMessage.replace("%kingdomOnlineMembersCount%",String.valueOf(getOnOffPlayers(kingdom.getMembers(),true).size()));
        replacedMessage = replacedMessage.replace("%kingdomOfflineMembers%",String.join(", ",getOnOffPlayers(kingdom.getMembers(),false)));
        replacedMessage = replacedMessage.replace("%kingdomOfflineMembersCount%",String.valueOf(getOnOffPlayers(kingdom.getMembers(),false).size()));
        return replacedMessage;
    }

    public static String replaceFieldPlaceholders(OfflinePlayer offlinePlayer, String message){
        Timestamp joinTS = new Timestamp(offlinePlayer.getFirstPlayed());
        Timestamp lastPlayerTS = new Timestamp(offlinePlayer.getLastPlayed());
        String replacedMessage = message.replace("%username%",offlinePlayer.getName());
        replacedMessage = replacedMessage.replace("%serverJoindate%",joinTS.toString());
        replacedMessage = replacedMessage.replace("%lastPlayed%", lastPlayerTS.toString());
        return replacedMessage;
    }

    public static EmbedBuilder embedCreator(String commandPath,String embedPath){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(config.getString("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".title"), null);
        eb.setColor(new Color(config.getInt("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".color.r"), config.getInt("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".color.g"), config.getInt("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".color.b")));
        eb.setDescription(config.getString("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".description"));
        if (config.getBoolean("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".footer.enabled"))
            eb.setFooter(config.getString("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".footer.message"));
        if (config.getBoolean("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".thumbnail.enabled"))
            eb.setThumbnail(config.getString("Bot.special-rank.commands."+commandPath+".embeds."+embedPath+".thumbnail.image-link"));
        return eb;
    }

    public static EmbedBuilder embedCreatorN(String commandPath,String embedPath){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(config.getString("Bot."+commandPath+".embeds."+embedPath+".title"), null);
        eb.setColor(new Color(config.getInt("Bot."+commandPath+".embeds."+embedPath+".color.r"), config.getInt("Bot."+commandPath+".embeds."+embedPath+".color.g"), config.getInt("Bot."+commandPath+".embeds."+embedPath+".color.b")));
        eb.setDescription(config.getString("Bot."+commandPath+".embeds."+embedPath+".description"));
        if (config.getBoolean("Bot."+commandPath+".embeds."+embedPath+".footer.enabled"))
            eb.setFooter(config.getString("Bot."+commandPath+".embeds."+embedPath+".footer.message"));
        if (config.getBoolean("Bot."+commandPath+".embeds."+embedPath+".thumbnail.enabled"))
            eb.setThumbnail(config.getString("Bot."+commandPath+".embeds."+embedPath+".thumbnail.image-link"));
        return eb;
    }


    public static String capS(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static SelectionMenu selectionMenuRanksCreator(HashMap<Rank,Integer> ranksMap,String target){
        Collection<SelectOption> ranksOptions = new ArrayList<>();
        for (Rank rank : ranksMap.keySet()) {
            SelectOption rankOpt = SelectOption.of(capS(rank.getName()), rank.getName()+":"+target);
            ranksOptions.add(rankOpt);
        }
        return SelectionMenu.create("menu:ranks")
                .setPlaceholder(config.getString("Bot.special-rank.commands.kingdom-promote.embeds.selection.selection-menu-placeholder"))
                .setRequiredRange(1,1)
                .addOptions(ranksOptions)
                .build();
    }

}
