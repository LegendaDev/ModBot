package net.legenda.DiscordBot.command.commands.fun;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Command.cmdInfo(name = "Random", description = "Tags a random member with your message", type = Command.Type.Fun)
public class RandomCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            throw new InvalidCommandArgumentException("Usage: .Random <Message>*");
        }
        String message = args[0];
        Guild guild = event.getGuild();
        List<Member> members = guild.getMembers().stream().filter(member -> !member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).filter(member -> !member.equals(event.getMember())).filter(member -> !member.getUser().isBot()).collect(Collectors.toList());
        Member random = members.get(new Random().nextInt(members.size()));
        if(random == null)
            throw new InvalidCommandStateException("Search yielded no results");
        sendEmbedMessage(random.getAsMention() + " " + message, event.getTextChannel(), false);

    }
}
