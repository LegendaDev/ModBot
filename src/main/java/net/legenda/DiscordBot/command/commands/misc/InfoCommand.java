package net.legenda.DiscordBot.command.commands.misc;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandArgumentException;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Command.cmdInfo(name = "UserInfo", description = "Lists out a user's information")
public class InfoCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        User user;
        if (args.length > 0)
            if (event.getMessage().getMentionedUsers().size() > 0 && args[0].matches("<@[0-9]+>"))
                user = event.getMessage().getMentionedUsers().get(0);
            else if (args[0].matches("[0-9]+"))
                try {
                    user = Main.INSTANCE.jdaBot.getUserById(args[0]);
                } catch (Exception e) {
                    throw new InvalidCommandArgumentException("Usage: `.UserInfo <@User || UserID>`");
                }
            else
                throw new InvalidCommandArgumentException("Usage: `.UserInfo <@User || UserID>`");
        else
            user = event.getAuthor();


        Member member = event.getGuild().getMember(user);
        EmbedBuilder builder = Main.INSTANCE.msgUtil.getDefaultBuilder();
        builder.setAuthor(user.getName() + "#" + user.getDiscriminator());
        builder.setThumbnail(user.getAvatarUrl());
        builder.addField("User Info",
                "ID: `" + user.getId()
                        + "`\n Created: `" + user.getCreationTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
                        + "`\n Status: `" + member.getOnlineStatus()
                        + "`\n Game: `" + (member.getGame() != null ? member.getGame().getName() : "NONE") + "`", false);
        builder.addField("Member Info",
                "Nickname: `" + (member.getNickname() != null ? member.getNickname() : "NONE")
                        + "`\n Joined: `" + member.getJoinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
                        + "`\n Roles: `" + String.join(", ", member.getRoles().stream().map(Role::getName).collect(Collectors.toList())) + "`", false);
        event.getTextChannel().sendMessage(builder.build()).queue();
    }
}
