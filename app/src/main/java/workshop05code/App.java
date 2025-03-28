package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            Logger.getLogger(SQLiteConnectionManager.class.getName()).log(Level.SEVERE, "Failed to load logging configuration.", e1);
            System.out.println("Failed launch Wordle.");
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");

        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "Wordle database connection established successfully.");
        } else {
            logger.log(Level.SEVERE,"ERROR: Unable to establish database connection.");
            System.out.println("Failed launch Wordle.");
            return;
        }
        
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO,"Database tables created successfully.");
        } else {
            logger.log(Level.SEVERE,"ERROR: Failed to create database tables.");
            System.out.println("Failed launch Wordle.");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file
        
        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.INFO, "Loaded word: {0}", line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR: Could not load words from data.txt.", e);
            System.out.println("Failed launch Wordle.");
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to Wordle");
            System.out.print("Enter a 4 letter word for a guess or 'q' to quit: ");

            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");

                if (wordleDatabaseConnection.isValidWord(guess)) { 
                    System.out.println("Success! It is in the the list.\n");
                }else{
                    logger.log(Level.WARNING, "Invalid guess! '" + guess+"'.");
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }

                System.out.print("Enter a 4 letter word for a guess or 'q' to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Unexpected input error.", e);
            System.out.println("Unexpected error occurred. Exiting Wordle.");
        }
    }
}