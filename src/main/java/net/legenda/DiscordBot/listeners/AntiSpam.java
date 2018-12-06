package net.legenda.DiscordBot.listeners;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.command.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AntiSpam extends ListenerAdapter {

    private Map<User, Integer> warned = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<Message> history = event.getTextChannel().getIterableHistory().complete().stream().limit(10).filter(msg -> !msg.equals(event.getMessage())).collect(Collectors.toList());
        int spam = history.stream().filter(message -> message.getAuthor().equals(event.getAuthor()) && !message.getAuthor().isBot()).filter(msg -> (event.getMessage().getCreationTime().toEpochSecond() - msg.getCreationTime().toEpochSecond()) < 6).collect(Collectors.toList()).size();
        if (spam > 2 && !event.getGuild().getOwner().equals(event.getMember()) && !PermissionUtil.checkPermission(event.getMember(), Permission.ADMINISTRATOR)) {
            int timesWarned = 0;
            if (warned.containsKey(event.getAuthor()))
                timesWarned = warned.get(event.getAuthor());

            warned.put(event.getAuthor(), timesWarned + 1);
            if (timesWarned >= 5)
                coolDownUser(event.getTextChannel(), event.getAuthor());
            else
                Command.sendErrorMessage("Please leave 2 seconds between messages, " + event.getAuthor().getAsMention(), event.getTextChannel(), true);
            event.getMessage().delete().queue();
        }
    }

    private void coolDownUser(TextChannel channel, User user) {
        Guild guild = channel.getGuild();
        Role muteRole = guild.getRoles().stream().filter(role -> role.getName().equalsIgnoreCase("Cooldown")).findFirst().orElse(null);
        if (muteRole != null && !PermissionUtil.checkPermission(channel, guild.getMember(user), Permission.ADMINISTRATOR) && guild.getMember(user).getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("Cooldown"))) {
            GuildController guildController = guild.getController();
            guildController.removeRolesFromMember(guild.getMember(user), guild.getMember(user).getRoles()).queue();
            guildController.addSingleRoleToMember(guild.getMember(user), muteRole).queue(success -> channel.sendMessage(Main.INSTANCE.msgUtil.wrapMessage("Added " + user.getAsMention() + " To Cooldown")).queue(callback -> warned.put(user, 0)));
        }
    }

}
