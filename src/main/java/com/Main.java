package com;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.time.LocalDate;

class Book {
    private String title;
    private String author;
    private String genre;
    private int year;
    private boolean available;

    public Book(String title, String author, String genre, int year, boolean available) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.available = available;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getYear() { return year; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("\"%s\" by %s (%d) [%s] - %s",
                title, author, year, genre, available ? "Available" : "Checked out");
    }
}

public class LibraryBookSearchSystem {

    private List<Book> catalog;

    public LibraryBookSearchSystem() {
        catalog = new ArrayList<>();
        loadCatalog();
    }

