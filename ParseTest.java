package puzzle;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.StringReader;
import java.util.LinkedHashSet;

/** Tests of Parser class.
 *  @author Tanner Wauchope */
public class ParseTest {

    private static StringReader reader;

    /**
     * @return the reader
     */
    public static StringReader getReader() {
        return reader;
    }

    /** Set reader to contain the contents of LINES. */
    public static void setUp(String... lines) {
        StringBuilder str = new StringBuilder();

        for (String line : lines) {
            str.append(line);
            str.append("\n");
        }
        reader = new StringReader(str.toString());
    }

    /** Test help message ('java solve' with no arguments). */
    @Test
    public void basicParsing() {
        setUp("John is   the carpenter.   "
              + "Paul does not live in the yellow house.   ",
              "  Who is   the\tcarpenter?");

        Parser p = Parser.parse(reader);

        assertEquals("bad assertion count", 2, p.numAssertions());
        assertEquals("bad question count", 1, p.numQuestions());
        assertEquals("Fact #0", "John is the carpenter.",  p.getAssertion(0));
        assertEquals("Fact #1", "Paul does not live in the yellow house.",
                     p.getAssertion(1));
        assertEquals("Q #0", "Who is the carpenter?", p.getQuestion(0));
        LinkedHashSet<String> testPeople = new LinkedHashSet<String>();
        testPeople.add("john");
        testPeople.add("paul");
        assertEquals("bad people set", testPeople, p.getPeople());
        LinkedHashSet<String> testJobs = new LinkedHashSet<String>();
        testJobs.add("carpenter");
        assertEquals("bad jobs set", testJobs, p.getJobs());
        LinkedHashSet<String> testColors = new LinkedHashSet<String>();
        testColors.add("yellow");
        assertEquals("bad colors set", testColors, p.getColors());
    }
}
