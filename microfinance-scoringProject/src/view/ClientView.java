package view;

import model.Employe;
import model.Personne;
import model.Professionnel;
import model.enums.SecteurActivite;
import model.enums.SecteurEmploye;
import model.enums.SituationFamiliale;
import model.enums.TypeContrat;
import org.postgresql.util.OSUtil;
import service.ClientService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ClientView {
    private static Scanner sc = new Scanner(System.in);
    private static ClientService clientService = new ClientService();


    //Méthode globale pour la gestion des personnes

    public void gestionPersonne() throws Exception {

        boolean running = true;

        //while loop
        while (running) {
            showMenu();
            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1:
                    ajouterPersonne();
                    break;
                case 2:
                    rechercherPersonneParId();
                    break;
                case 3:
                    getAllPersonne();
                    break;
                case 4:
                    supprimerPersonne();
                    break;
                case 5:
                    modifierPersonne();
                    break;
                case 6:
                    MenuPrincipale.start();
                    break;
                case 7:
                    running = false;
                    System.out.println("Vous etes hors programme");
                    break;
                default:
                    System.out.println("choix invalide !!!");
            }
        }
    }


    //afficher menu :
    public static void showMenu() {
        System.out.println("\n=== Gestion des Personnes ===");
        System.out.println("1. Ajouter une Personne");
        System.out.println("2. Afficher une Personne");
        System.out.println("3. Afficher tous les Personnes");
        System.out.println("4. Supprimer une Personne");
        System.out.println("5. Modifier une Personne");
        System.out.println("6. Retour");
        System.out.print("Choix : ");
    }

    //ajouter personne :
    public static void ajouterPersonne() throws SQLException {
        System.out.println("Taper 1 pour saisir les données d'un employée et 2 pour les données d'un professionel :");
        int type = sc.nextInt();
        sc.nextLine();

        System.out.println("Nom :");
        String nom = sc.nextLine();

        System.out.println("Preom :");
        String prenom = sc.nextLine();

        System.out.println("Date de naissance (yyyy-mm-dd) : ");
        LocalDate dateNaissance = LocalDate.parse(sc.nextLine());

        System.out.println("Ville :");
        String ville = sc.nextLine();

        System.out.println("Nombre enfant :");
        int nbEnfants = Integer.parseInt(sc.nextLine());

        System.out.println("Investissement taper (true or false):");
        boolean investissement = sc.nextBoolean();
        sc.nextLine();

        System.out.println("placement taper (true or false):");
        Boolean placement = sc.nextBoolean();
        sc.nextLine();

        System.out.println("Situation familiale (CELIBATAIRE, MARIE, DIVORCE) : ");
        SituationFamiliale situation = SituationFamiliale.valueOf(sc.nextLine().toUpperCase());

        int score = 0;
        LocalDateTime createdAt = LocalDateTime.now();


        //eregistrement de reste des données selon le type de la personne :
        if (type == 1) {
            System.out.print("Salaire : ");
            double salaire = Double.parseDouble(sc.nextLine());
            System.out.print("Ancienneté : ");
            int anciennete = Integer.parseInt(sc.nextLine());
            System.out.print("Poste : ");
            String poste = sc.nextLine();
            System.out.print("Type contrat (CDI, CDD, STAGE) : ");
            TypeContrat contrat = TypeContrat.valueOf(sc.nextLine().toUpperCase());
            System.out.print("Secteur employé (PUBLIC, GRANDE_ENTREPRISE, PME) : ");
            SecteurEmploye secteur = SecteurEmploye.valueOf(sc.nextLine().toUpperCase());

            Employe e = new Employe(nom, prenom, dateNaissance, ville, nbEnfants,
                    investissement, placement, situation, createdAt, score,
                    salaire, anciennete, poste, contrat, secteur);
            clientService.ajouterPersonne(e);

        } else if (type == 2) {

            System.out.println("Revenu :");
            double revenu = Double.parseDouble(sc.nextLine());

            System.out.println("immatriculation Fiscale :");
            String immatriculation = sc.nextLine();
            System.out.print("Secteur activité (AGRICULTURE, SERVICE, COMMERCE, CONSTRUCTION) : ");
            SecteurActivite secteur = SecteurActivite.valueOf(sc.nextLine().toUpperCase());

            System.out.print("Activité : ");
            String activite = sc.nextLine();

            Professionnel p = new Professionnel(nom, prenom, dateNaissance, ville, nbEnfants,
                    investissement, placement, situation, createdAt, score,
                    revenu, immatriculation, secteur, activite);
            clientService.ajouterPersonne(p);
        } else {
            System.out.println("type invalide !!! ");
        }
    }

    //afficher personne par id :
    public static void rechercherPersonneParId() throws SQLException {
        System.out.println("Enter l'id de la personne à chercher:");
        Long id = sc.nextLong();
        sc.nextLine();
        Optional<Personne> personne = clientService.chercherClientParId(id);
        if (personne.isPresent()) {
            System.out.println("personne trouvée :");
            System.out.println(personne.get().toString());
        } else {
            System.out.println("aucune personne est trouvée avec l'id =" + id);
        }
    }


    //afficher tout les personnes (avec les détails selon le type)
    public static void getAllPersonne() throws SQLException {
        System.out.println("voici la liste de tout les personnes :");
        List<Personne> personnes = clientService.getAllPersonne();

        if (personnes.isEmpty()) {
            System.out.println("Aucune personne récupérée");
        } else {
            personnes.forEach(System.out::println);
        }


    }

    //Supprimer une personne
    public static void supprimerPersonne() {
        System.out.println("Saisir l'id de l'élément à chercher :");
        int id = sc.nextInt();
        sc.nextLine();

        try {
            clientService.deletePersonne(id);
            System.out.println("Le client est bien supprimé");
        } catch (Exception e) {
            System.out.println("Erreur :" + e.getMessage());
        }
    }

    //Modifier une personne
    public static void modifierPersonne() {
        System.out.print("ID de la personne à modifier : ");
        long updateId = sc.nextLong();
        sc.nextLine();

        try {
            Optional<Personne> optionalPersonne = clientService.chercherClientParId(updateId);
            if (optionalPersonne.isPresent()) {
                Personne existingPersonne = optionalPersonne.get();

                System.out.print("Nouveau nom (actuel: " + existingPersonne.getNom() + ") : ");
                String newNom = sc.nextLine();
                if (newNom.isEmpty()) newNom = existingPersonne.getNom();

                System.out.print("Nouveau prénom (actuel: " + existingPersonne.getPrenom() + ") : ");
                String newPrenom = sc.nextLine();
                if (newPrenom.isEmpty()) newPrenom = existingPersonne.getPrenom();

                System.out.print("Nouvelle ville (actuelle: " + existingPersonne.getVille() + ") : ");
                String newVille = sc.nextLine();
                if (newVille.isEmpty()) newVille = existingPersonne.getVille();

                System.out.print("Nombre d'enfants (actuel: " + existingPersonne.getNombreEnfants() + ") : ");
                String nbEnfantsInput = sc.nextLine();
                int newNbEnfants = nbEnfantsInput.isEmpty() ? existingPersonne.getNombreEnfants() : Integer.parseInt(nbEnfantsInput);

                System.out.print("Investissement (actuel: " + existingPersonne.getInvestissement() + ") : ");
                String investInput = sc.nextLine();
                boolean newInvestissement = investInput.isEmpty() ? existingPersonne.getInvestissement() : Boolean.parseBoolean(investInput);

                System.out.print("Placement (actuel: " + existingPersonne.getPlacement() + ") : ");
                String placementInput = sc.nextLine();
                boolean newPlacement = placementInput.isEmpty() ? existingPersonne.getPlacement() : Boolean.parseBoolean(placementInput);

                System.out.print("Situation familiale (actuelle: " + existingPersonne.getSituationFamiliale() + ") : ");
                String situationInput = sc.nextLine();
                SituationFamiliale newSituation = situationInput.isEmpty() ? existingPersonne.getSituationFamiliale() : SituationFamiliale.valueOf(situationInput.toUpperCase());

                if (existingPersonne instanceof Employe e) {
                    System.out.print("Salaire (actuel: " + e.getSalaire() + ") : ");
                    String salaireInput = sc.nextLine();
                    double newSalaire = salaireInput.isEmpty() ? e.getSalaire() : Double.parseDouble(salaireInput);

                    System.out.print("Ancienneté (actuelle: " + e.getAnciennete() + ") : ");
                    String ancienneteInput = sc.nextLine();
                    int newAnciennete = ancienneteInput.isEmpty() ? e.getAnciennete() : Integer.parseInt(ancienneteInput);

                    System.out.print("Poste (actuel: " + e.getPoste() + ") : ");
                    String newPoste = sc.nextLine();
                    if (newPoste.isEmpty()) newPoste = e.getPoste();

                    System.out.print("Type contrat (actuel: " + e.getTypeContrat() + ") : ");
                    String contratInput = sc.nextLine();
                    TypeContrat newTypeContrat = contratInput.isEmpty() ? e.getTypeContrat() : TypeContrat.valueOf(contratInput.toUpperCase());

                    System.out.print("Secteur employé (actuel: " + e.getSecteur() + ") : ");
                    String secteurInput = sc.nextLine();
                    SecteurEmploye newSecteur = secteurInput.isEmpty() ? e.getSecteur() : SecteurEmploye.valueOf(secteurInput.toUpperCase());

                    Employe updatedEmploye = new Employe(newNom, newPrenom, e.getDateNaissance(), newVille, newNbEnfants,
                            newInvestissement, newPlacement, newSituation, e.getCreatedAt(), e.getScore(),
                            newSalaire, newAnciennete, newPoste, newTypeContrat, newSecteur);
                    updatedEmploye.setId(existingPersonne.getId());
                    clientService.update(updatedEmploye);

                } else if (existingPersonne instanceof Professionnel p) {
                    System.out.print("Revenu (actuel: " + p.getRevenu() + ") : ");
                    String revenuInput = sc.nextLine();
                    double newRevenu = revenuInput.isEmpty() ? p.getRevenu() : Double.parseDouble(revenuInput);

                    System.out.print("Immatriculation fiscale (actuelle: " + p.getImmatriculationFiscale() + ") : ");
                    String newImmatriculation = sc.nextLine();
                    if (newImmatriculation.isEmpty()) newImmatriculation = p.getImmatriculationFiscale();

                    System.out.print("Secteur activité (actuel: " + p.getSecteurActivite() + ") : ");
                    String secteurInput = sc.nextLine();
                    SecteurActivite newSecteurActivite = secteurInput.isEmpty() ? p.getSecteurActivite() : SecteurActivite.valueOf(secteurInput.toUpperCase());

                    System.out.print("Activité (actuelle: " + p.getActivite() + ") : ");
                    String newActivite = sc.nextLine();
                    if (newActivite.isEmpty()) newActivite = p.getActivite();

                    Professionnel updatedProfessionnel = new Professionnel(newNom, newPrenom, p.getDateNaissance(), newVille, newNbEnfants,
                            newInvestissement, newPlacement, newSituation, p.getCreatedAt(), p.getScore(),
                            newRevenu, newImmatriculation, newSecteurActivite, newActivite);
                    updatedProfessionnel.setId(existingPersonne.getId());
                    clientService.update(updatedProfessionnel);
                }

                System.out.println("Personne modifiée avec succès.");

            } else {
                System.out.println("Personne non trouvée.");
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }


}
