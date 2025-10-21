/****************************************************
 * @file: Proj2.java
 * @description:
 *   Dual-mode driver:
 *   (A) Spec Timing Mode  -> java Proj2 {dataset-file} {N}
 *       - Reads up to N lines into ArrayList
 *       - Builds BST/AVL on SORTED and RANDOM orders (4 trees total)
 *       - Times INSERT and SEARCH for each tree
 *       - Prints human-readable summary to console
 *       - Appends CSV to output.txt
 *   (B) Command Mode      -> java Proj2 {command-file}
 *       - Uses your existing command runner (search/insert/remove/print)
 *
 *  @acknowledgment:
 *   Portions of this code and documentation were developed
 *   with assistance from ChatGPT by OpenAI.
 * @author: Tim Hultman
 * @date: 10/19/25
 ****************************************************/

import java.io.*;
import java.util.*;

public class Proj2 {
    /**
     * Entry point of the program.
     * Supports two modes:
     * <ul>
     *     <li><b>Timing mode:</b> {@code java Proj2 dataset.csv N}</li>
     *     <li><b>Command mode:</b> {@code java Proj2 commands.txt}</li>
     * </ul>
     *
     * @param args command-line arguments
     * @throws FileNotFoundException if any input file is missing
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 2) {
            // ===== Spec Timing Mode =====
            String dataset = args[0];
            int N = Integer.parseInt(args[1]);
            runTimingMode(dataset, N);
        } else if (args.length == 1) {
            // ===== Command Mode (your prior harness) =====
            String commandFile = args[0];
            BST<Movie> bst = new BST<>();
            AvlTree<Movie> avl = new AvlTree<>();
            loadCSV("movie_data.csv", bst, avl); // uses your existing CSV loader name
            processCommands(commandFile, bst, avl);
            System.out.println("All operations complete. See output.txt for results.");
        } else {
            System.err.println("Usage:");
            System.err.println("  Timing mode : java Proj2 {dataset-file} {N}");
            System.err.println("  Command mode: java Proj2 {command-file}");
        }
    }

    // ======= TIMING MODE (Spec) =======
    /**
     * Executes the timing mode benchmark for AVL and BST trees.
     * Measures insert and search performance on sorted and randomized datasets.
     *
     * @param dataset the dataset file name (CSV)
     * @param N       the number of rows to read from the dataset
     * @throws FileNotFoundException if the dataset file is not found
     */
    private static void runTimingMode(String dataset, int N) throws FileNotFoundException {
        // 1) Read up to N rows into ArrayList
        ArrayList<Movie> arr = readMovies(dataset, N);

        // 2) Build sorted & randomized input sequences
        ArrayList<Movie> sorted = new ArrayList<>(arr);
        Collections.sort(sorted);
        ArrayList<Movie> random = new ArrayList<>(arr);
        Collections.shuffle(random, new Random(42)); // deterministic shuffle

        // 3) Time INSERTS for each case
        Result insBST_sorted = timeInsertBST(sorted);
        Result insAVL_sorted = timeInsertAVL(sorted);
        Result insBST_rand   = timeInsertBST(random);
        Result insAVL_rand   = timeInsertAVL(random);

        // 4) Build trees again (fresh) for SEARCH timing (to keep apples-to-apples)
        BST<Movie> bst_sorted = buildBST(sorted);
        AvlTree<Movie> avl_sorted = buildAVL(sorted);
        BST<Movie> bst_rand = buildBST(random);
        AvlTree<Movie> avl_rand = buildAVL(random);

        Result srchBST_sorted = timeSearchBST(bst_sorted, arr);
        Result srchAVL_sorted = timeSearchAVL(avl_sorted, arr);
        Result srchBST_rand   = timeSearchBST(bst_rand, arr);
        Result srchAVL_rand   = timeSearchAVL(avl_rand, arr);

        // 5) Print human-readable summary
        System.out.println("==== Project 2 Timing Summary ====");
        System.out.println("Dataset: " + dataset + " | N=" + arr.size());
        System.out.println();
        System.out.println("INSERT Totals (seconds) and Rate (sec/node):");
        prettyLine("BST  (sorted)", insBST_sorted, arr.size());
        prettyLine("AVL  (sorted)", insAVL_sorted, arr.size());
        prettyLine("BST  (random)", insBST_rand,   arr.size());
        prettyLine("AVL  (random)", insAVL_rand,   arr.size());
        System.out.println();
        System.out.println("SEARCH Totals (seconds) and Rate (sec/node):");
        prettyLine("BST  (sorted)", srchBST_sorted, arr.size());
        prettyLine("AVL  (sorted)", srchAVL_sorted, arr.size());
        prettyLine("BST  (random)", srchBST_rand,   arr.size());
        prettyLine("AVL  (random)", srchAVL_rand,   arr.size());

        // 6) Append CSV to output.txt (one row per run)
        // Columns: date,dataset,N,op,order,tree,total_sec,rate_sec_per_node
        try (FileWriter fw = new FileWriter("output.txt", true)) {
            String date = new Date().toString();
            appendCSV(fw, date, dataset, arr.size(), "insert","sorted","BST", insBST_sorted);
            appendCSV(fw, date, dataset, arr.size(), "insert","sorted","AVL", insAVL_sorted);
            appendCSV(fw, date, dataset, arr.size(), "insert","random","BST", insBST_rand);
            appendCSV(fw, date, dataset, arr.size(), "insert","random","AVL", insAVL_rand);

            appendCSV(fw, date, dataset, arr.size(), "search","sorted","BST", srchBST_sorted);
            appendCSV(fw, date, dataset, arr.size(), "search","sorted","AVL", srchAVL_sorted);
            appendCSV(fw, date, dataset, arr.size(), "search","random","BST", srchBST_rand);
            appendCSV(fw, date, dataset, arr.size(), "search","random","AVL", srchAVL_rand);
        } catch (IOException ignored) {}
    }

