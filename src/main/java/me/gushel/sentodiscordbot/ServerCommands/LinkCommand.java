package me.gushel.sentodiscordbot.ServerCommands;

import me.gushel.sentodiscordbot.MySQL.MySQL;
import me.gushel.sentodiscordbot.SentoDiscordBot;
import me.gushel.sentodiscordbot.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LinkCommand implements CommandExecutor {
    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();
    public int generateRandomCode(){
        int min = config.getInt("Minecraft.link-command.code-generator.range-min");
        int max = config.getInt("Minecraft.link-command.code-generator.range-max");
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            sender.sendMessage(Util.color("&aYou need to use this command from in-game!!"));
            return true;
        }
        Player player = (Player) sender;
        int linkCode = generateRandomCode();
        if (SentoDiscordBot.notVerifiedUsers.containsKey(String.valueOf(player.getUniqueId()))){
            String vLinkCode = SentoDiscordBot.notVerifiedUsers.get(String.valueOf(player.getUniqueId()));
            for (String string : config.getStringList("Minecraft.messages.link.link-already-started")){
                player.sendMessage(Util.colorPapi(player,string.replace("%linkcode%",vLinkCode)));
            }
            return true;
        }
        if (MySQL.getVerifyCodeFromUUID(String.valueOf(player.getUniqueId())) != null){
            for (String string : config.getStringList("Minecraft.messages.link.already-linked")){
                player.sendMessage(Util.colorPapi(player,string));
            }
            return true;
        }
        for (String string : config.getStringList("Minecraft.messages.link.link-new")){
            player.sendMessage(Util.colorPapi(player,string.replace("%linkcode%",String.valueOf(linkCode))));
        }
        SentoDiscordBot.notVerifiedUsers.put(String.valueOf(player.getUniqueId()),String.valueOf(linkCode));
        return true;
    }

}
