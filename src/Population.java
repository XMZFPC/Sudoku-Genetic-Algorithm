import java.util.ArrayList;

public class Population {
    private ArrayList<Individual> population = new ArrayList<>();
    private ArrayList<rou> roulette = new ArrayList<>();
    public int fitness_sum;
    private float mutate_ratio;
    private float crossover_ratio;
    private int quantity;
    private int[][] start_condition;
    public int generation_number;
    public int max_fit;
    public int min_fit;
    public Individual bestIn;
    public Individual worstIn;

    /*
     * Erzeuge die erste Population
     * @param st: ursprünglicher Rätsel
     * @param number: Populationsgröße
     * @param cr: Crossoverwahrscheinlichkeit
     * @param mr: Mutationswahrscheinlichkeit
     */
    Population(int[][] st, int number, float cr,float mr) {
        // randomly generate the first generation
        generation_number = 0;
        start_condition = st;
        quantity = number;
        mutate_ratio = mr;
        crossover_ratio = cr;
        for (int i = 0; i < number; i++) {
            population.add(new Individual(start_condition, null));
        }
        recordFitness();
    }

    /*
     * Selektiere ein Individuum mit Hilfe von Glücksradauswahl
     */
    private Individual select() {
        int goal_number = (int) (Math.random() * fitness_sum);
        for (rou r : roulette) {
            if (r.begin <= goal_number && goal_number <= r.end) {
                return r;
            }
        }
        return null;
    }

    /*
     * Datenstruktur für Glücksrad
     */
    private class rou extends Individual {
        private int begin, end;

        rou(Individual i, int b, int e) {
            super(i);
            this.begin = b;
            this.end = e;
        }
    }

    /*
     * Erzeuge ein Glücksrad
     */
    private void recordFitness() {
        fitness_sum = 0;
        max_fit = 0;
        min_fit = 144;
        roulette.clear();
        for (Individual individual : population) {
            if (individual.fitness > max_fit) {
                max_fit = individual.fitness;
                bestIn = individual;
            }
            if (individual.fitness < min_fit) {
                min_fit = individual.fitness;
                worstIn = individual;
            }
            roulette.add(new rou(individual, fitness_sum, fitness_sum + individual.fitness - 1));
            fitness_sum += individual.fitness;
        }
//        bestIn.printDNA();
//        worstIn.printDNA();
    }

    /*
     * Evolution der Population
     * @param age: ob Alter für Individuum verwendet
     */
    public void evolve(Boolean age) {
        generation_number++;
        ArrayList<Individual> next_generation = new ArrayList<>();
        while (next_generation.size() < quantity) {
            Individual parent1 = select();
            Individual parent2 = select();
            Individual[] children;
            if (Math.random() < crossover_ratio) {
                children = parent1.pairing(parent2, mutate_ratio);
                if (age){
                    for (int j = 0; j < 2; j++) {
                        for (int i = 0; i < quantity; i++) {
                            if (children[j].dna.equals(population.get(i).dna)) {
                                children[j].age = population.get(i).age + 1;
                                break;
                            }
                        }
                    }
                }
            } else {
                children = new Individual[]{new Individual(parent1), new Individual(parent2)};
            }
            if (children[0].fitness > 0)
                next_generation.add(children[0]);
            if (children[1].fitness > 0)
                next_generation.add(children[1]);
        }
        next_generation.add(new Individual(bestIn));
        recordFitness();
        population = next_generation;
    }
}