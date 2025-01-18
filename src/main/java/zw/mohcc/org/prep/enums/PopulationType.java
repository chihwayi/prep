package zw.mohcc.org.prep.enums;

import java.text.Normalizer;
import java.util.logging.Logger;

public enum PopulationType {
    SW("SW"),
    MSM("MSM"),
    TG("TG"),
    AGYW("AGYW"),
    OTHER("Other"),
    GEN_POP("Gen Pop"),
    SERO_DISCORDANT("Sero_discordant");

    private static final Logger logger = Logger.getLogger(PopulationType.class.getName());

    private final String displayName;

    PopulationType(String displayName) { this.displayName = displayName; }

    @Override
    public String toString() {
        return this.displayName;
    }

    public static PopulationType fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty");
        }

        String normalizedDisplayName = normalize(displayName);
        logger.info("Attempting to match normalized displayName: '" + normalizedDisplayName + "'");

        for (PopulationType type : PopulationType.values()) {
            String normalizedEnumDisplayName = normalize(type.displayName);
            logger.info("Comparing against normalized enum value: '" + normalizedEnumDisplayName + "'");
            if (normalizedEnumDisplayName.equals(normalizedDisplayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PrepExperienceStatus: '" + displayName + "'");
    }

    private static String normalize(String input) {
        if (input == null) {
            return "";
        }
        return Normalizer.normalize(input.trim(), Normalizer.Form.NFC)
                .toLowerCase()
                .replace("–", "-") // Replace en-dash
                .replace("—", "-") // Replace em-dash
                .replace("−", "-"); // Replace minus sign
    }
}
