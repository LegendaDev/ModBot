package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

@Command.cmdInfo(name = "SkipTo", description = "Skips to a position within the queue", type = Command.Type.Music, role = "DJ")
public class SkipToCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String currentsong = Main.INSTANCE.musicUtils.getAudioPlayer(event.getGuild()).getPlayingTrack().getInfo().title;
        sendMessage(":track_next: Skipped: `" + currentsong + "`", event.getTextChannel());
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usuage: .SkipTo (Position)");
        int position = Integer.parseInt(args[0]);
        if (position != 1)
            Main.INSTANCE.musicUtils.skipTrack(event.getGuild(), position);
    }

}
