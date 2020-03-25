package it.tigierrei.fwparty.config;

import com.google.common.reflect.TypeToken;
import it.tigierrei.fwparty.FWParty;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {

    private final FWParty plugin;
    private final Path configDir;

    public ConfigManager(FWParty plugin, Path configDir) {
        this.plugin = plugin;
        this.configDir = configDir;
    }

    public ConfigValues loadConfig(){
        try{
            if(!configDir.toFile().exists()){
                boolean exitStatus = configDir.toFile().mkdirs();
                if(exitStatus) throw new IOException("Error creating " + configDir.toString());
            }
            File configFile = new File(configDir.toFile(), "FWParty.conf");
            if(!configFile.exists()){
                boolean exitStatus = configFile.createNewFile();
                if(exitStatus) throw new IOException("Error creating FWParty.conf");
            }
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(configFile).build();
            CommentedConfigurationNode config = loader.load(ConfigurationOptions.defaults().setObjectMapperFactory(plugin.getFactory()).setShouldCopyDefaults(true));
            ConfigValues root = config.getValue(TypeToken.of(ConfigValues.class), new ConfigValues());
            loader.save(config);
            return root;
        }catch (IOException | ObjectMappingException e){
            plugin.getLogger().error(e.getMessage());
            return null;
        }
    }

}
