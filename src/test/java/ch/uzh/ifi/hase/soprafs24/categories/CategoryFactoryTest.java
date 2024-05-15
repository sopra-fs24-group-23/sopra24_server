package ch.uzh.ifi.hase.soprafs24.categories;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryFactoryTest {

    @Test
    public void testCreateCategory(){
        Category category = CategoryFactory.createCategory("Animal");
        assertTrue(category instanceof Animal);

        category = CategoryFactory.createCategory("Country");
        assertTrue(category instanceof Country);

        category = CategoryFactory.createCategory("City");
        assertTrue(category instanceof City);

        category = CategoryFactory.createCategory("Movie/Series");
        assertTrue(category instanceof MoviesSeries);

        category = CategoryFactory.createCategory("Celebrity");
        assertTrue(category instanceof Celebrity);

        category = CategoryFactory.createCategory("Food");
        assertTrue(category instanceof Food);

        category = CategoryFactory.createCategory("Car");
        assertTrue(category instanceof Car);
    }

    @Test
    public void testGetRandomCategories(){
        List<Category> categories = CategoryFactory.getRandomCategories();

        assertEquals(3, categories.size());
        assertNotEquals(categories.get(0), categories.get(1));
        assertNotEquals(categories.get(0), categories.get(2));
        assertNotEquals(categories.get(1), categories.get(2));
    }
}
