package model;

import model.enums.TypeIncident;

import java.time.LocalDate;

public class Incident {
    private int id;
    private int echeanceId;
    private LocalDate dateIncident;
    private Echeance echeance;
    private int score;
    private TypeIncident typeIncident;

    public Incident(int echeanceId,LocalDate dateIncident, Echeance echeance, int score, TypeIncident typeIncident) {
        this.echeanceId=echeanceId;
        this.dateIncident = dateIncident;
        this.echeance = echeance;
        this.score = score;
        this.typeIncident = typeIncident;
    }

    // Getters and Setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getEcheanceId() {return echeanceId;}
    public void setEcheanceId(int echeanceId) {this.echeanceId = echeanceId;}
    public LocalDate getDateIncident() { return dateIncident; }
    public void seAtDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }
    public Echeance getEcheance() { return echeance; }
    public void setEcheance(Echeance echeance) { this.echeance = echeance; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public TypeIncident getTypeIncident() { return typeIncident; }
    public void setTypeIncident(TypeIncident typeIncident) { this.typeIncident = typeIncident; }

    @Override
    public String toString() {
        return "Incident{" +
                "dateIncident=" + dateIncident +
                ", echeance=" + echeance +
                ", score=" + score +
                ", typeIncident=" + typeIncident +
                '}';
    }
}

