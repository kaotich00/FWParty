package it.tigierrei.fwparty.listener;

import it.tigierrei.fwparty.FWParty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
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
        if (event.getTargetEntity() instanceof Player && src.getSource() instanceof Player) {
            Player player = (Player)src.getSource();
            Player target = (Player) event.getTargetEntity();
            if(plugin.getPartyManager().areInSameParty(player, target)){
                event.setCancelled(true);
            }
        }else if(event.getTargetEntity() instanceof Player && src.getSource() instanceof Projectile){
            ProjectileSource projectileSource = ((Projectile) src.getSource()).getShooter();
            if(projectileSource instanceof Player){
                Player damager = (Player) projectileSource;
                Player target = (Player)event.getTargetEntity();
                if(plugin.getPartyManager().areInSameParty(damager, target)){
                    event.setCancelled(true);
                }
            }
        }
    }
}
