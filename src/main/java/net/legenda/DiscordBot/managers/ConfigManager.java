package net.legenda.DiscordBot.managers;

import net.dv8tion.jda.core.entities.Guild;
import net.legenda.DiscordBot.Main;
import net.legenda.DiscordBot.config.Preset;
import net.legenda.DiscordBot.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class ConfigManager {

    public HashMap<Guild, ArrayList<Preset>> presets = new LinkedHashMap<>();

    public void readData() {
        try {
            FileReader reader = new FileReader(FileUtils.configFile());
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject json = new JSONObject(tokener);
            for (Map.Entry<String, Object> guild : json.toMap().entrySet()) {
                JSONArray array = json.getJSONArray(guild.getKey());
                for (Object object : array.toList()) {
                    Map.Entry<String, String> entry = FileUtils.getJsonEntry(object.toString());
                    if (entry != null) {
                        Guild g = Main.INSTANCE.jdaBot.getGuildById(guild.getKey());
                        if (g != null)
                            appendPreset(g, new Preset(entry.getKey(), entry.getValue()));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        List<String> data = new ArrayList<>();
        JSONObject json = new JSONObject();
        for (Map.Entry<Guild, ArrayList<Preset>> entry : presets.entrySet()) {
            JSONArray jsonArray = new JSONArray();
            for (Preset preset : entry.getValue()) {
                JSONObject modChannel = new JSONObject();
                modChannel.put(preset.getKey(), preset.getValue());
                jsonArray.put(modChannel);
            }
            json.put(entry.getKey().getId(), jsonArray);
        }
        data.add(json.toString());
        FileUtils.writeLines(FileUtils.configFile(), data);
    }

    public String getToken(String path) {
        FileReader fileReader;
        BufferedReader bufferedReader;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            return bufferedReader.readLine();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        Main.INSTANCE.LOGGER.warn("TOKEN NOT FOUND");
        System.exit(0);
        return null;
    }

    public void appendPreset(Guild guild, Preset preset) {
        Map.Entry<Guild, ArrayList<Preset>> entry = presets.entrySet().stream().filter(set -> set.getKey().equals(guild)).findAny().orElse(null);
        ArrayList<Preset> data = new ArrayList<>();
        if (entry != null)
            data.addAll(entry.getValue());
        data.stream().filter(val -> val.getKey().equals(preset.getKey())).findAny().ifPresent(val -> val.setValue(preset.getValue()));
        if (data.stream().noneMatch(val -> val.getKey().equals(preset.getKey())))
            data.add(preset);
        presets.put(guild, data);
        saveData();
    }
}
