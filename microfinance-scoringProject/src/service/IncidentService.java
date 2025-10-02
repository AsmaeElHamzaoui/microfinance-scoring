package service;

import model.Incident;
import model.Echeance;
import repository.IncidentRepository;
import repository.EcheanceRepository; // Non nécessaire si on utilise les méthodes statiques du service

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IncidentService {
    private static final IncidentRepository repoIncident = new IncidentRepository();

    public static void ajouterIncident(Incident incident) throws SQLException {
        try {
            long id = repoIncident.save(incident);
            System.out.println("L'incident est ajouté avec succès avec l'id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Optional<Incident> chercherIncidentParId(int id) throws SQLException {
        try {
            Optional<Incident> incidentOpt = repoIncident.findById(id);
            if (incidentOpt.isPresent()) {
                Incident incident = incidentOpt.get();
                // Chargement optionnel de l'Echeance associée
                Optional<Echeance> echeanceOpt = EcheanceService.chercherEcheanceParId((long) incident.getEcheanceId());
                echeanceOpt.ifPresent(incident::setEcheance);
            }
            return incidentOpt;
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static List<Incident> getAllIncidents() throws SQLException {
        try {
            List<Incident> incidents = repoIncident.getAllIncidents();
            // Chargement optionnel de l'Echeance pour chaque incident
            for (Incident incident : incidents) {
                Optional<Echeance> echeanceOpt = EcheanceService.chercherEcheanceParId((long) incident.getEcheanceId());
                echeanceOpt.ifPresent(incident::setEcheance);
            }
            return incidents;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retourner une liste vide si aucun incident n'existe
        }
    }

    public static List<Incident> getIncidentsByEcheanceId(long echeanceId) throws SQLException {
        try {
            List<Incident> incidents = repoIncident.findByEcheanceId(echeanceId);
            // Chargement optionnel de l'Echeance (même pour tous les incidents de cette échéance)
            Optional<Echeance> echeanceOpt = EcheanceService.chercherEcheanceParId(echeanceId);
            echeanceOpt.ifPresent(e -> incidents.forEach(i -> i.setEcheance(e)));
            return incidents;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void updateIncident(Incident incident) throws SQLException {
        try {
            repoIncident.update(incident);
            System.out.println("L'incident est mis à jour avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteIncident(long id) throws SQLException {
        try {
            repoIncident.delete(id);
            System.out.println("L'incident est supprimé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}