import org.chocosolver.memory.IStateInt;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableEvaluator;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.IntVar;

public class Strategy implements VariableSelector<IntVar>, VariableEvaluator<IntVar> {

    private final IStateInt lastArray; // index of the last non-instantiated variable
    private final IStateInt lastChoice;
    private final IntVar[][] varArrays;
    public Strategy(Model model, IntVar[][] varArrays){
        this.varArrays = varArrays;
        this.lastArray = model.getEnvironment().makeInt(0);
        this.lastChoice = model.getEnvironment().makeInt(0);

    }


    @Override
    public IntVar getVariable(IntVar[] variables) { // ja n'utilise pas "variables" car je ne peux pas la changer en IntVar[][] variables car cette méthode est hérité de la classe VariableSelector<IntVar>
        IntVar var = varArrays[lastArray.get()][lastChoice.get()];
        //IntVar smallVar = null;
        int smallDSize = Integer.MAX_VALUE;



        if( lastArray.get() ==0) {
            //premiere variable non instanciée dans la liste en question
            int idx = lastChoice.get();
            while(idx < varArrays[lastArray.get()].length && varArrays[lastArray.get()][idx].isInstantiated()) {
                idx++;
            }
            lastChoice.set(idx);
            //search for the leftmost variable in the current list varArrays[lastArray] with smallest domain
            while (idx < varArrays[lastArray.get()].length) {
                final int dsize = varArrays[lastArray.get()][idx].getDomainSize();
                if (dsize < smallDSize && dsize > 1) {
                    // the variable is candidate for having the smallest domain
                    //and  the variable is not instantiated
                    var = varArrays[lastArray.get()][lastChoice.get()];

                    smallDSize = dsize;
                    // cannot be smaller than a boolean domain
                    if (dsize == 2) {
                        break;
                    }
                }
                idx++;
            }
            //Passer aux listes suivantes :
            lastArray.set(lastArray.get() + 1);
        }
        else {
            // pour avoir le y(ou width ou height) correspondant à x (indexé à lastChoice)
            var = varArrays[lastArray.get()][lastChoice.get()];
            lastArray.set(lastArray.get() + 1);
            //Si on a décidé des 4 var on choisit à nouveau un x
            if (lastArray.get() >= varArrays.length) {
                lastArray.set(0);
            }
        }
        return var;
    }

    @Override
    public double evaluate(IntVar variable) { // je vérifie si variable est dans le tab varArrays car je n'utilise pas IntVar[] variables
        return variable.getDomainSize();
        /*for (int i = 0; i < varArrays.length; i++) {
            for (int j = 0; j < varArrays[i].length; j++) {
                if (varArrays[i][j] == variable) {
                    return variable.getDomainSize();
                }
            }
        }
        throw new IllegalArgumentException("Variable not found in variable array.");
*/
    }
}
