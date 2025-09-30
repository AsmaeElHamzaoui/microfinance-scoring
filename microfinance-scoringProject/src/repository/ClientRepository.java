package repository;

import model.Employe;
import model.Personne;
import model.Professionnel;
import util.DatabaseConnection;

import javax.swing.plaf.SliderUI;
import java.sql.*;

public class ClientRepository {
    // méthode pour ajouter une nouvelPersonne

    public void save(Personne personne) throws SQLException {
        String query="INSERT INTO personne (id, nom, prenom, date_naissance, ville, nombre_enfants, " +
                "investissement, placement, situation_familiale, created_at, score, " +
                "type_client, salaire, anciennete, poste, type_contrat, secteur_employe, " +
                "revenu, immatriculation_fiscale, secteur_activite, activite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection con=DatabaseConnection.getConnection()){
            PreparedStatement stmt=con.prepareStatement(query);
            stmt.setLong(1, personne.getId());
            stmt.setString(2,personne.getNom());
            stmt.setString(3,personne.getPrenom());
            stmt.setDate(4, Date.valueOf(personne.getDateNaissance()));
            stmt.setString(5, personne.getVille());
            stmt.setInt(6, personne.getNombreEnfants());
            stmt.setDouble(7, personne.getInvestissement());
            stmt.setDouble(8, personne.getPlacement());
            stmt.setString(9,personne.getSituationFamiliale().name());
            stmt.setTimestamp(10, Timestamp.valueOf(personne.getCreatedAt()));
            stmt.setInt(11, personne.getScore());

            if(personne instanceof Employe e){
                stmt.setString(12, "EMPLOYE");
                stmt.setDouble(13, e.getSalaire());
                stmt.setInt(14, e.getAnciennete());
                stmt.setString(15, e.getPoste());
                stmt.setString(16, e.getTypeContrat().name());
                stmt.setString(17, e.getSecteur().name());

                stmt.setNull(18, Types.DOUBLE);
                stmt.setNull(19, Types.VARCHAR);
                stmt.setNull(20, Types.VARCHAR);
                stmt.setNull(21, Types.VARCHAR);
            }else if(personne instanceof Professionnel p){
                stmt.setString(12, "PROFESSIONNEL");
                stmt.setNull(13, Types.DOUBLE);
                stmt.setNull(14, Types.INTEGER);
                stmt.setNull(15, Types.VARCHAR);
                stmt.setNull(16, Types.VARCHAR);
                stmt.setNull(17, Types.VARCHAR);

                stmt.setDouble(18, p.getRevenu());
                stmt.setString(19, p.getImmatriculationFiscale());
                stmt.setString(20, p.getSecteurActivite().name());
                stmt.setString(21, p.getActivite());
            }else{
                throw new IllegalArgumentException("Type de personne non supporté");
            }

            stmt.executeUpdate();
        }
    }

}
