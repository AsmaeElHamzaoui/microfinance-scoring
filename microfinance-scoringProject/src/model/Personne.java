package model;

import model.enums.SituationFamiliale;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Personne {
    private long id; // sera généré par la DB
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String ville;
    private int nombreEnfants;
    private double investissement;
    private double placement;
    private SituationFamiliale situationFamiliale;
    private LocalDateTime createdAt;
    private int score;

    // Constructeur sans id pour la création
    public Personne(String nom, String prenom, LocalDate dateNaissance, String ville, int nombreEnfants,
                    double investissement, double placement, SituationFamiliale situationFamiliale,
                    LocalDateTime createdAt, int score) {
        this.id = 0; // temporaire avant insertion
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.ville = ville;
        this.nombreEnfants = nombreEnfants;
        this.investissement = investissement;
        this.placement = placement;
        this.situationFamiliale = situationFamiliale;
        this.createdAt = createdAt;
        this.score = score;
    }

    // Getters et Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public int getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(int nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public double getInvestissement() { return investissement; }
    public void setInvestissement(double investissement) { this.investissement = investissement; }
    public double getPlacement() { return placement; }
    public void setPlacement(double placement) { this.placement = placement; }
    public SituationFamiliale getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(SituationFamiliale situationFamiliale) { this.situationFamiliale = situationFamiliale; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", ville='" + ville + '\'' +
                ", nombreEnfants=" + nombreEnfants +
                ", investissement=" + investissement +
                ", placement=" + placement +
                ", situationFamiliale=" + situationFamiliale +
                ", createdAt=" + createdAt +
                ", score=" + score +
                '}';
    }
}
