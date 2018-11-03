package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.util.HashMap;
import java.util.Map;

@Command.cmdInfo(name = "Loop", description = "Loops current track", type = Command.Type.Music, role = "DJ", alias = {"Repeat"})
public class LoopCommand extends Command {

    private final static HashMap<Guild, Integer> times = new HashMap<>();

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        int times = getGuildRepeat(guild);
        if (args.length >= 1) {
            int input;
            try {
                input = Integer.parseInt(args[0]);

            } catch (NumberFormatException e) {
                throw new InvalidCommandStateException("Usage: `.Loop (Amount)` \n`.Loop to toggle`");
            }
            times = input + 1;
            setGuildRepeat(guild, times);
            sendMessage(":repeat: Loop: `" + input + "` times", channel);
            return;
        }
        if (times <= -1) {
            times = 1;
            sendMessage(":repeat: Loop: Disabled", channel);
        } else {
            times = -1;
            sendMessage(":repeat: Loop: ∞", channel);
        }
        setGuildRepeat(guild, times);

    }

    private static int getGuildRepeat(Guild guild) {
        return times.entrySet().stream().filter(guildIntegerEntry -> guildIntegerEntry.getKey().equals(guild)).map(Map.Entry::getValue).findFirst().orElse(1);
    }

    private static void setGuildRepeat(Guild guild, Integer value) {
        times.remove(guild);
        times.put(guild, value);
    }

    public static boolean getRepeat(Guild guild) {
        int times = getGuildRepeat(guild);
        if (times != 0)
            times--;
        setGuildRepeat(guild, times);
        return times <= -1 || times != 0;
    }

    public static void endRepeat(Guild guild) {
        setGuildRepeat(guild, 0);
    }

}
