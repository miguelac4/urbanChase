package fractals;

public class LSystem {
    private String sequence;
    private Rule[] ruleset;
    private int generation;

    public LSystem(String axiom, Rule[] ruleset){
        sequence = axiom;
        this.ruleset = ruleset;
        generation = 0;
    }

    public int getGeneration() {
        return generation;
    }

    public String getSequence() {
        return sequence;
    }

    public void  nextGeneration(){
        generation++;

        String nextGen = "";
        for(int i=0; i<sequence.length(); i++){
            char c = sequence.charAt(i);
            String replace = "" + c;
            for(int j=0; j<ruleset.length; j++){
                if(c == ruleset[j].getSymbol()){
                    replace = ruleset[j].getString();
                    break;
                }
            }
            nextGen += replace;
        }
        this.sequence = nextGen;
    }
}
