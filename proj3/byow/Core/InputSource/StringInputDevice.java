package byow.Core.InputSource;


/** Class that represents a StringInputDevice. Implements the InputSource interface. */
public class StringInputDevice implements InputSource {
    private String input;
    private int index;

    /** One parameter constructor that takes in a string that represents the input line. */
    public StringInputDevice(String s) {
        index = 0;
        input = s;
    }

    /** Overridden getNextKey method that returns the current character of the input string. */
    @Override
    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return Character.toUpperCase(returnChar);
    }

    /** Overridden possibleNextInput method that returns true if there are still characters that need to be
     * parsed within the string.
     * Returns false if there are no more characters within the string. */
    @Override
    public boolean possibleNextInput() {
        return index < input.length();
    }
}
