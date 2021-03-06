package net.legenda.DiscordBot.managers;

import net.dv8tion.jda.core.Permission;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.legenda.DiscordBot.command.commands.admin.*;
import net.legenda.DiscordBot.command.commands.fun.PingCommand;
import net.legenda.DiscordBot.command.commands.fun.RandomCommand;
import net.legenda.DiscordBot.command.commands.fun.RollCommand;
import net.legenda.DiscordBot.command.commands.misc.DevelopersCommand;
import net.legenda.DiscordBot.command.commands.misc.HelpCommand;
import net.legenda.DiscordBot.command.commands.misc.InfoCommand;
import net.legenda.DiscordBot.command.commands.music.*;
import net.legenda.DiscordBot.exceptions.IllegalCommandAccessException;
import net.legenda.DiscordBot.exceptions.InvalidCommandException;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    public static final HashMap<String, Command> commands = new LinkedHashMap<>();

    static void execute(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        Guild guild = event.getGuild();
        User user = event.getAuthor();
        TextChannel channel = event.getTextChannel();
        if (msg.getContentDisplay().startsWith(Main.INSTANCE.cmdPrefix)) {
            String message = msg.getContentRaw().substring(Main.INSTANCE.cmdPrefix.length());
            String[] args = message.split(" ");
            if (!message.isEmpty()) {
                for (Command cmd : commands.values()) {
                    if (cmd.getName().equalsIgnoreCase(args[0]) || Arrays.stream(cmd.getAlias()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                        if (hasPermission(cmd, guild, channel, user)) {
                            if (hasRole(cmd, guild, user) || PermissionUtil.checkPermission(channel, guild.getMember(user), Permission.ADMINISTRATOR)) {
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
            }
            throw new InvalidCommandException("Unknown command: Use .help ");
        }
    }

    public void initCommands() {
        //Administrative
        commands.put("BanCommand", new BanCommand());
        commands.put("CleanCommand", new CleanCommand());
        commands.put("CooldownCommand", new CooldownCommand());
        commands.put("KickCommand", new KickCommand());
        commands.put("LogEventCommand", new LogEventCommand());
        commands.put("UnbanCommand", new UnbanCommand());

        //Fun
        commands.put("PingCommand", new PingCommand());
        commands.put("RandomCommand", new RandomCommand());
        commands.put("RollCommand", new RollCommand());

        //Misc
        commands.put("DeveloperCommand", new DevelopersCommand());
        commands.put("HelpCommand", new HelpCommand());
        commands.put("InfoCommand", new InfoCommand());

        //Music
        commands.put("ClearCommand", new ClearCommand());
        commands.put("PlayCommand", new PlayCommand());
        commands.put("PlayTopCommand", new PlayTopCommand());
        commands.put("JoinCommand", new JoinCommand());
        commands.put("QueueCommand", new QueueCommand());
        commands.put("LeaveCommand", new LeaveCommand());
        commands.put("LoopCommand", new LoopCommand());
        commands.put("NowPlayingCommand", new NowPlayingCommand());
        commands.put("SeekCommand", new SeekCommand());
        commands.put("SkipCommand", new SkipCommand());
        commands.put("SkipToCommand", new SkipToCommand());
        commands.put("ShuffleCommand", new ShuffleCommand());
        commands.put("StopCommand", new StopCommand());
        commands.put("RemoveCommand", new RemoveCommand());
        commands.put("ResumeCommand", new ResumeCommand());

    }

    public boolean isCommand(Message msg) {
        if (!msg.getContentDisplay().startsWith(Main.INSTANCE.cmdPrefix))
            return false;
        String message = msg.getContentDisplay().substring(1);
        String[] args = message.split(" ");
        for (Command cmd : commands.values()) {
            if (cmd.getName().equalsIgnoreCase(args[0]) || Arrays.stream(cmd.getAlias()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRole(Command cmd, Guild guild, User user) {
        List<Role> roles = guild.getRoles();
        int hierarchyofperm = 10000;
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getName().equalsIgnoreCase(cmd.getRole()))
                hierarchyofperm = i;
        }
        int hierarchyofuser = roles.indexOf(guild.getMember(user).getRoles().get(0));
        return hierarchyofperm >= hierarchyofuser;
    }

    private static boolean hasPermission(Command cmd, Guild guild, Channel channel, User user) {
        return cmd.getPermission() == Permission.UNKNOWN || PermissionUtil.checkPermission(channel, guild.getMember(user), cmd.getPermission());
    }

}
