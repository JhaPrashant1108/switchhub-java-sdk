package in.switchhub.switchhub_sdk.util;

import in.switchhub.switchhub_sdk.constants.SwitchHubConstants;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeightedBooleanGeneratorTest {

    @Test
    void testConstructor_ValidPercentage() {
        assertDoesNotThrow(() -> new WeightedBooleanGenerator(50));
        assertDoesNotThrow(() -> new WeightedBooleanGenerator(0));
        assertDoesNotThrow(() -> new WeightedBooleanGenerator(100));
    }

    @Test
    void testConstructor_InvalidPercentage_Negative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new WeightedBooleanGenerator(-1);
        });

        assertEquals(SwitchHubConstants.ErrorMessages.PERCENTAGE_OUT_OF_RANGE, exception.getMessage());
    }

    @Test
    void testConstructor_InvalidPercentage_OverHundred() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new WeightedBooleanGenerator(101);
        });

        assertEquals(SwitchHubConstants.ErrorMessages.PERCENTAGE_OUT_OF_RANGE, exception.getMessage());
    }

    @Test
    void testNext_ZeroPercentage() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(0);

        // All values should be false
        for (int i = 0; i < 200; i++) {
            assertFalse(generator.next());
        }
    }

    @Test
    void testNext_HundredPercentage() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(100);

        // All values should be true
        for (int i = 0; i < 200; i++) {
            assertTrue(generator.next());
        }
    }

    @Test
    void testNext_FiftyPercentage() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(50);

        int trueCount = 0;
        int totalCalls = 100;

        for (int i = 0; i < totalCalls; i++) {
            if (generator.next()) {
                trueCount++;
            }
        }

        // Should be exactly 50 true values in 100 calls
        assertEquals(50, trueCount);
    }

    @Test
    void testNext_SeventyPercentage() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(70);

        int trueCount = 0;
        int totalCalls = 100;

        for (int i = 0; i < totalCalls; i++) {
            if (generator.next()) {
                trueCount++;
            }
        }

        // Should be exactly 70 true values in 100 calls
        assertEquals(70, trueCount);
    }

    @Test
    void testNext_ThirtyPercentage() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(30);

        int trueCount = 0;
        int totalCalls = 100;

        for (int i = 0; i < totalCalls; i++) {
            if (generator.next()) {
                trueCount++;
            }
        }

        // Should be exactly 30 true values in 100 calls
        assertEquals(30, trueCount);
    }

    @Test
    void testNext_ResetsAfterPoolExhaustion() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(25);

        int trueCount = 0;

        // Exhaust the pool multiple times (300 calls = 3 full cycles of 100)
        for (int i = 0; i < 300; i++) {
            if (generator.next()) {
                trueCount++;
            }
        }

        // Should have 25 true values per 100 calls, so 75 total in 300 calls
        assertEquals(75, trueCount);
    }

    @Test
    void testNext_ShuffledDistribution() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(50);

        List<Boolean> firstCycle = new ArrayList<>();
        List<Boolean> secondCycle = new ArrayList<>();

        // Collect first 100 values
        for (int i = 0; i < 100; i++) {
            firstCycle.add(generator.next());
        }

        // Collect second 100 values
        for (int i = 0; i < 100; i++) {
            secondCycle.add(generator.next());
        }

        // Both cycles should have same number of true values (50)
        long firstCycleTrueCount = firstCycle.stream().filter(b -> b).count();
        long secondCycleTrueCount = secondCycle.stream().filter(b -> b).count();

        assertEquals(50, firstCycleTrueCount);
        assertEquals(50, secondCycleTrueCount);

        // But the order should be different (shuffled)
        // Check that at least some positions are different
        int differentPositions = 0;
        for (int i = 0; i < 100; i++) {
            if (!firstCycle.get(i).equals(secondCycle.get(i))) {
                differentPositions++;
            }
        }

        // With shuffling, we expect at least some differences
        // (probability of all being same is astronomically low)
        assertTrue(differentPositions > 0, "Expected shuffling between cycles");
    }

    @Test
    void testNext_EdgeCase_OnePercent() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(1);

        int trueCount = 0;
        int totalCalls = 100;

        for (int i = 0; i < totalCalls; i++) {
            if (generator.next()) {
                trueCount++;
            }
        }

        assertEquals(1, trueCount);
    }

    @Test
    void testNext_EdgeCase_NinetyNinePercent() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(99);

        int trueCount = 0;
        int totalCalls = 100;

        for (int i = 0; i < totalCalls; i++) {
            if (generator.next()) {
                trueCount++;
            }
        }

        assertEquals(99, trueCount);
    }

    @Test
    void testNext_MultipleInstances_IndependentState() {
        WeightedBooleanGenerator generator1 = new WeightedBooleanGenerator(50);
        WeightedBooleanGenerator generator2 = new WeightedBooleanGenerator(50);

        // Advance generator1
        for (int i = 0; i < 50; i++) {
            generator1.next();
        }

        // Generator2 should be independent
        int trueCount2 = 0;
        for (int i = 0; i < 100; i++) {
            if (generator2.next()) {
                trueCount2++;
            }
        }

        assertEquals(50, trueCount2);
    }

    @Test
    void testNext_ConsistentPercentageOverMultipleCycles() {
        WeightedBooleanGenerator generator = new WeightedBooleanGenerator(40);

        // Test over 5 complete cycles
        for (int cycle = 0; cycle < 5; cycle++) {
            int trueCount = 0;
            for (int i = 0; i < 100; i++) {
                if (generator.next()) {
                    trueCount++;
                }
            }
            assertEquals(40, trueCount, "Cycle " + cycle + " should have exactly 40 true values");
        }
    }
}
