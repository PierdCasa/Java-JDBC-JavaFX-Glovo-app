import enums.VehicleType;
import models.*;
import services.*;

import java.util.Arrays;
import java.util.List;

public class Main {
        public static void main(String[] args) {
                App app = new App();

                System.out.println("==========================================================");
                System.out.println("   PLATFORMA DE DELIVERY TIP GLOVO - DEMO COMPLET");
                System.out.println("==========================================================\n");

                // ============================================================
                // 1. CATEGORII
                // ============================================================
                System.out.println(">>> 1. Adaugare categorii");
                Category pizza = app.addCategory("Pizza");
                Category burger = app.addCategory("Burgeri");
                Category paste = app.addCategory("Paste");
                Category desert = app.addCategory("Deserturi");
                Category bauturi = app.addCategory("Bauturi");
                Category shawarma = app.addCategory("Shawarma");

                // Afis. categegorii sortate alfabetic (Comparable pe Category)
                app.showSortedCategories();

                // ============================================================
                // 2. PRODUSE (cu ingrediente locked/optionale)
                // ============================================================
                System.out.println("\n>>> 2. Adaugare produse (cu ingrediente personalizabile)");

                Product margherita = app.addProduct("Pizza Margherita", pizza, "Sos rosii, mozzarella, busuioc", 28.50);
                Product pepperoni = app.addProduct("Pizza Pepperoni", pizza, "Sos rosii, mozzarella, pepperoni", 35.00);
                Product quattro = app.addProduct("Pizza Quattro Formaggi", pizza, "4 tipuri de branza", 38.00);
                Product classicBurger = app.addProduct("Classic Burger", burger, "Vita, salata, rosii, sos", 32.00);
                Product cheeseBurger = app.addProduct("Cheese Burger", burger, "Vita, cheddar, ceapa, sos", 35.50);
                Product carbonara = app.addProduct("Paste Carbonara", paste, "Spaghetti, bacon, parmezan", 29.00);
                Product bolognese = app.addProduct("Paste Bolognese", paste, "Penne, sos carne, parmezan", 27.50);
                Product tiramisu = app.addProduct("Tiramisu", desert, "Mascarpone, cafea, cacao", 22.00);
                Product cheesecake = app.addProduct("Cheesecake", desert, "Crema de branza, fructe", 24.50);
                Product cola = app.addProduct("Coca-Cola 330ml", bauturi, "Bautura racoritoare", 8.00);
                Product apa = app.addProduct("Apa plata 500ml", bauturi, "Apa minerala", 5.00);

                // Produs cu personalizari: SHAWARMA
                Product shawarmaClassic = app.addProduct("Shawarma Classic", shawarma, "Shawarma cu pui si garnituri", 25.00);

                // Ingrediente LOCKED (mereu incluse)
                System.out.println("\n  Configurare ingrediente Shawarma:");
                shawarmaClassic.addLockedIngredient(new Ingredient("Pui", 0, true));
                shawarmaClassic.addLockedIngredient(new Ingredient("Cartofi prajiti", 0, true));
                shawarmaClassic.addLockedIngredient(new Ingredient("Paine", 0, true));
                shawarmaClassic.addLockedIngredient(new Ingredient("Salata", 0, true));

                // Ingrediente OPTIONALE (clientul alege, cu cost extra)
                Ingredient sosUsturoi = new Ingredient("Sos usturoi", 2.00, false);
                Ingredient sosPicant = new Ingredient("Sos picant", 2.00, false);
                Ingredient ceapaCrocanta = new Ingredient("Ceapa crocanta", 3.00, false);
                Ingredient ardeiIute = new Ingredient("Ardei iute", 1.50, false);
                Ingredient branzaExtra = new Ingredient("Branza extra", 4.00, false);

                shawarmaClassic.addOptionalIngredient(sosUsturoi);
                shawarmaClassic.addOptionalIngredient(sosPicant);
                shawarmaClassic.addOptionalIngredient(ceapaCrocanta);
                shawarmaClassic.addOptionalIngredient(ardeiIute);
                shawarmaClassic.addOptionalIngredient(branzaExtra);

                // Afiseaza customizarile disponibile
                System.out.println();
                shawarmaClassic.showAvailableCustomizations();

                // Adauga ingrediente optionale si la burger
                Ingredient baconExtra = new Ingredient("Bacon extra", 5.00, false);
                Ingredient jalapenoIng = new Ingredient("Jalapeno", 2.50, false);
                classicBurger.addLockedIngredient(new Ingredient("Carne vita", 0, true));
                classicBurger.addLockedIngredient(new Ingredient("Chifla", 0, true));
                classicBurger.addOptionalIngredient(baconExtra);
                classicBurger.addOptionalIngredient(jalapenoIng);

                // ============================================================
                // 3. RESTAURANTE (cu locatii geografice)
                // ============================================================
                System.out.println("\n>>> 3. Adaugare restaurante (cu coordonate GPS)");

                Restaurant laItaliano = new Restaurant("La Italiano", "Str. Victoriei 12, Bucuresti", 0.0,
                                new Location(10.0, 15.0, "Str. Victoriei 12"));
                app.addRestaurant(laItaliano);
                laItaliano.addProduct(margherita);
                laItaliano.addProduct(pepperoni);
                laItaliano.addProduct(quattro);
                laItaliano.addProduct(carbonara);
                laItaliano.addProduct(bolognese);
                laItaliano.addProduct(tiramisu);
                laItaliano.addProduct(cola);
                laItaliano.addProduct(apa);

                Restaurant burgerHouse = new Restaurant("Burger House", "Bd. Unirii 45, Bucuresti", 0.0,
                                new Location(20.0, 25.0, "Bd. Unirii 45"));
                app.addRestaurant(burgerHouse);
                burgerHouse.addProduct(classicBurger);
                burgerHouse.addProduct(cheeseBurger);
                burgerHouse.addProduct(cheesecake);
                burgerHouse.addProduct(cola);
                burgerHouse.addProduct(apa);

                Restaurant shawarmaKing = new Restaurant("Shawarma King", "Calea Mosilor 88, Bucuresti", 0.0,
                                new Location(5.0, 8.0, "Calea Mosilor 88"));
                app.addRestaurant(shawarmaKing);
                shawarmaKing.addProduct(shawarmaClassic);
                shawarmaKing.addProduct(cola);
                shawarmaKing.addProduct(apa);

                // Afis. restaurante si meniuri
                app.showAllRestaurants();

                System.out.println("\n>>> 4. Afisare meniu restaurant");
                app.getMenu(laItaliano);
                app.getMenu(shawarmaKing);

                // ============================================================
                // 5. INREGISTRARE UTILIZATORI (cu locatii si wallet)
                // ============================================================
                System.out.println("\n>>> 5. Inregistrare livratori (cu locatii GPS)");
                DeliveryMan livrator1 = app.registerDeliveryMan("Andrei", "Popescu", "0741234567",
                                "andrei@glovo.ro", "pass123", "B-123-ABC", VehicleType.MOTORCYCLE,
                                new Location(12.0, 18.0, "Zona Victoriei"));
                DeliveryMan livrator2 = app.registerDeliveryMan("Mihai", "Ionescu", "0752345678",
                                "mihai@glovo.ro", "pass456", "B-456-DEF", VehicleType.BICYCLE,
                                new Location(22.0, 28.0, "Zona Unirii"));
                DeliveryMan livrator3 = app.registerDeliveryMan("George", "Stanescu", "0763456789",
                                "george@glovo.ro", "pass789", "B-789-GHI", VehicleType.CAR,
                                new Location(7.0, 10.0, "Zona Mosilor"));

                System.out.println("\n>>> 6. Inregistrare clienti");
                Customer client1 = app.register("Maria", "Dumitrescu", "0774567890",
                                "maria@email.com", "maria123", "Str. Floreasca 10, Bucuresti",
                                new Location(15.0, 20.0, "Str. Floreasca 10"));
                Customer client2 = app.register("Ion", "Vasilescu", "0785678901",
                                "ion@email.com", "ion456", "Bd. Magheru 22, Bucuresti",
                                new Location(18.0, 12.0, "Bd. Magheru 22"));

                // Inregistrare cu email deja existent
                app.register("Ana", "Test", "0752401581", "maria@email.com", "test", "Adresa test",
                                new Location(0, 0, "Test"));

                System.out.println("\n>>> 6b. Inregistrare Admin");
                Admin admin = app.getUserService().registerAdmin("Alexandru", "Admin", "0700000000",
                                "admin@glovo.ro", "admin123");

                // ============================================================
                // 6. TOP-UP WALLET (simulare depunere bani)
                // ============================================================
                System.out.println("\n>>> 7. Top-up portofel virtual (simulare card)");
                app.topUpWallet(client1, 500.00);
                app.topUpWallet(client2, 300.00);

                // ============================================================
                // 7. PROMO CODES
                // ============================================================
                System.out.println("\n>>> 8. Adaugare coduri promotionale");
                PromoCode promo20 = app.addPromoCode("MINUS20", 20.0, false, 5);
                PromoCode promoFree = app.addPromoCode("FREEDELIVERY", 0.0, true, 10);
                System.out.println("  " + promo20);
                System.out.println("  " + promoFree);

                // ============================================================
                // 8. LOGIN SI PLASARE COMANDA
                // ============================================================
                System.out.println("\n>>> 9. Login");
                // Login gresit
                app.login("maria@email.com", "parolagresita");
                // Login corect
                app.login("maria@email.com", "maria123");

                // --- Comanda 1: La Italiano, fara promo, cu tip ---
                System.out.println("\n>>> 10. Plasare comanda #1 (La Italiano, cu tip 10 RON)");
                List<OrderItem> produseComanda1 = Arrays.asList(
                                new OrderItem(margherita, 1),
                                new OrderItem(carbonara, 1),
                                new OrderItem(cola, 2));
                Order comanda1 = app.order(laItaliano, produseComanda1, 10.0, null);

                // --- Comanda 2: Shawarma cu personalizari + promo MINUS20 ---
                System.out.println("\n>>> 11. Plasare comanda #2 (Shawarma personalizat + cod promo MINUS20)");

                // Cream un OrderItem cu personalizari alese
                OrderItem shawarmaItem = new OrderItem(shawarmaClassic, 1, 
                                Arrays.asList(sosUsturoi, ceapaCrocanta, ardeiIute));
                System.out.println("  Shawarma personalizat: " + shawarmaItem);

                List<OrderItem> produseComanda2 = Arrays.asList(shawarmaItem, new OrderItem(cola, 1));
                Order comanda2 = app.order(shawarmaKing, produseComanda2, 5.0, promo20);

                // ============================================================
                // 9. VERIFICARE STATUS SI ACTUALIZARE (State Pattern + Observer)
                // ============================================================
                System.out.println("\n>>> 12. Verificare si actualizare status comanda (State Pattern)");
                app.checkStatus(comanda1);

                System.out.println("\n  --- Actualizare status comanda #1 pas cu pas ---");
                comanda1.updateStatus(); // PENDING -> ACCEPTED
                comanda1.updateStatus(); // ACCEPTED -> PREPARING
                comanda1.updateStatus(); // PREPARING -> PICKED_UP
                comanda1.updateStatus(); // PICKED_UP -> ARRIVING
                comanda1.updateStatus(); // ARRIVING -> DELIVERED (trigger settlement via Observer!)
                comanda1.updateStatus(); // Deja livrata

                // ============================================================
                // 10. REVIEW SYSTEM
                // ============================================================
                System.out.println("\n>>> 13. Adaugare review-uri (doar dupa livrare)");

                // Incercare review pe comanda nelivrata
                app.addReview(client1, comanda2, shawarmaKing, 5, "Excelent!");

                // Livram comanda2 mai intai
                System.out.println("\n  --- Livram comanda #2 ---");
                comanda2.updateStatus(); // PENDING -> ACCEPTED
                comanda2.updateStatus(); // ACCEPTED -> PREPARING
                comanda2.updateStatus(); // PREPARING -> PICKED_UP
                comanda2.updateStatus(); // PICKED_UP -> ARRIVING
                comanda2.updateStatus(); // ARRIVING -> DELIVERED

                // Acum putem lasa review
                System.out.println();
                app.addReview(client1, comanda1, laItaliano, 5, "Mancarea a fost exceptionala! Livrare rapida.");
                app.addReview(client1, comanda2, shawarmaKing, 4, "Shawarma buna, dar putin cam picant.");

                // Afisare review-uri
                System.out.println();
                laItaliano.showReviews();
                shawarmaKing.showReviews();

                // ============================================================
                // 11. SCHIMBA STRATEGIA DE LIVRARE SI PLASEAZA ALTA COMANDA
                // ============================================================
                System.out.println("\n>>> 14. Logout, login alt utilizator, strategie livrare diferita");
                app.logout();
                app.login("ion@email.com", "ion456");

                // Schimba la strategie VehicleType
                System.out.println();
                app.setDeliveryFeeStrategy(new VehicleTypeStrategy());

                System.out.println("\n  --- Comanda #3: Burger House cu strategie VehicleType ---");
                OrderItem burgerItem = new OrderItem(classicBurger, 2, Arrays.asList(baconExtra));
                List<OrderItem> produseComanda3 = Arrays.asList(burgerItem, new OrderItem(cheesecake, 1));
                Order comanda3 = app.order(burgerHouse, produseComanda3, 15.0, promoFree);

                // Schimba la strategie Rainy Weather
                System.out.println();
                app.setDeliveryFeeStrategy(new RainyWeatherStrategy());

                System.out.println("\n  --- Comanda #4: La Italiano cu strategie RainyWeather ---");
                List<OrderItem> produseComanda4 = Arrays.asList(
                                new OrderItem(pepperoni, 1),
                                new OrderItem(tiramisu, 1));
                Order comanda4 = app.order(laItaliano, produseComanda4, 8.0, null);

                // Livram si comanda 3 si 4
                System.out.println("\n  --- Livram comanda #3 ---");
                if (comanda3 != null) {
                        comanda3.updateStatus(); // PENDING -> ACCEPTED
                        comanda3.updateStatus(); // ACCEPTED -> PREPARING
                        comanda3.updateStatus(); // PREPARING -> PICKED_UP
                        comanda3.updateStatus(); // PICKED_UP -> ARRIVING
                        comanda3.updateStatus(); // ARRIVING -> DELIVERED
                }

                System.out.println("\n  --- Livram comanda #4 ---");
                if (comanda4 != null) {
                        comanda4.updateStatus();
                        comanda4.updateStatus();
                        comanda4.updateStatus();
                        comanda4.updateStatus();
                        comanda4.updateStatus();
                }

                // Review de la Ion
                if (comanda3 != null) {
                        System.out.println();
                        app.addReview(client2, comanda3, burgerHouse, 3, "Burger-ul era rece, dar ok.");
                }
                if (comanda4 != null) {
                        app.addReview(client2, comanda4, laItaliano, 5, "Cea mai buna pizza din Bucuresti!");
                }

                // ============================================================
                // 12. ANULARE COMANDA (cu refund)
                // ============================================================
                System.out.println("\n>>> 15. Plasare si anulare comanda (cu refund)");
                List<OrderItem> produseComanda5 = Arrays.asList(new OrderItem(bolognese, 1));
                Order comanda5 = app.order(laItaliano, produseComanda5, 0, null);
                if (comanda5 != null) {
                        comanda5.cancelOrder(); // CANCELLED -> refund via Observer
                }

                // ============================================================
                // 13. RAPOARTE FINANCIARE
                // ============================================================
                System.out.println("\n>>> 16. Rapoarte financiare restaurante");
                laItaliano.showRevenueReport();
                System.out.println();
                shawarmaKing.showRevenueReport();
                System.out.println();
                burgerHouse.showRevenueReport();

                // ============================================================
                // 14. LOYALTY POINTS
                // ============================================================
                System.out.println("\n>>> 17. Puncte de loialitate");
                System.out.println("  Maria - Puncte loialitate: " + client1.getLoyaltyPoints());
                System.out.println("  Ion   - Puncte loialitate: " + client2.getLoyaltyPoints());

                // Rascumparare puncte
                System.out.println("\n  --- Maria rascumpara puncte ---");
                client1.redeemPoints(10); // 10 puncte = 5 RON
                System.out.println("  Sold Maria dupa rascumparare: " + String.format("%.2f", client1.getWallet().getBalance()) + " RON");

                // ============================================================
                // 15. PORTOFEL LIVRATORI
                // ============================================================
                System.out.println("\n>>> 18. Solduri portofel livratori");
                for (DeliveryMan dm : app.getUserService().getDeliveryMen()) {
                        System.out.println("  " + dm.getFirstName() + " " + dm.getSecondName() 
                                + " (" + dm.getVehicleType() + "): " 
                                + String.format("%.2f", dm.getWallet().getBalance()) + " RON");
                }

                // ============================================================
                // 16. AFISARE TOATE COMENZILE
                // ============================================================
                System.out.println("\n>>> 19. Afisare toate comenzile");
                app.showAllOrders();

                // ============================================================
                // 17. COMENZI PER CLIENT
                // ============================================================
                System.out.println("\n>>> 20. Comenzi per client");
                app.showCustomerOrders(client1);
                app.showCustomerOrders(client2);

                // ============================================================
                // 18. REVIEW-URI FINALE
                // ============================================================
                System.out.println("\n>>> 21. Review-uri finale restaurante");
                app.showAllRestaurants();
                System.out.println();
                laItaliano.showReviews();
                System.out.println();
                burgerHouse.showReviews();
                System.out.println();
                shawarmaKing.showReviews();

                // ============================================================
                // 19. ADMIN DASHBOARD
                // ============================================================
                System.out.println("\n>>> 22. Dashboard Admin");
                admin.viewPlatformStats(
                        app.getOrderService().getTotalOrders(),
                        app.getUserService().getCustomers().size(),
                        app.getUserService().getDeliveryMen().size(),
                        app.getTotalRestaurants(),
                        app.getPaymentService().getPlatformRevenue()
                );

                // ============================================================
                // 20. TRANZACTII PLATFORMA
                // ============================================================
                System.out.println("\n>>> 23. Istoric tranzactii platforma (escrow)");
                app.getPaymentService().getPlatformWallet().showTransactionHistory();

                // ============================================================
                // 21. TRANZACTII CLIENT
                // ============================================================
                System.out.println("\n>>> 24. Istoric tranzactii Maria");
                System.out.println("  Sold final: " + String.format("%.2f", client1.getWallet().getBalance()) + " RON");
                client1.getWallet().showTransactionHistory();

                System.out.println("\n>>> 25. Eliminare produs din restaurant");
                laItaliano.removeProduct(apa);
                app.getMenu(laItaliano);

                System.out.println("\n==========================================================");
                System.out.println("   DEMO COMPLET FINALIZAT!");
                System.out.println("==========================================================");
                System.out.println("\n  Design Patterns folosite:");
                System.out.println("  1. State Pattern     - OrderStatus transitions (PENDING->...->DELIVERED)");
                System.out.println("  2. Strategy Pattern  - IDeliveryFeeStrategy (Standard, Rainy, VehicleType)");
                System.out.println("  3. Observer Pattern  - IOrderObserver (auto-settlement/refund)");
                System.out.println("  4. Facade Pattern    - App.java deleaga la servicii specializate");
                System.out.println("\n  OOP Principles:");
                System.out.println("  - Inheritance:     User -> Customer, DeliveryMan, Admin");
                System.out.println("  - Interfaces:      IAuthenticable, IBusiness, IOrderObserver, IDeliveryFeeStrategy");
                System.out.println("  - Polymorphism:    Restaurant/GroceryStore implement IBusiness");
                System.out.println("  - Encapsulation:   Private fields, public getters/setters");
                System.out.println("  - Abstraction:     User abstract class");
                System.out.println("  - Comparable:      Category implements Comparable<Category>");
                System.out.println("\n Exit \n");
        }
}
