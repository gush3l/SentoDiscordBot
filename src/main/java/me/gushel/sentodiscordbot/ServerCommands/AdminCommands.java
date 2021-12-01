package me.gushel.sentodiscordbot.ServerCommands;

import me.gushel.sentodiscordbot.MySQL.MySQL;
import me.gushel.sentodiscordbot.SentoDiscordBot;
import me.gushel.sentodiscordbot.Util;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class AdminCommands implements CommandExecutor {

    private FileConfiguration config = SentoDiscordBot.getInstance().getConfig();

    public int generateRandomCode(){
        int min = config.getInt("Minecraft.link-command.code-generator.range-min");
        int max = config.getInt("Minecraft.link-command.code-generator.range-max");
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.hasPermission("sentodiscordbot.admin")){
            sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.no-permission")));
        }
        if (args.length == 0){
            sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-unlink.syntax")));
            sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-link.syntax")));
            return true;
        }
        if (args.length == 4 && args[0].equals("unlink")){
            if (args[1].equalsIgnoreCase("discordID")) {
                Member member = SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).getMemberById(args[2]);
                if (member == null && !args[3].equalsIgnoreCase("true")) {
                    sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-unlink.invalid-discord-id")));
                    return true;
                }
                MySQL.removeUserFromDatabaseID(args[2]);
                SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).removeRoleFromMember(SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).getMemberById(args[2]),SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).getRoleById(config.getString("Bot.link-command.linked-role")));
                sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-unlink.discord-unlink")));
                return true;
            }
            if (args[1].equalsIgnoreCase("uuid")) {
                OfflinePlayer member = Bukkit.getOfflinePlayer(args[2]);
                if (member == null && !args[3].equalsIgnoreCase("true")) {
                    sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-unlink.invalid-id")));
                    return true;
                }
                MySQL.removeUserFromDatabase(args[2]);
                sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-unlink.unlink")));
                return true;
            }
        }
        if (args.length == 3 && args[0].equals("link")){
            String uuid = args[1];
            String discordID = args[2];
            String verificationCode = String.valueOf(generateRandomCode());
            MySQL.addUserToDatabase(uuid,discordID,verificationCode);
            SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).addRoleToMember(SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).getMemberById(discordID),SentoDiscordBot.jda.getGuildById(config.getString("Bot.server-id")).getRoleById(config.getString("Bot.link-command.linked-role"))).queue();
            sender.sendMessage(Util.color(config.getString("Minecraft.messages.admin-commands.force-link.link")));
        }
        return true;
    }

}
