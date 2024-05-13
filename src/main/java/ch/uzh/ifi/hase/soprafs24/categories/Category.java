package ch.uzh.ifi.hase.soprafs24.categories;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Country.class, name = "country"),
        @JsonSubTypes.Type(value = City.class, name = "city"),
        @JsonSubTypes.Type(value = Animal.class, name = "animal"),
        @JsonSubTypes.Type(value = Celebrity.class, name = "celebrity"),
        @JsonSubTypes.Type(value = MoviesSeries.class, name = "movies"),
        @JsonSubTypes.Type(value = Car.class, name = "car"),
        @JsonSubTypes.Type(value = Food.class, name = "food"),



        // Other implementations
})
public interface Category {
    boolean validateAnswer(String answer);
    String fetchResultsFromApi(String input);
    String getName();
}