    /**
     * Prints a formatted summary line showing total time and per-node rate.
     *
     * @param label the label to print
     * @param r     the result containing timing in nanoseconds
     * @param n     the number of nodes processed
     */
    private static void prettyLine(String label, Result r, int n) {
        double totalSec = r.ns / 1_000_000_000.0;
        double rate = (n == 0) ? 0.0 : totalSec / n;
        System.out.printf("  %-14s  total=%.6f s   rate=%.9f s/node%n", label, totalSec, rate);
    }

    /**
     * Appends one benchmark result row to the output CSV file.
     *
     * @param fw       the FileWriter to write to
     * @param date     the date of the run
     * @param dataset  the dataset name
     * @param N        number of records processed
     * @param op       operation type ("insert" or "search")
     * @param order    data order ("sorted" or "random")
     * @param tree     tree type ("BST" or "AVL")
     * @param r        the result object containing elapsed nanoseconds
     * @throws IOException if writing to the file fails
     */
    private static void appendCSV(FileWriter fw, String date, String dataset, int N,
                                  String op, String order, String tree, Result r) throws IOException {
        double totalSec = r.ns / 1_000_000_000.0;
        double rate = (N == 0) ? 0.0 : totalSec / N;
        fw.write(String.join(",",
                date, dataset, String.valueOf(N), op, order, tree,
                String.format(Locale.US, "%.9f", totalSec),
                String.format(Locale.US, "%.12f", rate)) + "\n");
    }

    /**
     * Simple holder class for timing results in nanoseconds.
     */
    private static class Result { long ns; Result(long ns){ this.ns = ns; } }

    /**
     * Reads up to {@code N} movies from the given CSV file.
     *
     * @param filename the CSV file to read
     * @param N        maximum number of movies to read
     * @return a list of Movie objects
     * @throws FileNotFoundException if the file is not found
     */
    private static ArrayList<Movie> readMovies(String filename, int N) throws FileNotFoundException {
        ArrayList<Movie> list = new ArrayList<>();
        Scanner sc = new Scanner(new File(filename));
        if (sc.hasNextLine()) sc.nextLine(); // skip header
        int count = 0;
        while (sc.hasNextLine() && count < N) {
            String line = sc.nextLine();
            Movie m = convert(splitCSV(line, 8));
            list.add(m);
            count++;
        }
        sc.close();
        return list;
    }

    /**
     * Times insertion of all movies into a Binary Search Tree (BST).
     *
     * @param src the list of movies to insert
     * @return a Result object containing elapsed nanoseconds
     */
    private static Result timeInsertBST(List<Movie> src){
        BST<Movie> bst = new BST<>();
        long t0 = System.nanoTime();
        for (Movie m : src) bst.insert(m);
        long t1 = System.nanoTime();
        return new Result(t1 - t0);
    }

