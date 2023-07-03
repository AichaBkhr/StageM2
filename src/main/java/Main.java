import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.chocosolver.solver.search.strategy.selectors.values.*;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import static org.chocosolver.solver.search.strategy.Search.intVarSearch;
public class Main{



    // -------------------------- Début partie raisonnement qualitatif -------------------------------------------
    /**
     *
     * @param r1 Rectangle 1
     * @param r2 Rectangle 2
     * @return true if r1 equals r2
     */
    public static Boolean isEqual(Rectangle r1, Rectangle r2){
        int x1 = r1.getX();
        int y1 = r1.getY();
        int width1 = r1.getWidth();
        int height1 = r1.getHeight();

        int x2 = r2.getX();
        int y2 = r2.getY();
        int width2 = r2.getWidth();
        int height2 = r2.getHeight();

        return x1 == x2 && y1 == y2 && width1 == width2 && height1 == height2;
    }

    /**
     *
     * @param r1 Rectangle 1
     * @param r2 Rectangle 2
     * @return true if r1 and r2 are disconnected
     */
    public static Boolean isDC(Rectangle r1 , Rectangle r2){
        int x1 = r1.getX();
        int y1 = r1.getY();
        int width1 = r1.getWidth();
        int height1 = r1.getHeight();

        int x2 = r2.getX();
        int y2 = r2.getY();
        int width2 = r2.getWidth();
        int height2 = r2.getHeight();

        return x1 > (x2 + width2) || (x1 + width1) < x2 || y1 > (y2 + height2) || (y1 + height1) < y2;
    }

    /**
     *
     * @param r1 Rectangle 1
     * @param r2 Rectangle 2
     * @return true if r1 and r2 are externally connected
     */
    public static Boolean isEC(Rectangle r1 , Rectangle r2){
        int x1 = r1.getX();
        int y1 = r1.getY();
        int width1 = r1.getWidth();
        int height1 = r1.getHeight();

        int x2 = r2.getX();
        int y2 = r2.getY();
        int width2 = r2.getWidth();
        int height2 = r2.getHeight();

        return x1 + width1 == x2 || x2 + width2 == x1 || y1 + height1 == y2 || y2 + height2 == y1;
    }

    /**
     *
     * @param r1 Rectangle 1
     * @param r2 Rectangle 2
     * @return true if r1 is tangent proper part of r2
     */
    public static Boolean isTPP(Rectangle r1 , Rectangle r2){
        int x1 = r1.getX();
        int y1 = r1.getY();
        int width1 = r1.getWidth();
        int height1 = r1.getHeight();

        int x2 = r2.getX();
        int y2 = r2.getY();
        int width2 = r2.getWidth();
        int height2 = r2.getHeight();

        return ((x2 > x1 && x2 + width2 == x1 + width1) || (x2 == x1 && x2 + width2 < x1 + width1))
                && ((y2 == y1 && y2 + height2 < y1) || (y2 > y1 && y2 + height2 == y1 + height1));
    }

    /**
     *
     * @param r1 Rectangle 1
     * @param r2 Rectangle 2
     * @return true if r1 is non-tangent proper part of r2
     */
    public static Boolean isNTPP(Rectangle r1 , Rectangle r2){
        int x1 = r1.getX();
        int y1 = r1.getY();
        int width1 = r1.getWidth();
        int height1 = r1.getHeight();

        int x2 = r2.getX();
        int y2 = r2.getY();
        int width2 = r2.getWidth();
        int height2 = r2.getHeight();

        return (x2 > x1 && x2 + width2 < x1 + width1) && (y2 > y1 && y2 + height2 < y1 + height1);
    }

