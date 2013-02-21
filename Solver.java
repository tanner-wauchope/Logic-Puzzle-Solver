package puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

/** A puzzle-solving engine.
    @author Tanner Wauchope */
class Solver {

    /** The number of people in the puzzle, including anonymous people. */
    private int complexity;

    /** Indicates whether impossibility has been confirmed. */
    private boolean impossible;

    /**
     *  This is a set of the associations that have been made.
     *  Each association is a 2-item array with one of the following types:
     *      [person, job]
     *      [person, color]
     *      [job, color]
     */
    private ArrayList<String[]> associations;

    /**
     *  This is a set of the disassociations that have been made.
     *  Each disassociation is a 2-item array with one of the following types:
     *      [person, job]
     *      [person, color]
     *      [job, color]
     */
    private ArrayList<String[]> disassociations;

    /**
     *  A list of the people in the puzzle, including anonymous people.
     *  A person's index in this list is the same as that person's index
     *  in the list this.factsByPerson.
     */
    private ArrayList<String> people;

    /**
     *  A list of the jobs in the puzzle, including anonymous jobs.
     *  A job's index in this list is the same as the that job's index
     *  in the list this.factsByJob.
     */
    private ArrayList<String> jobs;

    /**
     *  A list of the colors in the puzzle, including anonymous colors.
     *  A person's index in this list is the same as the color's index
     *  in the list this.factsByPerson.
     */
    private ArrayList<String> colors;

    /**
     *  Each sublist in this.factsByPerson corresponds to a specific person,
     *  and each sublist contains two sub-sublists. The first sub-sublist holds
     *  possible jobs, and the second sub-sublist holds possible colors.
     *  factsByPerson might look like this:
     *      [[[professor, doctor], [blue, green]],
     *       [[software engineer], [blue, green]],
     *       [[professor, doctor], [yellow]    ]]]
     */
    private ArrayList<ArrayList<ArrayList<String>>> factsByPerson;

    /**
     *  Each sublist in this.factsByPerson corresponds to a specific person,
     *  and each sublist contains two sub-sublists. The first sub-sublist holds
     *  possible jobs, and the second sub-sublist holds possible colors.
     *  factsByPerson might look like this:
     *      [[[Bob, Mark], [blue, green]],
     *       [[Alexander], [blue, green]],
     *       [[Bob, Mark], [yellow]    ]]]
     */
    private ArrayList<ArrayList<ArrayList<String>>> factsByJob;

    /**
     *  Each sublist in this.factsByPerson corresponds to a specific person,
     *  and each sublist contains two sub-sublists. The first sub-sublist holds
     *  possible jobs, and the second sub-sublist holds possible colors.
     *  factsByPerson might look like this:
     *      [[[Bob, Mark], [professor, doctor]],
     *       [[Alexander], [software engineer]],
     *       [[Bob, Mark], [professor, doctor]]]
     */
    private ArrayList<ArrayList<ArrayList<String>>> factsByColor;

    /** A new Solver, containing no information. */
    Solver() {
        this.factsByPerson = new ArrayList<ArrayList<ArrayList<String>>>();
        this.factsByJob = new ArrayList<ArrayList<ArrayList<String>>>();
        this.factsByColor = new ArrayList<ArrayList<ArrayList<String>>>();
        this.associations = new ArrayList<String[]>();
        this.disassociations = new ArrayList<String[]>();
        this.people = new ArrayList<String>();
        this.jobs = new ArrayList<String>();
        this.colors = new ArrayList<String>();
        this.complexity = 0;
        this.impossible = false;
    }

    /**
     * @return the associations
     */
    public ArrayList<String[]> getAssociations() {
        return this.associations;
    }

    /**
     * @return the associations
     */
    public ArrayList<String[]> getDisassociations() {
        return this.disassociations;
    }

    /**
     * @return the people
     */
    public ArrayList<String> getPeople() {
        return this.people;
    }

    /**
     * @return the jobs
     */
    public ArrayList<String> getJobs() {
        return this.jobs;
    }

    /**
     * @return the colors
     */
    public ArrayList<String> getColors() {
        return this.colors;
    }

    /**
     * @return the complexity
     */
    public int getComplexity() {
        return this.complexity;
    }

