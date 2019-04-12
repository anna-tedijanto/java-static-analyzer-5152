package exercism_parser;
import java.util.Objects;

class Twofer {
  String twofer(String name) {
    return String.format("One for %s, one for me.", Objects.toString(name, "you"));
  }
}