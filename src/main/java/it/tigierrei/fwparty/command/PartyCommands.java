package it.tigierrei.fwparty.command;

import it.tigierrei.fwparty.FWParty;
import it.tigierrei.fwparty.exception.InvalidPartyException;
import it.tigierrei.fwparty.party.Party;
import it.tigierrei.fwparty.party.PartyManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

public class PartyCommands {

    private FWParty plugin;

    public PartyCommands(FWParty plugin) {
        this.plugin = plugin;
    }

    private final CommandSpec invite = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
            .executor(((src, args) -> {
                if (src instanceof Player) {
                    Player partyLeader = (Player) src;
                    Player playerInvited = args.<Player>getOne("player").get();
                    PartyManager partyManager = plugin.getPartyManager();
                    if (partyLeader.equals(playerInvited)) {
                        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().cannot_invite_yourself));
                    } else if (partyManager.doesPartyExist(partyLeader) && partyManager.getParty(partyLeader).getPlayerList().contains(playerInvited)) {
                        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_already_in_party.replace("%player%", playerInvited.getName())));
                    } else {
                        plugin.getPartyManager().createParty(partyLeader);
                        plugin.getPartyManager().addInvite(playerInvited, partyLeader);
                        partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_message.replace("%player%", playerInvited.getName())));
                        playerInvited.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_received_message.replace("%player%", partyLeader.getName())));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            }))
            .build();

    private final CommandSpec accept = CommandSpec.builder()
            .arguments()
            .executor(((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    if (partyManager.hasPendingInvite(player)) {
                        if (!partyManager.isPartyLeader(player)) {
                            Player partyLeader = partyManager.removeInvite(player);
                            try {
                                partyManager.addPlayerToParty(player, partyLeader);
                                player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_accepted.replace("%player%", partyLeader.getName())));
                                partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().accept_party_notification.replace("%player%", player.getName())));
                            } catch (InvalidPartyException e) {
                                player.sendMessages(Text.of(plugin.getConfigValues().invalid_party));
                            }
                        } else {
                            player.sendMessage(Text.of(plugin.getConfigValues().already_in_party));
                        }
                    } else {
                        player.sendMessage(Text.of(plugin.getConfigValues().no_invites));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            }))
            .build();

    private final CommandSpec decline = CommandSpec.builder()
            .arguments()
            .executor(((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    if (partyManager.hasPendingInvite(player)) {
                        Player partyLeader = partyManager.removeInvite(player);
                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_refused.replace("%player%", partyLeader.getName())));
                        partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().decline_party_notification.replace("%player%", player.getName())));
                    } else {
                        player.sendMessage(Text.of(plugin.getConfigValues().no_invites));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            }))
            .build();


    private final CommandSpec disband = CommandSpec.builder()
            .arguments()
            .executor(((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    if (partyManager.isPartyLeader(player)) {
                        partyManager.deleteParty(player);
                        player.sendMessages(Text.of(plugin.getConfigValues().disband_message));
                    } else {
                        player.sendMessages(Text.of(plugin.getConfigValues().error_message));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            }))
            .build();

    private final CommandSpec join = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("password"))))
            .executor(((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    try {
                        Player partyLeader = args.<Player>getOne("player").orElseThrow(() -> {
                            throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                        });
                        String password = args.<String>getOne("password").orElseThrow(() -> {
                            throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                        });
                        if (partyManager.isPlayerInParty(player)) {
                            player.sendMessages(Text.of(plugin.getConfigValues().already_in_party));
                        } else {
                            Party party = partyManager.getParty(partyLeader);
                            if (party.getPassword().equals(password)) {
                                try {
                                    partyManager.addPlayerToParty(player, partyLeader);
                                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_accepted.replace("%player%", partyLeader.getName())));
                                    partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().accept_party_notification.replace("%player%", player.getName())));
                                } catch (InvalidPartyException e) {
                                    player.sendMessages(Text.of(plugin.getConfigValues().invalid_party));
                                }
                            } else {
                                player.sendMessages(Text.of(plugin.getConfigValues().wrong_password));
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessages(Text.of(e.getMessage()));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            }))
            .build();

    private final CommandSpec createParty = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("password"))))
            .executor((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    String password = args.<String>getOne("password").orElse(null);
                    PartyManager partyManager = plugin.getPartyManager();
                    //TODO Il secondo controllo e' ridondante ma lo lascio per chiarezza
                    if (partyManager.isPlayerInParty(player) || partyManager.doesPartyExist(player)) {
                        player.sendMessages(Text.of(plugin.getConfigValues().already_in_party));
                    } else {
                        partyManager.createParty(player, password);
                        player.sendMessages(Text.of(plugin.getConfigValues().party_created));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();

    private final CommandSpec setPassword = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("password"))))
            .executor((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    try {
                        String password = args.<String>getOne("password").orElseThrow(() -> {
                            throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                        });
                        PartyManager partyManager = plugin.getPartyManager();
                        if (partyManager.isPartyLeader(player)) {
                            partyManager.getParty(player).setPassword(password);
                            player.sendMessages(Text.of(plugin.getConfigValues().password_changed));
                        } else {
                            player.sendMessages(Text.of(plugin.getConfigValues().error_message));
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessages(Text.of(e.getMessage()));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();

    private final CommandSpec leave = CommandSpec.builder()
            .arguments()
            .executor((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    if(partyManager.isPlayerInParty(player)){
                        if(partyManager.isPartyLeader(player)){
                            player.sendMessages(Text.of(plugin.getConfigValues().party_leader_left));
                        }else{
                            Player partyLeader = partyManager.getPlayerParty(player).getLeader();
                            partyManager.removePlayerFromParty(player);
                            partyLeader.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_left_party.replace("%player%", player.getName())));
                        }
                    }else{
                        player.sendMessages(Text.of(plugin.getConfigValues().not_on_party));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();

    private final CommandSpec kick = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
            .executor((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    Player toBeKicked = args.<Player>getOne("player").orElseThrow(() -> {throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);});
                    PartyManager partyManager = plugin.getPartyManager();
                    if(partyManager.isPartyLeader(player)){
                        if(partyManager.getPlayerParty(player).equals(partyManager.getPlayerParty(toBeKicked))){
                            partyManager.removePlayerFromParty(toBeKicked, player);
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_kicked.replace("%player%", player.getName())));
                        }else{
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_not_in_party.replace("%player%", player.getName())));
                        }
                    }else{
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().not_leader.replace("%player%", player.getName())));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();
    
    private final CommandSpec chat = CommandSpec.builder()
    		.arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
    		.executor((src, args) -> {
                if (src instanceof Player) {
	                Player player = (Player) src;
	                PartyManager partyManager = plugin.getPartyManager();
	    			if (partyManager.isPlayerInParty(player) || partyManager.doesPartyExist(player)) {
		                for (Player p : partyManager.getPlayerParty(player).getPlayerList()) {
		                	String message = args.<String>getOne("message").get();
		                    p.sendMessage(Text.builder("[PARTY] " + player + ": " + message).color(TextColors.GREEN).build());
		                }
	    			} else {
                        player.sendMessages(Text.of(plugin.getConfigValues().not_on_party));
	    			}
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }

                return CommandResult.success();
            })
    		.build();

    private final CommandSpec help = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("password"))))
            .executor((src, args) -> {
                if (src instanceof Player) {
                    src.sendMessage(Text.of(
                            "-------{ FWParty }-------\n" +
                                    "/party create <password>\n" +
                                    "/party disband\n" +
                                    "/party setPassword <password>\n" +
                                    "/party join <partyLeaderName> <password>\n" +
                                    "/party invite <playerName>\n" +
                                    "/party accept\n" +
                                    "/party decline\n" +
                                    "/party leave\n" +
                                    "/party kick <player>\n" +
                                    "/party chat"
                    ));
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .child(createParty, "create", "new", "crea", "nuovo")
            .child(disband, "disband", "delete", "cancella")
            .child(setPassword, "setPassword", "sp", "password")
            .child(join, "join", "entra")
            .child(invite, "invite", "add", "aggiungi", "invita")
            .child(accept, "accept", "accetta")
            .child(decline, "decline", "declina", "rifuta")
            .child(leave,"leave","esci")
            .child(kick,"kick","rimuovi")
            .child(chat,"chat","c")
            .build();

    public void registerCommands(){
        Sponge.getCommandManager().register(plugin, help, "party");
    }

}
