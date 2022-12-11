package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toCollection;

public class Main {
    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    record Monkey(ArrayList<BigDecimal> items, Function<BigDecimal, BigDecimal> operation, Predicate<BigDecimal> test, int trueMonkey, int falseMonkey) {
    }

    final String path = "TODO";
    List<Monkey> monkeys = new ArrayList<>();
    List<Long> inspectedItems = new ArrayList<>();

    void run() throws IOException {
        this.readMonkeys();

        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < this.monkeys.size(); j++) {
                Monkey monkey = this.monkeys.get(j);
                int inspectedItemsOfMonkey = monkey.items.size();
                this.inspectedItems.set(j, this.inspectedItems.get(j) + inspectedItemsOfMonkey);
                this.doMoves(monkey);
            }
            System.out.printf("%s Round\n", i);
            // printMonkeys();
        }

        for (int i = 0; i < this.inspectedItems.size(); i++) {
            System.out.printf("Monkey %s: %s\n", i, this.inspectedItems.get(i));
        }
    }

    void doMoves(Monkey monkey) {
        while (monkey.items.size() != 0) {
            BigDecimal item = monkey.items.get(0);
            monkey.items.remove(0);
            item = monkey.operation.apply(item);
            // Task 1
            // BigDecimal input = BigDecimal.valueOf(item);
            // input = input.divide(BigDecimal.valueOf(3), 0, RoundingMode.DOWN);
            // item = input.longValue();

            // Task 2 (keep value small)
            // % (3 * 13 *  19 * 17 * 5 * 7 * 11 * 2)
            // % 9699690
            item = item.remainder(new BigDecimal(9699690));

            if (monkey.test.test(item)) {
                this.monkeys.get(monkey.trueMonkey).items.add(item);
            } else {
                this.monkeys.get(monkey.falseMonkey).items.add(item);
            }
        }
    }

    void readMonkeys() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(this.path + "input.txt"))) {

            String line;

            ArrayList<BigDecimal> items = null;
            Function<BigDecimal, BigDecimal> operation = null;
            Predicate<BigDecimal> test = null;
            int trueMonkey = 0;
            int falseMonkey = 0;
            while ((line = br.readLine()) != null) {

                if ("".equals(line)) {
                    this.monkeys.add(new Monkey(items, operation, test, trueMonkey, falseMonkey));
                    this.inspectedItems.add(0L);
                    continue;
                }

                if (line.contains("Starting items")) {
                    String strItems = line.split("  Starting items: ")[1];
                    items = Arrays.stream(strItems.split(", ")).map(v -> BigDecimal.valueOf(Long.parseLong(v))).collect(toCollection(ArrayList::new));
                    continue;
                }

                if (line.contains("Operation")) {
                    String operationTerm = line.split("Operation: new = old ")[1];
                    String[] parts = operationTerm.split(" ");
                    String op = parts[0];

                    operation = switch (op) {
                        case "+" -> (old) -> old.add((parts[1].equals("old") ? old : BigDecimal.valueOf(Integer.parseInt(parts[1]))));
                        case "-" -> (old) -> old.subtract(parts[1].equals("old") ? old : BigDecimal.valueOf(Integer.parseInt(parts[1])));
                        case "*" -> (old) -> old.multiply(parts[1].equals("old") ? old : BigDecimal.valueOf(Integer.parseInt(parts[1])));
                        default -> throw new UnsupportedOperationException();
                    };
                    continue;
                }

                if (line.contains("Test")) {
                    int value = Integer.parseInt(line.split("  Test: divisible by ")[1]);
                    test = (v) -> v.remainder(new BigDecimal(value)).compareTo(BigDecimal.ZERO) == 0;
                    continue;
                }

                if (line.contains("true")) {
                    trueMonkey = Integer.parseInt(line.split("    If true: throw to monkey ")[1]);
                    continue;
                }

                if (line.contains("false")) {
                    falseMonkey = Integer.parseInt(line.split("    If false: throw to monkey ")[1]);
                    continue;
                }
            }
        }
    }

    void printMonkeys() {
        for (int i = 0; i < this.monkeys.size(); i++) {
            Monkey monkey = this.monkeys.get(i);
            System.out.printf("Monkey %s: %s\n", i, Arrays.toString(monkey.items.toArray()));
        }
        System.out.println("-".repeat(30));
    }
}