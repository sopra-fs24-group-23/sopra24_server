package ch.uzh.ifi.hase.soprafs24.constant;

public enum ColorRequirement {

    BLACK("#000000", 0),
    GREEN("#008000", 200),
    BLUE("#0000FF", 500),
    ORANGE("#FFA500", 1000),
    RED("#FF0000", 2000),
    PURPLE("#A020F0", 5000);

    private final String colorCode;
    private final int scoreRequirement;

    ColorRequirement(String colorCode, int scoreRequirement) {
        this.colorCode = colorCode;
        this.scoreRequirement = scoreRequirement;
    }

    public String getColorCode() {
        return colorCode;
    }

    public int getScoreRequirement() {
        return scoreRequirement;
    }

    public static ColorRequirement getByColorCode(String colorCode) {
        for (ColorRequirement requirement : ColorRequirement.values()) {
            if (requirement.getColorCode().equals(colorCode)) {
                return requirement;
            }
        }
        throw new IllegalArgumentException("Invalid color code: " + colorCode);
    }
}
