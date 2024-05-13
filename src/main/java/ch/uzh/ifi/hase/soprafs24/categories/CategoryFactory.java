package ch.uzh.ifi.hase.soprafs24.categories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryFactory {
    private static final List<Category> standardCategories = new ArrayList<>(Arrays.asList(
            new Animal(),
            new Country(),
            new City(),
            new MoviesSeries(),
            new Celebrity(),
            new Food(),
            new Car()
    ));

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
            case "Random":
                return new RandomCategory(standardCategories);
            case "Random: Movie/Series":
                return new RandomCategory(standardCategories);
            case "Random: City":
                return new RandomCategory(standardCategories);
            case "Random: Country":
                return new RandomCategory(standardCategories);
            case "Random: Animal":
                return new RandomCategory(standardCategories);
            case "Random: Food":
                return new RandomCategory(standardCategories);
            case "Random: Car":
                return new RandomCategory(standardCategories);
            case "Random: Celebrity":
                return new RandomCategory(standardCategories);

            default:
                throw new IllegalArgumentException("Invalid category name: " + categoryName);
        }
    }
}
