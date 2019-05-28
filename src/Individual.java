import java.util.ArrayList;
import java.util.Arrays;

public class Individual {
    private int[] alphabet = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    public ArrayList<Integer> dna;
    private int[][] start_condition;
    public int age = 0;
    public int fitness = 144;

    /*
     * Erzeuge ein Individuum zufällig oder nach bestimmter DNA, danach berechne Fitnees.
     * @param st: ursprüngliches Rätsel
     * @param grid: DNA der Eltern
     */
    Individual(int[][] st, ArrayList<Integer> grid) {
        start_condition = st;
        if (grid == null) {
            dna = extractDNA(st);
            dna = randomGenreate(st);
        } else {
            dna = grid;
        }
        calculateFitness();
    }

    /*
     * Erzeuge ein Individuum nach DNA des Elter.
     * @param individual: Elter
     */
    Individual(Individual individual) {
        dna = individual.dna;
        start_condition = individual.start_condition;
        age = individual.age + 1;
        calculateFitness();
    }

    /*
     * Dies ist, wo genetische Operationen stattfinden.
     * @param mate: Das andere Individuum
     * @param mutate_ratio: Mutationswahrscheinlichkeit
     */
    public Individual[] pairing(Individual mate, float mutate_ratio) {
        ArrayList<Integer> p1_dna = this.dna;
        ArrayList<Integer> p2_dna = mate.dna;
        // Erzeuge zwei zufällige Positionen
        int start_position = (int) (Math.random() * p1_dna.size());
        int end_position = (int) (Math.random() * p1_dna.size());
        // Falls die Anfangsposition größer als die Endeposition ist
        if (start_position > end_position) {
            int temp = start_position;
            start_position = end_position;
            end_position = temp;
        }
        // Korrigiere die Positionen, um Rekombination zwischen Blöcke zu garantieren
        int dna_index = -1;
        int block_index = 0;
        int[] start_indexes = new int[9];
        int[] end_indexes = new int[9];
        for (int i = 0; i < 9; i = i +3) {
            for (int j = 0; j < 9; j = j +3) {
                int st = dna_index + 1;
                for (int k = i; k < i + 3; k++)
                    for (int l = j; l < j + 3; l++)
                        if (start_condition[k][l] == 0)
                            dna_index++;
                int en = dna_index;
                start_indexes[block_index] = st;
                end_indexes[block_index] = en;
                if (st <= start_position && en >= start_position)
                    start_position = st;
                if (st <= end_position && en >= end_position)
                    end_position = en;
                block_index++;
            }
        }
        // Finde die Rekombination statt
        ArrayList<Integer> child1_dna = new ArrayList<>();
        ArrayList<Integer> child2_dna = new ArrayList<>();
        block_index = 0;
        for (int i = 0; i < p1_dna.size(); i++) {
            if (block_index >= start_position && block_index <= end_position) {
                child1_dna.add(p2_dna.get(i));
                child2_dna.add(p1_dna.get(i));
            } else {
                child1_dna.add(p1_dna.get(i));
                child2_dna.add(p2_dna.get(i));
            }
            block_index++;
        }
        // Wende die Mutationsoperation an
        if (Math.random() < mutate_ratio) {
            int mutation_index = (int) (Math.random() * 9);
            child1_dna = mutate(child1_dna, start_indexes[mutation_index], end_indexes[mutation_index]);
            mutation_index = (int) (Math.random() * 9);
            child2_dna = mutate(child2_dna, start_indexes[mutation_index], end_indexes[mutation_index]);
        }
        checkLegal(child1_dna);
        checkLegal(child2_dna);
        // Rückgabe
        return new Individual[]{new Individual(start_condition, child1_dna), new Individual(start_condition, child2_dna)};
    }

    /*
     * Wende Mutation an
     * @param dna: DNA
     * @param st: Anfangsopsition
     * @param en: Endposition
     */
    private ArrayList<Integer> mutate(ArrayList<Integer> dna, int st, int en){

        int pos1 = (int) (Math.random() * (en - st + 1) + st);
        int pos2 = (int) (Math.random() * (en - st + 1) + st);
        int gen1 = dna.get(pos1);
        int gen2 = dna.get(pos2);
        dna.remove(pos1);
        dna.add(pos1, gen2);
        dna.remove(pos2);
        dna.add(pos2, gen1);

        return dna;
    }

