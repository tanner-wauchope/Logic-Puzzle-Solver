package puzzle;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;

/** A sequence of Assertions and Questions parsed from a given file.
 *  @author Tanner Wauchope */
class Parser {

    /**  A set of the people that were named in the assertions. */
    private LinkedHashSet<String> people;

    /** A set of the jobs that were named in the assertions. */
    private LinkedHashSet<String> jobs;

    /** A set of the colors that were named in the assertions. */
    private LinkedHashSet<String> colors;

    /**
     *  Each sublist of QUESTIONS holds useful info about a single question.
     *  The first item of a sublist is the verbatim text of a question.
     *  The second item of a sublist is the question type.
     *  The third item of a sublist is the first entity named by the question.
     *  The fourth item in a sublist, if it exists, is the second entity
     *  named by a question.
     */
    private ArrayList<ArrayList<String>> questions;

    /**
     *  Each sublist of ASSERTIONS holds useful info about a single assertion.
     *  The first item of a sublist is the verbatim text of an assertion.
     *  The second item of a sublist is the assertion type.
     *  The third item of a sublist is the first entity named by the assertion.
     *  The fourth item in a sublist, if it exists, is the second entity
     *  named by the assertion.
     */
    private ArrayList<ArrayList<String>> assertions;

    /** A new Parser, containing no assertions or questions. */
    private Parser() {
        this.assertions = new ArrayList<ArrayList<String>>();
        this.questions = new ArrayList<ArrayList<String>>();
        this.people = new LinkedHashSet<String>();
        this.jobs = new LinkedHashSet<String>();
        this.colors = new LinkedHashSet<String>();
    }

    /**
     * @return the colors
     */
    public LinkedHashSet<String> getColors() {
        return this.colors;
    }

    /**
     * @return the jobs
     */
    public LinkedHashSet<String> getJobs() {
        return this.jobs;
    }

    /**
     * @return the people
     */
    public LinkedHashSet<String> getPeople() {
        return this.people;
    }

    /** Returns a Parser that contains assertions and questions from
     *  READER. */
    static Parser parse(Reader reader) {
        Scanner inp = new Scanner(reader);
        Parser result = new Parser();
        ArrayList<String> sentences = new ArrayList<String>();
        while (inp.hasNextLine()) {
            String line = inp.nextLine();
            if (line.trim().length() == 0) {
                continue;
            }
            String[] words = line.trim().split("\\s+");
            String sentence = "";
            char lastChar = '0';
            for (int i = 0; i < words.length; i += 1) {
                lastChar = words[i].charAt(words[i].length() - 1);
                if (lastChar == '.' || lastChar == '?') {
                    if (words[i].length() > 1) {
                        sentence += words[i];
                        sentences.add(sentence);
                        sentence = "";
                    } else {
                        sentence = sentence.trim();
                        sentence += words[i];
                        sentences.add(sentence);
                    }
                } else {
                    sentence += words[i];
                    sentence += " ";
                }
            }
            if (!(lastChar == '.' || lastChar == '?')) {
                inp.close();
                throw new PuzzleException(
                        "a line contained an incomplete sentence");
            }
        }
        inp.close();
        distributeSentences(result, sentences);
        checkGrammar(result);
        result.assertions = loadEntities(result.assertions);
        result.questions = loadEntities(result.questions);
        result.people = people(result.assertions);
        result.jobs = jobs(result.assertions);
        result.colors = colors(result.assertions);
        checkDiction(result);
        return result;
    }

    /**
     * @param puzzle   a parser that needs assertions and questions
     * @param sentences   a list of assertions and questions
     */
    private static void distributeSentences(Parser puzzle,
            ArrayList<String> sentences) {
        char prevEndMark = '0';
        char currEndMark = '0';
        for (int i = 0; i < sentences.size(); i += 1) {
            currEndMark = sentences.get(i).charAt(
                    sentences.get(i).length() - 1);
            if (currEndMark == '.') {
                ArrayList<String> assertion = new ArrayList<String>();
                assertion.add(sentences.get(i));
                puzzle.assertions.add(assertion);
            } else if (currEndMark == '?') {
                ArrayList<String> question = new ArrayList<String>();
                question.add(sentences.get(i));
                puzzle.questions.add(question);
            }
            if (prevEndMark == '?' && currEndMark == '.') {
                throw new PuzzleException(
                        "assertions must precede questions");
            }
            prevEndMark = currEndMark;
        }
    }

