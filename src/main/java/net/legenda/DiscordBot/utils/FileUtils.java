package net.legenda.DiscordBot.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static String configFile() {
        return "D:/GuildConfig/Config.json";
    }

    public static List<String> readLines(String path) {
        List<String> data = new ArrayList<>();
        try {
            data.addAll(Files.lines(Paths.get(path)).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeLines(String path, List<String> data) {
        try {
            clearFile(path);
            Files.write(Paths.get(path), data, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void clearFile(String path) {
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
