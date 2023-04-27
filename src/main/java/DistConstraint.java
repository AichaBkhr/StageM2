import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.List;

public class DistConstraint {
    private final int largeurParcelle;
    private final int hauteurParcelle;
    private final IntVar[] X;
    private final IntVar[] Y;
    private final Model model;
    private final List<String> op;
    private final List<List<String>> varConstraint;
    private final int c;
    private final List<Integer> borne;
    private final int nombreType;
    private final List<String> nomElement;
    private final List<Integer> nombreElement;



    public DistConstraint(Model model,int c, List<List<String>> varConstraint, int largeurParcelle, int hauteurParcelle, IntVar[] X, IntVar[] Y, List<String> op, List<Integer> borne, int nombreType, List<String> nomElement,  List<Integer> nombreElement) {
        this.largeurParcelle = largeurParcelle;
        this.hauteurParcelle = hauteurParcelle;
        this.X = X;
        this.Y = Y;
        this.model = model;
        this.op =op;
        this.c=c;
        this.varConstraint =varConstraint;
        this.borne=borne;
        this.nombreElement=nombreElement;
        this.nomElement=nomElement;
        this.nombreType=nombreType;
    }
    public void createConstraint() {
        if(varConstraint.get(c).get(0).equals(varConstraint.get(c).get(1))){
            ArrayList<IntVar> ListVarX = new ArrayList<>();
            ArrayList<IntVar> ListVarY = new ArrayList<>();
            int cpt=0;
            for(int i=0; i<nombreType-1; i++){

                if(nomElement.get(i).equals(varConstraint.get(c).get(0))){
                    for(int j=0; j<nombreElement.get(i); j++){
                        ListVarX.add(X[cpt +j]);
                        ListVarY.add(Y[cpt +j]);
                    }

                }
                cpt+= nombreElement.get(i);
            }

            IntVar[] varX = ListVarX.toArray(new IntVar[ListVarX.size()]);
            IntVar[] varY = ListVarY.toArray(new IntVar[ListVarY.size()]);



            //contrainte dist :
            for (int i = 0; i < varX.length - 1; i++) {

                for (int j = i + 1; j < varY.length; j++) {
                    //System.out.println(i + "-" +j);

                    IntVar dx = model.intVar("dx", 0, Math.max(largeurParcelle, hauteurParcelle));
                    IntVar dy = model.intVar("dy", 0, Math.max(largeurParcelle, hauteurParcelle));
                    model.distance(varX[i], varX[j], "=", dx).post();  //dx=|x2 - x1|
                    model.distance(varY[i], varY[j], "=", dy).post(); //dy=|y2 - y1|
                    IntVar dx2 = model.intVar("dx2", 0, Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle));
                    IntVar dy2 = model.intVar("dy2", 0, Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle));
                    model.times(dx, dx, dx2).post();
                    model.times(dy, dy, dy2).post();
                    int C = borne.get(c);
                    model.arithm(dx2, "+", dy2, op.get(c), C * C).post();


                }
            }

        }
        else {
            ArrayList<IntVar> ListVarX_1 = new ArrayList<>();
            ArrayList<IntVar> ListVarY_1 = new ArrayList<>();
            ArrayList<IntVar> ListVarX_2 = new ArrayList<>();
            ArrayList<IntVar> ListVarY_2 = new ArrayList<>();
            int cpt1=0;
            for(int i=0; i<nombreType-1; i++){

                if(nomElement.get(i).equals(varConstraint.get(c).get(0))){
                    for(int j=0; j<nombreElement.get(i); j++){
                        ListVarX_1.add(X[cpt1 +j]);
                        ListVarY_1.add(Y[cpt1 +j]);
                    }

                }
                cpt1+= nombreElement.get(i);
            }
            int cpt2=0;
            for(int i=0; i<nombreType-1; i++){

                if(nomElement.get(i).equals(varConstraint.get(c).get(1))){
                    for(int j=0; j<nombreElement.get(i); j++){
                        ListVarX_2.add(X[cpt2 +j]);
                        ListVarY_2.add(Y[cpt2 +j]);
                    }

                }
                cpt2+= nombreElement.get(i);
            }
            IntVar[] varX_1 = ListVarX_1.toArray(new IntVar[ListVarX_1.size()]);
            IntVar[] varY_1 = ListVarY_1.toArray(new IntVar[ListVarY_1.size()]);
            IntVar[] varX_2 = ListVarX_2.toArray(new IntVar[ListVarX_2.size()]);
            IntVar[] varY_2 = ListVarY_2.toArray(new IntVar[ListVarY_2.size()]);

            for (int i = 0; i < varX_1.length; i++) {
                for (int j = 0; j < varX_2.length; j++) {
                    IntVar dx = model.intVar("dx", 0, Math.max(largeurParcelle, hauteurParcelle));
                    IntVar dy = model.intVar("dy", 0, Math.max(largeurParcelle, hauteurParcelle));
                    model.distance(varX_1[i], varX_2[j], "=", dx).post();  //dx=|x2 - x1|
                    model.distance(varY_1[i], varY_2[j], "=", dy).post(); //dy=|y2 - y1|
                    //System.out.println();
                    IntVar dx2 = model.intVar("dx2", 0, Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle));
                    IntVar dy2 = model.intVar("dy2", 0, Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle));
                    //model.square(dx2, dx);//dx2 = dx^2
                    //model.square(dy2, dy);//dy2 = dy^2
                    model.times(dx, dx, dx2).post();
                    model.times(dy, dy, dy2).post();
                    int C = borne.get(c);
                    model.arithm(dx2, "+", dy2, op.get(c), C * C).post();




                }
            }


        }
    }

}