    /**
     * @param puzzle   a parser containing potentially unformatted input
     */
    private static void checkGrammar(Parser puzzle) {
        for (ArrayList<String> assertion : puzzle.assertions) {
            if (!properAssertionGrammar(assertion)) {
                throw new PuzzleException(
                        "an assertion is unformatted");
            }
        }
        for (ArrayList<String> question : puzzle.questions) {
            if (!properQuestionGrammar(question)) {
                throw new PuzzleException(
                        "a question is unformatted");
            }
        }
    }

    /**
     * @param assertion   a potentially unformatted assertion
     * @return result   a boolean specifying whether the assertion is formatted
     */
    static boolean properAssertionGrammar(ArrayList<String> assertion) {
        boolean result = false;
        String person = "[A-Z][a-z]+";
        String job = "[a-z]+";
        String color = "[a-z]+";
        ArrayList<String> forms = new ArrayList<String>();
        forms.add(person + " lives in the " + color + " house\\.");
        forms.add("The " + job + " lives in the " + color + " house\\.");
        forms.add(person + " does not live in the " + color + " house\\.");
        forms.add(
                "The " + job + " does not live in the " + color + " house\\.");
        forms.add(person + " is the " + job + "\\.");
        forms.add(person + " is not the " + job + "\\.");
        forms.add(person + " lives around here\\.");
        forms.add("The " + job + " lives around here\\.");
        forms.add("There is a " + color + " house\\.");
        for (int i = 0; i < forms.size(); i += 1) {
            if (assertion.get(0).matches(forms.get(i))) {
                result = true;
                assertion.add(ASSERTION_TYPES.get(i));
            }
        }
        return result;
    }

    /**
     * @param question   a potentially unformatted question
     * @return result   a boolean specifying whether the question is formatted
     */
    static boolean properQuestionGrammar(ArrayList<String> question) {
        boolean result = false;
        String person = "[A-Z][a-z]+";
        String job = "[a-z]+";
        String color = "[a-z]+";
        ArrayList<String> forms = new ArrayList<String>();
        forms.add("What do you know about " + person + "\\?");
        forms.add("What do you know about the " + job + "\\?");
        forms.add("What do you know about the " + color + " house\\?");
        forms.add("Who is the " + job + "\\?");
        forms.add("Who lives in the " + color + " house\\?");
        forms.add("What does " + person + " do\\?");
        forms.add("What does the occupant of the " + color + " house do\\?");
        forms.add("Where does " + person + " live\\?");
        forms.add("Where does the " + job + " live\\?");
        for (int i = 0; i < forms.size(); i += 1) {
            if (question.get(0).matches(forms.get(i))) {
                result = true;
                question.add(QUESTION_TYPES.get(i));
            }
        }
        return result;
    }

    /**
     * @param puzzle   a parser containing potentially unformatted input
     */
    private static void checkDiction(Parser puzzle) {
        LinkedHashSet<String> queriedPeople = people(puzzle.questions);
        LinkedHashSet<String> queriedJobs = jobs(puzzle.questions);
        LinkedHashSet<String> queriedColors = colors(puzzle.questions);
        if (!puzzle.people.containsAll(queriedPeople)
                || !puzzle.jobs.containsAll(queriedJobs)
                || !puzzle.colors.containsAll(queriedColors)) {
            throw new PuzzleException("you asked about something random");
        }
        enforceDistinctness(KEYSET, puzzle.people);
        enforceDistinctness(KEYSET, puzzle.jobs);
        enforceDistinctness(KEYSET, puzzle.colors);
        enforceDistinctness(capitalizeAll(puzzle.people), puzzle.jobs);
        enforceDistinctness(capitalizeAll(puzzle.people), puzzle.colors);
        enforceDistinctness(puzzle.jobs, puzzle.colors);
    }

    /**
     * Throws an error if SET1 and SET2 have intersecting elements.
     */
    private static void enforceDistinctness(
            LinkedHashSet<String> set1, LinkedHashSet<String> set2) {
        LinkedHashSet<String> clone = new LinkedHashSet<String>(set1);
        clone.retainAll(set2);
        if (!clone.isEmpty()) {
            throw new PuzzleException("keywords, people, occupations, and "
                    + "colors must have unique spellings");
        }
    }

