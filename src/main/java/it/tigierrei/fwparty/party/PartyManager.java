package it.tigierrei.fwparty.party;

import it.tigierrei.fwparty.exception.InvalidPartyException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager implements Serializable {

    private final Map<UUID, UUID> inviteMap = new HashMap<>();
    private final Map<UUID, Party> partyMap = new HashMap<>();
    private final Map<UUID, Party> playerMap = new HashMap<>();
    private final Map<UUID, Boolean> isPlayerChat = new HashMap<>();

    public void addInvite(UUID playerInvited, UUID partyToJoin){
        inviteMap.put(playerInvited, partyToJoin);
    }

    public boolean hasPendingInvite(UUID player){
        return inviteMap.containsKey(player);
    }

    /**
     *
     * @param playerInvited
     * @return Party leader
     */
    public UUID removeInvite(UUID playerInvited){
        return inviteMap.remove(playerInvited);
    }

    public void addPlayerToParty(UUID playerToAdd, UUID partyLeader) throws InvalidPartyException{
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.get(partyLeader);
            party.addPlayer(playerToAdd);
            playerMap.put(playerToAdd, party);
            isPlayerChat.put(playerToAdd, false);
        }else{
            throw new InvalidPartyException();
        }
    }

    public void removePlayerFromParty(UUID player, UUID partyLeader){
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.get(partyLeader);
            party.removePlayer(player);
            playerMap.remove(player);
            isPlayerChat.remove(player);
        }
    }

    public void removePlayerFromParty(UUID player){
        if(playerMap.containsKey(player)) {
            Party party = playerMap.remove(player);
            party.removePlayer(player);
            playerMap.remove(player);
            isPlayerChat.remove(player);
        }
    }

    public boolean doesPartyExist(UUID partyLeader){
        return partyMap.containsKey(partyLeader);
    }

    public boolean isPartyLeader(UUID partyLeader){
        return doesPartyExist(partyLeader);
    }

    public Party getParty(UUID partyLeader){
        return partyMap.get(partyLeader);
    }

    public void deleteParty(UUID partyLeader){
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.remove(partyLeader);
            party.getPlayerList().forEach(playerMap::remove);
        }
    }

    public Party getPlayerParty(UUID player){
        return playerMap.get(player);
    }

    public boolean isPlayerInParty(UUID player){
        return playerMap.containsKey(player);
    }

    public void createParty(UUID partyLeader, String password){
        Party party = new Party(partyLeader, password);
        partyMap.put(partyLeader, party);
        playerMap.put(partyLeader, party);
        isPlayerChat.put(partyLeader, false);
    }

    public void createParty(UUID partyLeader){
        createParty(partyLeader, null);
        isPlayerChat.put(partyLeader, false);
    }

    public int getPartySize(UUID partyLeader) throws InvalidPartyException {
        try {
            return partyMap.get(partyLeader).getPlayersNumber();
        }catch (Exception e){
            throw new InvalidPartyException();
        }
    }

    public void sendMessageToPartyMembers(UUID partyLeader, String message){
        Text text = TextSerializers.FORMATTING_CODE.deserialize(message);
        if(partyMap.containsKey(partyLeader)){
            for(UUID player : partyMap.get(partyLeader).getPlayerList()){
                Sponge.getServer().getPlayer(player).get().sendMessages(text);
            }
        }
    }

    public boolean areInSameParty(Player player1, Player player2){
        Party targetParty = getPlayerParty(player1.getUniqueId());
        Party playerParty = getPlayerParty(player2.getUniqueId());
        if (targetParty != null && playerParty != null) {
            return playerParty.equals(targetParty);
        }
        return false;
    }

    /**
     * Adding a player UUID to a list of players who have activated party chat.
     * @param player
     */
    public void addChattingPlayer(UUID player) {
    	isPlayerChat.replace(player, true);
    }
    
    /**
     * Removing a player UUID to a list of players who have activated party chat.
     * @param player
     */
    public void removeChattingPlayer(UUID player) {
    	isPlayerChat.replace(player, false);
    }
    
    /**
     * If the player UUID has activated the party chat.
     * @param player
     * @return Boolean 
     */
    public Boolean isPlayerChatting(UUID player) {
    	return isPlayerChat.get(player);
    }
}
