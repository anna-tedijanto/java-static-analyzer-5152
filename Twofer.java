class Twofer {
    private static final String STRING_CONSTANT = "One for %s, one for me.";
    String twofer(String name)
    {
        String newName = name;
        if (name == null || name.isEmpty()) {
            newName = "you";
        }

        return String.format(STRING_CONSTANT, newName);
    }
}