    /**
     * @param sentences  either the assertions or questions of a puzzle
     * @return sentences  the argument, but with the entity names appended
     *                    to the the sentences wrapper
     */
    private static ArrayList<ArrayList<String>> loadEntities(
            ArrayList<ArrayList<String>> sentences) {
        for (int i = 0; i < sentences.size(); i += 1) {
            String[] line = sentences.get(i).get(0).split(" ");
            for (int j = 0; j < line.length; j += 1) {
                String word = line[j].toLowerCase();
                if (j == line.length - 1) {
                    word = line[j].replace(".", "").replace("?", "");
                }
                if (!KEYSET.contains(word)) {
                    sentences.get(i).add(word);
                }
            }
        }
        return sentences;
    }

    /**
     * @param sentences  either the assertions or the questions of a puzzle
     * @return result  a LinkedHashSet of the people found in sentences
     */
    private static LinkedHashSet<String> people(
            ArrayList<ArrayList<String>> sentences) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (int i = 0; i < sentences.size(); i += 1) {
            String type = sentences.get(i).get(1);
            if (type.equals(ASSERTION_TYPES.get(0))
                    || type.equals(ASSERTION_TYPES.get(2))
                    || type.equals(ASSERTION_TYPES.get(4))
                    || type.equals(ASSERTION_TYPES.get(5))
                    || type.equals(ASSERTION_TYPES.get(6))
                    || type.equals(QUESTION_TYPES.get(0))
                    || type.equals(QUESTION_TYPES.get(5))
                    || type.equals(QUESTION_TYPES.get(7))) {
                result.add(sentences.get(i).get(2).toLowerCase());
            }
        }
        return result;
    }

    /**
     * @param sentences  either the assertions or the questions of a puzzle
     * @return result  a LinkedHashSet of the jobs found in sentences
     */
    private static LinkedHashSet<String> jobs(
            ArrayList<ArrayList<String>> sentences) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (int i = 0; i < sentences.size(); i += 1) {
            String type = sentences.get(i).get(1);
            if (type.equals(ASSERTION_TYPES.get(1))
                    || type.equals(ASSERTION_TYPES.get(3))
                    || type.equals(ASSERTION_TYPES.get(7))
                    || type.equals(QUESTION_TYPES.get(1))
                    || type.equals(QUESTION_TYPES.get(3))
                    || type.equals(QUESTION_TYPES.get(8))) {
                result.add(sentences.get(i).get(2));
            } else if (type.equals(ASSERTION_TYPES.get(4))
                    || type.equals(ASSERTION_TYPES.get(5))) {
                result.add(sentences.get(i).get(3));
            }
        }
        return result;
    }

    /**
     * @param sentences  either the assertions or the questions of a puzzle
     * @return result  a LinkedHashSet of the colors found in sentences
     */
    private static LinkedHashSet<String> colors(
            ArrayList<ArrayList<String>> sentences) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (int i = 0; i < sentences.size(); i += 1) {
            String type = sentences.get(i).get(1);
            if (type.equals(ASSERTION_TYPES.get(8))
                    || type.equals(QUESTION_TYPES.get(2))
                    || type.equals(QUESTION_TYPES.get(4))
                    || type.equals(QUESTION_TYPES.get(6))) {
                result.add(sentences.get(i).get(2));
            } else if (type.equals(ASSERTION_TYPES.get(0))
                    || type.equals(ASSERTION_TYPES.get(1))
                    || type.equals(ASSERTION_TYPES.get(2))
                    || type.equals(ASSERTION_TYPES.get(3))) {
                result.add(sentences.get(i).get(3));
            }
        }
        return result;
    }

    /**
     * @param solver a solver that needs to be populated with the information
     *        in this parser
     * @return  the solver after at has been given the information
     */
    Solver inform(Solver solver) {
        solver.setParameters(this.people, this.jobs, this.colors);
        for (int i = 0; i < this.assertions.size(); i += 1) {
            if (this.assertions.get(i).size() == 4) {
                inform(solver, i);
            }
        }
        solver.makeInferences();
        if (solver.getAssociations().size() < solver.getComplexity() * 3
                && !solver.impossible()) {
            Solver possibleAnswer = solver.exploreBranches();
            solver = possibleAnswer != null ? possibleAnswer : solver;
        }
        return solver;
    }

    /** Inform SOLVER of the information in assertion K. */
    void inform(Solver solver, int k) {
        String e0 = this.assertions.get(k).get(2).toLowerCase();
        String e1 = this.assertions.get(k).get(3).toLowerCase();
        String type = this.assertions.get(k).get(1);
        if (type.equals(ASSERTION_TYPES.get(0))) {
            solver.personColorAssociate(e0, e1);
        } else if (type.equals(ASSERTION_TYPES.get(1))) {
            solver.jobColorAssociate(e0, e1);
        } else if (type.equals(ASSERTION_TYPES.get(2))) {
            solver.personColorDisassociate(e0, e1);
        } else if (type.equals(ASSERTION_TYPES.get(3))) {
            solver.jobColorDisassociate(e0, e1);
        } else if (type.equals(ASSERTION_TYPES.get(4))) {
            solver.personJobAssociate(e0, e1);
        } else if (type.equals(ASSERTION_TYPES.get(5))) {
            solver.personJobDisassociate(e0, e1);
        }
    }

    /** Returns the number of assertions I have parsed. */
    int numAssertions() {
        return this.assertions.size();
    }

    /** Returns the text of assertion number K (numbering from 0), with extra
     *  spaces removed. */
    String getAssertion(int k) {
        return this.assertions.get(k).get(0);
    }

    /** Returns the number of questions I have parsed. */
    int numQuestions() {
        return this.questions.size();
    }

    /** Return the text of question number K (numbering from 0), with extra
     *  spaces removed. */
    String getQuestion(int k) {
        return this.questions.get(k).get(0);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER. */
    String getAnswer(Solver solver, int k) {
        String type = questions.get(k).get(1);
        String topic = questions.get(k).get(1).equals(QUESTION_TYPES.get(0))
                || questions.get(k).get(1).equals(QUESTION_TYPES.get(5))
                || questions.get(k).get(1).equals(QUESTION_TYPES.get(7))
                ? capitalize(questions.get(k).get(2))
                : questions.get(k).get(2);
        String result = null;
        ArrayList<ArrayList<String>> info =
                solver.knownAbout(topic.toLowerCase());
        ArrayList<String> attr1guesses = info.get(0);
        ArrayList<String> attr2guesses = info.get(1);
        String attr1 = questions.get(k).get(1).equals(QUESTION_TYPES.get(0))
                || questions.get(k).get(1).equals(QUESTION_TYPES.get(5))
                || questions.get(k).get(1).equals(QUESTION_TYPES.get(7))
                ? attr1guesses.get(0) : capitalize(attr1guesses.get(0));
        String attr2 = attr2guesses.get(0);
        boolean attr1known = attr1guesses.size() == 1 && !isAnonymous(attr1);
        boolean attr2known = attr2guesses.size() == 1 && !isAnonymous(attr2);
        if (QUESTION_TYPES.indexOf(type) < 3) {
            result = answerBroadQuestion(attr1known, attr2known, type,
                    topic, attr1, attr2);
        } else {
            result = answerSpecificQuestion(attr1known, attr2known, type,
                    topic, attr1, attr2);
        }
        return result;
    }

    /**
     * @param name  a name that needs to be capitalized
     * @return the capitalized version of NAME
     */
    private static String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * @param uncapitalizedSet  a set of names that needs to be capitalized
     * @return a set containing every name in the input, except capitalized
     */
    private static LinkedHashSet<String> capitalizeAll(
            LinkedHashSet<String> uncapitalizedSet) {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        Iterator<String> iter = uncapitalizedSet.iterator();
        while (iter.hasNext()) {
            result.add(capitalize(iter.next()));
        }
        return result;
    }

    /** Return TRUE if NAME represents an unknown name returned
     *  by makeName. */
    private static boolean isAnonymous(String name) {
        return name.contains("#");
    }

    /**
     * @param attr1known  true when their is only 1 guess for attr1
     * @param attr2known  true when their is only 2 guess for attr2
     * @param type  the type of question, which is enumerated in QUESTION_TYPES
     * @param topic  the entity which appears in the question
     * @param attr1  a guess about the topic's first attribute
     * @param attr2  a guess about the topic's second attribute
     * @return result  the answer text for the question
     */
    private static String answerBroadQuestion(
            boolean attr1known, boolean attr2known, String type,
            String topic, String attr1, String attr2) {
        String result = "Nothing.";
        if (type.equals(QUESTION_TYPES.get(0))) {
            if (attr1known && attr2known) {
                result = String.format(
                        "%s is the %s and lives in the %s house.",
                        topic, attr1, attr2);
            } else if (attr1known && !attr2known) {
                result = String.format("%s is the %s.",
                        topic, attr1);
            } else if (!attr1known && attr2known) {
                result = String.format("%s lives in the %s house.",
                        topic, attr2);
            }
        } else if (type.equals(QUESTION_TYPES.get(1))) {
            if (attr1known && attr2known) {
                result = String.format(
                        "%s is the %s and lives in the %s house.",
                        attr1, topic, attr2);
            } else if (attr1known && !attr2known) {
                result = String.format("%s is the %s.",
                        attr1, topic);
            } else if (!attr1known && attr2known) {
                result = String.format("The %s lives in the %s house.",
                        topic, attr2);
            }
        } else if (type.equals(QUESTION_TYPES.get(2))) {
            if (attr1known && attr2known) {
                result = String.format(
                        "%s is the %s and lives in the %s house.",
                        attr1, attr2, topic);
            } else if (attr1known && !attr2known) {
                result = String.format("%s lives in the %s house.",
                        attr1, topic);
            } else if (!attr1known && attr2known) {
                result = String.format("The %s lives in the %s house.",
                        attr2, topic);
            }
        }
        return result;
    }

    /**
     * @param attr1known  true when their is only 1 guess for attr1
     * @param attr2known  true when their is only 2 guess for attr2
     * @param type  the type of question, which is enumerated in QUESTION_TYPES
     * @param topic  the entity which appears in the question
     * @param attr1  a guess about the topic's first attribute
     * @param attr2  a guess about the topic's second attribute
     * @return result  the answer text for the question
     */
    private static String answerSpecificQuestion(
            boolean attr1known, boolean attr2known, String type,
            String topic, String attr1, String attr2) {
        String result = "I don't know.";
        if (attr1known) {
            if (type.equals(QUESTION_TYPES.get(3))) {
                result = String.format("%s is the %s.",
                        attr1, topic);
            } else if (type.equals(QUESTION_TYPES.get(4))) {
                result = String.format("%s lives in the %s house.",
                        attr1, topic);
            } else if (type.equals(QUESTION_TYPES.get(5))) {
                result = String.format("%s is the %s.",
                        topic, attr1);
            }
        }
        if (attr2known) {
            if (type.equals(QUESTION_TYPES.get(6))) {
                result = String.format("The %s lives in the %s house.",
                        attr2, topic);
            } else if (type.equals(QUESTION_TYPES.get(7))) {
                result = String.format("%s lives in the %s house.",
                        topic, attr2);
            } else if (type.equals(QUESTION_TYPES.get(8))) {
                result = String.format("The %s lives in the %s house.",
                        topic, attr2);
            }
        }
        return result;
    }

    /** Phrases that collectively contain every keyword. */
    private static final String KEYSTRING =
            "the house "
            + "lives around here "
            + "there is a "
            + "what do you know about "
            + "who "
            + "what does the occupant of "
            + "not live in "
            + "where";

    /** The set of all keywords. */
    private static final LinkedHashSet<String> KEYSET =
            new LinkedHashSet<String>(Arrays.asList(KEYSTRING.split("\\s+")));

    /** Phrases that uniquely identify every assertion type. */
    private static final String ASSERTION_TYPE_STRING =
            "person_with_color "
            + "job_with_color "
            + "person_not_with_color "
            + "job_not_with_color "
            + "person_with_job "
            + "person_not_with_job "
            + "person_exists "
            + "job_exists "
            + "color_exists";

    /** A list of the nine assertion types. */
    private static final ArrayList<String> ASSERTION_TYPES =
            new ArrayList<String>(Arrays.asList(
                    ASSERTION_TYPE_STRING.trim().split("\\s+")));

    /** Phrases that uniquely identify every question type. */
    private static final String QUESTION_TYPE_STRING =
            "about_person "
            + "about_job "
            + "about_color "
            + "person_of_job "
            + "person_of_color "
            + "job_of_person "
            + "job_of_color "
            + "color_of_person "
            + "color_of_job";

    /** A list of the nine question types. */
    private static final ArrayList<String> QUESTION_TYPES =
            new ArrayList<String>(Arrays.asList(
                    QUESTION_TYPE_STRING.trim().split("\\s+")));
}
