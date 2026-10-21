package com.example.recipesuggester.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.recipesuggester.model.PantryItem;

import org.junit.Test;

// unit tests for the form validation. these run on the PC, no emulator needed.
// null means "no error"
public class IngredientValidatorTest {

    @Test
    public void validNames() {
        assertNull(IngredientValidator.validateName("Flour"));
        assertNull(IngredientValidator.validateName("  self-raising flour  "));
        assertNull(IngredientValidator.validateName("baker's yeast"));
    }

    @Test
    public void badNames() {
        assertNotNull(IngredientValidator.validateName(""));
        assertNotNull(IngredientValidator.validateName("   "));
        assertNotNull(IngredientValidator.validateName(null));
        assertNotNull(IngredientValidator.validateName("eggs2"));
        assertNotNull(IngredientValidator.validateName("---"));
        assertNotNull(IngredientValidator.validateName("a".repeat(41)));
    }

    @Test
    public void validQuantities() {
        assertNull(IngredientValidator.validateQuantity("2"));
        assertNull(IngredientValidator.validateQuantity("0.5"));
        assertNull(IngredientValidator.validateQuantity("0,5")); // comma keyboards
    }

    @Test
    public void zeroNegativeAndJunkQuantitiesRejected() {
        assertNotNull(IngredientValidator.validateQuantity(""));
        assertNotNull(IngredientValidator.validateQuantity("0"));
        assertNotNull(IngredientValidator.validateQuantity("-3"));
        assertNotNull(IngredientValidator.validateQuantity("abc"));
        assertNotNull(IngredientValidator.validateQuantity("NaN"));
        assertNotNull(IngredientValidator.validateQuantity("Infinity"));
        assertNotNull(IngredientValidator.validateQuantity("1000001"));
    }

    @Test
    public void unitMustBeFromTheList() {
        assertNull(IngredientValidator.validateUnit("kg"));
        assertNotNull(IngredientValidator.validateUnit(""));
        assertNotNull(IngredientValidator.validateUnit("cups"));
        assertNotNull(IngredientValidator.validateUnit(null));
    }

    @Test
    public void expiryDateIsOptionalButMustBeReal() {
        assertNull(IngredientValidator.validateExpiryDate(""));
        assertNull(IngredientValidator.validateExpiryDate(null));
        assertNull(IngredientValidator.validateExpiryDate("2026-12-31"));
        assertNotNull(IngredientValidator.validateExpiryDate("2026-02-30")); // no 30th of Feb
        assertNotNull(IngredientValidator.validateExpiryDate("2026-1-5"));
        assertNotNull(IngredientValidator.validateExpiryDate("31/12/2026"));
    }

    @Test
    public void quantityFormatting() {
        assertEquals("2", PantryItem.formatQuantity(2.0));
        assertEquals("0.5", PantryItem.formatQuantity(0.50));
        assertEquals("1500", PantryItem.formatQuantity(1500));
    }
}
