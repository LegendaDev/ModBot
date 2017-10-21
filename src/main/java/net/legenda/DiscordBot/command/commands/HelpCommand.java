package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.managers.CommandManager;

@Command.cmdInfo(name = "Help", description = "A help command")
public class HelpCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        StringBuilder msg = new StringBuilder();
        if(args.length < 1){
            msg.append("__Admin Commands:__ \n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Admin)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));

            msg.append("\n__Music Commands:__ \n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Music)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));

            msg.append("\n__Fun Commands:__\n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Fun)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));

            msg.append("\n__Other Commands:__\n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(Type.Misc)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));
        } else{
            String casez = args[0];
            Type type = null;
            switch(casez.toLowerCase()){
                case "admin":
                    type = Type.Admin;
                    break;
                case "music":
                    type = Type.Music;
                    break;
                case "fun" :
                    type = Type.Fun;
                    break;
                case "misc":
                    type = Type.Misc;
            }
            if(type == null)
                throw new InvalidCommandArgumentException("Usage: .help <cmdType>");
            final Type type2 = type;
            msg.append("__").append(type2.name()).append(" Commands__:\n\n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(type2)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));
        }

        String message = new String(msg);
        sendEmbedMessage(message, event.getTextChannel(), false);

    }
}
