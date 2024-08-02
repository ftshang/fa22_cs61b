package byow.Core.InputSource;

/** Interface that contains methods that need to be implemented by all classes that implement InputSource. */
public interface InputSource {
    /** Method that returns the next key of the input. */
    public char getNextKey();

    /** Method that returns whether there is a key within the remaining input. */
    public boolean possibleNextInput();
}
