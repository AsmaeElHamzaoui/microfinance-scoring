package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Employe extends Personne {
    private double salaire;
    private int anciennete;
    private String poste;
    private TypeContrat typeContrat;
    private SecteurEmploye secteur;

    public Employe(String nom, String prenom, LocalDate dateNaissance, String ville, int nombreEnfants,
                   double investissement, double placement, SituationFamiliale situationFamiliale,
                   LocalDateTime createdAt, int score, double salaire, int anciennete, String poste,
                   TypeContrat typeContrat, SecteurEmploye secteur) {
        super(nom, prenom, dateNaissance, ville, nombreEnfants, investissement, placement, situationFamiliale, createdAt, score);
        this.salaire = salaire;
        this.anciennete = anciennete;
        this.poste = poste;
        this.typeContrat = typeContrat;
        this.secteur = secteur;
    }

    // Getters and Setters
    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }
    public int getAnciennete() { return anciennete; }
    public void setAnciennete(int anciennete) { this.anciennete = anciennete; }
    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }
    public SecteurEmploye getSecteur() { return secteur; }
    public void setSecteur(SecteurEmploye secteur) { this.secteur = secteur; }

    @Override
    public String toString() {
        return "Employe{" + super.toString() +
                ", salaire=" + salaire +
                ", anciennete=" + anciennete +
                ", poste='" + poste + '\'' +
                ", typeContrat=" + typeContrat +
                ", secteur=" + secteur +
                '}';
    }
}

