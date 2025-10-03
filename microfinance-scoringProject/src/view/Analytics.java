package view;

import model.*;
import model.enums.Decision;
import model.enums.SituationFamiliale;
import model.enums.TypeContrat;
import model.enums.TypeIncident;
import org.postgresql.gss.GSSOutputStream;
import org.w3c.dom.ls.LSOutput;
import service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Analytics {

    private static final ClientService clientService = new ClientService();
    private static final CreditService creditService = new CreditService();
    private static final EcheanceService echeanceService = new EcheanceService();
    private static final IncidentService incidentService = new IncidentService();

    static Scanner sc=new Scanner(System.in);
    //analytics menu
    public static void analytics() throws Exception {
         boolean running=true;
         while(running){
             showMenu();
             int choix=sc.nextInt();
             sc.nextLine();

             switch (choix){
                 case 1:
                     afficherClientsEligibles();
                     break;
                 case 2:
                     clientARisque();
                     break;
                 case 3:
                     triAvecCritre();
                     break;
                 case 4:
                     repartitionParTypeEmploi();
                     break;
                     case 5:
                         running = false;
                         MenuPrincipale.start();
                 default :
                     System.out.println("Choix invalide !!!");
             }

         }
    }

    //showMenu
    public static void showMenu(){
        System.out.println("1-Recherche clients éligibles crédit immobilier");
        System.out.println("2-Clients à risque nécessitant suivi et accompagnement(top 10)");
        System.out.println("3-Tri par score, revenus, ancienneté");
        System.out.println("4-Répartition par type d'emploi ");
        System.out.println("5-Retour");
        System.out.println("Entrer votre choix:");
    }


    //Filtrer employés éligibles
    public static List<Employe> employesEligiblesImmobilier() throws SQLException {
        List<Personne> personnes = clientService.getAllPersonne();

        return personnes.stream()
                .filter(p -> p instanceof Employe) // uniquement les employés
                .map(p -> (Employe) p)
                .filter(e -> {
                    int age = Period.between(e.getDateNaissance(), LocalDate.now()).getYears();
                    return age >= 25 && age <= 50;
                })
                .filter(e -> e.getSalaire() > 4000)
                .filter(e -> e.getTypeContrat() == TypeContrat.CDI)
                .filter(e -> {
                    try {
                        return ScoringSystem.totalScore(e) > 70;
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .filter(e -> e.getSituationFamiliale() == SituationFamiliale.MARIE)
                .collect(Collectors.toList());
    }

    // Filtrer professionnels éligibles
    public static List<Professionnel> professionnelsEligiblesImmobilier() throws SQLException {
        List<Personne> personnes = clientService.getAllPersonne();

        return personnes.stream()
                .filter(p -> p instanceof Professionnel) // uniquement les pros
                .map(p -> (Professionnel) p)
                .filter(pro -> {
                    int age = Period.between(pro.getDateNaissance(), LocalDate.now()).getYears();
                    return age >= 25 && age <= 50;
                })
                .filter(pro -> pro.getRevenu() > 4000)
                .filter(pro -> {
                    try {
                        return ScoringSystem.totalScore(pro) > 70;
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .filter(pro -> pro.getSituationFamiliale() == SituationFamiliale.MARIE)
                .collect(Collectors.toList());
    }

    // Affichage
    public static void afficherClientsEligibles() throws SQLException {
        List<Employe> employes = employesEligiblesImmobilier();
        List<Professionnel> pros = professionnelsEligiblesImmobilier();

        System.out.println("===== Employés éligibles au crédit immobilier =====");
        if (employes.isEmpty()) {
            System.out.println("Aucun employé éligible.");
        } else {
            employes.forEach(e -> {
                try {
                    System.out.println(
                            "ID: " + e.getId() +
                                    " | Nom: " + e.getNom() +
                                    " | Âge: " + Period.between(e.getDateNaissance(), LocalDate.now()).getYears() +
                                    " | Salaire: " + e.getSalaire() + " DH" +
                                    " | Score: " + ScoringSystem.totalScore(e)
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        System.out.println("\n===== Professionnels éligibles au crédit immobilier =====");
        if (pros.isEmpty()) {
            System.out.println("Aucun professionnel éligible.");
        } else {
            pros.forEach(p -> {
                try {
                    System.out.println(
                            "ID: " + p.getId() +
                                    " | Nom: " + p.getNom() +
                                    " | Âge: " + Period.between(p.getDateNaissance(), LocalDate.now()).getYears() +
                                    " | Revenu: " + p.getRevenu() + " DH" +
                                    " | Score: " + ScoringSystem.totalScore(p)
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }



    //  Clients à risque nécessitant suivi et accompagnement(top 10) : score <60 & Incidents récents et fréquents( < 6 mois )Triés par score décroissant
    public static void clientARisque() {
        System.out.println("\n=====  Top 10 Clients à risque nécessitant suivi et accompagnement =====");
        try {
            List<Personne> allClients = clientService.getAllPersonne();
            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

            // Filter clients avec  score < 60 and recent incidents <6 mois
            List<Personne> clientsAtRisk = allClients.stream()
                    .filter(p -> {
                        try {
                            return ScoringSystem.totalScore(p) < 60;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .filter(p -> {
                        try {
                            List<Credit> credits = creditService.getCreditsByClientId(p.getId());
                            for (Credit credit : credits) {
                                List<Echeance> echeances = echeanceService.getEcheancesByCreditId(credit.getId());
                                for (Echeance echeance : echeances) {
                                    List<Incident> incidents = incidentService.getIncidentsByEcheanceId(echeance.getId());
                                    for (Incident incident : incidents) {
                                        if (incident.getDateIncident().isAfter(sixMonthsAgo) &&
                                                (incident.getTypeIncident() == TypeIncident.EN_RETARD ||
                                                        incident.getTypeIncident() == TypeIncident.PAYE_EN_RETARD ||
                                                        incident.getTypeIncident() == TypeIncident.IMPAYE_NON_REGLE)) {
                                            return true; // At least one recent incident
                                        }
                                    }
                                }
                            }
                            return false;
                        } catch (SQLException e) {
                            return false;
                        }
                    })
                    .sorted(Comparator.comparingInt(p -> {
                        try {
                            return -ScoringSystem.totalScore(p); // Descending score
                        } catch (Exception e) {
                            return 0;
                        }
                    }))
                    .limit(10) // Top 10
                    .collect(Collectors.toList());

            if (clientsAtRisk.isEmpty()) {
                System.out.println("No clients at risk found.");
                return;
            }

            // Afficher résultat
            for (int i = 0; i < clientsAtRisk.size(); i++) {
                Personne p = clientsAtRisk.get(i);
                try {
                    int score = ScoringSystem.totalScore(p);
                    System.out.println((i + 1) + ". ID: " + p.getId() +
                            " | Name: " + p.getNom() + " " + p.getPrenom() +
                            " | Score: " + score);
                } catch (Exception e) {
                    System.out.println((i + 1) + ". ID: " + p.getId() + " | Error calculating score.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching clients: " + e.getMessage());
        }
    }

    // Tri par score, revenus, ancienneté
    public static void triAvecCritre() throws SQLException {
        System.out.println("\n=====  Tri par score, revenus, ancienneté =====");
        List<Personne> personnes=clientService.getAllPersonne();
        personnes.stream()
                .sorted(
                        Comparator.comparingInt(Personne::getScore).reversed()// 1-score décroissant
                                .thenComparingDouble(p->{
                                    if(p instanceof Employe e) return e.getSalaire();
                                    if(p instanceof Professionnel pr) return pr.getRevenu();
                                    return 0.0;
                                }).reversed() // revenu ou salire décroissant
                                .thenComparingInt(p->{
                                    if(p instanceof Employe e) return e.getAnciennete();
                                    return 0; // professionel n'a pas d'anncienete
                                }).reversed() // 3-anciennete
    ).forEach(System.out::println);
    }

    // Répartition par type d'emploi
    public static void  repartitionParTypeEmploi() throws SQLException{
        System.out.println("\n===== Répartition par type d'emploi  =====");
        List<Personne> personnes=clientService.getAllPersonne();

        //Regroupement par type
        Map<String, List<Personne>> regroupement=personnes.stream()
                .filter(p-> p instanceof Employe e)
                .map(e->(Employe) e)
                .collect(Collectors.groupingBy(e->((Employe) e).getTypeContrat().name()+"-"+((Employe) e).getSecteur()));

        //Parcourir chaque groupe et calculant les statistiques
        regroupement.forEach((type,liste)->{
            int nbClients=liste.size();
            double scoreMoyen=liste.stream()
                                   .mapToInt(e->e.getScore())
                                   .average().orElse(0);
            double revenuMoyen=liste.stream()
                    .filter(p-> p instanceof Employe e)
                    .mapToDouble(e->((Employe) e).getSalaire())
                    .average().orElse(0);

            // règle d'approbation : score >= 80 accepté automatiquement
            long nbAcceptes = liste.stream()
                    .filter(e -> e.getScore() >= 80)
                    .count();
            double tauxApprobation = (nbClients > 0) ? (nbAcceptes * 100.0 / nbClients) : 0;

            System.out.printf("%s : %d clients%n", type, nbClients);
            System.out.printf("Score moyen : %.1f%n", scoreMoyen);
            System.out.printf("Revenus moyens : %.0f DH%n", revenuMoyen);
            System.out.printf("Taux approbation : %.0f%%%n%n", tauxApprobation);
        });

    }




}
