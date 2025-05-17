import java.util.List;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // 2. Inisialisasi NPC
        NPC Dasco = new NPC(
            "Dasco", 
            List.of("The Legends of Spakbor", "Cooked Pig's Head", "Wine", "Fugu", "Spakbor Salad"),
            List.of("Fish Sandwich", "Fish Stew", "Baguette", "Fish n Chips"),
            List.of("Legend", "Grape", "Cauliflower", "Wheat", "Pufferfish", "Salmon")
        );

        // 3. View awal: perintah dari pemain
        NPCView view = new NPCView("");

        // 4. Controller yang menghubungkan view dan model
        NPCController controller = new NPCController(Dasco, view);

        // 5. Scanner untuk input dari pemain
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("=== Welcome to NPC Interaction Demo ===");
        while (true) {
            System.out.println("\nAvailable Commands: Chat, Gift, Propose, Marry, Status, Exit");
            System.out.print("Your Action: ");
            command = scanner.nextLine().trim();

            // Set command ke View
            view.setCommand(command);

            if (command.equalsIgnoreCase("Exit")) {
                System.out.println("Exiting game...");
                break;
            }

            switch (command) {
                case "Chat":
                    controller.chat();
                    break;
                case "Gift":
                    System.out.print("Enter item name to give: ");
                    String itemName = scanner.nextLine();
                    controller.gift(itemName);
                    break;
                case "Propose":
                    controller.propose(); // Simulasi sukses propose
                    break;
                case "Marry":
                    Dasco.setEngaged(1);        // Sudah memenuhi syarat nikah
                    controller.marriage();
                    break;
                case "Status":
                    Dasco.showStats();
                    view.showAction();
                    break;
                default:
                    System.out.println("Unknown command!");
            }
        }

        scanner.close();
    }
}

