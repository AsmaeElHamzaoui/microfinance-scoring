package service;


import model.*;
import model.enums.Decision;
import model.enums.SituationFamiliale;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Service central pour le calcul du score crédit
 */
public class ScoringSystem {

    // **** STABILITÉ PROFESSIONNELLE (30 points) ****
    public static int stabiliteProffessionnelle(Personne personne) {
        //test pour le type d'emploi :
        int score = 0;

        if (personne instanceof Employe) {
            Employe e = (Employe) personne;
            String condition = e.getTypeContrat() + "-" + e.getSecteur();
            switch (condition) {
                case "CDI-PUBLIC":
                    score += 25;
                    break;
                case "CDI-GRANDE_ENTREPRISE":
                    score += 15;
                    break;
                case "CDI-PME":
                    score += 12;
                    break;
                case "CDD":
                case "INTERIM":
                    score += 10;
                    break;
                default:
                    System.out.println("saisie invalide !!!");
            }

            //test d'ancienneté :
            int anciennete = e.getAnciennete();
            if (anciennete == 5) score += 5;
            if (anciennete > 2 && anciennete < 5) score += 3;
            if (anciennete >= 1 && anciennete <= 2) score += 1;
            if (anciennete < 1) return score;

        } else if (personne instanceof Professionnel) {
            score += 30;
        }
        return score;
    }

    // **** CAPACITÉ FINANCIÈRE (30 points) ****
    public static int capaciteFinanciere(Personne personne) {
        int score = 0;
        if (personne instanceof Professionnel) {
            Professionnel p = (Professionnel) personne;
            double revenu = p.getRevenu();
            if (revenu >= 10000) score += 30;
            if (revenu >= 8000 && revenu < 10000) score += 25;
            if (revenu >= 5000 && revenu < 8000) score += 20;
            if (revenu >= 3000 && revenu < 5000) score += 15;
            if (revenu < 3000) score += 10;
        }
        return score;
    }

    // **** HISTORIQUE (15 points) **** //
    public static int historiquePaiement(Personne personne) throws Exception{
        int score=0;
        List<Credit> credits=CreditService.getCreditsByClientId(personne.getId());
        if(credits.isEmpty()){
            return score; // dans le cas ou il n'existe aucun crédit pour cette personne
        }

        //Récuper le dernier credit
        Credit dernierCredit=credits
                .stream()
                .max(Comparator.comparing(Credit::getDateCredit))
                .orElse(null);

        if(dernierCredit==null) return score;

        List<Echeance> echeances=EcheanceService.getEcheancesByCreditId(dernierCredit.getId());
        int nbrRetads=0;
        boolean aucunIncident=true;

        for(Echeance e:echeances){
            List<Incident> incidents=IncidentService.getIncidentsByEcheanceId(e.getId());
            for(Incident i:incidents){
                  switch(i.getTypeIncident()){
                      case IMPAYE_NON_REGLE :
                          score -=10;
                          aucunIncident = false;
                          break;
                      case IMPAYE_REGLE:
                          score +=5;
                          aucunIncident = false;
                          break;
                      case EN_RETARD:
                          nbrRetads++;
                          aucunIncident = false;
                          break;
                      case PAYE_EN_RETARD:
                          score +=3;
                          aucunIncident = false;
                          break;
                      default:
                          break;
                  }
            }
        }

        //traitement des retards sur le dernier crédit
        if(nbrRetads>=1 && nbrRetads<=3){
            score -=3;
        }else if(nbrRetads >=4){
            score -=5;
        }

        //dans le cas ou tout les incidents ont le statut : payé au temps
        if(aucunIncident){
            score +=10;
        }

        //Garder les 15 points c'est aucun cas est réalisable:
        if(score>15) score=15;
        if(score< -15) score =-15;


        return score;

    }


