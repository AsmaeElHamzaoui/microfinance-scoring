package view;

import java.util.Scanner;

public class MenuPrincipale {

    public static void start() throws Exception {
        ClientView client = new ClientView();
        CreditView credit = new CreditView();
        GestionEcheanceIncident echeanceIncident = new GestionEcheanceIncident();
        Analytics analytics = new Analytics();

        Scanner sc = new Scanner(System.in);

        boolean running = true;
        while (running) {
            showGlobalMenu();
            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1:
                    client.gestionPersonne();
                    break;
                case 2:
                    credit.gestionCredit();
                    break;
                case 3:
                    echeanceIncident.gestionEcheanceIncident();
                    break;
                case 4:
                    analytics.analytics();
                    break;
                case 5:
                    running=false;
                    System.out.println("Merci pour votre visite");
                    break;

                default:
                    System.out.println("choix invalide !!!");
            }
        }


    }

    public static void showGlobalMenu() {
        System.out.println("1-Gestion des personnes");
        System.out.println("2-Gestion des crédits");
        System.out.println("3-Gestion des echeances et incidents");
        System.out.println("4-Analytics");
        System.out.println("5-Quitter");
        System.out.println("Enter votre choix :");
    }


}
