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
})

public abstract class Category {

    abstract public boolean validateAnswer(String answer);

    abstract public String getName();

    abstract String fetchResultsFromApi(String input);


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.getName().equals(((Category) obj).getName());
    }

    @Override
    public int hashCode() {
        return getName() == null ? 0 : getName().hashCode();
    }
}