    // **** RELATION CLIENT (10 points) *** ///
    public static int relationClient(Personne personne) throws SQLException {
        int score = 0;

        List<Credit> credits = CreditService.getCreditsByClientId(personne.getId());
        boolean nouveau = credits.isEmpty();

        //nouveau client
        if (nouveau) {
            LocalDate aujourdHui = LocalDate.now();

            //calcul de l'age :
            int age = Period.between(personne.getDateNaissance(), aujourdHui).getYears();
            if (age >= 18 && age <= 25) score += 4;
            if (age >= 26 && age <= 35) score += 8;
            if (age >= 36 && age <= 55) score += 10;
            if (age > 50) score += 6;

            // situation familiale :
            if (personne.getSituationFamiliale() == SituationFamiliale.MARIE) {
                score += 3;
            } else if (personne.getSituationFamiliale() == SituationFamiliale.CELIBATAIRE) {
                score += 2;
            }

            //NombreEnfant:
            int nbrEnfant = personne.getNombreEnfants();
            if (nbrEnfant == 0) score += 2;
            if (nbrEnfant >= 1 && nbrEnfant <= 2) score += 1;

        } else {
            LocalDate premierCrédit = credits
                    .stream()
                    .filter(c -> c.getClientId() == personne.getId())
                    .map(Credit::getDateCredit)
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());
            int anciennete = Period.between(premierCrédit, LocalDate.now()).getYears();
            if (anciennete > 3) {
                return score += 10;
            } else if (anciennete >= 1 && anciennete <= 3) {
                return score += 8;
            } else {
                return score += 5;
            }
        }
        return score;
    }

    // ****  CRITÈRES COMPLÉMENTAIRES (10 points) *** //
    public static int criteresComplementaire(Personne personne) {
        int score = 0;
        if (personne.getInvestissement() == true) {
            return score += 10;
        }
        if (personne.getPlacement() == true) {
            return score += 10;
        }
        return score;
    }


    public static int totalScore(Personne personne) throws Exception {
        int score = 0;
        score += stabiliteProffessionnelle(personne);
        score += capaciteFinanciere(personne);
        score += historiquePaiement(personne);
        score += relationClient(personne);
        score += criteresComplementaire(personne);

        return score;
    }

    //Caclcul du montant octroyé:
    public static double calculMontantOctroye(Personne personne) throws Exception {
        List<Credit> listCredit = CreditService.getCreditsByClientId(personne.getId());
        boolean nouveau = listCredit.isEmpty();
        double montantOctroye = 0;
        if (nouveau && personne instanceof Employe) {
            Employe e = (Employe) personne;
            montantOctroye = e.getSalaire() * 4;
        } else if (personne instanceof Professionnel) {
            Professionnel p = (Professionnel) personne;
            if (totalScore(p) >= 60 && totalScore(p) <= 80) {
                montantOctroye = p.getRevenu() * 7;
            } else if (totalScore(p) > 80) {
                montantOctroye = p.getRevenu() * 10;
            }
        }
        return montantOctroye;
    }


    //Décision automatique :
    public static Decision decisonAutomatique(Personne personne) throws Exception {
        List<Credit> listCredit = CreditService.getCreditsByClientId(personne.getId());
        boolean nouveau = listCredit.isEmpty();

        if (totalScore(personne) >= 80) {
            return Decision.ACCORD_IMMEDIAT;
        } else if (nouveau) {
            if (totalScore(personne) >= 60 && totalScore(personne) <= 70) {
                return Decision.ETUDE_MANUELLE;
            } else if (totalScore(personne) < 60) {
                return Decision.REFUS_AUTOMATIQUE;
            }
        } else {
            if (totalScore(personne) >= 50 && totalScore(personne) <= 79) {
                return Decision.ETUDE_MANUELLE;
            } else if (totalScore(personne) < 50) {
                return Decision.REFUS_AUTOMATIQUE;
            }
        }
        return Decision.REFUS_AUTOMATIQUE;
    }

}