    /**
     *
     * @param r1 Rectangle 1
     * @param r2 Rectangle 2
     * @param nameR1 nom du rectangle 1
     * @param nameR2 nom du rectangle 2
     * @return la relation de bas de RCC8 entre r1 et r2.
     *          Remarque : r1 {TPP} r2 <=> r2 {TPP^-1} r1   &   r1 {NTPP} r2 <=> r2 {NTPP^-1} r1
     *          Si aucune des relation définits par les methodes vu au dessus alors reutn {PO} c'est-à-dire r1 chevauche partiellemnt r2
     */
    public static String definirRelation (Rectangle r1 , Rectangle r2, String nameR1 , String nameR2){
        if (isEqual(r1,r2)) return nameR1 + " {EQ} " + nameR2;
        else if (isEC(r1, r2)) return nameR1 + " {EC} " + nameR2;
        else if (isDC(r1, r2)) return nameR1 + " {DC} " + nameR2;
        else if (isTPP(r1, r2)) return nameR1 + " {TPP} " + nameR2;
        else if (isTPP(r2,r1)) return nameR1 + " {TPP^-1} " + nameR2;
        else if (isNTPP(r1, r2)) return nameR1 + " {NTPP} " + nameR2;
        else if (isNTPP(r2,r1)) return nameR1 + " {NTPP^-1} " + nameR2;
        else  return nameR1 + " {PO} " + nameR2;
    }

    // -------------------------- Fin partie raisonnement qualitatif -------------------------------------------


