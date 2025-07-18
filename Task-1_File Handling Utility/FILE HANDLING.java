import java.io.*;
import java.util.Scanner;
public class FileUtility {

    static Scanner scanner = new Scanner(System.in);
    static String filePath = "codtech_task1.txt";

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== FILE HANDLING UTILITY ===");
            System.out.println("1. Write to File");
            System.out.println("2. Read from File");
            System.out.println("3. Modify File");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    writeFile();
                    break;
                case 2:
                    readFile();
                    break;
                case 3:
                    modifyFile();
                    break;
                case 4:
                    System.out.println("Exiting the utility. Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Method to write content to the file
    public static void writeFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            System.out.println("Enter content to write into the file:");
            String content = scanner.nextLine();
            writer.write(content);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Method to read content from the file
    public static void readFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            System.out.println("\n--- File Content ---");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("---------------------");
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Please write to the file first.");
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    // Method to modify file content
    public static void modifyFile() {
        try {
            System.out.println("Enter the new content to overwrite the file:");
            String newContent = scanner.nextLine();
            FileWriter fw = new FileWriter(filePath);
            fw.write(newContent);
            fw.close();
            System.out.println("File modified successfully.");
        } catch (IOException e) {
            System.out.println("Error modifying the file: " + e.getMessage());
        }
    }
}
