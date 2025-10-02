package repository;

import model.Credit;
import model.enums.Decision;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreditRepository {

    public long save(Credit credit) throws SQLException {
        String query = "INSERT INTO credit (client_id, date_credit, montant_demande, montant_octroye, " +
                "taux_interet, duree_en_mois, type_credit, decision) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, credit.getClientId());
            stmt.setDate(2, Date.valueOf(credit.getDateCredit()));
            stmt.setDouble(3, credit.getMontantDemande());
            stmt.setDouble(4, credit.getMontantOctroye());
            stmt.setDouble(5, credit.getTauxInteret());
            stmt.setInt(6, credit.getDureeEnMois());
            stmt.setString(7, credit.getTypeCredit());
            stmt.setString(8, credit.getDecision().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int  generatedId = rs.getInt(1);
                    credit.setId(generatedId); // Assuming Credit has an id field; add if not
                    return generatedId;
                }
            }
        }
        return -1;
    }

    public Optional<Credit> findById(long id) throws SQLException {
        String query = "SELECT * FROM credit WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCredit(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Credit> getAllCredits() throws SQLException {
        String query = "SELECT * FROM credit";
        List<Credit> credits = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    credits.add(mapResultSetToCredit(rs));
                }
            }
        }
        return credits;
    }

    public List<Credit> findByClientId(long clientId) throws SQLException {
        String query = "SELECT * FROM credit WHERE client_id = ?";
        List<Credit> credits = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    credits.add(mapResultSetToCredit(rs));
                }
            }
        }
        return credits;
    }

    public void update(Credit credit) throws SQLException {
        String query = "UPDATE credit SET client_id = ?, date_credit = ?, montant_demande = ?, " +
                "montant_octroye = ?, taux_interet = ?, duree_en_mois = ?, type_credit = ?, decision = ? " +
                "WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, credit.getClientId());
            stmt.setDate(2, Date.valueOf(credit.getDateCredit()));
            stmt.setDouble(3, credit.getMontantDemande());
            stmt.setDouble(4, credit.getMontantOctroye());
            stmt.setDouble(5, credit.getTauxInteret());
            stmt.setInt(6, credit.getDureeEnMois());
            stmt.setString(7, credit.getTypeCredit());
            stmt.setString(8, credit.getDecision().name());
            stmt.setLong(9, credit.getId()); // Assuming Credit has an id field; add if not

            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String query = "DELETE FROM credit WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Credit mapResultSetToCredit(ResultSet rs) throws SQLException {
        // Assuming Credit has an id field; add if not: private long id; with getter/setter
        Credit credit = new Credit(
                rs.getLong("client_id"),
                rs.getDate("date_credit").toLocalDate(),
                rs.getDouble("montant_demande"),
                rs.getDouble("montant_octroye"),
                rs.getDouble("taux_interet"),
                rs.getInt("duree_en_mois"),
                rs.getString("type_credit"),
                Decision.valueOf(rs.getString("decision"))
        );
        credit.setId(rs.getInt("id")); // Set id if field exists
        return credit;
    }
}
