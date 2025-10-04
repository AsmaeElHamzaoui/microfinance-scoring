package view;

import model.Credit;
import model.Personne;
import model.enums.Decision;
import service.ClientService;
import service.CreditService;
import service.ScoringSystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;



public class CreditView {
    private static Scanner sc = new Scanner(System.in);
    private static CreditService creditService = new CreditService();
    private static final ClientService clientService = new ClientService();

    public void gestionCredit() throws Exception {
        boolean running = true;

        //while loop
        while (running) {
            showMenu();
            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1:
                    ajouterCredit();
                    break;
                case 2:
                    afficherCreditParIdClient();
                    break;
                case 3:
                    afficherCredits();
                    break;
                case 4:
                    running = false;
                    MenuPrincipale.start();
                    break;
                default:
                    System.out.println("choix invaldie !!!");
            }


        }


    }

    //fonction pour afficher le menu de gestion des crédits
    public static void showMenu() {
        System.out.println("\n=== Gestion des Crédits ===");
        System.out.println("1. Ajouter une crédit");
        System.out.println("2. Afficher les crédits d'un client spécifique");
        System.out.println("3. Afficher tous les crédits");
        System.out.println("4. Retour");
        System.out.print("Choix : ");
    }

    //fonction pour ajouter un crédit
    public static void ajouterCredit() throws Exception {
        System.out.println("ID du client :");
        long clientId = Long.parseLong(sc.nextLine());

        // Vérifier si le client existe
        Optional<Personne> clientOpt=clientService.chercherClientParId(clientId);
        if (clientOpt.isEmpty()) {
            System.out.println("Erreur : Client avec l'ID " + clientId + " n'existe pas !");
            return;
        }

        Personne personne=clientOpt.get();

        LocalDate dateCredit = LocalDate.now();

        System.out.println("Montant demandé :");
        double montantDemande = Double.parseDouble(sc.nextLine());

        //Calcul automtique du montant octroyé:
        double montantOctroye =ScoringSystem.calculMontantOctroye(personne);
        //vérification pour ne pas dépasser le montant demandé:
        if(montantOctroye>montantDemande){
            montantOctroye = montantDemande;
        }

        System.out.println("le montant octroyé pour cette personne est :" +montantOctroye);

        System.out.println("Taux d'intérêt (ex: 0.05 pour 5%) :");
        double tauxInteret = Double.parseDouble(sc.nextLine());

        System.out.println("Durée en mois :");
        int dureeEnMois = Integer.parseInt(sc.nextLine());

        System.out.println("Type de crédit :");
        String typeCredit = sc.nextLine();

        //Décision automatique
        Decision decision = ScoringSystem.decisonAutomatique(personne);
        System.out.println("Décision du crédit est :" + decision);
        System.out.println("le score est :" + ScoringSystem.totalScore(personne));
        // Création de l'objet Credit
        Credit credit = new Credit(clientId, dateCredit, montantDemande, montantOctroye, tauxInteret,
                dureeEnMois, typeCredit, decision);

        // Enregistrement du crédit
        creditService.ajouterCredit(credit);
    }

    //fonction pour afficher le crédit d'une personne
    public static void afficherCreditParIdClient() throws Exception {
        System.out.println("Enter l'id du client :");
        Long id = sc.nextLong();
        sc.nextLine();

        Optional<Personne> personne = clientService.chercherClientParId(id);
        List<Credit> credits = CreditService.getCreditsByClientId(id);
        if (personne.isPresent() && !credits.isEmpty()) {
            System.out.println("Personne trouvée : " + personne.get().getNom() + " " + personne.get().getPrenom());
            System.out.println("Listes des crédits :");
            for(Credit c:credits){
                System.out.println(c.toString());
            }
        } else {
            System.out.println("aucune resultat trouvee ");
        }
    }

    //fonction pour récupérer tout les crédits:
    public static void afficherCredits() throws Exception {

        System.out.println("voici la liste des credits :");
        List<Credit> credtis = creditService.getAllCredits();
        if (credtis.isEmpty()) {
            System.out.println("Aucune credit récupérée");
        } else {
            credtis.forEach(System.out::println);
        }

    }





}




