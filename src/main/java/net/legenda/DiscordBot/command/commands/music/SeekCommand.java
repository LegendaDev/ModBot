package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Command.cmdInfo(name = "Seek", description = "Sets position of the current track", type = Command.Type.Music, role = "DJ")
public class SeekCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if(args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.Seek <time:stamp>`");
        String arg = args[0];
        String regex1 = "(?:\\d?\\d):(?:[012345]\\d)";
        String regex2 = "(?:\\d?\\d):(?:[012345]\\d):(?:[012345]\\d)";
        long time;
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher match1 = pattern1.matcher(arg);
        Matcher match2 = pattern2.matcher(arg);
        if (match1.find()){
            String minutes = arg.split(":")[0];
            String seconds = arg.split(":")[1];
            time = Long.parseLong(minutes) * 60000 + Long.parseLong(seconds) * 1000;

        } else if (match2.find()) {
            String hours = arg.split(":")[0];
            String minutes = arg.split(":")[1];
            String seconds = arg.split(":")[2];
            time = Long.parseLong(hours) * 3600000 + Long.parseLong(minutes) * 60000 + Long.parseLong(seconds) * 1000;
        }  else {
            throw new InvalidCommandArgumentException("Must be a time stamp in format `minutes:seconds`");
        }
        sendMessage(":mag: Seeked: `" + arg + "`", event.getTextChannel());
        Main.INSTANCE.musicUtils.seek(event.getGuild(), time);
    }
}
