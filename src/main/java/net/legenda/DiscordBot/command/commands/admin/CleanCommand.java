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
        int amount;
        String selected = "all";
        Stream<Message> history = event.getChannel().getIterableHistory().complete().stream().filter(message -> !message.equals(event.getMessage()));
        if (args.length <= 0)
            throw new InvalidCommandArgumentException("Usage: .Clean <Amount(default 100)> <all/commands/@User>*");

        if (args.length > 1) {
            selected = args[1];
        }
        try {
            amount = Integer.parseInt(args[0]);
        } catch (Exception e){
            amount = 100;
            selected = args[0];
        }
        if (amount > 100 || amount < 1)
            throw new InvalidCommandArgumentException("Enter a number between 1 - 100, inclusively");
        List<Message> toDelete;
        if (selected.equalsIgnoreCase("all")) {
            toDelete = history.limit(amount).collect(Collectors.toList());
        } else if (selected.equalsIgnoreCase("commands")) {
            toDelete = history.filter(message -> Main.INSTANCE.cmdManager.isCommand(message) || message.getAuthor().isBot()).limit(amount).collect(Collectors.toList());
        } else {
            if (selected.startsWith("@")) {
                toDelete = history.filter(message -> message.getAuthor().equals(event.getMessage().getMentionedUsers().get(0))).limit(amount).collect(Collectors.toList());
            } else
                throw new InvalidCommandArgumentException("Usage: .Clean <Amount> <all/commands/@User>*");
            if (toDelete.isEmpty()) {
                throw new InvalidCommandStateException("That user has not posted any messages in that channel");
            }
        }

        if (toDelete.isEmpty())
            throw new InvalidCommandStateException("The message query returned no messages to delete");
        else if (toDelete.size() <= 1)
            event.getChannel().deleteMessageById(toDelete.get(0).getId()).queue();
        else
            event.getTextChannel().deleteMessages(toDelete).queue();

        event.getChannel().deleteMessageById(event.getMessageId()).queueAfter(3L, TimeUnit.SECONDS);
        sendEmbedMessage(":recycle:" + amount + " Messages Cleaned", event.getTextChannel(), true);

    }

}
