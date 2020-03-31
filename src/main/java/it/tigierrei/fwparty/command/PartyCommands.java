package it.tigierrei.fwparty.command;

import it.tigierrei.fwparty.FWParty;
import it.tigierrei.fwparty.exception.InvalidPartyException;
import it.tigierrei.fwparty.party.Party;
import it.tigierrei.fwparty.party.PartyManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;
import java.util.UUID;

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
                    } else if (partyManager.doesPartyExist(partyLeader.getUniqueId()) && partyManager.getParty(partyLeader.getUniqueId()).getPlayerList().contains(playerInvited)) {
                        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_already_in_party.replace("%player%", playerInvited.getName())));
                    } else {
                        plugin.getPartyManager().createParty(partyLeader.getUniqueId());
                        plugin.getPartyManager().addInvite(playerInvited.getUniqueId(), partyLeader.getUniqueId());
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
                    if (partyManager.hasPendingInvite(player.getUniqueId())) {
                        if (!partyManager.isPartyLeader(player.getUniqueId())) {
                            UUID partyLeaderUUID = partyManager.removeInvite(player.getUniqueId());
                            try {
                                if (partyManager.getPartySize(partyLeaderUUID) >= plugin.getConfigValues().party_limit) {
                                    player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_limit_reached));
                                } else {
                                    partyManager.addPlayerToParty(player.getUniqueId(), partyLeaderUUID);
                                    Optional<User> partyLeader = Sponge.getServiceManager().provide(UserStorageService.class).get().get(partyLeaderUUID);
                                    partyLeader.ifPresent(user -> player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_accepted.replace("%player%", user.getName()))));
                                    Optional<Player> partyLeaderOptional = Sponge.getServer().getPlayer(partyLeaderUUID);
                                    partyLeaderOptional.ifPresent(value -> value.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().accept_party_notification.replace("%player%", player.getName()))));
                                }
                            } catch (InvalidPartyException e) {
                                player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invalid_party));
                            }
                        } else {
                            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().already_in_party));
                        }
                    } else {
                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().no_invites));
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
                    if (partyManager.hasPendingInvite(player.getUniqueId())) {
                        UUID partyLeaderUUID = partyManager.removeInvite(player.getUniqueId());
                        Optional<Player> partyLeaderOptional = Sponge.getServer().getPlayer(partyLeaderUUID);
                        Optional<User> partyLeader = Sponge.getServiceManager().provide(UserStorageService.class).get().get(partyLeaderUUID);
                        partyLeader.ifPresent(user -> player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_refused.replace("%player%", user.getName()))));
                        partyLeaderOptional.ifPresent(value -> value.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().decline_party_notification.replace("%player%", player.getName()))));
                    } else {
                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().no_invites));
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
                    if (partyManager.isPartyLeader(player.getUniqueId())) {
                        partyManager.sendMessageToPartyMembers(player.getUniqueId(),plugin.getConfigValues().disband_message.replace("%player%",player.getName()));
                        partyManager.deleteParty(player.getUniqueId());
                    } else {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().error_message));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            }))
            .build();

    private final CommandSpec join = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("password"))))
            .executor(((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();

                    try {
                        String partyLeaderName = args.<String>getOne("player").orElseThrow(() -> {
                            throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                        });
                        String password = args.<String>getOne("password").orElseThrow(() -> {
                            throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                        });
                        Optional<User> optionalUser = Sponge.getServiceManager().provide(UserStorageService.class).get().get(partyLeaderName);
                        if(optionalUser.isPresent()){
                            UUID partyLeaderUUID = optionalUser.get().getUniqueId();
                            if (partyManager.isPlayerInParty(player.getUniqueId())) {
                                player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().already_in_party));
                            } else if(partyManager.doesPartyExist(partyLeaderUUID)){
                                Party party = partyManager.getParty(partyLeaderUUID);
                                if (party.getPassword().equals(password)) {
                                    try {
                                        if (partyManager.getPartySize(partyLeaderUUID) >= plugin.getConfigValues().party_limit) {
                                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_limit_reached));
                                        } else {
                                            partyManager.addPlayerToParty(player.getUniqueId(), partyLeaderUUID);
                                            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_accepted.replace("%player%", partyLeaderName)));
                                            Optional<Player> partyLeaderOptional = Sponge.getServer().getPlayer(partyLeaderUUID);
                                            if(partyLeaderOptional.isPresent()){
                                                Player partyLeader = partyLeaderOptional.get();
                                                partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().accept_party_notification.replace("%player%", player.getName())));
                                            }
                                        }
                                    } catch (InvalidPartyException e) {
                                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invalid_party));
                                    }
                                } else {
                                    player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().wrong_password));
                                }
                            }else{
                                player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invalid_party));
                            }
                        }else{
                            throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(e.getMessage()));
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
                    if (partyManager.isPlayerInParty(player.getUniqueId()) || partyManager.doesPartyExist(player.getUniqueId())) {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().already_in_party));
                    } else {
                        partyManager.createParty(player.getUniqueId(), password);
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_created));
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
                        if (partyManager.isPartyLeader(player.getUniqueId())) {
                            partyManager.getParty(player.getUniqueId()).setPassword(password);
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().password_changed));
                        } else {
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().error_message));
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(e.getMessage()));
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
                    if (partyManager.isPlayerInParty(player.getUniqueId())) {
                        if (partyManager.isPartyLeader(player.getUniqueId())) {
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_leader_left));
                        } else {
                            UUID partyLeader = partyManager.getPlayerParty(player.getUniqueId()).getLeader();
                            partyManager.removePlayerFromParty(player.getUniqueId());
                            Sponge.getServer().getPlayer(partyLeader).get().sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_left_party.replace("%player%", player.getName())));
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_left));
                        }
                    } else {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().not_on_party));
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
                    Player toBeKicked = args.<Player>getOne("player").orElseThrow(() -> {
                        throw new IllegalArgumentException(plugin.getConfigValues().insufficient_parameters);
                    });
                    PartyManager partyManager = plugin.getPartyManager();
                    if (partyManager.isPartyLeader(player.getUniqueId())) {
                        if (partyManager.getPlayerParty(player.getUniqueId()).equals(partyManager.getPlayerParty(toBeKicked.getUniqueId()))) {
                            if (partyManager.isPartyLeader(toBeKicked.getUniqueId())) {
                                player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_leader_left));
                            } else {
                                partyManager.removePlayerFromParty(toBeKicked.getUniqueId(), player.getUniqueId());
                                partyManager.sendMessageToPartyMembers(player.getUniqueId(), plugin.getConfigValues().player_kicked.replace("%player%", toBeKicked.getName()));
                                toBeKicked.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().kicked));
                            }
                        } else {
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_not_in_party.replace("%player%", toBeKicked.getName())));
                        }
                    } else {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().not_leader.replace("%player%", player.getName())));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();

    private final CommandSpec info = CommandSpec.builder()
            .executor((src, args) -> {
                if (src instanceof Player) {
                    PartyManager partyManager = plugin.getPartyManager();
                    Player player = (Player)src;
                    if(partyManager.isPlayerInParty(player.getUniqueId())){
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(partyManager.getPlayerParty(player.getUniqueId()).getPartyInfo()));
                    }else{
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().not_on_party));
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();
    
    private final CommandSpec chat = CommandSpec.builder()
            .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("message"))))
            .executor((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    String message = args.<String>getOne("message").orElse("");
                    if (!message.isEmpty()) {
	                    if (partyManager.isPlayerInParty(player.getUniqueId()) || partyManager.doesPartyExist(player.getUniqueId())) {
	                        partyManager.sendMessageToPartyMembers(partyManager.getPlayerParty(player.getUniqueId()).getLeader(),"&2[PARTY] &a" + player.getName() + ": " + message);
	                    } else {
	                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().not_on_party));
	                    }
                    } else {
                    	if (partyManager.isPlayerChatting(player.getUniqueId())) {
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize("&eParty chat disabilitata!"));
                    		partyManager.removeChattingPlayer(player.getUniqueId());
                    	} else {
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize("&eParty chat abilitata!"));
                    		partyManager.addChattingPlayer(player.getUniqueId());
                    	}
                    }
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }

                return CommandResult.success();
            })
            .build();

    private final CommandSpec credits = CommandSpec.builder()
            .executor((src, args) -> {
                if (src instanceof Player) {
                    src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(
                            "&aParty system made by &6Tigierrei\n" +
                                    "&aChat system made by &6Markus__27"
                    ));
                } else {
                    src.sendMessage(Text.of("Only players can run that command"));
                }
                return CommandResult.success();
            })
            .build();

    private final CommandSpec help = CommandSpec.builder()
            .executor((src, args) -> {
                if (src instanceof Player) {
                    src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(
                            "&6-------{ FWParty }-------\n" +
                                    "&a/party create <password>\n" +
                                    "&a/party disband\n" +
                                    "&a/party setPassword <password>\n" +
                                    "&a/party join <partyLeaderName> <password>\n" +
                                    "&a/party invite <playerName>\n" +
                                    "&a/party accept\n" +
                                    "&a/party decline\n" +
                                    "&a/party leave\n" +
                                    "&a/party kick <player>\n" +
                                    "&a/party chat <message> oppure /pc <message>\n" +
                                    "&a/party info\n" +
                                    "&a/party credits"
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
            .child(leave, "leave", "esci")
            .child(kick, "kick", "rimuovi")
            .child(info,"info")
            .child(chat,"chat", "c")
            .child(credits, "credits", "crediti")
            .build();

    public void registerCommands() {
        Sponge.getCommandManager().register(plugin, help, "party");
        Sponge.getCommandManager().register(plugin, chat, "pc");
    }

}