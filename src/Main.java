import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class Main {
    private static int[][] start_condition = new int[9][9];
    private static float mutate_ratio;
    private static float crossover_ratio;
    private static int population_size;
    private static Population population;
    private static Timer t;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sudoku Problem");         // Set the name of the frame
        frame.setSize(1000, 450);                   // Set the width and height of frame
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel() {                             // Create Jpanel to place components
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < 10; i++) {                    // Paint the lines of 9X9 grid
                    g.drawLine(600 + i * 40, 400, 600 + i * 40, 40);
                    g.drawLine(600, 400 - i * 40, 960, 400 - i * 40);
                }
            }
        };
        frame.add(panel);                               // Add the panel
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                placeComponents(panel);
            }
        });
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel popLabel = new JLabel("Populationsgrössße:");                // Create textfield to read population size and its label
        popLabel.setBounds(220, 50, 150, 25);
        panel.add(popLabel);

        JTextField popText = new JTextField();
        popText.setBounds(320, 75, 80, 25);
        popText.setText("2000");
        panel.add(popText);

        JLabel mutateLabel = new JLabel("Mutationswahrscheinlichkeit:");    // Create textfield to read mutation ratio and its label
        mutateLabel.setBounds(220, 100, 200, 25);
        panel.add(mutateLabel);

        JTextField mutateText = new JTextField();
        mutateText.setBounds(320, 125, 80, 25);
        mutateText.setText("0.2");
        panel.add(mutateText);

        JLabel crossLabel = new JLabel("Crossoverwahrscheinlichkeit:");     // Create textfield to read crossover ratio and its label
        crossLabel.setBounds(220, 150, 200, 25);
        panel.add(crossLabel);

        JTextField crossText = new JTextField();
        crossText.setBounds(320, 175, 80, 25);
        crossText.setText("0.8");
        panel.add(crossText);

        JButton startButton = new JButton("start");                 // Create start button
        startButton.setBounds(320, 225, 80, 25);
        panel.add(startButton);

        JButton stopButton = new JButton("stop");                   // Create stop button
        stopButton.setBounds(320, 250, 80, 25);
        panel.add(stopButton);

        JCheckBox age = new JCheckBox("Alter");                     // Create check box for age
        age.setBounds(320,200,160,25);
        age.setSelected(false);
        panel.add(age);

        JLabel gridLabel = new JLabel("Rätseleingabe:");            // Create label
        gridLabel.setBounds(20, 25, 200, 25);
        panel.add(gridLabel);

        JTextField[] inputText = new JTextField[9];                      // Create 9 textfield with initial value to accept puzzle
        String[] init = new String[]{"800624003", "516009000", "004000860", "030900470", "905000602", "048003090", "052000300", "000100785", "100765004"};
        for (int i = 0; i < 9; i++) {
            JLabel xLabel = new JLabel("Zeile " + (i + 1) + ": ");
            xLabel.setBounds(20, 50 + i * 30, 50, 25);
            panel.add(xLabel);

            inputText[i] = new JTextField();
            inputText[i].setBounds(70, 50 + i * 30, 120, 25);
            inputText[i].setText(init[i]);
            panel.add(inputText[i]);
        }

        JLabel bestLabel = new JLabel("Aktuelle beste DNA:");       // Create 9x9 numbers to demonstrate current best Individual
        bestLabel.setBounds(600, 20, 200, 25);
        panel.add(bestLabel);
        JLabel[][] bestDNA = new JLabel[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                bestDNA[i][j] = new JLabel("0");
                bestDNA[i][j].setBounds(610 + j * 40, 47 + i * 40, 25, 25);
                bestDNA[i][j].setFont(new Font(bestDNA[i][j].getFont().getName(), bestDNA[i][j].getFont().getStyle(), 30));
                panel.add(bestDNA[i][j]);
            }
        }

        // Create labels for current information of the population
        JLabel bestIn = new JLabel("best-fit: ");
        bestIn.setBounds(450, 50, 100, 25);
        panel.add(bestIn);
        JLabel bestShow = new JLabel("0");
        bestShow.setBounds(530, 50, 100, 25);
        panel.add(bestShow);
        JLabel worstIn = new JLabel("worst-fit: ");
        worstIn.setBounds(450, 100, 100, 25);
        panel.add(worstIn);
        JLabel worstShow = new JLabel("0");
        worstShow.setBounds(530, 100, 100, 25);
        panel.add(worstShow);
        JLabel avgIn = new JLabel("average-fit: ");
        avgIn.setBounds(450, 150, 100, 25);
        panel.add(avgIn);
        JLabel avgShow = new JLabel("0");
        avgShow.setBounds(530, 150, 100, 25);
        panel.add(avgShow);
        JLabel generation = new JLabel("Generation: ");
        generation.setBounds(450, 200, 100, 25);
        panel.add(generation);
        JLabel generationShow = new JLabel("0");
        generationShow.setBounds(530, 200, 100, 25);
        panel.add(generationShow);

        // Set a timer to evolve the population and update its information (best Individual, current status)
        t = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                population.evolve(age.isSelected());
                int[][] best = population.bestIn.embedDNA();
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        bestDNA[i][j].setText(Integer.toString(best[i][j]));
                    }
                }
                bestShow.setText(Integer.toString(population.max_fit));
                worstShow.setText(Integer.toString(population.min_fit));
                avgShow.setText(Integer.toString(population.fitness_sum/population_size));
                generationShow.setText(Integer.toString(population.generation_number));
                if (population.max_fit == 144) {
                    JOptionPane.showMessageDialog(new JFrame(),"Lösung gefunden!");
                    t.stop();
                }
            }
        });


        // Create an actionListener for start button, if clicked, population will be initialized and start the timer
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 9; i++) {
                    String input = inputText[i].getText();
                    if (input.length() != 9) {
                        JOptionPane.showMessageDialog(new JFrame(),"Zeile " + i + " ist nicht 9-stellig ");
                        return;
                    }
                    for (int j = 0; j < 9; j++) {
                        bestDNA[i][j].setForeground(Color.black);
                        start_condition[i][j] = Character.getNumericValue(input.charAt(j));
                        if (start_condition[i][j] != 0) {
                            bestDNA[i][j].setForeground(Color.red);
                        }
                    }
                }
                mutate_ratio = Float.parseFloat(mutateText.getText());
                crossover_ratio = Float.parseFloat(crossText.getText());
                population_size = Integer.parseInt(popText.getText());
                population = new Population(start_condition, population_size, crossover_ratio, mutate_ratio);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        bestDNA[i][j].setText(Integer.toString(start_condition[i][j]));
                    }
                }
                t.start();
            }
        });

        // Create an actionListener for stop button, to stop evolving
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t.stop();
            }
        });
    }
}