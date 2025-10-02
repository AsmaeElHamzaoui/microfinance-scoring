package service;

import model.Echeance;
import repository.EcheanceRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EcheanceService {
    private static final EcheanceRepository repoEcheance = new EcheanceRepository();

    public static void ajouterEcheance(Echeance echeance) throws SQLException {
        try {
            long id = repoEcheance.save(echeance);
            System.out.println("L'échéance est ajoutée avec succès avec l'id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cette méthode était manquante ou non compilée – elle est maintenant explicitement définie
    public static Optional<Echeance> chercherEcheanceParId(Long id) throws SQLException {
        try {
            return repoEcheance.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Méthode alias pour plus de lisibilité (optionnelle, mais recommandée)
    public static Optional<Echeance> getEcheanceById(long id) throws SQLException {
        return chercherEcheanceParId(id);
    }

    public static List<Echeance> getAllEcheances() throws SQLException {
        try {
            return repoEcheance.getAllEcheances();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retourner une liste vide si aucune échéance n'existe
        }
    }

    public static List<Echeance> getEcheancesByCreditId(long creditId) throws SQLException {
        try {
            return repoEcheance.findByCreditId(creditId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void updateEcheance(Echeance echeance) throws SQLException {
        try {
            repoEcheance.update(echeance);
            System.out.println("L'échéance est mise à jour avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteEcheance(long id) throws SQLException {
        try {
            repoEcheance.delete(id);
            System.out.println("L'échéance est supprimée avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}