    public static void main(String[] args) {



        // lecture des données du fichier texte en question
        File fichier = new File("Instance_6.txt");


        List<List <Integer>> hauteurPossible = new ArrayList<>();
        List<List <Integer>> largeurPossible = new ArrayList<>();
        List<Integer> nombreElement = new ArrayList<>();
        List<String> couleurFill = new ArrayList<>();
        List<String> couleurStroke = new ArrayList<>();
        List<String> nomElement = new ArrayList<>();
        int nombreElemntTotal = 0; //la somme des nombreElement
        List<Integer> surfaceTotal = new ArrayList<>();
        List<List<String>> varConstraintDist = new ArrayList<>();
        List<List<String>> varConstraintZonage = new ArrayList<>();
        List<String> op = new ArrayList<>();
        List<Integer> borne = new ArrayList<>();
        int nbDistConstraint = 0;
        int nbZoneConstraint=0;
        List<List<List<Integer>>> coordTypeZone = new ArrayList<>();



        try {
            Scanner scanner = new Scanner(fichier);

            while (scanner.hasNextLine()  ) {
                String paramsElement = scanner.nextLine();
                paramsElement = paramsElement.replaceAll("\\s", "");
                String[] argElement = paramsElement.split(";");

                    if (argElement[0].equals("Contraintes:")) {
                        break;
                    }
                if(argElement.length == 6) {

                    // Nom Element
                    if (argElement[0].length() >= 1) {
                        nomElement.add(argElement[0]);
                    } else {
                        throw new IllegalArgumentException("Erreur: Nom Element  manquant");
                    }

                    // Couleur Element
                    if (argElement[1].length() >= 1) {
                        couleurFill.add(argElement[1]);
                    } else {
                        throw new IllegalArgumentException("Erreur: Couleur de remplissage du type " + argElement[0] + " manquante");
                    }
                    if (argElement[2].length() >= 1) {
                        couleurStroke.add(argElement[2]);
                    } else {
                        throw new IllegalArgumentException("Erreur: Couleur de bordure du type " + argElement[0] + " manquante");
                    }

                    // Nombre Element
                    if (argElement[3].length() >= 1) {
                        if (argElement[3].matches("[0-9]+")) {
                            int n = Integer.parseInt(argElement[3]);
                            nombreElement.add(n);
                            nombreElemntTotal += n;
                        } else {
                            throw new IllegalArgumentException("Erreur: Nombre de copie du type " + argElement[0] + "doit être un entier");
                        }
                    } else {
                        throw new IllegalArgumentException("Erreur: Nombre de copie du type " + argElement[0] + "manquant");
                    }

                    // Surface totale
                    if (argElement[4].length() >=1) {
                        surfaceTotal.add(Integer.parseInt(argElement[4]));
                    } else {
                        throw new IllegalArgumentException("Erreur: Surface totale du type " + argElement[0] + "manquante");
                    }


                    //Les largeurs et hauteurs possibles
                    if (argElement[5].length()>=1) {
                        List<Integer> larg = new ArrayList<>();
                        List<Integer> haut = new ArrayList<>();
                        String arg7 = argElement[5].substring(1, argElement[5].length() - 1);
                        String[] p = arg7.split("&"); // je sépare les p= les (.,.)
                        for (String value : p) {
                            value = value.substring(1, value.length() - 1);
                            String[] vals = value.split(",");
                            for (int i = 0; i < vals.length / 2; i++) {
                                if (vals[0].matches("[0-9]+") && vals[1].matches("[0-9]+")) {
                                    larg.add(Integer.parseInt(vals[0]));
                                    haut.add(Integer.parseInt(vals[1]));
                                } else {
                                    throw new IllegalArgumentException("Erreur: les couples possibles de largeur-hauteur doivent obligatoirement contenir des entiers");
                                }
                            }

                        }
                        hauteurPossible.add(haut);
                        largeurPossible.add(larg);
                    }else {
                        throw new IllegalArgumentException("Erreur: Couples (largeur,hauteur) possibles manquantes du type " + argElement[0] );

                    }

                }else {
                    throw new IllegalArgumentException("Erreur: Obligation d'avoir 6 données pour définir un type d'élément et ses caractéristiques");
                }

            }

            // ----------------------------------Lecture des données des contraintes  ---------------------------
            while (scanner.hasNextLine() ) {


                String paramsConstraint = scanner.nextLine();
                paramsConstraint = paramsConstraint.replaceAll("\\s", "");
                String[] argConstraint = paramsConstraint.split(";");

                if (argConstraint[0].equals("dist")) {
                    nbDistConstraint += 1;

                    if(argConstraint.length == 4 ) {
                        String[] p = argConstraint[1].substring(1, argConstraint[1].length() - 1).split(",");
                        List<String> varName = new ArrayList<>(Arrays.asList(p));
                        //pour chaque contrainte je rajoute une liste de ses portés
                        varConstraintDist.add(varName);


                        op.add(argConstraint[2]);
                        borne.add(Integer.parseInt(argConstraint[3]));
                    } else {
                        throw new IllegalArgumentException("Erreur: Obligation d'avoir 4 données pour définir la contrainte de distance");

                    }
                }
                if(argConstraint[0].equals("zonage")) {
                    nbZoneConstraint++;
                    if (argConstraint.length >= 3) {
                        String[] p1 = argConstraint[1].substring(1, argConstraint[1].length() - 1).split(",");
                        List<String> varName = new ArrayList<>(Arrays.asList(p1));
                        //pour chaque contrainte je rajoute une liste de ses portés
                        varConstraintZonage.add(varName);
                        //System.out.println(varConstraintZonage);

                        String[] p = argConstraint[2].substring(1, argConstraint[2].length() - 1).split("&");
                        //(.;.;.;.)
                        List<String> coord = new ArrayList<>(Arrays.asList(p));
                        List<List<Integer>> listCoordInt = new ArrayList<>();
                        for (String s : coord) {
                            String[] m = s.substring(1, s.length() - 1).split(",");
                            List<Integer> coordInt = new ArrayList<>();
                            for (String value : m) {
                                coordInt.add(Integer.parseInt(value));
                            }
                            listCoordInt.add(coordInt);
                        }
                        coordTypeZone.add(listCoordInt);
                    } else {
                        throw new IllegalArgumentException("Erreur: Obligation d'avoir 3 données pour définir la contrainte de zonage");
                    }
                }
            }
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
        int largeurParcelle = largeurPossible.get(nombreType-1).get(0);
        int hauteurParcelle = hauteurPossible.get(nombreType-1).get(0);

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

        //éviter les répétitions
        int[][] longueurDistinctes = new int [nombreType-1][];
        for(int e=0; e<nombreType-1; e++) {
            longueurDistinctes[e] = Arrays.stream(longueurPossibleTotale[e]).distinct().toArray();
        }


        // Définition des domaines et noms de chaque variable
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

        // Définir des dictionnaire pour faciliter l'usaage des données:

            // Dictionnaire <nom du type, indice de début des variables de ce type>
        Map<String,Integer> ElementIndices = new HashMap<>();
        int indice=0;
        for(int i=0; i<nombreType-1;i++){
            ElementIndices.put(nomElement.get(i),indice);
            indice+=nombreElement.get(i);
        }
            // Dictionnaire <nom du type; nombre de copies de ce type>
        Map<String,Integer> ElementNbElem = new HashMap<>();
        for(int i=0; i<nombreType-1;i++){
            ElementNbElem.put(nomElement.get(i),nombreElement.get(i));
        }

        //Définition des contraintes

        //Contrainte 1 : interdir le chevauchement

        model.diffN(X, Y, width, height, false).post();



        //Cotrainte 2 : Placer les éléments dans la parcelle et non pas en dehors de la prcelle

        for (int i = 0; i < nombreElemntTotal-1; i++) {
            model.arithm(X[i], "+", width[i], "<=", largeurParcelle).post();
            model.arithm(Y[i], "+", height[i], "<=", hauteurParcelle).post();
        }


        //Contrainte 3 : Orientation et choix de forme

        // créer des tuples pour chaque type à partir des couples autorisés
        int index =0;
        for(int element=0; element<nombreType-1; element++){

            //créer les tuples
            Tuples tuplesVals = new Tuples(true);
            for(int i=0; i<largeurPossible.get(element).size(); i++){
                tuplesVals.add(largeurPossible.get(element).get(i), hauteurPossible.get(element).get(i));

            }
            //contrainte table : la séquence de variables {width[i],height[i]} doit appartenir à la liste des tuples pour chaque élément i.
            for(int i=index; i<index+nombreElement.get(element); i++){
                model.table(new IntVar[] {width[i],height[i]}, tuplesVals).post();
            }
            index += nombreElement.get(element);
        }





        //Contraintes 4 : s'assurer que les dimensions demandées sont bien respectées

        int cmptr =0;
        for (int i = 0; i < nombreType-1; i++) {
            if(nombreElement.get(i) > 1) {
                IntVar[] dimensionVar = new IntVar[nombreElement.get(i)];
                // pour chaque type d'élément je calcule la dimension de chaque rectangle qui le représente. Je stock cela dans une liste de variables
                for (int k = 0; k < nombreElement.get(i); k++) {
                    dimensionVar[k] = model.intVar("aireDuRect", 0, largeurParcelle * hauteurParcelle);
                    model.times(width[cmptr], height[cmptr], dimensionVar[k]).post();
                    cmptr++;
                }
                model.sum(dimensionVar, "=", surfaceTotal.get(i)).post();
            }
            else{
                model.times(width[cmptr], height[cmptr], surfaceTotal.get(i)).post();
                cmptr++;
            }


        }

        //Contrainte 5 : calcul de la distance entre plusieurs réctangles
        int c = 0;
        while (c<nbDistConstraint) {
            //Distanciation entre les éléments du même type
            if (varConstraintDist.get(c).get(0).equals(varConstraintDist.get(c).get(1))) {
                ArrayList<IntVar> ListVarX = new ArrayList<>();
                ArrayList<IntVar> ListVarY = new ArrayList<>();
                String nomVar = varConstraintDist.get(c).get(0);
                int indiceVar = ElementIndices.get(nomVar);
                int nbVar = ElementNbElem.get(nomVar);
                for (int i = indiceVar; i < indiceVar + nbVar; i++) {
                    ListVarX.add(X[i]);
                    ListVarY.add(Y[i]);
                }

                IntVar[] varX = ListVarX.toArray(new IntVar[0]);
                IntVar[] varY = ListVarY.toArray(new IntVar[0]);


                //contrainte dist :
                for (int i = 0; i < varX.length - 1; i++) {

                    for (int j = i + 1; j < varY.length; j++) {
                        IntVar dx = model.intVar("dx", 0, Math.max(largeurParcelle, hauteurParcelle));
                        IntVar dy = model.intVar("dy", 0, Math.max(largeurParcelle, hauteurParcelle));
                        model.distance(varX[i], varX[j], "=", dx).post();  //dx=|x2 - x1|
                        model.distance(varY[i], varY[j], "=", dy).post(); //dy=|y2 - y1|
                        int maxVal = Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle);
                        IntVar dx2 = model.intVar("dx2", 0, maxVal);
                        IntVar dy2 = model.intVar("dy2", 0, maxVal);
                        model.times(dx, dx, dx2).post();
                        model.times(dy, dy, dy2).post();
                        int C = borne.get(c);
                        model.arithm(dx2, "+", dy2, op.get(c), C * C).post();
                    }
                }

            } else {
                //Distanciation entre des éléments des types différents
                ArrayList<IntVar> ListVarX_1 = new ArrayList<>();
                ArrayList<IntVar> ListVarY_1 = new ArrayList<>();
                ArrayList<IntVar> ListVarX_2 = new ArrayList<>();
                ArrayList<IntVar> ListVarY_2 = new ArrayList<>();

                String nomVar1 = varConstraintDist.get(c).get(0);
                int indiceVar1 = ElementIndices.get(nomVar1);
                int nbVar1 = ElementNbElem.get(nomVar1);
                for (int i = indiceVar1; i < indiceVar1 + nbVar1; i++) {
                    ListVarX_1.add(X[i]);
                    ListVarY_1.add(Y[i]);
                }

                String nomVar2 = varConstraintDist.get(c).get(1);
                int indiceVar2 = ElementIndices.get(nomVar2);
                int nbVar2 = ElementNbElem.get(nomVar2);
                for (int i = indiceVar2; i < indiceVar2 + nbVar2; i++) {
                    ListVarX_2.add(X[i]);
                    ListVarY_2.add(Y[i]);
                }
                IntVar[] varX_1 = ListVarX_1.toArray(new IntVar[0]);
                IntVar[] varY_1 = ListVarY_1.toArray(new IntVar[0]);
                IntVar[] varX_2 = ListVarX_2.toArray(new IntVar[0]);
                IntVar[] varY_2 = ListVarY_2.toArray(new IntVar[0]);
                for (int i = 0; i < varX_1.length; i++) {
                    for (int j = 0; j < varX_2.length; j++) {
                        IntVar dx = model.intVar("dx", 0, Math.max(largeurParcelle, hauteurParcelle));
                        IntVar dy = model.intVar("dy", 0, Math.max(largeurParcelle, hauteurParcelle));
                        model.distance(varX_1[i], varX_2[j], "=", dx).post();  //dx=|x2 - x1|
                        model.distance(varY_1[i], varY_2[j], "=", dy).post(); //dy=|y2 - y1|
                        IntVar dx2 = model.intVar("dx2", 0, Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle));
                        IntVar dy2 = model.intVar("dy2", 0, Math.max(largeurParcelle * largeurParcelle, hauteurParcelle * hauteurParcelle));
                        model.times(dx, dx, dx2).post();
                        model.times(dy, dy, dy2).post();
                        int C = borne.get(c);
                        model.arithm(dx2, "+", dy2, op.get(c), C * C).post();
                    }
                }
            }
            c++;
        }



