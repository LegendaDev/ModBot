package net.legenda.DiscordBot.command.commands;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;

import java.util.List;

@Command.cmdInfo(name = "Developers", description = "Defines the developer role")
public class DevelopersCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String onlineDevs = !getOnlineDevs(event.getGuild()).isEmpty() ? "\n*Online Devs include:*\n" + getOnlineDevs(event.getGuild()) : "";
        sendEmbedMessage("A developer is an individual that builds and create software and applications. " +
                "He or she writes, debugs and executes the source code of a software application. " + onlineDevs, event.getTextChannel(), false);
    }

    private String getOnlineDevs(Guild guild) {
        List<Member> members = guild.getMembers();
        StringBuilder builder = new StringBuilder();
        for (Member member : members) {
            if (!member.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                if (member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("Developer"))) {
                    builder.append("-").append(member.getUser().getName()).append("\n");
                }
        }
        return new String(builder);
    }
}
