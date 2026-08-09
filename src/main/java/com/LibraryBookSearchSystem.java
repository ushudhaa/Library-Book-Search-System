package com;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

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

    // Search by exact title, returns Optional
    public Optional<Book> findByTitle(String title) {
        return catalog.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    // Predicate composition: available AND published after a year
    public List<Book> findAvailablePublishedAfter(int year) {
        Predicate<Book> isAvailable = Book::isAvailable;
        Predicate<Book> publishedAfter = b -> b.getYear() > year;

        return catalog.stream()
                .filter(isAvailable.and(publishedAfter))
                .collect(Collectors.toList());
    }

    // Predicate composition: genre match OR author match
    public List<Book> findByGenreOrAuthor(String genre, String author) {
        Predicate<Book> genreMatch = b -> b.getGenre().equalsIgnoreCase(genre);
        Predicate<Book> authorMatch = b -> b.getAuthor().equalsIgnoreCase(author);

        return catalog.stream()
                .filter(genreMatch.or(authorMatch))
                .collect(Collectors.toList());
    }

    // Unavailable books using negate()
    public List<Book> findUnavailableBooks() {
        Predicate<Book> isAvailable = Book::isAvailable;
        return catalog.stream()
                .filter(isAvailable.negate())
                .collect(Collectors.toList());
    }

    // Sorted by year, oldest first, limited results
    public List<Book> oldestBooks(int limit) {
        return catalog.stream()
                .sorted(Comparator.comparingInt(Book::getYear))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Borrow a book: returns Optional with due date message, functional style
    public Optional<String> borrowBook(String title) {
        return findByTitle(title)
                .filter(Book::isAvailable)
                .map(book -> {
                    book.setAvailable(false);
                    LocalDate dueDate = LocalDate.now().plusWeeks(2);
                    return "You borrowed \"" + book.getTitle() + "\". Due back on " + dueDate;
                });
    }

    // Group titles by genre
    public Map<String, List<String>> titlesByGenre() {
        return catalog.stream()
                .collect(Collectors.groupingBy(Book::getGenre,
                        Collectors.mapping(Book::getTitle, Collectors.toList())));
    }

    public static void main(String[] args) {
        LibraryBookSearchSystem library = new LibraryBookSearchSystem();

        System.out.println("=== Search: 'Dune' ===");
        Optional<Book> book = library.findByTitle("Dune");
        System.out.println(book.map(Book::toString).orElse("Not found"));

        System.out.println("\n=== Search: 'Unknown Book' ===");
        Optional<Book> missing = library.findByTitle("Unknown Book");
        System.out.println(missing.map(Book::toString).orElse("Not found in catalog"));

        System.out.println("\n=== Available Books Published After 1960 ===");
        library.findAvailablePublishedAfter(1960).forEach(System.out::println);

        System.out.println("\n=== Genre 'Sci-Fi' OR Author 'Robert Martin' ===");
        library.findByGenreOrAuthor("Sci-Fi", "Robert Martin").forEach(System.out::println);

        System.out.println("\n=== Currently Unavailable Books ===");
        library.findUnavailableBooks().forEach(System.out::println);

        System.out.println("\n=== 3 Oldest Books ===");
        library.oldestBooks(3).forEach(System.out::println);

        System.out.println("\n=== Books Grouped by Genre ===");
        library.titlesByGenre().forEach((genre, titles) ->
                System.out.println(genre + ": " + titles));

        System.out.println("\n=== Borrowing 'Dune' ===");
        library.borrowBook("Dune").ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Could not borrow the book.")
        );

        System.out.println("\n=== Trying to Borrow Already Checked-Out '1984' ===");
        library.borrowBook("1984").ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Book unavailable - already checked out.")
        );
    }
}