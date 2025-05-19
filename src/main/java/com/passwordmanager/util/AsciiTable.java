package com.passwordmanager.util;

import java.util.ArrayList;
import java.util.List;

public class AsciiTable {
    private final List<String[]> rows;
    private final String[] headers;
    private final int[] columnWidths;

    public AsciiTable(String[] headers) {
        this.headers = headers;
        this.rows = new ArrayList<>();
        this.columnWidths = new int[headers.length];
        
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }
    }

    public void addRow(String... values) {
        rows.add(values);
        
        for (int i = 0; i < values.length; i++) {
            if (values[i].length() > columnWidths[i]) {
                columnWidths[i] = values[i].length();
            }
        }
    }

    public void print() {
        printHorizontalLine();
        printRow(headers);
        printHorizontalLine();

        for (String[] row : rows) {
            printRow(row);
        }
        printHorizontalLine();
    }

    private void printHorizontalLine() {
        StringBuilder sb = new StringBuilder();
        for (int width : columnWidths) {
            sb.append("+" + "-".repeat(width + 2));
        }
        sb.append("+");
        System.out.println(sb.toString());
    }

    private void printRow(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append("| ").append(padRight(values[i], columnWidths[i])).append(" ");
        }
        sb.append("|");
        System.out.println(sb.toString());
    }

    private String padRight(String str, int length) {
        return String.format("%-" + length + "s", str);
    }
}
