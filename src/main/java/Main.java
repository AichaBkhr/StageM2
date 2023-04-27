import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import static org.chocosolver.solver.search.strategy.Search.*;

import org.chocosolver.solver.search.strategy.assignments.DecisionOperator;
import org.chocosolver.solver.search.strategy.selectors.values.*;
import org.chocosolver.solver.search.strategy.selectors.variables.*;

import static org.chocosolver.solver.search.strategy.Search.intVarSearch;
import static org.knowm.xchart.internal.chartpart.Axis.Direction.X;
import static org.knowm.xchart.internal.chartpart.Axis.Direction.Y;

public class Main{


    public static void main(String[] args) {

        File fichier = new File("file-V.1-2.txt");

        List<Integer> largeurTotalElement = new ArrayList<>(); // if flexible alors c'est ligne[5], si c"est standards alors c'est ligne[4]*ligne[5]
        List<Integer> hauteurTotalElement = new ArrayList<>(); // if flexible alors c'est ligne[6], si c"est standards alors c'est ligne[4]*ligne[6]
        List<List <Integer>> hauteurPossible = new ArrayList<>();
        List<List <Integer>> largeurPossible = new ArrayList<>();
        List<String> etatElement = new ArrayList<>();   //ligne[1] = standard ou flexible
        List<Integer> nombreElement = new ArrayList<>();  // ligne[4]
        List<String> couleurFill = new ArrayList<>();  //ligne[2]
        List<String> couleurStroke = new ArrayList<>(); //ligne[3]
        List<String> nomElement = new ArrayList<>(); //ligne[0]
        int nombreElemntTotal = 0;

        List<String> typeContraintes = new ArrayList<>();
        List<List<String>> varConstraint = new ArrayList<>();
        List<String> op = new ArrayList<>();
        List<Integer> borne = new ArrayList<>();
        int nbDistConstraint = 0;



        try {
            Scanner scanner = new Scanner(fichier);

            while (scanner.hasNextLine()  ) {
                String paramsElement = scanner.nextLine();
                String[] argElement = paramsElement.split("; ");

                //String next = scanner.nextLine();

                if (argElement[0].equals("contraintes")) {
                    break;
                }

                // Etat element
                etatElement.add(argElement[1]);

                // Nom Element
                nomElement.add(argElement[0]);


                //Les largeurs et hauteurs possibles

                List<Integer> blaa = new ArrayList<>();
                String arg7 = argElement[7].substring(1, argElement[7].length() - 1);
                String[] p = arg7.split(",");
                for (int i = 0; i < p.length; i++) {
                    blaa.add(Integer.parseInt(p[i]));
                }
                hauteurPossible.add(blaa);

                List<Integer> blaa2 = new ArrayList<>();
                String arg8 = argElement[8].substring(1, argElement[8].length() - 1);
                String[] p2 = arg8.split(",");
                for (int i = 0; i < p2.length; i++) {
                    blaa2.add(Integer.parseInt(p2[i]));

                }
                largeurPossible.add(blaa2);

                //Les hauteurs et largeurs max
                if (argElement[1].equals("flexible")) {
                    largeurTotalElement.add(Integer.parseInt(argElement[5]));
                    hauteurTotalElement.add(Integer.parseInt(argElement[6]));
                } else {
                    largeurTotalElement.add(Integer.parseInt(argElement[5]));
                    hauteurTotalElement.add(Integer.parseInt(argElement[6]) * Integer.parseInt(argElement[4]));
                }

                //nombre Element
                int n = Integer.parseInt(argElement[4]);
                nombreElement.add(n);
                nombreElemntTotal += n;

                //Couleur Element
                couleurFill.add(argElement[2]);
                couleurStroke.add(argElement[3]);
            }


            while (scanner.hasNextLine() ) {

                nbDistConstraint += 1;
                String paramsConstraint = scanner.nextLine();
                String[] argConstraint = paramsConstraint.split("; ");

                typeContraintes.add(argConstraint[0]);
                if (argConstraint[0].equals("dist")) {
                    List<String> varName = new ArrayList<>();

                    String[] p = argConstraint[1].substring(1, argConstraint[1].length() - 1).split(", ");
                    for (int i = 0; i < p.length; i++) {
                        varName.add(p[i]);
                    }
                    //pour chaque contrainte je rajoute une liste de ses portés
                    varConstraint.add(varName);


                    op.add(argConstraint[2]);
                    borne.add(Integer.parseInt(argConstraint[3]));
                }

            }


            /*for(int i=0; i<varConstraint.size(); i++){
                System.out.println(varConstraint.get(i));
            }
            System.out.println(typeContraintes);
            System.out.println(op);
            System.out.println(borne);*/


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




        // Définition du modèle
        Model model = new Model("Placement d'éléments");




        //Définition des variables
        IntVar[] X = new IntVar[nombreElemntTotal-1];
        IntVar[] Y = new IntVar[nombreElemntTotal-1];
        IntVar[] width = new IntVar[nombreElemntTotal-1];
        IntVar[] height = new IntVar[nombreElemntTotal-1];
        int nombreType = nomElement.size();
        int largeurParcelle = largeurTotalElement.get(nombreType-1);
        int hauteurParcelle = hauteurTotalElement.get(nombreType-1);

        // Définir le domaine des variable width et height de chaque type
        int[] nbTotalPosssibilite = new int[nombreType-1];
        for(int e=0; e<nombreType-1; e++) {
            nbTotalPosssibilite[e] += largeurPossible.get(e).size() + hauteurPossible.get(e).size();
        }

        int[][] longueurPossibleTotale = new int[nombreType -1][];
        for(int e=0; e<nombreType-1; e++) {
            longueurPossibleTotale[e] = new int[nbTotalPosssibilite[e]];
            System.arraycopy(largeurPossible.get(e).stream().mapToInt(s -> s).toArray() , 0 ,longueurPossibleTotale[e], 0,largeurPossible.get(e).size());
            System.arraycopy(hauteurPossible.get(e).stream().mapToInt(s -> s).toArray() , 0 ,longueurPossibleTotale[e], largeurPossible.get(e).size(),hauteurPossible.get(e).size());
        }

        /*  print les longueurs possibles de chaque élément
        for(int i=0; i<longueurPossibleTotale.length; i++){
            for(int j=0; j<longueurPossibleTotale[i].length; j++){
                System.out.print(longueurPossibleTotale[i][j]);
            }
            System.out.println(" ");
        }*/


        //éviter les répétitions
        int[][] longueurDistinctes = new int [nombreType-1][];
        for(int e=0; e<nombreType-1; e++) {
            longueurDistinctes[e] = Arrays.stream(longueurPossibleTotale[e]).distinct().toArray();
        }

        /* print longueurDistinctes de chaque élément
        for(int e=0; e<nombreType-1; e++) {
            for(int i=0;i< longueurDistinctes[e].length; i++) {
                System.out.println(longueurDistinctes[e][i]);
            }
            System.out.println("next element");
        }*/


        int varIndex =0;
        for (int i = 0; i < nombreType-1; i++) {
            for(int j=0; j<nombreElement.get(i);j++) {
                X[varIndex] = model.intVar("x_" + nomElement.get(i) + "_"+ varIndex,  0, largeurParcelle);
                Y[varIndex] = model.intVar("y_" + nomElement.get(i)+"_"+ varIndex, 0, hauteurParcelle);
                width[varIndex] = model.intVar("width_" + nomElement.get(i)+"_"+ varIndex,  longueurDistinctes[i]);
                height[varIndex] = model.intVar("height_" + nomElement.get(i)+"_"+ varIndex,  longueurDistinctes[i]);
                varIndex++;
            }
        }


        //Définition des contraintes

        //Contrainte 1 : interdir le chevauchement

        model.diffN(X, Y, width, height, false).post();



        //Cotrainte 2 : Placer les élément dans la parcelle et non en dehors de la prcelle

        for (int i = 0; i < nombreElemntTotal-1; i++) {
            model.arithm(X[i], "+", width[i], "<=", largeurParcelle).post();
            model.arithm(Y[i], "+", height[i], "<=", hauteurParcelle).post();
        }



        //Contrainte 3 : Orientation et choix de forme

        //chercher les doublons :
        List<List<Integer>> listeDoublons = new ArrayList<>(nombreType-1) ;
        for(int e=0; e<nombreType-1; e++) {  // pour chaque élément
            listeDoublons.add(new ArrayList<Integer>());
            for (int i = 0; i < largeurPossible.get(e).size(); i++) {  // je parcours ces deux liste et je les compare
                for (int j = 0; j < hauteurPossible.get(e).size(); j++) {
                    if (largeurPossible.get(e).get(i) == hauteurPossible.get(e).get(j) && !listeDoublons.contains(largeurPossible.get(e).get(i))) { // je l'ajoute à la liste s'il n y a pas encore
                        listeDoublons.get(e).add(largeurPossible.get(e).get(i));
                    }
                }
            }
        }

        // créer des tuples pour chaque type + contrainte table pour toute copie de ce type
        for(int element=0; element<nombreType-1; element++){

            //créer les tuples
            Tuples tuplesVals = new Tuples(true);
            int STAR = -1; //toute valeur du domaine de la variable en question
            tuplesVals.setUniversalValue(STAR);
            for (int i = 0; i < listeDoublons.get(element).size(); i++) {
                if(!listeDoublons.get(element).isEmpty()) {
                    tuplesVals.add(STAR, listeDoublons.get(element).get(i));
                    tuplesVals.add(listeDoublons.get(element).get(i), STAR);
                }
            }
            for(int i=0; i<largeurPossible.get(element).size(); i++){
                if(! listeDoublons.get(element).contains(largeurPossible.get(element).get(i)) ) {
                    for (int j = 0; j < hauteurPossible.get(element).size(); j++) {
                        if (! listeDoublons.get(element).contains(hauteurPossible.get(element).get(j))){
                            tuplesVals.add(largeurPossible.get(element).get(i),hauteurPossible.get(element).get(j));
                            tuplesVals.add(hauteurPossible.get(element).get(j),largeurPossible.get(element).get(i));
                        }
                    }
                }
            }

            //contrainte table
            for(int i=0; i<nombreElement.get(element); i++){
                //System.out.println(tuplesVals);
                model.table(new IntVar[] {height[i],width[i]}, tuplesVals).post();
            }
        }



        /* // marche seulement si les deux listes sont disjointes
        int cmpt=0;
        while(cmpt<nombreElemntTotal-1) {
            for (int i = 0; i < nombreType-1; i++) {
                int[] valsAutoiseeHauteur = hauteurPossible.get(i).stream().mapToInt(s -> s).toArray();
                int[] valsAutoiseeLargeur = largeurPossible.get(i).stream().mapToInt(s -> s).toArray();

                for(int j=0; j<nombreElement.get(i);j++) {
                        model.ifOnlyIf(model.member(height[cmpt],valsAutoiseeHauteur ), model.member(width[cmpt], valsAutoiseeLargeur));
                        cmpt++;
                }
            }
        }*/




        //Contraintes 4 : s'assurer que les dimensions demandées sont bien respectées

        int cmptr =0;
        while(cmptr<nombreElemntTotal-1) { // pas vraiment besoin !
            for (int i = 0; i < nombreType-1; i++) {
                IntVar[] dimensionVar = new IntVar[nombreElement.get(i)];
                // pour chaque type d'élément je calcule la dimension de chaque rectangle qui le représente. Je stock cela dans une liste de variables


                for (int k = 0; k < nombreElement.get(i); k++) {
                    dimensionVar[k] = model.intVar("aireDuRect", 0, largeurParcelle * hauteurParcelle);
                    model.times(width[cmptr], height[cmptr], dimensionVar[k]).post();
                    cmptr++;
                }
                model.sum(dimensionVar, "=", largeurTotalElement.get(i)*hauteurTotalElement.get(i)).post();


            }
        }



        //Contrainte 5 : calcul de la distance entre plusieurs réctangles


        int c=0;
        while (c<nbDistConstraint){
            //contrainte dist :
            DistConstraint distConstraint = new DistConstraint( model, c, varConstraint, largeurParcelle,   hauteurParcelle,  X,   Y, op, borne, nombreType, nomElement, nombreElement);
            distConstraint.createConstraint();
            c++;
        }









        // Stratégie 1 : concatener X,Y,height et width. choisir la var non instanciée qui a le plus petit domaine, lui assigner la plus petite valeur de son domaine.
/*
        IntVar[] totalVar = new IntVar[(nombreElemntTotal-1)*4];
        System.arraycopy(X,0,totalVar,0,nombreElemntTotal-1);
        System.arraycopy(Y,0,totalVar,nombreElemntTotal-1,nombreElemntTotal-1);
        System.arraycopy(width,0,totalVar,(nombreElemntTotal-1)*2,nombreElemntTotal-1);
        System.arraycopy(height,0,totalVar,(nombreElemntTotal-1)*3,nombreElemntTotal-1);


        Solver s = model.getSolver();
        s.setSearch(intVarSearch(
                // selects the variable of smallest domain size
                new FirstFail(model),
                // selects the smallest domain value (lower bound)
                new IntDomainMin(),
                // variables to branch on
                totalVar

        ));

*/
        IntVar[][] totalVar = new IntVar[4][(nombreElemntTotal - 1) ];
        totalVar[0] = X;
        totalVar[1] = Y;
        totalVar[2] = width;
        totalVar[3] = height;
        IntVar[] totalVar2 = new IntVar[(nombreElemntTotal-1)*4];
        System.arraycopy(X,0,totalVar2,0,nombreElemntTotal-1);
        System.arraycopy(Y,0,totalVar2,nombreElemntTotal-1,nombreElemntTotal-1);
        System.arraycopy(width,0,totalVar2,(nombreElemntTotal-1)*2,nombreElemntTotal-1);
        System.arraycopy(height,0,totalVar2,(nombreElemntTotal-1)*3,nombreElemntTotal-1);

        Solver s = model.getSolver();
        s.setSearch(intVarSearch(
                // selects the variable of smallest domain size
                new Strategy(model, totalVar),
                // selects the smallest domain value (lower bound)
                new IntDomainMin(),
                // variables to branch on
                totalVar2

        ));

        //Stratégie 2 : Ordonner les vars de telle sorte qu'on décide des 4 coordonnées d'un rectangle donné avant de passer à un autre rectangle. On force le solver à suivre l'ordre x0, y0, height0, width0, x1, y1, height1, width1, ...

        /*IntVar[] vars = new IntVar[(nombreElemntTotal-1)*4];
        int idx = 0;
        for (int i = 0; i < nombreElemntTotal-1; i++) {
            if (!X[i].isInstantiated()) {
                vars[idx++] = X[i];
                vars[idx++] = Y[i];
                vars[idx++] = height[i];
                vars[idx++] = width[i];
            }
        }
        model.getSolver().setSearch(
                org.chocosolver.solver.search.strategy.Search.domOverWDegRefSearch(vars)
        );*/



        // Résolution du modèle
        //model.getSolver().showDecisions();
        model.getSolver().solve();



        // Affichage des résultats en matrice
        for(int i =0; i<nombreElemntTotal-1; i++) {
            System.out.print( X[i] +"\t" + Y[i]  +"\t"+height[i] +"\t" + width[i] + "\n");
        }

        // Affichage des résultats en SVG
        SVG svgRect = new SVG(1000, 1000);
        int echelle = 15;
        double strokeWidth =5.0;
        // Afficher la parcelle
        Rectangle parcelle = new Rectangle( 0, 0, largeurParcelle*echelle, hauteurParcelle*echelle);
        Style styleParcelle = new Style();
        styleParcelle.setFillOpacity(0.0);
        styleParcelle.setStroke(couleurStroke.get(nombreType-1));
        styleParcelle.setStrokeWidth(strokeWidth );
        parcelle.setStyle(styleParcelle);
        svgRect.add(parcelle);


        int cpt =0;
        List<Rectangle> rect = new ArrayList<>();
        while (cpt<nombreElemntTotal-1){
            //Vérifier si toutes les variables sont instanciées avant de les afficher. Si y a pas de sol, aucune variable ne sera instansiée ?
            if (X[cpt].isInstantiated() & Y[cpt].isInstantiated() & height[cpt].isInstantiated() & width[cpt].isInstantiated()) {
                for(int i=0; i<nombreType-1;i++){
                    for(int m=0; m<nombreElement.get(i);m++) {
                        //je crée un style pour chaque type d'élément.
                        Style styleElement = new Style();
                        styleElement.setFill(couleurFill.get(i));
                        styleElement.setStroke(couleurStroke.get(i));
                        styleElement.setStrokeWidth(strokeWidth );
                        //je crée autant de réctangle que de nombreElement pour chaque élément.
                        Rectangle newRectangle = new Rectangle(X[cpt].getValue()*echelle, Y[cpt].getValue()*echelle, width[cpt].getValue()*echelle, height[cpt].getValue()*echelle);
                        newRectangle.setStyle(styleElement);
                        rect.add(newRectangle);
                        cpt++;
                    }
                }
                for(int r=0; r<nombreElemntTotal-1;r++) {
                    svgRect.add(rect.get(r));
                }
                svgRect.saveAsFile("farm-V.1-2.svg");
            } else {
                System.out.println("Pas de solution");
                break;
            }
        }

    }
}
