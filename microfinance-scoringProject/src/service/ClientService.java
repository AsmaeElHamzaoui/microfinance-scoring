package service;

import model.Personne;
import repository.ClientRepository;

import java.sql.SQLException;

public class ClientService {
    private static final ClientRepository repoPersone=new ClientRepository();

    public static void ajouterPersonne(Personne p) throws SQLException {
        try{
            repoPersone.save(p);
            System.out.println("le client est ajouté avec succés");
        }catch(Exception e){
            e.printStackTrace();
        }
   }
}