    /**
     * @param importedPeople  The people recognized by calling parser.
     *                        It does not include anonymous people.
     * @param importedJobs  The jobs recognized by the calling parser.
     *                      It does not include anonymous jobs.
     * @param importedColors  The colors property of the calling parser.
     *                        It does not include anonymous colors.
     */
    void setParameters(LinkedHashSet<String> importedPeople,
            LinkedHashSet<String> importedJobs,
            LinkedHashSet<String> importedColors) {
        this.complexity = Math.max(importedPeople.size(),
                Math.max(importedJobs.size(), importedColors.size()));
        this.people.addAll(importedPeople);
        this.jobs.addAll(importedJobs);
        this.colors.addAll(importedColors);
        while (this.people.size() != this.complexity) {
            this.people.add(String.format("person#%d", this.people.size()));
        }
        while (this.jobs.size() != this.complexity) {
            this.jobs.add(String.format("job#%d", this.jobs.size()));
        }
        while (this.colors.size() != this.complexity) {
            this.colors.add(String.format("color#%d", this.colors.size()));
        }
        populatePossibilities();
    }

    /**
     *  Loads all possible options into the three facts lists.
     */
    private void populatePossibilities() {
        for (int i = 0; i < this.complexity; i += 1) {
            this.factsByPerson.add(new ArrayList<ArrayList<String>>());
            this.factsByPerson.get(i).add(new ArrayList<String>());
            this.factsByPerson.get(i).add(new ArrayList<String>());
            for (int j = 0; j < this.complexity; j += 1) {
                this.factsByPerson.get(i).get(0).add(this.jobs.get(j));
                this.factsByPerson.get(i).get(1).add(this.colors.get(j));
            }
            this.factsByJob.add(new ArrayList<ArrayList<String>>());
            this.factsByJob.get(i).add(new ArrayList<String>());
            this.factsByJob.get(i).add(new ArrayList<String>());
            for (int j = 0; j < this.complexity; j += 1) {
                this.factsByJob.get(i).get(0).add(this.people.get(j));
                this.factsByJob.get(i).get(1).add(this.colors.get(j));
            }
            this.factsByColor.add(new ArrayList<ArrayList<String>>());
            this.factsByColor.get(i).add(new ArrayList<String>());
            this.factsByColor.get(i).add(new ArrayList<String>());
            for (int j = 0; j < this.complexity; j += 1) {
                this.factsByColor.get(i).get(0).add(this.people.get(j));
                this.factsByColor.get(i).get(1).add(this.jobs.get(j));
            }
        }
    }

    /**
     * PERSON is associated with JOB.
     */
    void personJobAssociate(String person, String job) {
        String[] association = {person, job, "personJob"};
        if (!deepContains(this.associations, association)) {
            this.associations.add(association);
            int personIndex = this.people.indexOf(person);
            this.factsByPerson.get(personIndex).get(0).clear();
            this.factsByPerson.get(personIndex).get(0).add(job);
            int jobIndex = this.jobs.indexOf(job);
            this.factsByJob.get(jobIndex).get(0).clear();
            this.factsByJob.get(jobIndex).get(0).add(person);
            for (int i = 0; i < this.complexity; i += 1) {
                if (!this.people.get(i).equals(person)) {
                    personJobDisassociate(this.people.get(i), job);
                }
                if (!this.jobs.get(i).equals(job)) {
                    personJobDisassociate(person, this.jobs.get(i));
                }
            }
        }
    }

    /**
     * This method attempts to use the association of PERSON and JOB
     * to make inferences.
     */
    private void personJobReassociate(String person, String job) {
        for (int i = 0; i < this.complexity; i += 1) {
            if (!this.factsByColor.get(i).get(0).contains(person)) {
                jobColorDisassociate(job, this.colors.get(i));
            }
            if (!this.factsByColor.get(i).get(1).contains(job)) {
                personColorDisassociate(person, this.colors.get(i));
            }
            String[] premise1 = {person, this.colors.get(i), "personColor"};
            if (deepContains(this.associations, premise1)) {
                jobColorAssociate(job, this.colors.get(i));
            }
            String[] premise2 = {job, this.colors.get(i), "personColor"};
            if (deepContains(this.associations, premise2)) {
                personColorAssociate(person, this.colors.get(i));
            }
        }
    }

