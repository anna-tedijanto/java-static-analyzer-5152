class Twofer_2 {
  String twofer(String name) {
    return String.format("One for %s, one for me.", name != null ? name : "you");
  }
}
