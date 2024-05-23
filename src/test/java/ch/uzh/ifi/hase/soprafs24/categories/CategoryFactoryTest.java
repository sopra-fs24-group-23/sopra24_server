package ch.uzh.ifi.hase.soprafs24.categories;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryFactoryTest {

    @Test
    public void createCategory_validInput_createCorrectCategory(){
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
    public void getRandomCategories_returnThreeDifferentCategories() {
        List<Category> categories = CategoryFactory.getRandomCategories();
        assertEquals(3, categories.size());

        Set<Class> classes = new HashSet<>();
        for (Category category : categories) {
            assertFalse(classes.contains(category.getClass()));
            classes.add(category.getClass());
        }
    }
}
