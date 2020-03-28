package it.tigierrei.fwparty.listener;

import it.tigierrei.fwparty.FWParty;
import it.tigierrei.fwparty.party.PartyManager;

import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.ChangeEntityPotionEffectEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
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

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPotionEffectGain(ChangeEntityPotionEffectEvent event) {
        if(event.getTargetEntity() instanceof Player  && event.getSource() instanceof Projectile){
            ProjectileSource projectileSource = ((Projectile) event.getSource()).getShooter();
            if(projectileSource instanceof Player){
                Player target = (Player)event.getTargetEntity();
                Player thrower = (Player)projectileSource;
                if(plugin.getPartyManager().areInSameParty(target, thrower)){
                    PotionEffectType effect = event.getPotionEffect().getType();
                    if(effect == PotionEffectTypes.BLINDNESS || effect == PotionEffectTypes.INSTANT_DAMAGE || effect  == PotionEffectTypes.POISON){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerChat(MessageChannelEvent.Chat event) {
    	String message = event.getRawMessage().toString();
    	Player player = (Player) event.getSource();
    	PartyManager partyManager = plugin.getPartyManager();
    	if (partyManager.isPlayerChatting(player.getUniqueId())) {
    		event.setCancelled(true);
    		partyManager.sendMessageToPartyMembers(partyManager.getPlayerParty(player.getUniqueId()).getLeader(),"&2[PARTY] &a" + player.getName() + ": " + message);
    	}
    }
}