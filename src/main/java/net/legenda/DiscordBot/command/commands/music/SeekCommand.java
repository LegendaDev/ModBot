package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Command.cmdInfo(name = "Seek", description = "Skips to a particular point", type = Command.Type.Music)
public class SeekCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if(args.length < 1)
            throw new InvalidCommandArgumentException("Usage: .Seek <time:stamp>");
        String arg = args[0];
        String regex = "(?:\\d?\\d):(?:[012345]\\d)";
        Long time;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(arg);
        if (m.find()){
            String minutes = arg.split(":")[0];
            String seconds = arg.split(":")[1];
            System.out.println(minutes + ":" + seconds);
            time = Long.parseLong(minutes) * 60000 + Long.parseLong(seconds) * 1000;
        } else {

            throw new InvalidCommandArgumentException("Must be a time stamp in format minutes:seconds");
        }

        Main.INSTANCE.musicUtils.seek(event.getGuild(), time, event.getTextChannel());
    }
}
