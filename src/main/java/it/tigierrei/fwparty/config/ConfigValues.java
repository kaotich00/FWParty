package it.tigierrei.fwparty.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.event.Listener;

@ConfigSerializable
public class ConfigValues {
    @Setting(value = "party-limit", comment = "Massimo numero di giocatori per party")
    public int party_limit = 50;

    @Setting(value = "invite-message")
    public String invite_message = "&aHai invitato %player% al tuo party!";

    @Setting(value = "invite-received-message")
    public String invite_received_message = "&aHai ricevuto l'invito ad entrare nel party di %player%! Digita /party accept per accettare!";

    @Setting(value = "invite-refused")
    public String invite_refused = "&aHai rifiutato l'invito al party di %player%!";

    @Setting(value = "declined-party-notification")
    public String decline_party_notification = "&a%player% ha rifiutato l'invito al party!";

    @Setting(value = "declined-party-leader")
    public String declined_party_leader = "&c%player% ha rifiutato l'invito al party!";

    @Setting(value = "disband-message")
    public String disband_message = "&c Il party di %player% è stato cancellato!";

    @Setting(value = "party-full")
    public String party_full = "&cQuesto party è pieno!";

    @Setting(value = "invite-accepted")
    public String invite_accepted = "&Sei entrato nel party di %player%!";

    @Setting(value = "accept-party-notification")
    public String accept_party_notification = "&a%player% è entrato nel party!";

    @Setting(value = "player-left-party")
    public String player_left_party = "&c%player% è uscito dal party!";

    @Setting(value = "not-leader")
    public String not_leader = "&cNon sei ne il leader del party ne Tigierrei!";

    @Setting(value = "not-on-party")
    public String not_on_party = "&cNon sei in nessun party!";

    @Setting(value = "cannot-invite-yourself")
    public String cannot_invite_yourself = "&cNon puoi auto invitarti!";

    @Setting(value = "no-invite-to-accept")
    public String no_invites = "&cNon hai nessun invito in sospeso!";

    @Setting(value = "already-in-party")
    public String already_in_party = "&cSei già in un party!";

    @Setting(value = "player-already-in-party")
    public String player_already_in_party = "&c%player% è già in un party!";

    @Setting(value = "error-message")
    public String error_message = "&cNon sei in un party oppure non sei il party leader!";

    @Setting(value = "insufficient-parameters")
    public String insufficient_parameters = "&cParametri insufficienti!";

    @Setting(value = "wrong-password")
    public String wrong_password = "&cWrong password!";

    @Setting(value = "password-changed")
    public String password_changed = "&cPassword modificata!";

    @Setting(value = "invalid-party")
    public String invalid_party = "&cIl party a cui stai tentando di accedere non esiste più!";

    @Setting(value = "party-created")
    public String party_created = "&cParty creato con successo!";

    @Setting(value = "party-leader-left")
    public String party_leader_left = "&cSei il leader del party, non puoi uscire! Usa /party disband per cancellare il party!";

    @Setting(value = "player_not_in_party")
    public String player_not_in_party = "&c%player% non è nel tuo party!";

    @Setting(value = "player_kicked")
    public String player_kicked = "&c%player% è stato rimosso dal party!";
}
