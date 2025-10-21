/****************************************************
 * @file: Parser.java
 * @description: Loads all movies from movie_data.csv into a BST, then processes input commands (search/remove/print/insert) and writes results to output.txt. Quote-aware CSV parsing handles commas inside quoted fields (like description).
 * @author: Tim Hultman
 * @date: 9/24/25
 ****************************************************/
import java.io.*;
import java.util.Scanner;

public class Parser {
    //BST movie construction
    private BST<Movie> BST = new BST<>();


    // Constructor, clears output.txt, loads dataset, processes commands
    public Parser(String filename) throws FileNotFoundException {
        PrintWriter write = new PrintWriter("./output.txt");
        write.print("");
        write.close();

        loadCSV("movie_data.csv");
        process(new File(filename));
    }


    // Loads all movies from CSV into BST, skipping header row
    public void loadCSV(String CSV) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(CSV));
        scan.nextLine(); // skip header
        while (scan.hasNextLine()) {
            BST.insert(convert(splitCSV(scan.nextLine(), 8)));
        }
    }


    // Process input file commands
    public void process(File inputFile) throws FileNotFoundException {
        Scanner scan = new Scanner(inputFile);
        while (scan.hasNextLine()) {
            operate_BST(scan.nextLine().split(" +", 2));
        }
    }


    /**
     * Operates BST via cases
     * Takes in String[] command, 1D array of commands
     */
    public void operate_BST(String[] command) {
        switch (command[0]) {
            case "search":
                Movie dummy = new Movie(command[1], 0, "", "", 0, "", "", "");
                Node<Movie> found = BST.search(dummy);
                if (found != null) {
                    Movie mv = found.getValue();
                    writeToFile("found " + mv.getName()+"\n....Year: " + mv.getYear()
                            +"\n....Length: " + mv.getDuration()
                            +"\n....Genre: " + mv.getGenre()
                            +"\n....Score: " + mv.getRating()
                            +"\n....Description: " + mv.getDescription()
                            +"\n....Director: " + mv.getDirector()
                            +"\n....Actors: " + mv.getStars(), "./output.txt");
                }
                else {
                    writeToFile("search failed", "./output.txt");
                }
                break;

            case "remove":
                Movie film = new Movie(command[1], 0, "", "", 0, "", "", "");
                Node<Movie> removed = BST.remove(film);
                writeToFile(removed != null ? "removed " + command[1] : "remove failed", "./output.txt");
                break;

            case "insert":
                Movie mov = convert(splitCSV(command[1], 8));
                if (BST.search(mov)== null) {
                    BST.insert(mov);
                    writeToFile("inserted " + mov.getName(), "./output.txt");
                }
                else {
                    writeToFile("duplicate " + mov.getName(), "./output.txt");
                }
                break;
            case "print":
                StringBuilder build = new StringBuilder();
                build.append("Printing all movies...\n");
                int count = 1;
                for (Movie m : BST) {
                    build.append(count++).append(") ").append(m.toString()).append("\n");
                }
                writeToFile(build.toString().trim(), "./output.txt");
                break;
            default:
                writeToFile("Invalid command", "./output.txt");
                break;
        }

    }



    // Converts array of fields into a Movie
    public Movie convert(String[] f) {
        for (int i = 0; i < f.length; i++) f[i] = f[i].replace("\"", "");
        return new Movie(f[0], Integer.parseInt(f[1]), f[2], f[3],
                Double.parseDouble(f[4]), f[5], f[6], f[7]);
    }

    /**
     * Splits CSV with every 8 commas being a new movie, quote aware
     * Takes in String ln and int expected, the line scan and expected int value
     * Returns String[]
     */
    public String[] splitCSV(String ln, int expected) {
        String[] out = new String[expected];
        StringBuilder build = new StringBuilder();
        boolean inQ = false;
        int index = 0;

        for (char c : ln.toCharArray()) {
            if (c == '"') {
                inQ = !inQ;
            } else if (c == ',' && !inQ) {
                out[index++] = build.toString();
                build.setLength(0);
            } else {
                build.append(c);
            }
        }
        out[index] = build.toString();
        return out;
    }


    // Writes one line to output.txt until done
    public void writeToFile(String content, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(content + "\n");
        }
        catch (IOException e) {}
    }
}