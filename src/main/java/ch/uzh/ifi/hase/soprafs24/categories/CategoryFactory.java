package ch.uzh.ifi.hase.soprafs24.categories;

// This Factory class exists to convert a string into a category of the corresponding type
public class CategoryFactory {
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
}
