package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;

@Command.CmdInfo(name = "Ping", description = "Pong", type = Command.Type.Fun)
public class Ping extends Command{

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        long ping = event.getAuthor().getJDA().getPing();
        sendEmbedMessage("Pong: `" + ping + "ms`", event.getChannel(), false);

    }
}
