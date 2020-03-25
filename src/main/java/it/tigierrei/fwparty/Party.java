package it.tigierrei.fwparty;

import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class Party {

    private Player leader;
    private String password;
    private final Set<Player> playerList;

    public Party(Player leader, String password) {
        this.leader = leader;
        this.password = password;
        this.playerList = new HashSet<>();
        playerList.add(leader);
    }

    public Party(Player leader) {
        this(leader, null);
    }

    public void addPlayer(Player player){
        playerList.add(player);
    }

    public void removePlayer(Player player){
        playerList.remove(player);
    }

    public Set<Player> getPlayerList() {
        return Collections.unmodifiableSet(playerList);
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
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
}
