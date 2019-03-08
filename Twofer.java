class Twofer {
    private static final String STRING_CONSTANT = "One for %s, one for me.";
    String twofer(String name)
    {
        if (name == null || name.isEmpty()) {
            return String.format(STRING_CONSTANT, "you");
        }

        return String.format(STRING_CONSTANT, name);
    }
}
