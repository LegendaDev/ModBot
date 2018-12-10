package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;
import net.legenda.DiscordBot.managers.TrackManager;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

@CommandInfo(name = "Leave", description = "Leaves current channel", type = CommandType.Music, aliases = {"disconnect"})
public class LeaveCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        if (guild.getAudioManager().getConnectedChannel() == null && !guild.getAudioManager().isConnected())
            throw new InvalidCommandStateException("Currently not connected to any voice channels");
        Main.INSTANCE.musicUtils.closeAudio(guild);
        sendMessage(":arrow_up: Left channel", event.getTextChannel());
    }
}