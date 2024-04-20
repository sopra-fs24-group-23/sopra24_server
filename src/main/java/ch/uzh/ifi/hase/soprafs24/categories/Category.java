package ch.uzh.ifi.hase.soprafs24.categories;

public interface Category {
    boolean validateAnswer(String answer);
    String fetchResultsFromApi(String input);

    String getName();
}
