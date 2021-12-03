package me.gushel.sentodiscordbot;

import me.gushel.sentodiscordbot.DiscordListeners.*;
import me.gushel.sentodiscordbot.MySQL.MySQL;
import me.gushel.sentodiscordbot.ServerCommands.AdminCommands;
import me.gushel.sentodiscordbot.ServerCommands.LinkCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.HashMap;

import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public final class SentoDiscordBot extends JavaPlugin {

    private static SentoDiscordBot plugin;

    public static JDA jda;

    public static HashMap<String,String> notVerifiedUsers = new HashMap<>();

    private static Permission perms = null;


    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        plugin = this;
        saveDefaultConfig();
        Config.setup();
        Config.get().options().copyDefaults(true);
        Config.save();
        FileConfiguration config = getConfig();
        Activity activity;
        this.getCommand("link").setExecutor(new LinkCommand());
        this.getCommand("sentodiscordbot").setExecutor(new AdminCommands());
        switch (config.getString("Bot.activity.type")) {
            case "watching":
                activity = Activity.watching(config.getString("Bot.activity.watching.string"));
                break;
            case "playing":
                activity = Activity.playing(config.getString("Bot.activity.playing.string"));
                break;
            case "listening":
                activity = Activity.listening(config.getString("Bot.activity.listening.string"));
                break;
            case "competing":
                activity = Activity.competing(config.getString("Bot.activity.competing.string"));
                break;
            case "streaming":
                if (Activity.isValidStreamingUrl(config.getString("Bot.activity.streaming.url"))) {
                    activity = Activity.streaming(config.getString("Bot.activity.streaming.string"), config.getString("Bot.activity.streaming.url"));
                } else {
                    activity = Activity.playing(config.getString("Bot.activity.default.string"));
                }
                break;
            default:
                activity = Activity.playing(config.getString("Bot.activity.default.string"));
                break;
        }
        try {
            jda = JDABuilder.createLight(config.getString("Bot.token"), Collections.emptyList())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new Link())
                    .addEventListeners(new Kingdom())
                    .addEventListeners(new KingdomKick())
                    .addEventListeners(new KingdomPromote())
                    .addEventListeners(new KingdomInfo())
                    .addEventListeners(new SelectionListener())
                    .addEventListeners(new KingdomList())
                    .setActivity(activity)
                    .build().awaitReady();
            CommandListUpdateAction commands = jda.getGuildById(config.getString("Bot.server-id")).updateCommands();

            commands.addCommands(
                    new CommandData("link", config.getString("Bot.link-command.description"))
                            .addOptions(new OptionData(STRING, config.getString("Bot.link-command.argument.name"),
                                    config.getString("Bot.link-command.argument.description"))
                                    .setRequired(true))
            );
            commands.addCommands(
                    new CommandData("kingdom-kick", config.getString("Bot.special-rank.commands.kingdom-kick.description"))
                            .addOptions(new OptionData(STRING, config.getString("Bot.special-rank.commands.kingdom-kick.argument.name"),
                                    config.getString("Bot.special-rank.commands.kingdom-kick.argument.description"))
                                    .setRequired(true))
            );
            commands.addCommands(
                    new CommandData("kingdom-promote", config.getString("Bot.special-rank.commands.kingdom-promote.description"))
                            .addOptions(new OptionData(STRING, config.getString("Bot.special-rank.commands.kingdom-promote.argument.name"),
                                    config.getString("Bot.special-rank.commands.kingdom-promote.argument.description"))
                                    .setRequired(true))
            );
            commands.addCommands(
                    new CommandData("kingdom", config.getString("Bot.kingdom-command.description"))
                            .addOptions(new OptionData(STRING, config.getString("Bot.kingdom-command.argument.name"),
                                    config.getString("Bot.kingdom-command.argument.description"))
                                    .setRequired(true))
            );
            commands.addCommands(
                    new CommandData("kingdom-info", config.getString("Bot.kingdom-info-command.description"))
                            .addOptions(new OptionData(STRING, config.getString("Bot.kingdom-info-command.argument.name"),
                                    config.getString("Bot.kingdom-info-command.argument.description"))
                                    .setRequired(true))
            );
            commands.addCommands(
                    new CommandData("kingdom-list", config.getString("Bot.kingdom-info-command.description"))
            );
            commands.queue();
            getLogger().info("The Discord bot started succesfully!");
        } catch (LoginException | InterruptedException e) {
            getLogger().severe("An error has occurred trying to start the Discord bot!");
            e.printStackTrace();
        }
        MySQL.connect();
        MySQL.createTable();
        setupPermissions();
        getLogger().info("The plugin started in a time of " + (System.currentTimeMillis() - time) + " ms!");
    }

    @Override
    public void onDisable() {
        MySQL.disconnect();
    }

    public static SentoDiscordBot getInstance(){
        return plugin;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPermissions() {
        return perms;
    }

}
