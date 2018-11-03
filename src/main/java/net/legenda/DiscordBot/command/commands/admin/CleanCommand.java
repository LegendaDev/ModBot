package net.legenda.DiscordBot.command.commands.admin;

import net.dv8tion.jda.core.Permission;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command.cmdInfo(name = "Clean", description = "Removes messages from textChannel", type = Command.Type.Admin, permission = Permission.MESSAGE_MANAGE)
public class CleanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        int amount = 100;
        String[] strippedArgs = stripUsers(args);
        String selected = "all";
        Stream<Message> history = event.getChannel().getIterableHistory().complete().stream().filter(message -> !message.equals(event.getMessage()));
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: .Clean <all/commands/\"message\">* <Amount(default 100)>* <@User>*");
        try {
            if (strippedArgs.length == 1)
                amount = Integer.parseInt(strippedArgs[0]);
            else {
                selected = strippedArgs[0];
                amount = Integer.parseInt(strippedArgs[1]);
            }
        } catch (Exception e) {
            selected = strippedArgs[0];
        }

        if (amount > 100 || amount < 1)
            throw new InvalidCommandArgumentException("Enter a number between 1 - 100, inclusively");
        List<Message> toDelete;
        if (!event.getMessage().getMentionedUsers().isEmpty())
            history = history.filter(message -> event.getMessage().getMentionedUsers().stream().anyMatch(user -> message.getAuthor().equals(user)));
        if (selected.equalsIgnoreCase("all")) {
            toDelete = history.limit(amount).collect(Collectors.toList());
        } else if (selected.equalsIgnoreCase("commands")) {
            toDelete = history.filter(message -> Main.INSTANCE.cmdManager.isCommand(message) || message.getAuthor().isBot()).limit(amount).collect(Collectors.toList());
        } else if (selected.matches("[\"'].*[\"']")) {
            final String invalidMessage = selected.substring(1, selected.length() - 1).toLowerCase();
            toDelete = history.filter(message -> message.getContentDisplay().toLowerCase().contains(invalidMessage)).limit(amount).collect(Collectors.toList());
        } else if (args[0].startsWith("<@")) {
            toDelete = history.limit(amount).collect(Collectors.toList());
        } else {
            throw new InvalidCommandArgumentException("Usage: .Clean <all/commands/@User/\"message\">* <Amount(default 100)>*");
        }

        try {
            if (toDelete.isEmpty())
                throw new InvalidCommandStateException("The message query returned no messages to delete");
            else if (toDelete.size() <= 1)
                event.getChannel().deleteMessageById(toDelete.get(0).getId()).queue();
            else
                event.getTextChannel().deleteMessages(toDelete).queue();
        } catch (IllegalArgumentException e) {
            event.getChannel().deleteMessageById(event.getMessageId()).queueAfter(3L, TimeUnit.SECONDS);
            sendErrorMessage("Cannot Delete Messages Older Than 2 Weeks", event.getTextChannel(), true);
            return;
        }

        event.getChannel().deleteMessageById(event.getMessageId()).queueAfter(3L, TimeUnit.SECONDS);
        sendEmbedMessage(":recycle:" + toDelete.size() + " Messages Cleaned", event.getTextChannel(), true);
    }

    private String[] stripUsers(String[] args) {
        StringBuilder strippedArgs = new StringBuilder();
        for (String arg : args)
            if (!arg.matches("[<@].*[>]"))
                strippedArgs.append(arg).append(" ");
        return strippedArgs.toString().substring(0, strippedArgs.length() - 1).split(" ");
    }

}
