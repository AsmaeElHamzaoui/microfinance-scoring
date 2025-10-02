package repository;

import model.Incident;
import model.Echeance;
import model.enums.TypeIncident;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncidentRepository {

    public long save(Incident incident) throws SQLException {
        String query = "INSERT INTO incident (echeance_id, date_incident, score, type_incident) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, incident.getEcheanceId()); // Note: Assuming echeanceId changed to long in model for consistency
            stmt.setDate(2, Date.valueOf(incident.getDateIncident()));
            stmt.setInt(3, incident.getScore());
            stmt.setString(4, incident.getTypeIncident().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int  generatedId = rs.getInt(1);
                    incident.setId(generatedId); // Note: Assuming id changed to long in model for consistency
                    return generatedId;
                }
            }
        }
        return -1;
    }

    public Optional<Incident> findById(long id) throws SQLException {
        String query = "SELECT * FROM incident WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToIncident(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Incident> getAllIncidents() throws SQLException {
        String query = "SELECT * FROM incident";
        List<Incident> incidents = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(mapResultSetToIncident(rs));
                }
            }
        }
        return incidents;
    }

    public List<Incident> findByEcheanceId(long echeanceId) throws SQLException {
        String query = "SELECT * FROM incident WHERE echeance_id = ?";
        List<Incident> incidents = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, echeanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    incidents.add(mapResultSetToIncident(rs));
                }
            }
        }
        return incidents;
    }

    public void update(Incident incident) throws SQLException {
        String query = "UPDATE incident SET echeance_id = ?, date_incident = ?, score = ?, type_incident = ? " +
                "WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, incident.getEcheanceId());
            stmt.setDate(2, Date.valueOf(incident.getDateIncident()));
            stmt.setInt(3, incident.getScore());
            stmt.setString(4, incident.getTypeIncident().name());
            stmt.setLong(5, incident.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String query = "DELETE FROM incident WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Incident mapResultSetToIncident(ResultSet rs) throws SQLException {
        // Note: echeance object is not loaded here; set it manually in service if needed
        // Assuming id and echeanceId changed to long in model for consistency
        Incident incident = new Incident(
                (int) rs.getLong("echeance_id"), // Cast to int if model uses int; recommend changing to long
                rs.getDate("date_incident").toLocalDate(),
                null, // echeance not loaded; load separately if required
                rs.getInt("score"),
                TypeIncident.valueOf(rs.getString("type_incident"))
        );
        incident.setId((int) rs.getLong("id")); // Cast if int; recommend long
        incident.setEcheanceId((int) rs.getLong("echeance_id"));
        return incident;
    }
}
