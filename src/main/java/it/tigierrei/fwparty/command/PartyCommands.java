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
                                if (partyManager.getPartySize(partyLeader) >= plugin.getConfigValues().party_limit) {
                                    player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_limit_reached));
                                } else {
                                    partyManager.addPlayerToParty(player, partyLeader);
                                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_accepted.replace("%player%", partyLeader.getName())));
                                    partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().accept_party_notification.replace("%player%", player.getName())));
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
                    if (partyManager.hasPendingInvite(player)) {
                        Player partyLeader = partyManager.removeInvite(player);
                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_refused.replace("%player%", partyLeader.getName())));
                        partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().decline_party_notification.replace("%player%", player.getName())));
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
                    if (partyManager.isPartyLeader(player)) {
                        partyManager.deleteParty(player);
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().disband_message.replace("%player%",player.getName())));
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
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().already_in_party));
                        } else if(partyManager.doesPartyExist(partyLeader)){
                            Party party = partyManager.getParty(partyLeader);
                            if (party.getPassword().equals(password)) {
                                try {
                                    if (partyManager.getPartySize(partyLeader) >= plugin.getConfigValues().party_limit) {
                                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_limit_reached));
                                    } else {
                                        partyManager.addPlayerToParty(player, partyLeader);
                                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().invite_accepted.replace("%player%", partyLeader.getName())));
                                        partyLeader.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().accept_party_notification.replace("%player%", player.getName())));
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
                    if (partyManager.isPlayerInParty(player) || partyManager.doesPartyExist(player)) {
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().already_in_party));
                    } else {
                        partyManager.createParty(player, password);
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
                        if (partyManager.isPartyLeader(player)) {
                            partyManager.getParty(player).setPassword(password);
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
                    if (partyManager.isPlayerInParty(player)) {
                        if (partyManager.isPartyLeader(player)) {
                            player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_leader_left));
                        } else {
                            Player partyLeader = partyManager.getPlayerParty(player).getLeader();
                            partyManager.removePlayerFromParty(player);
                            partyLeader.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().player_left_party.replace("%player%", player.getName())));
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
                    if (partyManager.isPartyLeader(player)) {
                        if (partyManager.getPlayerParty(player).equals(partyManager.getPlayerParty(toBeKicked))) {
                            if (partyManager.isPartyLeader(toBeKicked)) {
                                player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(plugin.getConfigValues().party_leader_left));
                            } else {
                                partyManager.removePlayerFromParty(toBeKicked, player);
                                partyManager.sendMessageToPartyMembers(player, plugin.getConfigValues().player_kicked.replace("%player%", toBeKicked.getName()));
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
                    if(partyManager.isPlayerInParty(player)){
                        player.sendMessages(TextSerializers.FORMATTING_CODE.deserialize(partyManager.getPlayerParty(player).getPartyInfo()));
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
            .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
            .executor((src, args) -> {
                if (src instanceof Player) {
                    Player player = (Player) src;
                    PartyManager partyManager = plugin.getPartyManager();
                    if (partyManager.isPlayerInParty(player) || partyManager.doesPartyExist(player)) {
                        String message = args.<String>getOne("message").orElse("");
                        partyManager.sendMessageToPartyMembers(partyManager.getPlayerParty(player).getLeader(),"&2[PARTY] &a" + player + ": " + message);
                    } else {
                        player.sendMessages(Text.of(plugin.getConfigValues().not_on_party));
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
                                    "&aChat system made by &6Markus27"
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
                                    "&a/party chat <message>\n" +
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
    }

}
