package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

@Command.cmdInfo(name = "Clear", description = "Clears the current queue", type = Command.Type.Music, alias = {"connect"})
public class JoinCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        if (channel == null)
            throw new InvalidCommandStateException("You must be in a VoiceChannel to summon the bot");
        event.getGuild().getAudioManager().openAudioConnection(channel);
        sendMessage(":arrow_down: Joined:", event.getTextChannel());
    }
}
