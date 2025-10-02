package service;


import model.*;
import model.enums.SituationFamiliale;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (personne.getInvestissement() == 1) {
            return score += 10;
        }
        if (personne.getPlacement() == 1) {
            return score += 10;
        }
        return score;
    }


    public static int totalScore(Personne personne) throws SQLException {
        int score = 0;
        score += stabiliteProffessionnelle(personne);
        score += capaciteFinanciere(personne);
       // score += capaciteFinanciere(personne);
        score += relationClient(personne);
        score += criteresComplementaire(personne);

        return score;
    }
}

