package ch.uzh.ifi.hase.soprafs24.categories;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CategoryFactoryTest {

    @Test
    public void getRandomCategories_returnDifferentCategories() {
        List<Category> categories = CategoryFactory.getRandomCategories();
        Set<Class> classes = new HashSet<>();

        for (Category category : categories) {
            assertFalse(classes.contains(category.getClass()));
            classes.add(category.getClass());
        }
    }
}
