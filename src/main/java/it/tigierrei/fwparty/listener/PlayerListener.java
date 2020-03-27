package it.tigierrei.fwparty.listener;

import it.tigierrei.fwparty.FWParty;
import it.tigierrei.fwparty.party.Party;
import it.tigierrei.fwparty.party.PartyManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerListener {

    private final FWParty plugin;

    public PlayerListener(FWParty plugin) {
        this.plugin = plugin;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onDisconnect(ClientConnectionEvent.Disconnect event, @Root Player p) {
        if(plugin.getPartyManager().isPartyLeader(p.getUniqueId())){
            plugin.getPartyManager().deleteParty(p.getUniqueId());
        }else if(plugin.getPartyManager().isPlayerInParty(p.getUniqueId())){
            plugin.getPartyManager().removePlayerFromParty(p.getUniqueId());
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onDeath(AttackEntityEvent event, @First EntityDamageSource src) {
        plugin.getLogger().info("triggered");
        if (event.getTargetEntity() instanceof Player && src.getSource() instanceof Player) {
            plugin.getLogger().info("cast ok");
            Player player = (Player)src.getSource();
            Player target = (Player) event.getTargetEntity();
            PartyManager partyManager = plugin.getPartyManager();
            Party targetParty = partyManager.getPlayerParty(target.getUniqueId());
            Party playerParty = partyManager.getPlayerParty(player.getUniqueId());
            if (targetParty != null && playerParty != null) {
                plugin.getLogger().info("party not null");
                if (playerParty.equals(targetParty)) {
                    event.setCancelled(true);
                }else{
                    plugin.getLogger().info("different party");
                }
            }
        }
    }
}
