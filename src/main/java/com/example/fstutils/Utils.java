package com.example.fstutils;

import javafx.fxml.FXML;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Utils {


    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename).filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {

        List<List<T>> partitions = new ArrayList<>();
        if (list.isEmpty()) {
            return partitions;
        }

        int length = list.size();
        int numOfPartitions = length / size + ((length % size == 0) ? 0 : 1);

        for (int i = 0; i < numOfPartitions; i++) {
            int from = i * size;
            int to = Math.min((i * size + size), length);
            partitions.add(list.subList(from, to));
        }
        return partitions;
    }
}