    /*
     * Erzeuge Individuum zufallig.
     * @param start_condition: ursprüngliches Rätsel
     */
    private ArrayList<Integer> randomGenreate(int[][] start_condition) {
        int[][] chromosome = new int[9][9];
        for (int x = 0; x < 9; x = x + 3) {
            for (int y = 0; y < 9; y = y + 3) {
                // Speichere die verbleibend Elemente in aktuellem Block
                ArrayList<Integer> left_elem1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                for (int i = x; i < x + 3; i++) {
                    for (int j = y; j < y + 3; j++) {
                        if (start_condition[i][j] != 0) {
                            left_elem1.remove(new Integer(start_condition[i][j]));
                        }
                    }
                }
                // Fülle die leeren Felder zufällig aus
                for (int i = x; i < x + 3; i++) {
                    for (int j = y; j < y + 3; j++) {
                        if (start_condition[i][j] == 0) {
                            ArrayList<Integer> left_elem2 = new ArrayList<>(left_elem1);
                            ArrayList<Integer> left_elem3 = new ArrayList<>(left_elem1);
                            for (int k = 0; k < 9; k++) {
//                                System.out.println(start_condition[i][k]);
                                left_elem2.remove(new Integer(start_condition[i][k]));
                                left_elem3.remove(new Integer(start_condition[i][k]));
                                left_elem3.remove(new Integer(start_condition[k][j]));
                            }
                            int elem_index = (int) (Math.random() * left_elem1.size());
                            int elem = left_elem1.get(elem_index);
                            if (left_elem3.size() > 0) {
                                elem_index = (int) (Math.random() * left_elem3.size());
                                elem = left_elem3.get(elem_index);
                            } else if (left_elem2.size() > 0) {
                                elem_index = (int) (Math.random() * left_elem2.size());
                                elem = left_elem2.get(elem_index);
                            }
                            chromosome[i][j] = elem;
                            left_elem1.remove(new Integer(elem));
                        } else {
                            chromosome[i][j] = start_condition[i][j];
                        }
                    }
                }
            }
        }
        return extractDNA(chromosome);
    }

    /*
     * Berechne Fitness
     */
    private void calculateFitness() {
        for (int j = 0; j < 9; j++) {
            int[] element_count_in_row = new int[9];
            int[] element_count_in_col = new int[9];
            int[][] chromosome = embedDNA(dna);
            for (int k = 0; k < 9; k++) {
                // Falls eine Ziffer mehr als ein mal vorkommt, verringert sich die Fitness um 1.
                // Prüfe die Ziffern in Zeile
                if (element_count_in_row[chromosome[j][k] - 1] == 0) {
                    element_count_in_row[chromosome[j][k] - 1] = 1;
                } else {
                    fitness -= 1;
                }
                // Prüfe die Ziffern in Spalte
                if (element_count_in_col[chromosome[k][j] - 1] == 0) {
                    element_count_in_col[chromosome[k][j] - 1] = 1;
                } else {
                    fitness -= 1;
                }
            }
        }
        // Verringere sich die Fitness um Alter.
        fitness -= age;
    }

    /*
     * Extrahiere DNA
     * @param chromosome: von DNA eingebettete Rätsel
     */
    private ArrayList<Integer> extractDNA(int[][] chromosome) {
        ArrayList<Integer> dna_serie = new ArrayList<>();
        for (int i = 0; i < 9; i = i + 3) {
            for (int j = 0; j < 9; j = j + 3) {
                for (int k = i; k < i + 3; k++) {
                    for (int l = j; l < j + 3; l++) {
                        if (start_condition[k][l] == 0) {
                            dna_serie.add(chromosome[k][l]);
                        }
                    }
                }
            }
        }
        return dna_serie;
    }

    /*
     * Bette DNA im Rätsel ein
     * @param dna: dna vom Individuum
     */
    public int[][] embedDNA(ArrayList<Integer> dna) {
        int[][] chromosome = new int[9][9];
        int index = 0;
        for (int i = 0; i < 9; i = i + 3) {
            for (int j = 0; j < 9; j = j + 3) {
                for (int k = i; k < i + 3; k++) {
                    for (int l = j; l < j + 3; l++) {
                        if (start_condition[k][l] == 0) {
                            chromosome[k][l] = dna.get(index);
                            index++;
                        } else {
                            chromosome[k][l] = start_condition[k][l];
                        }
                    }
                }
            }
        }
        return chromosome;
    }

    public int[][] embedDNA() {
        return embedDNA(dna);
    }

    /*
     * Drücke DNA in Form von 9x9
     * @param dna: dna vom Individuum
     */
    public void printDNA(ArrayList<Integer> dna) {
        int[][] chromosome = embedDNA(dna);
        System.out.println("DNA: ");
        for (int i = 0; i < 9; i++) {
            System.out.println(Arrays.toString(chromosome[i]));
        }
    }

    public void printDNA() {
        int[][] chromosome = embedDNA(dna);
        for (int i = 0; i < 9; i++) {
            System.out.println(Arrays.toString(chromosome[i]));
        }
    }

    /*
     * Prüfe ob ein Individuum richtig generiert wird.
     * @param dna: dna vom Individuum
     */
    public boolean checkLegal(ArrayList<Integer> dna) {
        int[][] chromosome = embedDNA(dna);
        for (int i = 0; i < 9; i = i + 3) {
            for (int j = 0; j < 9; j = j + 3) {
                ArrayList<Integer> left_elem = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
                for (int k = i; k < i + 3; k++) {
                    for (int l = j; l < j + 3; l++) {
                        left_elem.remove(new Integer(chromosome[k][l]));
                    }
                }
                if (left_elem.size() > 0) {
                    return false;
//                    System.out.println("not Legal: " + i + " : " + j + " elem: " + left_elem.get(0));
//                    printDNA(dna);
                }
            }
        }
        return true;
    }
}
