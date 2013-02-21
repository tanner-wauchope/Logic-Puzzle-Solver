package puzzle;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileNotFoundException;

/** The Puzzle Solver.
 * @author Tanner Wauchope
 */
public class Solve {

    /** Solve the puzzle given in ARGS[0], if given.  Otherwise, print
     *  a help message. */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        if (args.length > 1) {
            System.err.println("Error: too many arguments");
            usage();
            System.exit(1);
        }

        File inputFileName = new File(args[0]);
        Reader input;

        try {
            input = new FileReader(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Error: file %s not found", inputFileName);
            System.exit(1);
            return;
        }

        try {
            Parser puzzle = Parser.parse(input);
            Solver solution = new Solver();
            solution = puzzle.inform(solution);
            for (int i = 0; i < puzzle.numAssertions(); i += 1) {
                System.out.println((i + 1) + ". " + puzzle.getAssertion(i));
            }
            System.out.println();
            if (solution.impossible()) {
                System.out.println("That's impossible.");
            } else {
                for (int i = 0; i < puzzle.numQuestions(); i += 1) {
                    System.out.println("Q: " + puzzle.getQuestion(i));
                    System.out.println("A: " + puzzle.getAnswer(solution, i));
                }
            }
        } catch (PuzzleException e) {
            System.err.printf("Error: " + e.getMessage() + "\n");
            System.exit(1);
        }
    }

    /** Print usage message. */
    private static void usage() {
        System.out.println(
                "This program can solve logic puzzles.\n"
                + "Try it out on a puzzle formatted like any of these:\n\n"
                + "Sue lives around here. There is a brown house.\n"
                + "The professor lives around here.\n"
                + "What do you know about Sue?\n\n"
                + "or\n\n"
                + "John is not the carpenter.\n"
                + "The plumber lives in the blue house.\n"
                + "John lives in the yellow house.\n"
                + "Mary does not live in the blue house.\n"
                + "Tom lives around here.\n"
                + "The architect lives around here.\n"
                + "What do you know about John? What do you know about Mary?\n"
                + "What do you know about Tom?\n\n"
                + "or\n\n"
                + "Jack lives in the blue house.\n"
                + "Mary does not live in the blue house.\n"
                + "The mechanic lives around here.\n"
                + "There is a red house. The architect lives around here.\n"
                + "The sailor lives around here.\n"
                + "Who is the mechanic? What do you know about Jack?\n"
                + "What do you know about Mary?");
    }
}

