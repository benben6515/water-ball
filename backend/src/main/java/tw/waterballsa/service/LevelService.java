package tw.waterballsa.service;

import org.springframework.stereotype.Service;

/**
 * Level calculation service for gamification system
 * Handles exp-to-level conversion and progress tracking
 */
@Service
public class LevelService {

    // XP required for each level (exponential growth)
    // Level 1: 0, Level 2: 100, Level 3: 250, Level 4: 450, etc.
    private static final int BASE_EXP = 100;
    private static final double GROWTH_RATE = 1.5;

    /**
     * Calculate level information from total experience points
     */
    public LevelInfo calculateLevelInfo(Integer totalExp) {
        if (totalExp == null || totalExp < 0) {
            totalExp = 0;
        }

        int level = calculateLevel(totalExp);
        int expForCurrentLevel = getExpRequiredForLevel(level);
        Integer expForNextLevel = getExpRequiredForLevel(level + 1);

        // Calculate progress within current level
        int expInCurrentLevel = totalExp - expForCurrentLevel;
        int expNeededForNextLevel = expForNextLevel - expForCurrentLevel;
        double progressPercentage = (expNeededForNextLevel > 0)
            ? (expInCurrentLevel * 100.0 / expNeededForNextLevel)
            : 100.0;

        return LevelInfo.builder()
                .level(level)
                .currentExp(totalExp)
                .expForNextLevel(expForNextLevel)
                .expProgressPercentage(Math.min(progressPercentage, 100.0))
                .build();
    }

    /**
     * Calculate level from total experience points
     */
    private int calculateLevel(int totalExp) {
        int level = 1;
        while (totalExp >= getExpRequiredForLevel(level + 1)) {
            level++;
        }
        return level;
    }

    /**
     * Get total experience required to reach a specific level
     */
    private int getExpRequiredForLevel(int level) {
        if (level <= 1) {
            return 0;
        }

        int totalExp = 0;
        for (int i = 2; i <= level; i++) {
            totalExp += (int) (BASE_EXP * Math.pow(GROWTH_RATE, i - 2));
        }
        return totalExp;
    }

    /**
     * Level information DTO
     */
    public static class LevelInfo {
        private Integer level;
        private Integer currentExp;
        private Integer expForNextLevel;
        private Double expProgressPercentage;

        public static LevelInfoBuilder builder() {
            return new LevelInfoBuilder();
        }

        public Integer getLevel() {
            return level;
        }

        public Integer getCurrentExp() {
            return currentExp;
        }

        public Integer getExpForNextLevel() {
            return expForNextLevel;
        }

        public Double getExpProgressPercentage() {
            return expProgressPercentage;
        }

        public static class LevelInfoBuilder {
            private Integer level;
            private Integer currentExp;
            private Integer expForNextLevel;
            private Double expProgressPercentage;

            public LevelInfoBuilder level(Integer level) {
                this.level = level;
                return this;
            }

            public LevelInfoBuilder currentExp(Integer currentExp) {
                this.currentExp = currentExp;
                return this;
            }

            public LevelInfoBuilder expForNextLevel(Integer expForNextLevel) {
                this.expForNextLevel = expForNextLevel;
                return this;
            }

            public LevelInfoBuilder expProgressPercentage(Double expProgressPercentage) {
                this.expProgressPercentage = expProgressPercentage;
                return this;
            }

            public LevelInfo build() {
                LevelInfo info = new LevelInfo();
                info.level = this.level;
                info.currentExp = this.currentExp;
                info.expForNextLevel = this.expForNextLevel;
                info.expProgressPercentage = this.expProgressPercentage;
                return info;
            }
        }
    }
}