    /**
     *  PERSON is associated with COLOR.
     */
    void personColorAssociate(String person, String color) {
        String[] association = {person, color, "personColor"};
        if (!deepContains(this.associations, association)) {
            this.associations.add(association);
            int personIndex = this.people.indexOf(person);
            this.factsByPerson.get(personIndex).get(1).clear();
            this.factsByPerson.get(personIndex).get(1).add(color);
            int colorIndex = this.colors.indexOf(color);
            this.factsByColor.get(colorIndex).get(0).clear();
            this.factsByColor.get(colorIndex).get(0).add(person);
            for (int i = 0; i < this.complexity; i += 1) {
                if (!this.people.get(i).equals(person)) {
                    personColorDisassociate(this.people.get(i), color);
                }
                if (!this.colors.get(i).equals(color)) {
                    personColorDisassociate(person, this.colors.get(i));
                }
            }
        }
    }

    /**
     * This method attempts to use the association of PERSON and COLOR
     * to make inferences.
     */
    private void personColorReassociate(String person, String color) {
        for (int i = 0; i < this.complexity; i += 1) {
            if (!this.factsByJob.get(i).get(0).contains(person)) {
                jobColorDisassociate(this.jobs.get(i), color);
            }
            if (!this.factsByJob.get(i).get(1).contains(color)) {
                personJobDisassociate(person, this.jobs.get(i));
            }
            String[] premise1 = {person, this.jobs.get(i), "personJob"};
            if (deepContains(this.associations, premise1)) {
                jobColorAssociate(this.jobs.get(i), color);
            }
            String[] premise2 = {this.jobs.get(i), color, "jobColor"};
            if (deepContains(this.associations, premise2)) {
                personJobAssociate(person, this.jobs.get(i));
            }
        }
    }

    /**
     *  JOB is associated with COLOR.
     */
    void jobColorAssociate(String job, String color) {
        String[] association = {job, color, "jobColor"};
        if (!deepContains(this.associations, association)) {
            this.associations.add(association);
            int jobIndex = this.jobs.indexOf(job);
            this.factsByJob.get(jobIndex).get(1).clear();
            this.factsByJob.get(jobIndex).get(1).add(color);
            int colorIndex = this.colors.indexOf(color);
            this.factsByColor.get(colorIndex).get(1).clear();
            this.factsByColor.get(colorIndex).get(1).add(job);
            for (int i = 0; i < this.complexity; i += 1) {
                if (!this.jobs.get(i).equals(job)) {
                    jobColorDisassociate(this.jobs.get(i), color);
                }
                if (!this.colors.get(i).equals(color)) {
                    jobColorDisassociate(job, this.colors.get(i));
                }
            }
        }
    }

    /**
     * This method attempts to use the association of JOB and COLOR
     * to make inferences.
     */
    private void jobColorReassociate(String job, String color) {
        for (int i = 0; i < this.complexity; i += 1) {
            if (!this.factsByPerson.get(i).get(0).contains(job)) {
                personColorDisassociate(this.people.get(i), color);
            }
            if (!this.factsByPerson.get(i).get(1).contains(color)) {
                personJobDisassociate(this.people.get(i), job);
            }
            String[] premise1 = {this.people.get(i), job, "personJob"};
            if (deepContains(this.associations, premise1)) {
                personColorAssociate(this.people.get(i), color);
            }
            String[] premise2 = {this.people.get(i), color, "personColor"};
            if (deepContains(this.associations, premise2)) {
                personJobAssociate(this.people.get(i), job);
            }
        }
    }

    /**
     *  PERSON is disassociated with JOB.
     */
    void personJobDisassociate(String person, String job) {
        String[] disassociation = {person, job, "personJob"};
        if (!deepContains(this.disassociations, disassociation)) {
            this.disassociations.add(disassociation);
            int personIndex = this.people.indexOf(person);
            int jobIndex = this.jobs.indexOf(job);
            this.factsByPerson.get(personIndex).get(0).remove(job);
            this.factsByJob.get(jobIndex).get(0).remove(person);
        }
    }

