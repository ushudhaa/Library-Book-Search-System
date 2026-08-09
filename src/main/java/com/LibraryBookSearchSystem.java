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

    private void loadCatalog() {
        catalog.add(new Book("Clean Code", "Robert Martin", "Programming", 2008, true));
        catalog.add(new Book("Effective Java", "Joshua Bloch", "Programming", 2018, false));
        catalog.add(new Book("Dune", "Frank Herbert", "Sci-Fi", 1965, true));
        catalog.add(new Book("Foundation", "Isaac Asimov", "Sci-Fi", 1951, true));
        catalog.add(new Book("1984", "George Orwell", "Dystopian", 1949, false));
        catalog.add(new Book("Brave New World", "Aldous Huxley", "Dystopian", 1932, true));
        catalog.add(new Book("The Pragmatic Programmer", "Andy Hunt", "Programming", 1999, true));
    }
    public Optional<Book> findByTitle(String title) {
        return catalog.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }
    public List<Book> findAvailablePublishedAfter(int year) {
        Predicate<Book> isAvailable = Book::isAvailable;
        Predicate<Book> publishedAfter = b -> b.getYear() > year;

        return catalog.stream()
                .filter(isAvailable.and(publishedAfter))
                .collect(Collectors.toList());
    }
    public List<Book> findByGenreOrAuthor(String genre, String author) {
        Predicate<Book> genreMatch = b -> b.getGenre().equalsIgnoreCase(genre);
        Predicate<Book> authorMatch = b -> b.getAuthor().equalsIgnoreCase(author);

        return catalog.stream()
                .filter(genreMatch.or(authorMatch))
                .collect(Collectors.toList());
    }
    public List<Book> findUnavailableBooks() {
        Predicate<Book> isAvailable = Book::isAvailable;
        return catalog.stream()
                .filter(isAvailable.negate())
                .collect(Collectors.toList());
    }
    public List<Book> oldestBooks(int limit) {
        return catalog.stream()
                .sorted(Comparator.comparingInt(Book::getYear))
                .limit(limit)
                .collect(Collectors.toList());
    }
    public Optional<String> borrowBook(String title) {
        return findByTitle(title)
                .filter(Book::isAvailable)
                .map(book -> {
                    book.setAvailable(false);
                    LocalDate dueDate = LocalDate.now().plusWeeks(2);
                    return "You borrowed \"" + book.getTitle() + "\". Due back on " + dueDate;

                }
