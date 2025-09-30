package model;

import model.enums.SecteurActivite;
import model.enums.SituationFamiliale;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Professionnel extends Personne {
    private double revenu;
    private String immatriculationFiscale;
    private SecteurActivite secteurActivite;
    private String activite;

    public Professionnel(long id,String nom, String prenom, LocalDate dateNaissance, String ville, int nombreEnfants,
                         double investissement, double placement, SituationFamiliale situationFamiliale,
                         LocalDateTime createdAt, int score, double revenu, String immatriculationFiscale,
                         SecteurActivite secteurActivite, String activite) {
        super(id,nom, prenom, dateNaissance, ville, nombreEnfants, investissement, placement, situationFamiliale, createdAt, score);
        this.revenu = revenu;
        this.immatriculationFiscale = immatriculationFiscale;
        this.secteurActivite = secteurActivite;
        this.activite = activite;
    }

    // Getters and Setters
    public double getRevenu() { return revenu; }
    public void setRevenu(double revenu) { this.revenu = revenu; }
    public String getImmatriculationFiscale() { return immatriculationFiscale; }
    public void setImmatriculationFiscale(String immatriculationFiscale) { this.immatriculationFiscale = immatriculationFiscale; }
    public SecteurActivite getSecteurActivite() { return secteurActivite; }
    public void setSecteurActivite(SecteurActivite secteurActivite) { this.secteurActivite = secteurActivite; }
    public String getActivite() { return activite; }
    public void setActivite(String activite) { this.activite = activite; }

    @Override
    public String toString() {
        return "Professionnel{" + super.toString() +
                ", revenu=" + revenu +
                ", immatriculationFiscale='" + immatriculationFiscale + '\'' +
                ", secteurActivite=" + secteurActivite +
                ", activite='" + activite + '\'' +
                '}';
    }
}