    /**
     * This method attempts to use the disassociation of PERSON and JOB
     * to make inferences.
     */
    void personJobRedisassociate(String person, String job) {
        for (int i = 0; i < this.complexity; i += 1) {
            if (this.factsByColor.get(i).get(0).size() == 1
                    && this.factsByColor.get(i).get(0).get(0).equals(person)) {
                jobColorDisassociate(job, this.colors.get(i));
            }
            if (this.factsByColor.get(i).get(1).size() == 1
                    && this.factsByColor.get(i).get(1).get(0).equals(job)) {
                personColorDisassociate(person, this.colors.get(i));
            }
        }
    }

    /**
     *  PERSON is disassociated with COLOR.
     */
    void personColorDisassociate(String person, String color) {
        String[] disassociation = {person, color, "personColor"};
        if (!deepContains(this.disassociations, disassociation)) {
            this.disassociations.add(disassociation);
            int personIndex = this.people.indexOf(person);
            this.factsByPerson.get(personIndex).get(1).remove(color);
            int colorIndex = this.colors.indexOf(color);
            this.factsByColor.get(colorIndex).get(0).remove(person);
        }
    }

    /**
     * This method attempts to use the disassociation of PERSON and COLOR
     * to make inferences.
     */
    void personColorRedisassociate(String person, String color) {
        for (int i = 0; i < this.complexity; i += 1) {
            if (this.factsByJob.get(i).get(0).size() == 1
                    && this.factsByJob.get(i).get(0).get(0).equals(person)) {
                jobColorDisassociate(this.jobs.get(i), color);
            }
            if (this.factsByJob.get(i).get(1).size() == 1
                    && this.factsByJob.get(i).get(1).get(0).equals(color)) {
                personJobDisassociate(person, this.jobs.get(i));
            }
        }
    }

    /**
     *  JOB is disassociated with COLOR.
     */
    void jobColorDisassociate(String job, String color) {
        String[] disassociation = {job, color, "jobColor"};
        if (!deepContains(this.disassociations, disassociation)) {
            this.disassociations.add(disassociation);
            int jobIndex = this.jobs.indexOf(job);
            this.factsByJob.get(jobIndex).get(1).remove(color);
            int colorIndex = this.colors.indexOf(color);
            this.factsByColor.get(colorIndex).get(1).remove(job);
        }
    }

    /**
     * This method attempts to use the disassociation of JOB and COLOR
     * to make inferences.
     */
    void jobColorRedisassociate(String job, String color) {
        for (int i = 0; i < this.complexity; i += 1) {
            if (this.factsByPerson.get(i).get(0).size() == 1
                    && this.factsByPerson.get(i).get(0).get(0).equals(job)) {
                personColorDisassociate(this.people.get(i), color);
            }
            if (this.factsByPerson.get(i).get(1).size() == 1
                    && this.factsByPerson.get(i).get(1).get(0).equals(color)) {
                personJobDisassociate(this.people.get(i), job);
            }
        }
    }

    /**
     * @param pairs  a set of arrays that may include the content of PAIR
     * @param pair  an array whose content may be contained in the PAIRS
     * @return a boolean that is true only when the content of pair is the
     *         same as the content in one of the arrays in PAIRS
     */
    static boolean deepContains(ArrayList<String[]> pairs,
            String[] pair) {
        boolean result = false;
        for (String[] curr : pairs) {
            if (Arrays.toString(curr).equals(Arrays.toString(pair))) {
                result = true;
                break;
            }
        }
        return result;
    }

    /** Return a list of two lists, in which each list include entities
     *  of a specific type that may be associated with the entity named ID.
     *  For example, knownAbout("Tom") might contain a list of lists:
     *     [plumber, architect, dentist],
     *     [red, blue, color#2 ]
     *  (where color#2 denotes an anonymous color.). */
    ArrayList<ArrayList<String>> knownAbout(String id) {
        if (this.people.contains(id.toLowerCase())) {
            return this.factsByPerson.get(
                    this.people.indexOf(id.toLowerCase()));
        } else if (this.jobs.contains(id)) {
            return this.factsByJob.get(this.jobs.indexOf(id));
        } else {
            return this.factsByColor.get(this.colors.indexOf(id));
        }
    }

