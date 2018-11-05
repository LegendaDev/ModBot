package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.managers.TrackManager;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;
import net.legenda.DiscordBot.utils.MusicUtils;

@Command.cmdInfo(name = "Leave", description = "Leaves current channel", type = Command.Type.Music, alias = {"disconnect"})
public class LeaveCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        AudioManager manager = event.getGuild().getAudioManager();
        if (event.getGuild().getAudioManager().getConnectedChannel() == null && !event.getGuild().getAudioManager().isConnected())
            throw new InvalidCommandStateException("Currently not connected to any voice channels");
        TrackManager track = Main.INSTANCE.musicUtils.getTrackManager(event.getGuild());
        Main.INSTANCE.musicUtils.players.remove(event.getGuild());
        manager.closeAudioConnection();
        track.clearQueue(true);
        sendMessage(":arrow_up: Left channel", event.getTextChannel());
    }
}