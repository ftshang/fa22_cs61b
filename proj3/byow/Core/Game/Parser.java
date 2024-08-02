package byow.Core.Game;

public class Parser {
    /** Method that parses the input string and returns a long that represents the seed. */
    public static Long parseInputForSeed(String input) {
        StringBuilder userString = new StringBuilder();

        for (int i = 0; i < input.length(); i += 1) {
            char c = input.charAt(i);

            if (c == 'N' || c == 'n') {
                continue;
            }
            if (c == 'S' || c == 's') {
                break;
            }
            userString.append(c);
        }

        return Long.parseLong(userString.toString());
    }

    /** Method that parses the input string and returns the string movements. */
    public static String parseInputForMovements(String input) {
        StringBuilder userString = new StringBuilder();

        int index = input.indexOf('S');
        index += 1;

        for (int i = index; i < input.length(); i += 1) {
            char c = input.charAt(i);
            if (c == 'L') {
                continue;
            }

            if (c == ':') {
                break;
            }
            userString.append(c);
        }
        return userString.toString();
    }

    /** Method that parses the input string and returns true if the input string contains a load character. */
    public static boolean isLoadString(String input) {
        return input.charAt(0) == 'L' || input.charAt(0) == 'l';
    }

}
