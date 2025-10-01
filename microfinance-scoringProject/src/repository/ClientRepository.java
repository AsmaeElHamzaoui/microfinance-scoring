package repository;

import model.Employe;
import model.Personne;
import model.Professionnel;
import util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class ClientRepository {

    public long save(Personne personne) throws SQLException {
        String query = "INSERT INTO personne (nom, prenom, date_naissance, ville, nombre_enfants, " +
                "investissement, placement, situation_familiale, created_at, score, " +
                "type_client, salaire, anciennete, poste, type_contrat, secteur_employe, " +
                "revenu, immatriculation_fiscale, secteur_activite, activite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, personne.getNom());
            stmt.setString(2, personne.getPrenom());
            stmt.setDate(3, Date.valueOf(personne.getDateNaissance()));
            stmt.setString(4, personne.getVille());
            stmt.setInt(5, personne.getNombreEnfants());
            stmt.setDouble(6, personne.getInvestissement());
            stmt.setDouble(7, personne.getPlacement());
            stmt.setString(8, personne.getSituationFamiliale().name());
            stmt.setTimestamp(9, Timestamp.valueOf(personne.getCreatedAt()));
            stmt.setInt(10, personne.getScore());

            if (personne instanceof Employe e) {
                stmt.setString(11, "EMPLOYE");
                stmt.setDouble(12, e.getSalaire());
                stmt.setInt(13, e.getAnciennete());
                stmt.setString(14, e.getPoste());
                stmt.setString(15, e.getTypeContrat().name());
                stmt.setString(16, e.getSecteur().name());

                stmt.setNull(17, Types.DOUBLE);
                stmt.setNull(18, Types.VARCHAR);
                stmt.setNull(19, Types.VARCHAR);
                stmt.setNull(20, Types.VARCHAR);

            } else if (personne instanceof Professionnel p) {
                stmt.setString(11, "PROFESSIONNEL");
                stmt.setNull(12, Types.DOUBLE);
                stmt.setNull(13, Types.INTEGER);
                stmt.setNull(14, Types.VARCHAR);
                stmt.setNull(15, Types.VARCHAR);
                stmt.setNull(16, Types.VARCHAR);

                stmt.setDouble(17, p.getRevenu());
                stmt.setString(18, p.getImmatriculationFiscale());
                stmt.setString(19, p.getSecteurActivite().name());
                stmt.setString(20, p.getActivite());
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    personne.setId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    public Optional<Personne> findById(long id) throws SQLException {
        String query = "SELECT * FROM personne WHERE id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPersonne(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        String type = rs.getString("type_client");
        if ("EMPLOYE".equals(type)) {
            return new Employe(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getDate("date_naissance").toLocalDate(),
                    rs.getString("ville"),
                    rs.getInt("nombre_enfants"),
                    rs.getDouble("investissement"),
                    rs.getDouble("placement"),
                    model.enums.SituationFamiliale.valueOf(rs.getString("situation_familiale")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getInt("score"),
                    rs.getDouble("salaire"),
                    rs.getInt("anciennete"),
                    rs.getString("poste"),
                    model.enums.TypeContrat.valueOf(rs.getString("type_contrat")),
                    model.enums.SecteurEmploye.valueOf(rs.getString("secteur_employe"))
            );
        } else {
            return new Professionnel(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getDate("date_naissance").toLocalDate(),
                    rs.getString("ville"),
                    rs.getInt("nombre_enfants"),
                    rs.getDouble("investissement"),
                    rs.getDouble("placement"),
                    model.enums.SituationFamiliale.valueOf(rs.getString("situation_familiale")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getInt("score"),
                    rs.getDouble("revenu"),
                    rs.getString("immatriculation_fiscale"),
                    model.enums.SecteurActivite.valueOf(rs.getString("secteur_activite")),
                    rs.getString("activite")
            );
        }
    }
}
