package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.utils.MusicUtils;

@Command.cmdInfo(name = "Skip", description = "Skips the current playing song", type = Command.Type.Music)
public class SkipCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String currentsong = MusicUtils.players.get(event.getGuild()).getKey().getPlayingTrack().getInfo().title;
        sendMessage(":track_next: Skipped: `" + currentsong + "`", event.getTextChannel());
        Main.INSTANCE.musicUtils.skipTrack(event.getGuild());
    }
}