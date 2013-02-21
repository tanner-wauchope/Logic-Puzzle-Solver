package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

import org.junit.Test;

/** The suite of all JUnit tests for the Puzzle Solver.
 *  @author Tanner Wauchope
 */
public class SolveTest {

    /** basic test case */
    @Test public void basicSolving() {
        ParseTest.setUp("Joe is not the plumber.",
                "Tom is not the electrician.",
                "Bob is the carpenter.",
                "Joe lives in the yellow house.",
                "What do you know about Joe?",
                "What does Tom do?");
        Parser p = Parser.parse(ParseTest.getReader());
        Solver s = new Solver();
        p.inform(s);

        assertEquals("wrong complexity", 3, s.getComplexity());
        ArrayList<String> testSolverPeople = new ArrayList<String>();
        testSolverPeople.add("joe");
        testSolverPeople.add("tom");
        testSolverPeople.add("bob");
        assertEquals("bad solver people", testSolverPeople,
                s.getPeople());
        ArrayList<String> testSolverJobs = new ArrayList<String>();
        testSolverJobs.add("plumber");
        testSolverJobs.add("electrician");
        testSolverJobs.add("carpenter");
        assertEquals("bad solver jobs", testSolverJobs,
                s.getJobs());
        ArrayList<String> testSolverColors = new ArrayList<String>();
        testSolverColors.add("yellow");
        testSolverColors.add("color#1");
        testSolverColors.add("color#2");
        assertEquals("bad solver colors", testSolverColors, s.getColors());
        assertEquals("wrong first answer",
                "Joe is the electrician and lives in the yellow house.",
                p.getAnswer(s, 0));
        assertEquals("wrong second answer", "Tom is the plumber.",
                p.getAnswer(s, 1));
        String[] assoc1 = {"bob", "carpenter", "personJob"};
        String[] assoc2 = {"joe", "yellow", "personColor"};
        String[] assoc3 = {"electrician", "yellow", "jobColor"};
        String[] assoc4 = {"joe", "electrician", "personJob"};
        String[] assoc5 = {"tom", "plumber", "personJob"};
        String[][] testAssociations = {assoc1, assoc2, assoc3, assoc4, assoc5};
        int i = 0;
        for (String[] assoc : s.getAssociations()) {
            assertEquals("bad associations", Arrays.toString(
                    testAssociations[i]), Arrays.toString(assoc));
            i += 1;
        }
    }

}
