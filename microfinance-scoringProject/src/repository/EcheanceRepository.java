package repository;

import model.Echeance;
import model.enums.StatutPaiement;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EcheanceRepository {

    public long save(Echeance echeance) throws SQLException {
        String query = "INSERT INTO echeance (credit_id, date_echeance, mensualite, date_paiement, statut_paiement) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, echeance.getCreditId()); // Note: Assuming creditId changed to long in model for consistency
            stmt.setDate(2, Date.valueOf(echeance.getDateEcheance()));
            stmt.setDouble(3, echeance.getMensualite());
            if (echeance.getDatePaiement() != null) {
                stmt.setDate(4, Date.valueOf(echeance.getDatePaiement()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, echeance.getStatutPaiement().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    echeance.setId(generatedId); // Assuming Echeance has an id field; add if not
                    return generatedId;
                }
            }
        }
        return -1;
    }

    public Optional<Echeance> findById(long id) throws SQLException {
        String query = "SELECT * FROM echeance WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEcheance(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Echeance> getAllEcheances() throws SQLException {
        String query = "SELECT * FROM echeance";
        List<Echeance> echeances = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    echeances.add(mapResultSetToEcheance(rs));
                }
            }
        }
        return echeances;
    }

    public List<Echeance> findByCreditId(long creditId) throws SQLException {
        String query = "SELECT * FROM echeance WHERE credit_id = ?";
        List<Echeance> echeances = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, creditId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    echeances.add(mapResultSetToEcheance(rs));
                }
            }
        }
        return echeances;
    }

    public void update(Echeance echeance) throws SQLException {
        String query = "UPDATE echeance SET credit_id = ?, date_echeance = ?, mensualite = ?, " +
                "date_paiement = ?, statut_paiement = ? " +
                "WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, echeance.getCreditId());
            stmt.setDate(2, Date.valueOf(echeance.getDateEcheance()));
            stmt.setDouble(3, echeance.getMensualite());
            if (echeance.getDatePaiement() != null) {
                stmt.setDate(4, Date.valueOf(echeance.getDatePaiement()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            stmt.setString(5, echeance.getStatutPaiement().name());
            stmt.setLong(6, echeance.getId()); // Assuming Echeance has an id field; add if not

            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String query = "DELETE FROM echeance WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Echeance mapResultSetToEcheance(ResultSet rs) throws SQLException {
        // Assuming Echeance has an id field; add if not: private long id; with getter/setter
        Echeance echeance = new Echeance(
                (int) rs.getLong("credit_id"), // Cast to int if model uses int; recommend changing to long
                rs.getDate("date_echeance").toLocalDate(),
                rs.getDouble("mensualite"),
                rs.getDate("date_paiement") != null ? rs.getDate("date_paiement").toLocalDate() : null,
                StatutPaiement.valueOf(rs.getString("statut_paiement"))
        );
        echeance.setId(rs.getInt("id")); // Set id if field exists
        return echeance;
    }
}
