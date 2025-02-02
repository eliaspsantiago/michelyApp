package michelyApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class jogador {
    private static final ArrayList<Set<Integer>> conjuntosDeNumeros = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gerador de Números Aleatórios");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Escolha uma opção:");
        userLabel.setBounds(10, 20, 200, 25);
        panel.add(userLabel);

        String[] options = {"3 ímpares e 3 pares", "4 ímpares e 2 pares", "6 números aleatórios", "5 ímpares e 1 par", "5 pares e 1 ímpar"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setBounds(10, 50, 200, 25);
        panel.add(comboBox);

        JLabel excludeLabel = new JLabel("Números a excluir (separados por vírgula):");
        excludeLabel.setBounds(10, 80, 300, 25);
        panel.add(excludeLabel);

        JTextField excludeField = new JTextField();
        excludeField.setBounds(10, 110, 200, 25);
        panel.add(excludeField);

        JLabel compareLabel = new JLabel("Números a comparar (separados por vírgula):");
        compareLabel.setBounds(10, 140, 300, 25);
        panel.add(compareLabel);

        JTextField compareField = new JTextField();
        compareField.setBounds(10, 170, 200, 25);
        panel.add(compareField);

        JButton generateButton = new JButton("Gerar Números");
        generateButton.setBounds(10, 200, 150, 25);
        panel.add(generateButton);

        JButton printButton = new JButton("Imprimir Números");
        printButton.setBounds(170, 200, 150, 25);
        panel.add(printButton);

        JButton clearButton = new JButton("Limpar Números");
        clearButton.setBounds(10, 230, 150, 25);
        panel.add(clearButton);

        JTextArea resultArea = new JTextArea();
        resultArea.setBounds(10, 260, 460, 280);
        resultArea.setEditable(false);
        panel.add(resultArea);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcao = comboBox.getSelectedIndex() + 1;
                String excludeText = excludeField.getText();
                Set<Integer> numerosExcluidos = parseExcludedNumbers(excludeText);

                Set<Integer> numerosEscolhidos = escolherNumeros(opcao, numerosExcluidos);
                if (numerosEscolhidos != null) {
                    conjuntosDeNumeros.add(numerosEscolhidos);
                }

                String compareText = compareField.getText();
                Set<Integer> numerosComparar = parseExcludedNumbers(compareText);

                atualizarAreaDeResultado(resultArea, numerosComparar);
            }
        });

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(new Printable() {
                    @Override
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        if (pageIndex > 0) {
                            return NO_SUCH_PAGE;
                        }
                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        int y = 20;
                        for (Set<Integer> conjunto : conjuntosDeNumeros) {
                            g2d.drawString(conjunto.toString(), 10, y);
                            y += 20;
                        }
                        return PAGE_EXISTS;
                    }
                });
                boolean doPrint = job.printDialog();
                if (doPrint) {
                    try {
                        job.print();
                    } catch (PrinterException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "Deseja limpar as dezenas sorteadas?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    conjuntosDeNumeros.clear();
                    resultArea.setText("");
                }
            }
        });
    }

    private static Set<Integer> parseExcludedNumbers(String excludeText) {
        Set<Integer> numerosExcluidos = new HashSet<>();
        if (!excludeText.isEmpty()) {
            String[] parts = excludeText.split(",");
            for (String part : parts) {
                try {
                    int num = Integer.parseInt(part.trim());
                    if (num >= 1 && num <= 60) {
                        numerosExcluidos.add(num);
                    }
                } catch (NumberFormatException e) {
                    // Ignorar entradas inválidas
                }
            }
        }
        return numerosExcluidos;
    }

    private static void atualizarAreaDeResultado(JTextArea resultArea, Set<Integer> numerosComparar) {
        StringBuilder resultado = new StringBuilder("Conjuntos de números gerados:\n");
        for (Set<Integer> conjunto : conjuntosDeNumeros) {
            int matches = 0;
            for (Integer num : conjunto) {
                if (numerosComparar.contains(num)) {
                    resultado.append("**").append(num).append("**").append(" ");
                    matches++;
                } else {
                    resultado.append(num).append(" ");
                }
            }
            resultado.append("\n");

            switch (matches) {
                case 3 -> resultado.append("Você fez um terno!\n");
                case 4 -> resultado.append("Você fez uma quadra!\n");
                case 5 -> resultado.append("Você fez uma quina!\n");
                case 6 -> resultado.append("Parabéns, você fez a sena!\n");
            }
        }

        resultArea.setText(resultado.toString());
    }

    public static Set<Integer> escolherNumeros(int opcao, Set<Integer> numerosExcluidos) {
        Set<Integer> numeros = new HashSet<>();
        Random random = new Random();

        switch (opcao) {
            case 1 -> {
                while (numeros.size() < 3) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 != 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
                while (numeros.size() < 6) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 == 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
            }
            case 2 -> {
                while (numeros.size() < 4) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 != 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
                while (numeros.size() < 6) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 == 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
            }
            case 3 -> {
                while (numeros.size() < 6) {
                    int num = random.nextInt(60) + 1;
                    if (!numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
            }
            case 4 -> {
                while (numeros.size() < 5) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 != 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
                while (numeros.size() < 6) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 == 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
            }
            case 5 -> {
                while (numeros.size() < 5) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 == 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
                while (numeros.size() < 6) {
                    int num = random.nextInt(60) + 1;
                    if (num % 2 != 0 && !numerosExcluidos.contains(num)) {
                        numeros.add(num);
                    }
                }
            }
        }

        return numeros;
    }
}