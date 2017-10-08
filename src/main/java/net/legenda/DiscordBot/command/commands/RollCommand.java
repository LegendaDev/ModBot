package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;

@Command.cmdInfo(name = "Roll", description = "Rolls a dice", type = Command.Type.Fun)
public class RollCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event){
        int sides = 6;
        if(args.length > 1)
            sides = Integer.parseInt(args[1]);
        int output = (int) Math.round((Math.random() * (sides - 1) + 1) * 10);
    }

}
