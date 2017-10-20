package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;

@Command.cmdInfo(name = "Clear", description = "Clears the current queue", type = Command.Type.Music)
public class ShuffleCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.shuffle(event.getGuild());
        sendMessage(":twisted_rightwards_arrows: Shuffled Queue:", event.getTextChannel());
    }
}
