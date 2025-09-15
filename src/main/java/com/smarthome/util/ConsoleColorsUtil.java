package com.smarthome.util;

/**
 * A utility class containing ANSI escape codes for coloring and styling
 * console output.
 * <p>
 * These constants can be concatenated with strings to produce colored and
 * formatted text in the console, making the application's output more readable.
 */
public class ConsoleColorsUtil {

    // Reset and text style codes
    /** Resets all color and style settings to default. */
    public static final String RESET = "\u001B[0m";
    /** Sets the text to bold. */
    public static final String BOLD = "\u001B[1m";
    /** Underlines the text. */
    public static final String UNDERLINE = "\u001B[4m";

    // Standard text color codes.
    /** Sets the text color to red. */
    public static final String RED = "\u001B[31m";
    /** Sets the text color to green. */
    public static final String GREEN = "\u001B[32m";
    /** Sets the text color to yellow. */
    public static final String YELLOW = "\u001B[33m";
    /** Sets the text color to blue. */
    public static final String BLUE = "\u001B[34m";
    /** Sets the text color to purple (or magenta). */
    public static final String PURPLE = "\u001B[35m";
    /** Sets the text color to cyan. */
    public static final String CYAN = "\u001B[36m";
    /** Sets the text color to white. */
    public static final String WHITE = "\u001B[37m";

    // Bright text color codes
    /** Sets the text color to bright magenta. */
    public static final String MAGENTA = "\u001B[95m"; // added bright magenta
}