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
import java.util.Optional;
import java.util.Scanner;

public class ClientView {
    private static Scanner sc = new Scanner(System.in);
    private static ClientService clientService = new ClientService();


    //Méthode globale pour la gestion des personnes

    public void gestionPersonne() throws SQLException {

        boolean running= true;

        //while loop
        while(running){
            showMenu();
            int choix=sc.nextInt();
            sc.nextLine();

            switch(choix){
                case 1:
                    ajouterPersonne();
                    break;
                case 2:
                    rechercherPersonneParId();
                    break;
                case 3:
                    running= false;
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
        System.out.println("4. Modifier une Personne");
        System.out.println("5. Supprimer une Personne");
        System.out.println("8. Retour");
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

        System.out.println("Investissement :");
        int investissement = sc.nextInt();
        sc.nextLine();

        System.out.println("placement :");
        int placement = sc.nextInt();
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
        } else{
            System.out.println("type invalide !!! ");
        }
    }

    //afficher personne par id :
    public static void rechercherPersonneParId() throws SQLException {
        System.out.println("Enter l'id de la personne à chercher:");
        Long id = sc.nextLong();
        sc.nextLine();
        Optional<Personne> personne=clientService.chercherClientParId(id);
        if(personne.isPresent()){
            System.out.println("personne trouvée :");
            System.out.println(personne.get().toString());
        }else{
            System.out.println("aucune personne est trouvée avec l'id =" +id);
        }
    }

}
