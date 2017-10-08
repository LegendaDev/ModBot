package net.legenda.DiscordBot.exceptions;

public class InvalidCommandStateException extends RuntimeException{

    public InvalidCommandStateException(String message){
        super(message);
    }
}
