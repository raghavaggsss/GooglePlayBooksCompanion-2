package models;

import models.exceptions.InvalidBookTitleException;
import models.exceptions.NoSuchBook;
import models.exceptions.NoSuchWordException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Book implements Serializable, WordTree {
    private String title;
    private String author;
    private ArrayList<Character> characters;
    private HashSet<Word> words;

    public void insertWord(Word word) {
        //setChanged();
        //notifyObservers(word);
        words.add(word);
    }

    public HashSet<Word> getWords() {
        return words;
    }

    // EFFECT: Create a new book object with title
    public Book(String title) throws InvalidBookTitleException {
        //super();
        words = new HashSet<>();
        if (!title.equals("")) {
            this.title = title;
            this.author = "";
            characters = new ArrayList<>();
        } else {
            throw new InvalidBookTitleException();
        }
    }

    public Book(String title, String author) throws InvalidBookTitleException {
        //super();
        words = new HashSet<>();
        if (!title.equals("")) {
            this.title = title;
            this.author = author;
            characters = new ArrayList<>();
        } else {
            throw new InvalidBookTitleException();
        }
    }

//    @Override
//    public void printWords() {
//        System.out.println("The words for " + title + " are:");
//        super.printWords();
//    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void insertCharacter(Character c) {
        if (!characters.contains(c)) {
            characters.add(c);
            c.addBook(this);
        }
    }

    public void printCharacters() {
        for (Character c : characters) {
            System.out.println(c.getName());
        }
    }

//    @Override
//    public void printBookTitle() {
//        System.out.println(title);
//    }

    public String getBookTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }

    @Override
    public String toString() {
        return this.getBookTitle();
    }

    public void removeWord(Word word) throws NoSuchWordException {
        if (!words.contains(word)) {
            throw new NoSuchWordException();
        }
        else {
            words.remove(word);
        }
    }

}
