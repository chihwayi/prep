package zw.mohcc.org.prep.enums;

import java.text.Normalizer;
import java.util.logging.Logger;

public enum PrepExperienceStatus {
    NAIVE("Naïve"),
    TRANSITIONING_OP("Transitioning-OP"),
    TRANSITIONING_DVR("Transitioning-DVR"),
    CAB_LA_FOLLOWUP_VISIT("CAB-LA followup visit");

    private static final Logger logger = Logger.getLogger(PrepExperienceStatus.class.getName());

    private final String displayName;

    PrepExperienceStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    public static PrepExperienceStatus fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty");
        }

        // Normalize the input to handle discrepancies
        String normalizedDisplayName = normalize(displayName);
        logger.info("Attempting to match normalized displayName: '" + normalizedDisplayName + "'");

        for (PrepExperienceStatus status : PrepExperienceStatus.values()) {
            String normalizedEnumDisplayName = normalize(status.displayName);
            logger.info("Comparing against normalized enum value: '" + normalizedEnumDisplayName + "'");
            if (normalizedEnumDisplayName.equals(normalizedDisplayName)) {
                return status;
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
