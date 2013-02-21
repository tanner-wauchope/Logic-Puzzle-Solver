package puzzle;

/** An unchecked exception that Represents any kind of user error in the
 *  input of a puzzle.
 *  @author P. N. Hilfinger
 */

class PuzzleException extends RuntimeException {

    /** If you don't explicitly specify serialVersionUID, a value is generated
     * automatically - but that's brittle because it's compiler implementation
     * dependent. */
    private static final long serialVersionUID = 1L;

    /** A PuzzleException with no message. */
    PuzzleException() {
    }

    /** A PuzzleException for which .getMessage() is MSG. */
    PuzzleException(String msg) {
        super(msg);
    }

}