    /** Return true iff the current set of facts is impossible. */
    boolean impossible() {
        return this.impossible;
    }

    /**
     * @return  A boolean that is true only if possibilities has been reduced
     *          to zero for any attribute of any entity.
     */
    boolean overconstrained() {
        for (int i = 0; i < this.complexity; i += 1) {
            if (this.factsByPerson.get(i).get(0).isEmpty()
                    || this.factsByPerson.get(i).get(1).isEmpty()
                    || this.factsByJob.get(i).get(0).isEmpty()
                    || this.factsByJob.get(i).get(1).isEmpty()
                    || this.factsByColor.get(i).get(0).isEmpty()
                    || this.factsByColor.get(i).get(1).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return  returns true if two or more entities of the same type
     *          claim the same attribute
     */
    boolean contradictory() {
        for (String[] assoc1 : this.associations) {
            if (deepContains(this.disassociations, assoc1)) {
                return true;
            }
            for (String[] assoc2 : this.associations) {
                if (assoc1[2].equals(assoc2[2])) {
                    if (assoc1[0].equals(assoc2[0])
                            && !assoc1[1].equals(assoc2[1])) {
                        return true;
                    }
                    if (!assoc1[0].equals(assoc2[0])
                            && assoc1[1].equals(assoc2[1])) {
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < this.complexity; i += 1) {
            int[] known = counts(
                    this.people.get(i), this.jobs.get(i), this.colors.get(i));
            for (int j = 0; j < 6; j += 1) {
                if (known[j] > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param person  this person is claimed by count2 jobs and count4 colors
     * @param job  this job is claimed by count0 people and count5 colors
     * @param color  this color is claimed by count1 people and count3 jobs
     * @return result  an array of the counts 0 through 5
     */
    private int[] counts(String person, String job, String color) {
        int[] result = {0, 0, 0, 0, 0, 0};
        for (int i = 0; i < this.complexity; i += 1) {
            if (this.factsByPerson.get(i).get(0).size() == 1
                    && this.factsByPerson.get(i).get(0).get(0).equals(job)) {
                result[0] += 1;
            }
            if (this.factsByPerson.get(i).get(1).size() == 1
                    && this.factsByPerson.get(i).get(1).get(0).equals(color)) {
                result[1] += 1;
            }
            if (this.factsByJob.get(i).get(0).size() == 1
                    && this.factsByJob.get(i).get(0).get(0).equals(person)) {
                result[2] += 1;
            }
            if (this.factsByJob.get(i).get(1).size() == 1
                    && this.factsByJob.get(i).get(1).get(0).equals(color)) {
                result[3] += 1;
            }
            if (this.factsByColor.get(i).get(0).size() == 1
                    && this.factsByColor.get(i).get(0).get(0).equals(person)) {
                result[4] += 1;
            }
            if (this.factsByColor.get(i).get(1).size() == 1
                    && this.factsByColor.get(i).get(1).get(0).equals(job)) {
                result[5] += 1;
            }
        }
        return result;
    }

    /**
     *  Updates associations and reviews disassociations
     *  until no new inferences are possible.
     */
    public void makeInferences() {
        boolean updating = true;
        boolean finalCheck = true;
        while (!this.impossible && (updating || finalCheck)) {
            finalCheck = updating ? true : false;
            ArrayList<String[]> prevAssociations =
                    new ArrayList<String[]>(this.associations);
            ArrayList<String[]> prevDisassociations =
                    new ArrayList<String[]>(this.disassociations);
            for (String[] pair : prevAssociations) {
                this.impossible =  this.impossible || this.overconstrained();
                if (this.impossible) {
                    break;
                }
                reassociate(pair);
            }
            for (String[] pair : prevDisassociations) {
                this.impossible =  this.impossible || this.overconstrained();
                if (!this.impossible) {
                    break;
                }
                redisassociate(pair);
            }
            for (int i = 0; i < this.complexity; i += 1) {
                ArrayList<String> p1jobs = this.factsByPerson.get(i).get(0);
                for (int j = 0; j < this.complexity; j += 1) {
                    ArrayList<String> p2jobs =
                            this.factsByPerson.get(j).get(0);
                    if (p1jobs.size() == 2 && i != j
                            && p1jobs.equals(p2jobs)) {
                        for (int k = 0; k < this.complexity; k += 1) {
                            if (k != i && k != j) {
                                personJobDisassociate(this.people.get(k),
                                        p1jobs.get(0));
                                personJobDisassociate(this.people.get(k),
                                        p1jobs.get(1));
                            }
                        }
                    }
                }
            }
            newEliminationAssociations();
            updating = !deepContainsAll(this.associations, prevAssociations)
                    || !deepContainsAll(
                            this.disassociations, prevDisassociations);
        }
        this.impossible = this.impossible || this.contradictory();
    }

    /**
     * @param superset  a possible superset to SUBSET
     * @param subset  a possible subset to SUPERSET
     * @return  a boolean that is true if SUBSET is contained by SUPERSET
     */
    private boolean deepContainsAll(ArrayList<String[]> superset,
            ArrayList<String[]> subset) {
        boolean result = true;
        for (String[] arr : subset) {
            result = result && deepContains(superset, arr);
        }
        return result;
    }

    /**
     * @param pair  a dissassociation that needs to be reviewed
     */
    private void reassociate(String[] pair) {
        if (this.people.contains(pair[0])
                && this.jobs.contains(pair[1])) {
            personJobReassociate(pair[0], pair[1]);
        } else if (this.people.contains(pair[0])
                && this.colors.contains(pair[1])) {
            personColorReassociate(pair[0], pair[1]);
        } else {
            jobColorReassociate(pair[0], pair[1]);
        }
    }

    /**
     * @param pair  a dissassociation that needs to be reviewed
     */
    private void redisassociate(String[] pair) {
        if (this.people.contains(pair[0])
                && this.jobs.contains(pair[1])) {
            personJobRedisassociate(pair[0], pair[1]);
        } else if (this.people.contains(pair[0])
                && this.colors.contains(pair[1])) {
            personColorRedisassociate(pair[0], pair[1]);
        } else {
            jobColorRedisassociate(pair[0], pair[1]);
        }
    }

    /**
     *  Looks for cases in which (this.compelxity - 1) disassociations act
     *  as the equivalent of an association. If such an association is found,
     *  the association is then formalized.
     */
    private void newEliminationAssociations() {
        for (int i = 0; i < this.complexity; i += 1) {
            if (this.factsByPerson.get(i).get(0).size() == 1) {
                String[] personJob = {this.people.get(i),
                        this.factsByPerson.get(i).get(0).get(0)};
                personJobAssociate(personJob[0], personJob[1]);
            }
            if (this.factsByPerson.get(i).get(1).size() == 1) {
                String[] personColor = {this.people.get(i),
                        this.factsByPerson.get(i).get(1).get(0)};
                personColorAssociate(personColor[0], personColor[1]);
            }
            if (this.factsByJob.get(i).get(1).size() == 1) {
                String[] jobColor = {this.jobs.get(i),
                        this.factsByJob.get(i).get(1).get(0)};
                jobColorAssociate(jobColor[0], jobColor[1]);
            }
        }
    }

    /**
     * @return  returns null if there no confirmed branches, but
     *          otherwise will return a complete solution
     */
    public Solver exploreBranches() {
        ArrayList<ArrayList<String>> bestPersonForGuessing =
                this.findBestPersonForGuessing();
        ArrayList<ArrayList<String[]>> guesses =
                this.findGuesses(bestPersonForGuessing);
        ArrayList<Solver> hypotheticals = new ArrayList<Solver>();
        for (ArrayList<String[]> guess : guesses) {
            Solver hypothetical = cloneSolver(this);
            hypothetical.jobColorAssociate(
                    guess.get(0)[0], guess.get(0)[1]);
            hypothetical.personJobAssociate(
                    guess.get(1)[0], guess.get(1)[1]);
            hypothetical.personColorAssociate(
                    guess.get(2)[0], guess.get(2)[1]);
            hypothetical.makeInferences();
            hypotheticals.add(hypothetical);
        }
        for (int i = 0; i < hypotheticals.size(); i += 1) {
            boolean otherwiseImpossible = true;
            if (!hypotheticals.get(i).impossible()) {
                for (int j = 0; j < hypotheticals.size(); j += 1) {
                    if (j != i) {
                        otherwiseImpossible = otherwiseImpossible
                                && hypotheticals.get(j).impossible();
                    }
                }
                if (otherwiseImpossible) {
                    return hypotheticals.get(i);
                }
            }
        }
        return null;
    }

    /**
     * @param original  a solver that needs to be cloned
     * @return  a clone of ORIGINAL
     */
    private static Solver cloneSolver(Solver original) {
        Solver result = new Solver();
        LinkedHashSet<String> newPeople = new LinkedHashSet<String>();
        LinkedHashSet<String> newJobs = new LinkedHashSet<String>();
        LinkedHashSet<String> newColors = new LinkedHashSet<String>();
        for (int i = 0; i < original.complexity; i += 1) {
            newPeople.add(original.people.get(i));
            newJobs.add(original.jobs.get(i));
            newColors.add(original.colors.get(i));
        }
        result.setParameters(newPeople, newJobs, newColors);
        for (String [] oldAssoc : original.associations) {
            result.associate(oldAssoc[0], oldAssoc[1], oldAssoc[2]);
        }
        for (String[] oldDisassoc : original.disassociations) {
            result.disassociate(oldDisassoc[0], oldDisassoc[1], oldDisassoc[2]);
        }
        return result;
    }

    /**
     * E0 and E1 become associated using the associator corresponding to TYPE.
     */
    private void associate(String e0, String e1, String type) {
        if (type.equals("personJob")) {
            personJobAssociate(e0, e1);
        } else if (type.equals("personColor")) {
            personColorAssociate(e0, e1);
        } else {
            jobColorAssociate(e0, e1);
        }
    }

    /**
     * E0 is disassociated with E1 by the disassociator corresponding to TYPE.
     */
    private void disassociate(String e0, String e1, String type) {
        if (type.equals("personJob")) {
            personJobDisassociate(e0, e1);
        } else if (type.equals("personColor")) {
            personColorDisassociate(e0, e1);
        } else {
            jobColorDisassociate(e0, e1);
        }
    }

    /**
     * @param mostConstrained  the most constrained person whose attributes
     *                         still are not completely known.
     * @return  a list of a sublists, such that each sublist contains three
     *          associations that constitute a guess of MOSTCONSTRAINED's
     *          attributes
     */
    private ArrayList<ArrayList<String[]>> findGuesses(
            ArrayList<ArrayList<String>> mostConstrained) {
        ArrayList<ArrayList<String[]>> result =
                new ArrayList<ArrayList<String[]>>();
        int index = this.factsByPerson.indexOf(mostConstrained);
        int attr1freedom = mostConstrained.get(0).size();
        int attr2freedom = mostConstrained.get(1).size();
        for (int i = 0; i < attr1freedom; i += 1) {
            for (int j = 0; j < attr2freedom; j += 1) {
                ArrayList<String[]> guess = new ArrayList<String[]>();
                String[] assoc1 = {mostConstrained.get(0).get(i),
                        mostConstrained.get(1).get(j)};
                String[] assoc2 = {this.people.get(index), assoc1[0]};
                String[] assoc3 = {this.people.get(index), assoc1[1]};
                guess.add(assoc1);
                guess.add(assoc2);
                guess.add(assoc3);
                result.add(guess);
            }
        }
        return result;
    }

    /**
     * @return  the most constrained person whose attributes
     *          still are not completely known.
     */
    private ArrayList<ArrayList<String>> findBestPersonForGuessing() {
        int degreesOfFreedom  = this.complexity * this.complexity;
        int personFreedom = -1;
        ArrayList<ArrayList<String>> result =
                new ArrayList<ArrayList<String>>();
        for (int i = 0; i < this.complexity; i += 1) {
            personFreedom = this.factsByPerson.get(i).get(0).size()
                    * this.factsByPerson.get(i).get(1).size();
            if (personFreedom != 1) {
                degreesOfFreedom = Math.min(degreesOfFreedom, personFreedom);
                if (personFreedom == degreesOfFreedom) {
                    result = this.factsByPerson.get(i);
                }
            }
        }
        return result;
    }
}
