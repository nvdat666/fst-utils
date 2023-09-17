package com.example.fstutils;

import javafx.fxml.FXML;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.Optional;

public class Utils {
  

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }


}
