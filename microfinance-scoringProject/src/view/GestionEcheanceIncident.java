package view;

import model.*;
import model.enums.Decision;
import model.enums.StatutPaiement;
import model.enums.TypeIncident;
import service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class GestionEcheanceIncident {

    private static final Scanner scanner = new Scanner(System.in);

    public void gestionEcheanceIncident() throws Exception {

        boolean runnig = true;

        while (runnig) {
            showMenu();
            int choix = scanner.nextInt();
            scanner.nextLine();
            switch (choix) {
                case 1:
                    genererEcheances();
                    break;
                case 2:
                    afficherEcheances();
                    break;
                case 3:
                    enregistrerPaiement();
                    break;
                case 4:
                    afficherIncidents();
                    break;
                case 5:
                    verifierScoreClient();
                    break;
                case 6:
                    MenuPrincipale.start();
                    break;
                default:
                    System.out.println("Choix invalide !");
            }

        }

    }

    public static void showMenu() throws SQLException {
        System.out.println("\n====== GESTION DES ECHEANCES & INCIDENTS ======");
        System.out.println("1. Générer les échéances pour un crédit validé");
        System.out.println("2. Afficher toutes les échéances d'un client");
        System.out.println("3. Enregistrer un paiement sur une échéance");
        System.out.println("4. Afficher les incidents d'un client");
        System.out.println("5. Vérifier le score et la décision automatique d'un client");
        System.out.println("6. Retour");
        System.out.print("Votre choix : ");
    }

    // 1 Génération automatique des échéances
    private static void genererEcheances() throws SQLException {
        System.out.print("ID du crédit : ");
        long creditId = scanner.nextLong();
        scanner.nextLine();

        Optional<Credit> creditOpt = new CreditService().chercherCreditParId(creditId);
        if (!creditOpt.isPresent()) {
            System.out.println("Crédit introuvable !");
            return;
        }

        Credit credit = creditOpt.get();
        List<Echeance> echeances = genererEcheancesAutomatiques(credit);

        for (Echeance e : echeances) {
            EcheanceService.ajouterEcheance(e);
        }
        System.out.println("Échéances générées automatiquement pour le crédit #" + creditId);
    }

    private static List<Echeance> genererEcheancesAutomatiques(Credit credit) {
        List<Echeance> liste = new ArrayList<>();
        double mensualite = credit.getMontantOctroye() / credit.getDureeEnMois();
        LocalDate dateDebut = credit.getDateCredit();

        for (int i = 1; i <= credit.getDureeEnMois(); i++) {
            LocalDate dateEcheance = dateDebut.plusMonths(i);
            Echeance e = new Echeance(
                    credit.getId(),
                    dateEcheance,
                    mensualite,
                    null,
                    StatutPaiement.IMPAYE_NON_REGLE
            );
            liste.add(e);
        }
        return liste;
    }

    // 2️Affichage des échéances
    private static void afficherEcheances() throws SQLException {
        System.out.print("ID du client : ");
        long clientId = scanner.nextLong();
        scanner.nextLine();

        List<Credit> credits = new CreditService().getCreditsByClientId(clientId);
        if (credits.isEmpty()) {
            System.out.println("Aucun crédit trouvé pour ce client.");
            return;
        }

        for (Credit c : credits) {
            System.out.println("\nCrédit #" + c.getId() + " | Montant : " + c.getMontantOctroye());
            List<Echeance> echeances = EcheanceService.getEcheancesByCreditId(c.getId());
            for (Echeance e : echeances) {
                System.out.println(e);
            }
        }
    }

    // 3️Enregistrement d’un paiement
    private static void enregistrerPaiement() throws SQLException {
        System.out.print("ID de l'échéance : ");
        long echeanceId = scanner.nextLong();
        scanner.nextLine();

        Optional<Echeance> echeanceOpt = EcheanceService.getEcheanceById(echeanceId);
        if (!echeanceOpt.isPresent()) {
            System.out.println("Échéance introuvable !");
            return;
        }

        Echeance e = echeanceOpt.get();

        System.out.print("Date de paiement (AAAA-MM-JJ) : ");
        String dateStr = scanner.nextLine();
        LocalDate datePaiement = LocalDate.parse(dateStr);
        e.setDatePaiement(datePaiement);

        // Mise à jour automatique du statut
        mettreAJourStatut(e);
        EcheanceService.updateEcheance(e);

        // Création automatique de l'incident
        Incident incident = genererIncident(e);
        IncidentService.ajouterIncident(incident);

        System.out.println("Paiement enregistré et incident généré.");
    }

    private static void mettreAJourStatut(Echeance e) {
        if (e.getDatePaiement() == null) {
            e.setStatutPaiement(StatutPaiement.IMPAYE_NON_REGLE);
        } else {
            long joursRetard = ChronoUnit.DAYS.between(e.getDateEcheance(), e.getDatePaiement());
            if (joursRetard <= 0) {
                e.setStatutPaiement(StatutPaiement.PAYE_A_TEMPS);
            } else if (joursRetard <= 30) {
                e.setStatutPaiement(StatutPaiement.EN_RETARD);
            } else {
                e.setStatutPaiement(StatutPaiement.PAYE_EN_RETARD);
            }
        }
    }

    private static Incident genererIncident(Echeance e) {
        Incident incident = new Incident(
                e.getId(),
                LocalDate.now(),
                e,
                0,
                TypeIncident.IMPAYE_NON_REGLE
        );

        switch (e.getStatutPaiement()) {
            case PAYE_A_TEMPS:
                incident.setTypeIncident(TypeIncident.PAYE_A_TEMPS);
                incident.setScore(+10);
                break;
            case EN_RETARD:
                incident.setTypeIncident(TypeIncident.EN_RETARD);
                incident.setScore(-5);
                break;
            case PAYE_EN_RETARD:
                incident.setTypeIncident(TypeIncident.PAYE_EN_RETARD);
                incident.setScore(-10);
                break;
            case IMPAYE_NON_REGLE:
                incident.setTypeIncident(TypeIncident.IMPAYE_NON_REGLE);
                incident.setScore(-20);
                break;
            case IMPAYE_REGLE:
                incident.setTypeIncident(TypeIncident.IMPAYE_REGLE);
                incident.setScore(-5);
                break;
        }
        return incident;
    }

    // 4 Affichage des incidents
    private static void afficherIncidents() throws SQLException {
        System.out.print("ID du client : ");
        long clientId = scanner.nextLong();
        scanner.nextLine();

        List<Credit> credits = new CreditService().getCreditsByClientId(clientId);
        if (credits.isEmpty()) {
            System.out.println("Aucun crédit trouvé pour ce client.");
            return;
        }

        for (Credit c : credits) {
            System.out.println("\nCrédit #" + c.getId());
            List<Echeance> echeances = EcheanceService.getEcheancesByCreditId(c.getId());
            for (Echeance e : echeances) {
                List<Incident> incidents = IncidentService.getIncidentsByEcheanceId(e.getId());
                for (Incident i : incidents) {
                    System.out.println(i);
                }
            }
        }
    }

    // 5 Vérification du score et décision automatique
    private static void verifierScoreClient() throws SQLException {
        System.out.print("ID du client : ");
        long clientId = scanner.nextLong();
        scanner.nextLine();

        Optional<Personne> persoOpt = ClientService.chercherClientParId(clientId);
        if (!persoOpt.isPresent()) {
            System.out.println("Client introuvable !");
            return;
        }

        Personne p = persoOpt.get();

        // Calcul du score total
        int score = ScoringSystem.totalScore(p);
        // Décision automatique initiale
        Decision decision = ScoringSystem.decisonAutomatique(p);

        System.out.println("Score total : " + score + "/100");
        System.out.println("Décision automatique : " + decision);

        // Cas spécifique : étude manuelle
        if (decision == Decision.ETUDE_MANUELLE) {
            System.out.println("Le score du client nécessite une étude manuelle.");
            System.out.println("Veuillez examiner ses échéances et incidents avant de décider.");

            afficherEcheances();
            afficherIncidents();

            System.out.print("Voulez-vous ACCORDER le crédit après étude manuelle ? (O/N) : ");
            String choix = scanner.nextLine();
            if (choix.equalsIgnoreCase("O")) {
                decision = Decision.ACCORD_IMMEDIAT;
                System.out.println("Crédit accordé après étude manuelle !");
            } else {
                decision = Decision.REFUS_AUTOMATIQUE;
                System.out.println("Crédit refusé après étude manuelle !");
            }
        }

        // Affichage de la décision finale (après éventuelle étude manuelle)
        System.out.println(">>> Décision finale : " + decision);
    }

}
