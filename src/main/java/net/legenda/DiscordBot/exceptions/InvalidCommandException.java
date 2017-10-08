package net.legenda.DiscordBot.exceptions;

public class InvalidCommandException extends RuntimeException{

    public InvalidCommandException(String message){
        super(message);
    }
}
