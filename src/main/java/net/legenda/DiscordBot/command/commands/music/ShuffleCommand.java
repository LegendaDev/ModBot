package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;

@CommandInfo(name = "Shuffle", description = "Shuffles the queue", type = CommandType.Music, role = "DJ")
public class ShuffleCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.shuffle(event.getGuild());
        sendMessage(":twisted_rightwards_arrows: Shuffled Queue:", event.getTextChannel());
    }
}
