package ch.uzh.ifi.hase.soprafs24.categories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CategoryFactory {

    // To add a new category, add the name here, and add it to the switch statement below
    private static final String[] categoryNames = new String[]{
            "Animal",
            "Country",
            "City",
            "Movie/Series",
            "Celebrity",
            "Food",
            "Car"
    };

    public static Category createCategory(String categoryName) {
        switch (categoryName) {
            case "Animal":
                return new Animal();
            case "Country":
                return new Country();
            case "City":
                return new City();
            case "Movie/Series":
                return new MoviesSeries();
            case "Celebrity":
                return new Celebrity();
            case "Food":
                return new Food();
            case "Car":
                return new Car();
            default:
                throw new IllegalArgumentException("Invalid category name: " + categoryName);
        }
    }

    // returns list of three, non-equal, random categories
    public static List<Category> getRandomCategories() {
        Random random = new Random();
        List<Category> categories = new ArrayList<>();

        while (categories.size() < 3) {
            int index = random.nextInt(categoryNames.length);

            Category randCategory = CategoryFactory.createCategory(categoryNames[index]);

            if (!categories.contains(randCategory)) {
                categories.add(randCategory);
            }
        }

        return categories;
    }
}
