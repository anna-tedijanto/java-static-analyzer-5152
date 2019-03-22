import java.util.Objects;

class twofer {
  String twofer(String name) {
    return String.format("One for %s, one for me.", Objects.toString(name, "you"));
  }
}