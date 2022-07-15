package com.github.bael;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSearch {
    public static void main(String[] args) {
        Path start = Paths.get("/home/dk/Downloads/basedir");

        Pair result = traverse(start.toFile(), 0);
        System.out.println(result.name + " -> " + result.deep);


    }

    private static class Pair {
        int deep;
        String name;

        public Pair(int deep, String name) {
            this.deep = deep;
            this.name = name;
        }
    }
    private static Pair traverse(File file, int deep) {
        if(file.listFiles() == null) {
            return new Pair(deep, file.getName());
        }

        int maxDeep = deep;
        String maxName = "";
        for(File child : file.listFiles()) {
            Pair tmp = traverse(child, deep + 1);
            if (maxDeep < tmp.deep) {
                maxName = tmp.name;
                maxDeep = tmp.deep;
            }
        }
        return new Pair(maxDeep, maxName);
    }
}
