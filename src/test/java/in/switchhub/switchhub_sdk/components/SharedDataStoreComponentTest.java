package in.switchhub.switchhub_sdk.components;

import in.switchhub.switchhub_sdk.models.SwitchModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SharedDataStoreComponentTest {

    private SharedDataStoreComponent sharedDataStoreComponent;

    @BeforeEach
    void setUp() {
        sharedDataStoreComponent = new SharedDataStoreComponent();
    }

    @Test
    void testPutDataMap() {
        SwitchModel switchModel = new SwitchModel();
        switchModel.setSwitchId("switch-123");

        sharedDataStoreComponent.putDataMap("test-switch", switchModel);

        SwitchModel retrieved = sharedDataStoreComponent.getDataMap("test-switch");
        assertEquals(switchModel, retrieved);
    }

    @Test
    void testGetDataMap_ExistingKey() {
        SwitchModel switchModel = new SwitchModel();
        switchModel.setSwitchId("switch-123");

        sharedDataStoreComponent.putDataMap("test-switch", switchModel);

        SwitchModel retrieved = sharedDataStoreComponent.getDataMap("test-switch");
        assertNotNull(retrieved);
        assertEquals("switch-123", retrieved.getSwitchId());
    }

    @Test
    void testGetDataMap_NonExistingKey() {
        SwitchModel retrieved = sharedDataStoreComponent.getDataMap("non-existent");
        assertNull(retrieved);
    }

    @Test
    void testDataMapContainsKey_True() {
        SwitchModel switchModel = new SwitchModel();
        sharedDataStoreComponent.putDataMap("test-switch", switchModel);

        assertTrue(sharedDataStoreComponent.dataMapContainsKey("test-switch"));
    }

    @Test
    void testDataMapContainsKey_False() {
        assertFalse(sharedDataStoreComponent.dataMapContainsKey("non-existent"));
    }

    @Test
    void testDataMapRemove() {
        SwitchModel switchModel = new SwitchModel();
        sharedDataStoreComponent.putDataMap("test-switch", switchModel);

        assertTrue(sharedDataStoreComponent.dataMapContainsKey("test-switch"));

        sharedDataStoreComponent.dataMapRemove("test-switch");

        assertFalse(sharedDataStoreComponent.dataMapContainsKey("test-switch"));
    }

    @Test
    void testDataMapGetAll_Empty() {
        Map<String, SwitchModel> allData = sharedDataStoreComponent.dataMapGetAll();
        assertNotNull(allData);
        assertTrue(allData.isEmpty());
    }

    @Test
    void testDataMapGetAll_WithData() {
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");

        SwitchModel switch2 = new SwitchModel();
        switch2.setSwitchId("switch-2");

        sharedDataStoreComponent.putDataMap("feature-1", switch1);
        sharedDataStoreComponent.putDataMap("feature-2", switch2);

        Map<String, SwitchModel> allData = sharedDataStoreComponent.dataMapGetAll();

        assertEquals(2, allData.size());
        assertTrue(allData.containsKey("feature-1"));
        assertTrue(allData.containsKey("feature-2"));
    }

    @Test
    void testRegisterWeights_ValidPercentage() {
        assertDoesNotThrow(() -> {
            sharedDataStoreComponent.registerWeights("test-switch", "default", 50);
        });

        assertTrue(sharedDataStoreComponent.weightedMeteredMapContainsKey("test-switch"));
    }

    @Test
    void testRegisterWeights_MultipleKeys() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 50);
        sharedDataStoreComponent.registerWeights("test-switch", "premium", 75);

        assertTrue(sharedDataStoreComponent.weightedMeteredMapContainsKey("test-switch"));
    }

    @Test
    void testRegisterWeights_InvalidPercentage() {
        assertThrows(IllegalArgumentException.class, () -> {
            sharedDataStoreComponent.registerWeights("test-switch", "default", 150);
        });
    }

    @Test
    void testGetNextWeight_ExistingGenerator() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 100);

        boolean result = sharedDataStoreComponent.getNextWeight("test-switch", "default");

        assertTrue(result); // 100% should always return true
    }

    @Test
    void testGetNextWeight_NonExistingSwitch() {
        boolean result = sharedDataStoreComponent.getNextWeight("non-existent", "default");

        assertFalse(result); // Should return false when no generator found
    }

    @Test
    void testGetNextWeight_NonExistingMeteredKey_FallbackToDefault() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 100);

        boolean result = sharedDataStoreComponent.getNextWeight("test-switch", "non-existent-key");

        assertTrue(result); // Should fall back to default generator
    }

    @Test
    void testGetNextWeight_NonExistingMeteredKey_NoDefaultFallback() {
        sharedDataStoreComponent.registerWeights("test-switch", "premium", 100);

        boolean result = sharedDataStoreComponent.getNextWeight("test-switch", "non-existent-key");

        assertFalse(result); // Should return false when no default generator exists
    }

    @Test
    void testGetNextWeight_ZeroPercentage() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 0);

        for (int i = 0; i < 100; i++) {
            boolean result = sharedDataStoreComponent.getNextWeight("test-switch", "default");
            assertFalse(result); // 0% should always return false
        }
    }

    @Test
    void testGetNextWeight_HundredPercentage() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 100);

        for (int i = 0; i < 100; i++) {
            boolean result = sharedDataStoreComponent.getNextWeight("test-switch", "default");
            assertTrue(result); // 100% should always return true
        }
    }

    @Test
    void testGetNextWeight_FiftyPercentage() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 50);

        int trueCount = 0;
        for (int i = 0; i < 100; i++) {
            if (sharedDataStoreComponent.getNextWeight("test-switch", "default")) {
                trueCount++;
            }
        }

        assertEquals(50, trueCount); // Should be exactly 50 out of 100
    }

    @Test
    void testWeightedMeteredMapContainsKey_True() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 50);

        assertTrue(sharedDataStoreComponent.weightedMeteredMapContainsKey("test-switch"));
    }

    @Test
    void testWeightedMeteredMapContainsKey_False() {
        assertFalse(sharedDataStoreComponent.weightedMeteredMapContainsKey("non-existent"));
    }

    @Test
    void testResetWeightedMeteredMap() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 50);

        assertTrue(sharedDataStoreComponent.weightedMeteredMapContainsKey("test-switch"));

        sharedDataStoreComponent.resetWeightedMeteredMap("test-switch");

        assertFalse(sharedDataStoreComponent.weightedMeteredMapContainsKey("test-switch"));
    }

    @Test
    void testResetWeightedMeteredMap_ThenRegisterAgain() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 30);

        int trueCount1 = 0;
        for (int i = 0; i < 100; i++) {
            if (sharedDataStoreComponent.getNextWeight("test-switch", "default")) {
                trueCount1++;
            }
        }
        assertEquals(30, trueCount1);

        sharedDataStoreComponent.resetWeightedMeteredMap("test-switch");
        sharedDataStoreComponent.registerWeights("test-switch", "default", 70);

        int trueCount2 = 0;
        for (int i = 0; i < 100; i++) {
            if (sharedDataStoreComponent.getNextWeight("test-switch", "default")) {
                trueCount2++;
            }
        }
        assertEquals(70, trueCount2);
    }

    @Test
    void testConcurrentAccess_DataMap() {
        // Test that ConcurrentHashMap allows concurrent access
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");

        SwitchModel switch2 = new SwitchModel();
        switch2.setSwitchId("switch-2");

        sharedDataStoreComponent.putDataMap("feature-1", switch1);
        sharedDataStoreComponent.putDataMap("feature-2", switch2);

        // Concurrent reads should work
        SwitchModel retrieved1 = sharedDataStoreComponent.getDataMap("feature-1");
        SwitchModel retrieved2 = sharedDataStoreComponent.getDataMap("feature-2");

        assertEquals("switch-1", retrieved1.getSwitchId());
        assertEquals("switch-2", retrieved2.getSwitchId());
    }

    @Test
    void testOverwriteExistingSwitch() {
        SwitchModel switch1 = new SwitchModel();
        switch1.setSwitchId("switch-1");
        switch1.setStatus(true);

        SwitchModel switch2 = new SwitchModel();
        switch2.setSwitchId("switch-2");
        switch2.setStatus(false);

        sharedDataStoreComponent.putDataMap("feature", switch1);
        assertEquals("switch-1", sharedDataStoreComponent.getDataMap("feature").getSwitchId());

        sharedDataStoreComponent.putDataMap("feature", switch2);
        assertEquals("switch-2", sharedDataStoreComponent.getDataMap("feature").getSwitchId());
        assertFalse(sharedDataStoreComponent.getDataMap("feature").isStatus());
    }

    @Test
    void testMultipleMeteredKeys_SameSwitch() {
        sharedDataStoreComponent.registerWeights("test-switch", "default", 25);
        sharedDataStoreComponent.registerWeights("test-switch", "premium", 75);

        int defaultTrueCount = 0;
        int premiumTrueCount = 0;

        for (int i = 0; i < 100; i++) {
            if (sharedDataStoreComponent.getNextWeight("test-switch", "default")) {
                defaultTrueCount++;
            }
            if (sharedDataStoreComponent.getNextWeight("test-switch", "premium")) {
                premiumTrueCount++;
            }
        }

        assertEquals(25, defaultTrueCount);
        assertEquals(75, premiumTrueCount);
    }

    @Test
    void testRemoveNonExistentKey() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            sharedDataStoreComponent.dataMapRemove("non-existent");
        });
    }

    @Test
    void testResetNonExistentWeightedMap() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            sharedDataStoreComponent.resetWeightedMeteredMap("non-existent");
        });
    }
}
