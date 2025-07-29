package com.app.notesappandroidproject.utils;

/**
 * Config
 * <p>
 * This class holds constant configuration values used throughout the application.
 * It serves as a centralized location for static constants related to UI design and predefined question lists.
 * <p>
 * Features:
 * 1. Define default text size and line spacing for consistent UI rendering.
 * 2. Store a predefined list of security or personal verification questions.
 * 3. Centralize commonly reused values for maintainability and consistency.
 * <p>
 * Dependencies:
 * - No external dependencies; this is a simple utility class for constant configuration values.
 */
public class Config {

    /**
     * Default text size for UI components like text views or edit texts.
     * Ensures uniformity in text appearance across the application.
     */
    public static final int TEXT_SIZE_DEFAULT = 15;

    /**
     * Default line spacing for UI text elements.
     * Ensures improved readability for multiline text displays.
     */
    public static final int TEXT_LINE_SPACE_DEFAULT = 10;

    /**
     * Predefined list of security or personal verification questions.
     * These can be used in user onboarding, security checks, or personalized user flows.
     */
    public static final String[] LST_QUESTION = {
            "Where's my birthplace?", "What was your first car?", "When is your anniversary?"
            , "What is your oldest sibling's middle name?", "In what city or town did your mother and father meet?"
    };
}