        //contrainte 6 : zonage
        //je parcours tout les éléments de la contraint et je dis x de cet element est a+width<=x+width<=a+c (a,b,c,d) et  b+height<=x+height<=b+d
        int nbContrainte =0;
        while(nbContrainte<nbZoneConstraint){ // je parcours les contraintes
            for (int i =0; i<varConstraintZonage.get(nbContrainte).size() ; i++){ // je parcours la portée de la contrainte
                String nomElem = varConstraintZonage.get(nbContrainte).get(i);
                int w = ElementIndices.get(nomElem);// me donne l'indice du débute de la boucle qui suit
                BoolVar[] boolV = model.boolVarArray(coordTypeZone.get(nbContrainte).size());
                int siz = w + ElementNbElem.get(nomElem);
                for (int p = w; p <siz ; p++){ //je parcours les rectangles correspondants
                    for (int j=0; j<coordTypeZone.get(nbContrainte).size(); j++){ //je parcours les zones autorisées pour le réctangle en question
                        int  XZone  = coordTypeZone.get(nbContrainte).get(j).get(0);
                        int YZone = coordTypeZone.get(nbContrainte).get(j).get(1);
                        int WidthZone = coordTypeZone.get(nbContrainte).get(j).get(2);
                        int HeightZone = coordTypeZone.get(nbContrainte).get(j).get(3);
                        boolV[j] = model.and(model.arithm(X[p], ">=", XZone),
                                model.arithm(X[p], "+", width[p], "<=", XZone + WidthZone),
                                model.arithm(Y[p], ">=", YZone),
                                model.arithm(Y[p], "+", height[p], "<=", YZone + HeightZone)).reify();
                    }
                    model.sum(boolV,">=",1).post(); // ou un or
                }
            }
            nbContrainte++;
        }


