package com.github.bael;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final NumberFormat nf = NumberFormat.getInstance(Locale.US);
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        Path p = Paths.get("./src/main/resources/dataset_91069.txt");
        File file = p.toFile();
        Scanner sc = new Scanner(file);
        int count = 0;
        sc.nextLine();


        long maxYear = 0;
        long maxPopulationDiff = 0;

        String line = sc.nextLine();
        long[] prevValues = parseLine(line);

        while (sc.hasNextLine()) {
            line = sc.nextLine();
            long[] currentValues = parseLine(line);

            if (currentValues[1] - prevValues[1] > maxPopulationDiff) {
                maxPopulationDiff =  currentValues[1] - prevValues[1];
                maxYear = currentValues[0];
            }

            prevValues = currentValues;
        }
        System.out.println(maxYear);

    }

    private static long[] parseLine(String line) throws ParseException {
        String[] split = line.split("\\s+");
        long year = Long.parseLong(split[0]);
        long population = nf.parse(split[1]).longValue();
        return new long[] {year, population};
    }


}
