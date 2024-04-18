package ch.uzh.ifi.hase.soprafs24.categories;

// This Factory class exists to convert a string into a category of the corresponding type
public class CategoryFactory {
    public static Category createCategory(String categoryName) {
        switch (categoryName) {
            case "Country":
                return new Country();
            case "City":
                return new City();
            // Add more
            default:
                throw new IllegalArgumentException("Invalid category name: " + categoryName);
        }
    }
}
