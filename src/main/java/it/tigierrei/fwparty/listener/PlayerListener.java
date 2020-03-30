package it.tigierrei.fwparty.listener;

import it.tigierrei.fwparty.FWParty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class PlayerListener {

    private final FWParty plugin;

    public PlayerListener(FWParty plugin) {
        this.plugin = plugin;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onEntityDamaged(DamageEntityEvent event) {
        Object root = event.getCause().root();
        if(root instanceof EntityDamageSource && event.getTargetEntity() instanceof Player){
            if(root instanceof IndirectEntityDamageSource) {
                IndirectEntityDamageSource src = (IndirectEntityDamageSource) root;
                Entity indirectSource = src.getIndirectSource();
                if(indirectSource instanceof Player){
                    Player damager = (Player) indirectSource;
                    Player target = (Player) event.getTargetEntity();
                    if(plugin.getPartyManager().areInSameParty(damager, target)){
                        event.setCancelled(true);
                    }
                }
            }else{
                EntityDamageSource src = (EntityDamageSource)root;
                if (src.getSource() instanceof Player) {
                    Player player = (Player) src.getSource();
                    Player target = (Player) event.getTargetEntity();
                    if (plugin.getPartyManager().areInSameParty(player, target)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}