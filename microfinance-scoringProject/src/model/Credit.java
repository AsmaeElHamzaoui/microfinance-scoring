package model;

import model.enums.Decision;

import java.time.LocalDate;

public class Credit {

    private int id;
    private long clientId;
    private LocalDate dateCredit;
    private double montantDemande;
    private double montantOctroye;
    private double tauxInteret;
    private int dureeEnMois;
    private String typeCredit;
    private Decision decision;

    public Credit(long clientId,LocalDate dateCredit, double montantDemande, double montantOctroye, double tauxInteret,
                  int dureeEnMois, String typeCredit, Decision decision) {
        this.clientId = clientId;
        this.dateCredit = dateCredit;
        this.montantDemande = montantDemande;
        this.montantOctroye = montantOctroye;
        this.tauxInteret = tauxInteret;
        this.dureeEnMois = dureeEnMois;
        this.typeCredit = typeCredit;
        this.decision = decision;

    }

    // Getters and Setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public long getClientId() {return clientId;}
    public void setClientId(long clientId) {this.clientId = clientId;}
    public LocalDate getDateCredit() { return dateCredit; }
    public void setDateCredit(LocalDate dateCredit) { this.dateCredit = dateCredit; }
    public double getMontantDemande() { return montantDemande; }
    public void setMontantDemande(double montantDemande) { this.montantDemande = montantDemande; }
    public double getMontantOctroye() { return montantOctroye; }
    public void setMontantOctroye(double montantOctroye) { this.montantOctroye = montantOctroye; }
    public double getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(double tauxInteret) { this.tauxInteret = tauxInteret; }
    public int getDureeEnMois() { return dureeEnMois; }
    public void setDureeEnMois(int dureeEnMois) { this.dureeEnMois = dureeEnMois; }
    public String getTypeCredit() { return typeCredit; }
    public void setTypeCredit(String typeCredit) { this.typeCredit = typeCredit; }
    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }

    @Override
    public String toString() {
        return "Credit{" +
                "dateCredit=" + dateCredit +
                ", montantDemande=" + montantDemande +
                ", montantOctroye=" + montantOctroye +
                ", tauxInteret=" + tauxInteret +
                ", dureeEnMois=" + dureeEnMois +
                ", typeCredit='" + typeCredit + '\'' +
                ", decision=" + decision +
                '}';
    }
}
