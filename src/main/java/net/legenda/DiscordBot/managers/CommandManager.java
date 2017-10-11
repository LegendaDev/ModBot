package net.legenda.DiscordBot.managers;

import net.dv8tion.jda.core.Permission;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.commands.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.legenda.DiscordBot.exceptions.IllegalCommandAccessException;
import net.legenda.DiscordBot.exceptions.InvalidCommandException;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    public static final HashMap<String, Command> commands = new LinkedHashMap<>();

    public CommandManager() {
        initCommands();
    }

    static void execute(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        Guild guild = event.getGuild();
        User user = event.getAuthor();
        TextChannel channel = event.getTextChannel();
        if (msg.getContent().startsWith(Main.INSTANCE.cmdPrefix)) {
            String message = msg.getContent().substring(1);
            String[] args = message.split(" ");
            for (Command cmd : commands.values()) {
                if (cmd.getName().equalsIgnoreCase(args[0])) {
                    if (hasPermission(cmd, guild, channel, user)) {
                        if (hasRole(cmd, guild, user)) {
                            String[] arguments = Arrays.copyOfRange(args, 1, args.length);
                            cmd.execute(arguments, event);
                            return;
                        } else {
                            throw new IllegalCommandAccessException("You don't have the required role (" + cmd.getRole() + ")");
                        }
                    } else {
                        throw new IllegalCommandAccessException("You don't have the required permission (" + cmd.getPermission().toString() + ")");
                    }
                }
            }
            throw new InvalidCommandException("Unknown command: Use .help ");
        }
    }

    private void initCommands() {
        commands.put("BanCommand", new BanCommand());
        commands.put("CleanCommand", new CleanCommand());
        commands.put("DeveloperCommand", new DevelopersCommand());
        commands.put("KickCommand", new KickCommand());
        commands.put("HelpCommand", new HelpCommand());
        commands.put("PingCommand", new PingCommand());
        commands.put("RollCommand", new RollCommand());

    }

    public boolean isCommand(Message msg) {
        if (!msg.getContent().startsWith(Main.INSTANCE.cmdPrefix))
            return false;
        String message = msg.getContent().substring(1);
        String[] args = message.split(" ");
        for (Command cmd : commands.values()) {
            if (cmd.getName().equalsIgnoreCase(args[0])) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRole(Command cmd, Guild guild, User user) {
        List<Role> roles = Main.roles.values().stream().filter(role -> role.getGuild().equals(guild)).collect(Collectors.toList());
        int hierarchyofperm = 10000;
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getName().equalsIgnoreCase(cmd.getRole()))
                hierarchyofperm = i;
        }
        int hierarchyofuser = roles.indexOf(guild.getMember(user).getRoles().get(0));
        if (hierarchyofperm >= hierarchyofuser)
            return true;
        return false;
    }

    private static boolean hasPermission(Command cmd, Guild guild, Channel channel, User user) {
        if (cmd.getPermission() == Permission.UNKNOWN)
            return true;
        if (PermissionUtil.checkPermission(channel, guild.getMember(user), cmd.getPermission()))
            return true;
        return false;
    }

}
