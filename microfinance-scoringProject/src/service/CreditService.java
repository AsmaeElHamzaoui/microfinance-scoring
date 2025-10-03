package service;

import model.Credit;
import repository.CreditRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CreditService {
    private static final CreditRepository repoCredit = new CreditRepository();

    public  void ajouterCredit(Credit credit) throws SQLException {
        try {
            long id = repoCredit.save(credit);
            System.out.println("Le crédit est ajouté avec succès avec l'id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  Optional<Credit> chercherCreditParId(Long id) throws SQLException {
        try {
            return repoCredit.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Credit> getAllCredits() throws SQLException {
        try {
            return repoCredit.getAllCredits();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retourner une liste vide si aucune crédit n'existe
        }
    }

    public static List<Credit> getCreditsByClientId(long clientId) throws SQLException {
        try {
            return repoCredit.findByClientId(clientId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void updateCredit(Credit credit) throws SQLException {
        try {
            repoCredit.update(credit);
            System.out.println("Le crédit est mis à jour avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCredit(long id) throws SQLException {
        try {
            repoCredit.delete(id);
            System.out.println("Le crédit est supprimé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

