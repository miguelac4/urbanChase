package fractals;

public class Rule {
    private char symbol;
    private String string;

    public Rule(char symbol, String string) {
        this.symbol = symbol;
        this.string = string;
    }
    public char getSymbol() {
        return symbol;
    }
    public String getString() {
        return string;
    }

}
