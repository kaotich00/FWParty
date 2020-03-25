package it.tigierrei.fwparty.party;

import it.tigierrei.fwparty.exception.InvalidPartyException;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PartyManager {

    private final Map<Player, Player> inviteMap = new HashMap<>();
    private final Map<Player, Party> partyMap = new HashMap<>();
    private final Map<Player, Party> playerMap = new HashMap<>();

    public void addInvite(Player playerInvited, Player partyToJoin){
        inviteMap.put(playerInvited, partyToJoin);
    }

    public boolean hasPendingInvite(Player player){
        return inviteMap.containsKey(player);
    }

    /**
     *
     * @param playerInvited
     * @return Party leader
     */
    public Player removeInvite(Player playerInvited){
        return inviteMap.remove(playerInvited);
    }

    public void addPlayerToParty(Player playerToAdd, Player partyLeader) throws InvalidPartyException{
        if(partyMap.containsKey(partyLeader)){
            partyMap.get(partyLeader).addPlayer(playerToAdd);
        }else{
            throw new InvalidPartyException();
        }
    }

    public void removePlayerFromParty(Player player, Player partyLeader){
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.get(partyLeader);
            party.removePlayer(player);
        }
    }

    public boolean doesPartyExist(Player partyLeader){
        return partyMap.containsKey(partyLeader);
    }

    public boolean isPartyLeader(Player partyLeader){
        return doesPartyExist(partyLeader);
    }

    public Party getParty(Player partyLeader){
        return partyMap.get(partyLeader);
    }

    public void deleteParty(Player partyLeader){
        if(partyMap.containsKey(partyLeader)){
            Party party = partyMap.remove(partyLeader);
            party.getPlayerList().forEach(playerMap::remove);
        }
    }

    public boolean isPlayerInParty(Player player){
        return playerMap.containsKey(player);
    }

    public void createParty(Player partyLeader, String password){
        partyMap.put(partyLeader, new Party(partyLeader, password));
    }

    public void createParty(Player partyLeader){
        createParty(partyLeader, null);
    }

}
