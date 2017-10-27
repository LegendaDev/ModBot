package net.legenda.DiscordBot.command.commands.music;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.legenda.DiscordBot.command.Command;
import net.legenda.DiscordBot.exceptions.InvalidCommandStateException;

@Command.cmdInfo(name = "Loop", description = "Loops current track", type = Command.Type.Music, role = "DJ", alias = {"Repeat"})
public class LoopCommand extends Command {

    private static int times;

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if(args.length >= 1){
            int input;
            try {
                input = Integer.parseInt(args[0]);

            } catch(NumberFormatException e){
                throw new InvalidCommandStateException("Usage: .Loop (Amount) || .Loop to toggle");
            }
            times = input + 1;
            sendMessage(":repeat: Loop: `" + input + "` times", event.getTextChannel());
            return;
        }
        if(times <= -1){
            times = 1;
            sendMessage(":repeat: Loop: Disabled", event.getTextChannel());
            return;
        }
        times = -1;
        sendMessage(":repeat: Loop: ∞", event.getTextChannel());
    }

    public static boolean getRepeat() {
        if(times != 0 )
            times--;
        return times <= -1 || times != 0;
    }
}
