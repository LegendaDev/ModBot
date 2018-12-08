package net.legenda.DiscordBot.command.commands.fun;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.command.CommandInfo;
import net.legenda.DiscordBot.command.CommandType;

@CommandInfo(name = "Ping", description = "Pong", type = CommandType.Fun)
public class PingCommand extends Command{

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        sendEmbedMessage("Pong: `" + event.getJDA().getPing() + "ms`", event.getTextChannel(), false);
    }
}
