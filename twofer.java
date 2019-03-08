package exercism_parser;
import java.util.Optional;

public class twofer {
    String twofer(String name) {
        String.format("One for %s, one for me.", Optional.ofNullable(name).orElse("you"));
    }
}