    /**
     * Times insertion of all movies into an AVL Tree.
     *
     * @param src the list of movies to insert
     * @return a Result object containing elapsed nanoseconds
     */
    private static Result timeInsertAVL(List<Movie> src){
        AvlTree<Movie> avl = new AvlTree<>();
        long t0 = System.nanoTime();
        for (Movie m : src) avl.insert(m);
        long t1 = System.nanoTime();
        return new Result(t1 - t0);
    }

    /**
     * Builds and returns a Binary Search Tree (BST) from the given list of movies.
     *
     * @param src the list of movies
     * @return a BST containing all movies
     */
    private static BST<Movie> buildBST(List<Movie> src){
        BST<Movie> bst = new BST<>();
        for (Movie m : src) bst.insert(m);
        return bst;
    }

    /**
     * Builds and returns an AVL Tree from the given list of movies.
     *
     * @param src the list of movies
     * @return an AVL Tree containing all movies
     */
    private static AvlTree<Movie> buildAVL(List<Movie> src){
        AvlTree<Movie> avl = new AvlTree<>();
        for (Movie m : src) avl.insert(m);
        return avl;
    }

    /**
     * Times searching for all given queries in the BST.
     *
     * @param bst     the Binary Search Tree to search
     * @param queries the list of Movie objects to look for
     * @return a Result object containing elapsed nanoseconds
     */
    private static Result timeSearchBST(BST<Movie> bst, List<Movie> queries){
        long t0 = System.nanoTime();
        for (Movie q : queries) bst.search(q);
        long t1 = System.nanoTime();
        return new Result(t1 - t0);
    }

    /**
     * Times searching for all given queries in the AVL Tree.
     *
     * @param avl     the AVL Tree to search
     * @param queries the list of Movie objects to look for
     * @return a Result object containing elapsed nanoseconds
     */
    private static Result timeSearchAVL(AvlTree<Movie> avl, List<Movie> queries){
        long t0 = System.nanoTime();
        for (Movie q : queries) avl.contains(q);
        long t1 = System.nanoTime();
        return new Result(t1 - t0);
    }

    // ======= COMMAND MODE (your original) =======

