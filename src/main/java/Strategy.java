import org.chocosolver.memory.IStateInt;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableEvaluator;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Strategy implements VariableSelector<IntVar>, VariableEvaluator<IntVar> {
    private  IntVar[][] varArrays;
    private  IStateInt lastChoice;
    private  IStateInt startingPoint;

    public Strategy(Model model, IntVar[][] variables) {
        this.varArrays = variables;
        this.lastChoice = model.getEnvironment().makeInt(-1);
        this.startingPoint = model.getEnvironment().makeInt(-1);
    }

    /**
     *
     * @param varArrays
     * @return true si toutes les variables sont instanciées
     */
    public boolean allVarInst(IntVar[][] varArrays){

        for(int i=0; i<varArrays.length; i++){
            for(int j=0; j<varArrays[i].length; j++){
                if(!varArrays[i][j].isInstantiated()){
                    return false;
                }
            }
        }
        //Si y a pas de variable non instancié :
        return true;
    }

    /**
     *
     * @param variables
     * @return une variable non instancié.
     *         Cette stratégie impose au solveur de finir d'instancier les variables d'un rectangle avant de passer aux variabes d'autes rectangles.
     *            S'il existe un x_i instancié et qu'une ou plus des variables y_i, height_i ou width_i ne sont pas instanciées alors ils seront choisis pour les tours qui suivent.
     *            Sinon on choisi le premier x qui a le plus petit domaine.
     */
    public IntVar getVariable(IntVar[] variables) {
        int minSize = Integer.MAX_VALUE;
        int indexMin = -1;
        if(lastChoice.get() != -1){
            for(int i=1; i<varArrays.length; i++){
                if(!varArrays[i][lastChoice.get()].isInstantiated()){
                    return varArrays[i][lastChoice.get()];
                }
            }
            //pas d'orphelin dans la colonne lastChoice (les orphelins sont les y, height et width non instanciées quand le x du même rectangle est instancié)
            lastChoice.set(-1);
        }

        for(int i=startingPoint.get()+1; i<varArrays[0].length; i++){
            if(varArrays[0][i].isInstantiated()){
                for(int j=1; j<varArrays.length; j++){
                    if(!varArrays[j][i].isInstantiated()){
                        lastChoice.set(i);
                        return varArrays[j][i];
                    }
                }
                if(startingPoint.get()==i-1) {
                    startingPoint.set(i);
                }
            }
            else {
                if(varArrays[0][i].getDomainSize()<minSize){
                    minSize = varArrays[0][i].getDomainSize();
                    indexMin = i;
                }
            }
        }

        // vérifier si toutes les var sont instancié
        if (allVarInst(varArrays)){
            return null;
        }

        // pas d'orphelin
        lastChoice.set(indexMin);
        return varArrays[0][indexMin];

    }

    public double evaluate(IntVar variable) {
        return variable.getDomainSize();
    }

}