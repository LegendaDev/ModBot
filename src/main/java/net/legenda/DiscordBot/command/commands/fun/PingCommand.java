package net.legenda.DiscordBot.command.commands.fun;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;

@Command.cmdInfo(name = "Ping", description = "Pong", type = Command.Type.Fun)
public class PingCommand extends Command{

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        sendEmbedMessage("Pong: `" + event.getJDA().getPing() + "ms`", event.getTextChannel(), false);
    }
}