        // Application de la stratégie 1 : concatener X,Y,height et width. choisir la var non instanciée qui a le plus petit domaine, lui assigner la plus petite valeur de son domaine.
     /* IntVar[] totalVar = new IntVar[(nombreElemntTotal-1)*4];
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
       //Application de la stratégie 2 (Strategy)
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


        // Résolution du modèle
        //model.getSolver().showDecisions();
        model.getSolver().solve();
        model.getSolver().printStatistics();



        // Affichage des résultats en matrice
        for(int i =0; i<nombreElemntTotal-1; i++) {
            System.out.print( X[i] +"\t" + Y[i]  +"\t"+height[i] +"\t" + width[i] +"\n");
        }


        Map<String,Rectangle> nomRec = new HashMap<>();
        for(int i =0; i<nombreElemntTotal-1; i++) {
            //construction du dictionnaire nomVar-Rectangle
            Rectangle rect = new Rectangle(X[i].getValue(),Y[i].getValue(),  width[i].getValue(), height[i].getValue());
            String nameRect = X[i].getName().substring(2);
            nomRec.put(nameRect,rect);

        }


        //traduction de CSP à RCC8
        for (int i=0; i<nombreElemntTotal-2; i++){
            for(int j=i+1; j<nombreElemntTotal-1; j++){
                String nameRect1 = X[i].getName().substring(2);
                String nameRect2 = X[j].getName().substring(2);

                System.out.println(definirRelation(nomRec.get(nameRect1),nomRec.get(nameRect2),nameRect1, nameRect2));
            }
        }

        // Affichage des résultats en SVG
        SVG svgRect = new SVG(1000, 1000);
        int echelle = 20;
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
                        //Rectangle rec = new Rectangle(X[cpt].getValue()*echelle, Y[cpt].getValue()*echelle, width[cpt].getValue()*echelle, height[cpt].getValue()*echelle);

                        newRectangle.setStyle(styleElement);
                        rect.add(newRectangle);
                        cpt++;
                    }
                }

                for(int r=0; r<nombreElemntTotal-1;r++) {
                    svgRect.add(rect.get(r));
                }
                svgRect.saveAsFile("Instance_6.svg");
            } else {
                System.out.println("Pas de solution");
                break;
            }
        }

    }

}
