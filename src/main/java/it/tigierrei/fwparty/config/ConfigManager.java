package it.tigierrei.fwparty.config;

import com.google.common.reflect.TypeToken;
import it.tigierrei.fwparty.FWParty;
import it.tigierrei.fwparty.party.PartyManager;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.*;

public class ConfigManager {

    private final FWParty plugin;
    private final File configDir;

    public ConfigManager(FWParty plugin, File configDir) {
        this.plugin = plugin;
        this.configDir = configDir;
    }

    public ConfigValues loadConfig(){
        try{
            if(!configDir.exists()){
                boolean exitStatus = configDir.mkdirs();
                if(!exitStatus) throw new IOException("Error creating " + configDir.toString());
            }
            File configFile = new File(configDir, "FWParty.conf");
            if(!configFile.exists()){
                boolean exitStatus = configFile.createNewFile();
                if(!exitStatus) throw new IOException("Error creating FWParty.conf");
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

    public PartyManager loadParties(){
        File partyFile = new File(configDir, "parties");
        if(partyFile.exists()){
            try{
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(partyFile));
                return (PartyManager)objectInputStream.readObject();
            }catch (IOException | ClassNotFoundException e){
                return new PartyManager();
            }
        }else{
            return new PartyManager();
        }
    }

    public void saveParties(PartyManager partyManager){
        File partyFile = new File(configDir, "parties");
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(partyFile));
            objectOutputStream.writeObject(partyManager);
            if( objectOutputStream != null ) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }
        }catch (Exception e){
            plugin.getLogger().error("Error while saving parties on file " + partyFile.getName());
        }
    }

}
