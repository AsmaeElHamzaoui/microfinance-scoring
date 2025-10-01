package service;

import model.Personne;
import repository.ClientRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private static final ClientRepository repoPersone = new ClientRepository();

    public static void ajouterPersonne(Personne p) throws SQLException {
        try {
            long id = repoPersone.save(p);
            System.out.println("Le client est ajouté avec succès avec l'id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<Personne> chercherClientParId(Long id) throws SQLException {
        try {
            return repoPersone.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static List<Personne> getAllPersonne() throws SQLException{

        try{
            return repoPersone.getAllPersonne();
        }catch(Exception e){
            e.printStackTrace();
            return  List.of(); // retourner une liste vide dans le cas ou aucune personne existe
        }

    }
}
