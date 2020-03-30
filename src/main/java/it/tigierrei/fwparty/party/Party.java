package it.tigierrei.fwparty.party;

import org.spongepowered.api.Sponge;

import java.io.Serializable;
import java.util.*;

public class Party implements Serializable {

    private UUID leader;
    private String password;
    private final Set<UUID> playerList;

    public Party(UUID leader, String password) {
        this.leader = leader;
        this.password = password;
        this.playerList = new HashSet<>();
        playerList.add(leader);
    }

    public Party(UUID leader) {
        this(leader, null);
    }

    public void addPlayer(UUID player){
        playerList.add(player);
    }

    public void removePlayer(UUID player){
        playerList.remove(player);
    }

    public Set<UUID> getPlayerList() {
        return Collections.unmodifiableSet(playerList);
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return leader.equals(party.leader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leader);
    }

    public int getPlayersNumber(){
        return playerList.size();
    }

    public String getPartyInfo(){
        StringBuilder stringBuilder = new StringBuilder()
                .append("&6Party leader: &a")
                .append(Sponge.getServer().getPlayer(getLeader()).get().getName())
                .append("\n")
                .append("&6Members list: &a\n");
        for(UUID player : playerList){
            stringBuilder.append(Sponge.getServer().getPlayer(player).get().getName()).append("\n");
        }
        return stringBuilder.toString();
    }
}
