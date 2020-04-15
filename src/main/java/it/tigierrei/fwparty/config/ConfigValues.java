package it.tigierrei.fwparty.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigValues {
    @Setting(value = "player-cannote-invite-not-leader")
    public String player_cannote_invite_not_leader = "&cNon puoi invitare altri player perche' non sei leader di un party";

    @Setting(value = "combat-time", comment = "Durata combat log in secondi")
    public int combat_time = 15;

    @Setting(value = "party-limit", comment = "Massimo numero di giocatori per party")
    public int party_limit = 50;

    @Setting(value = "party-limit-reached", comment = "Massimo numero di giocatori per party")
    public String party_limit_reached = "&cIl limite del party e' stato raggiunto!";

    @Setting(value = "invite-message")
    public String invite_message = "&eHai invitato %player% al tuo party!";

    @Setting(value = "invite-received-message")
    public String invite_received_message = "&eHai ricevuto l'invito ad entrare nel party di %player%! Digita\n/party accept per accettare\n/party decline per rifiutare";

    @Setting(value = "invite-refused")
    public String invite_refused = "&eHai rifiutato l'invito al party di %player%!";

    @Setting(value = "declined-party-notification")
    public String decline_party_notification = "&e%player% ha rifiutato l'invito al party!";

    @Setting(value = "disband-message")
    public String disband_message = "&cIl party di %player% e' stato cancellato!";

    @Setting(value = "invite-accepted")
    public String invite_accepted = "&aSei entrato nel party di %player%!";

    @Setting(value = "accept-party-notification")
    public String accept_party_notification = "&a%player% e' entrato nel party!";

    @Setting(value = "player-left-party")
    public String player_left_party = "&c%player% e' uscito dal party!";

    @Setting(value = "player-left")
    public String player_left = "&eSei uscito dal party!";

    @Setting(value = "not-leader")
    public String not_leader = "&cNon sei ne il leader del party ne Tigierrei!";

    @Setting(value = "not-on-party")
    public String not_on_party = "&cNon sei in nessun party!";

    @Setting(value = "cannot-invite-yourself")
    public String cannot_invite_yourself = "&cNon puoi auto invitarti!";

    @Setting(value = "no-invite-to-accept")
    public String no_invites = "&cNon hai nessun invito in sospeso!";

    @Setting(value = "already-in-party")
    public String already_in_party = "&cSei gia' in un party!";

    @Setting(value = "player-already-in-party")
    public String player_already_in_party = "&c%player% e' gia' in un party!";

    @Setting(value = "error-message")
    public String error_message = "&cNon sei in un party oppure non sei il party leader!";

    @Setting(value = "insufficient-parameters")
    public String insufficient_parameters = "&cParametri insufficienti!";

    @Setting(value = "wrong-password")
    public String wrong_password = "&cPassword sbagliata!";

    @Setting(value = "password-changed")
    public String password_changed = "&aPassword modificata!";

    @Setting(value = "invalid-party")
    public String invalid_party = "&cIl party a cui stai tentando di accedere non esiste piu'!";

    @Setting(value = "party-created")
    public String party_created = "&aParty creato con successo!";

    @Setting(value = "party-leader-left")
    public String party_leader_left = "&cSei il leader del party, non puoi uscire! Usa /party disband per cancellare il party!";

    @Setting(value = "player_not_in_party")
    public String player_not_in_party = "&c%player% non e' nel tuo party!";

    @Setting(value = "player_kicked")
    public String player_kicked = "&e%player% e' stato rimosso dal party!";

    @Setting(value = "kicked")
    public String kicked = "&cSei stato rimosso dal party!";
}