    /**
     * Loads movie data from a CSV file into both a BST and an AVL tree.
     *
     * @param filename the CSV file name
     * @param bst      the BST to populate
     * @param avl      the AVL tree to populate
     * @throws FileNotFoundException if the CSV file is missing
     */
    public static void loadCSV(String filename, BST<Movie> bst, AvlTree<Movie> avl) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(filename));
        if (scan.hasNextLine()) scan.nextLine(); // skip header
        while (scan.hasNextLine()) {
            Movie m = convert(splitCSV(scan.nextLine(), 8));
            bst.insert(m);
            avl.insert(m);
        }
        scan.close();
    }

    /**
     * Processes commands from an input file (insert, remove, search, print)
     * and writes results to {@code output.txt}.
     *
     * @param filename the command file name
     * @param bst      the BST to operate on
     * @param avl      the AVL tree to operate on
     * @throws FileNotFoundException if the command file is missing
     */
    public static void processCommands(String filename, BST<Movie> bst, AvlTree<Movie> avl) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(filename));
        FileWriter writer = null;
        try {
            writer = new FileWriter("output.txt", true); // APPEND per spec
            writer.write("==== Project 2 AVL vs BST Comparison ====\n");

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] cmd = line.split(" +", 2);
                String op = cmd[0];
                writer.write("\n> " + line + "\n");

                switch (op) {
                    case "search" -> handleSearch(cmd[1], bst, avl, writer);
                    case "insert" -> handleInsert(cmd[1], bst, avl, writer);
                    case "remove" -> handleRemove(cmd[1], bst, avl, writer);
                    case "print"  -> handlePrint(bst, avl, writer);
                    default       -> writer.write("Invalid command\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) { try { writer.close(); } catch (IOException ignored) {} }
            scan.close();
        }
    }

    /**
     * Handles a search command, comparing BST and AVL performance.
     *
     * @param query  the movie title to search for
     * @param bst    the BST to search
     * @param avl    the AVL tree to search
     * @param writer the FileWriter for output
     * @throws IOException if writing results fails
     */
    public static void handleSearch(String query, BST<Movie> bst, AvlTree<Movie> avl, FileWriter writer) throws IOException {
        Movie dummy = new Movie(query, 0, "", "", 0, "", "", "");
        long t0 = System.nanoTime();
        boolean bstFound = bst.search(dummy) != null;
        long t1 = System.nanoTime();
        long t2 = System.nanoTime();
        boolean avlFound = avl.contains(dummy);
        long t3 = System.nanoTime();
        writer.write((bstFound ? "BST found " : "BST search failed ") + query + " (" + (t1 - t0) + " ns)\n");
        writer.write((avlFound ? "AVL found " : "AVL search failed ") + query + " (" + (t3 - t2) + " ns)\n");
    }

    /**
     * Handles an insert command, adding a new movie to both trees and timing the operation.
     *
     * @param csv    the CSV line representing the movie
     * @param bst    the BST to insert into
     * @param avl    the AVL tree to insert into
     * @param writer the FileWriter for output
     * @throws IOException if writing results fails
     */
    public static void handleInsert(String csv, BST<Movie> bst, AvlTree<Movie> avl, FileWriter writer) throws IOException {
        Movie m = convert(splitCSV(csv, 8));
        long t0 = System.nanoTime(); bst.insert(m); long t1 = System.nanoTime();
        long t2 = System.nanoTime(); avl.insert(m); long t3 = System.nanoTime();
        writer.write("Inserted " + m.getName() + ":\n");
        writer.write("BST time: " + (t1 - t0) + " ns\n");
        writer.write("AVL time: " + (t3 - t2) + " ns\n");
    }

    /**
     * Handles a remove command, deleting a movie from both trees and timing the operation.
     *
     * @param name   the title of the movie to remove
     * @param bst    the BST to remove from
     * @param avl    the AVL tree to remove from
     * @param writer the FileWriter for output
     * @throws IOException if writing results fails
     */
    public static void handleRemove(String name, BST<Movie> bst, AvlTree<Movie> avl, FileWriter writer) throws IOException {
        Movie dummy = new Movie(name, 0, "", "", 0, "", "", "");
        long t0 = System.nanoTime(); bst.remove(dummy); long t1 = System.nanoTime();
        long t2 = System.nanoTime(); avl.remove(dummy); long t3 = System.nanoTime();
        writer.write("Removed " + name + ":\n");
        writer.write("BST time: " + (t1 - t0) + " ns\n");
        writer.write("AVL time: " + (t3 - t2) + " ns\n");
    }

    /**
     * Handles a print command by listing BST contents in order and printing
     * the AVL tree structure to the console.
     *
     * @param bst    the BST to print
     * @param avl    the AVL tree to print
     * @param writer the FileWriter for output
     * @throws IOException if writing results fails
     */
    public static void handlePrint(BST<Movie> bst, AvlTree<Movie> avl, FileWriter writer) throws IOException {
        writer.write("BST contents:\n");
        int count = 1;
        for (Movie m : bst) writer.write(count++ + ") " + m + "\n");
        writer.write("\nAVL contents:\n(Tree printed to console.)\n");
        avl.printTree(); // console
    }

    // === Utility CSV Helpers (shared) === //

    /**
     * Converts a CSV line (already split) into a Movie object.
     *
     * @param f the string array of CSV fields
     * @return a new Movie object
     */
    public static Movie convert(String[] f) {
        for (int i = 0; i < f.length; i++) f[i] = f[i].replace("\"", "");
        return new Movie(f[0], Integer.parseInt(f[1]), f[2], f[3],
                Double.parseDouble(f[4]), f[5], f[6], f[7]);
    }


    /**
     * Splits a CSV line into fields, handling quoted commas properly.
     *
     * @param ln       the CSV line
     * @param expected the expected number of columns
     * @return an array of strings representing the fields
     */
    public static String[] splitCSV(String ln, int expected) {
        String[] out = new String[expected];
        StringBuilder build = new StringBuilder();
        boolean inQ = false;
        int index = 0;
        for (char c : ln.toCharArray()) {
            if (c == '"') inQ = !inQ;
            else if (c == ',' && !inQ) { out[index++] = build.toString(); build.setLength(0); }
            else build.append(c);
        }
        out[index] = build.toString();
        return out;
    }
}
