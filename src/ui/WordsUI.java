package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Book;
import models.Button;
import models.Word;
import models.exceptions.InvalidBookTitleException;

import java.util.ArrayList;

public class WordsUI extends Application {

    ArrayList<Book> books = new ArrayList<>();
    private Stage window;
    private Scene home;
    private Scene book;
    private Scene character;


    public static void main(String[] args) {
        launch(args);
    }

    public Stage modalInit() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        return modal;
    }

    public void addBook() {
        Stage addBookStage = modalInit();
        addBookStage.setTitle("Add a new book");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Scene s = new Scene(grid);

        Label titleLabel = new Label("Book Title");
        GridPane.setConstraints(titleLabel, 0, 0);

        TextField titleInput = new TextField();
        titleInput.setPromptText("Title");
        GridPane.setConstraints(titleInput, 1, 0);

        Label authorLabel = new Label("Book Author");
        GridPane.setConstraints(authorLabel, 0, 1);

        TextField authorInput = new TextField();
        authorInput.setPromptText("Author");
        GridPane.setConstraints(authorInput, 1, 1);

        Button addBook = new Button("Add Book");
        // TODO: Popup for bookAdditionResult. Currently on the console.
        addBook.setOnAction(e -> {
            Label bookAdditionResult = new Label();
            try {
                Book inputBook = new Book(titleInput.getText(), authorInput.getText());
                if (!books.contains(inputBook)) {
                    books.add(inputBook);
                    bookAdditionResult.setText(titleInput.getText() + " added successfully!");
                } else {
                    bookAdditionResult.setText(titleInput.getText() + " already exists!");
                }
            } catch (InvalidBookTitleException f) {
                bookAdditionResult.setText("Invalid Book Title");
            } finally {
                System.out.println(bookAdditionResult.getText());
            }

        });
        GridPane.setConstraints(addBook, 1, 2);

        grid.getChildren().addAll(titleLabel, titleInput, authorInput, authorLabel, addBook);

        addBookStage.setScene(s);
        addBookStage.showAndWait();

    }

    public void wordToMeaning(Word word) {
        Stage meaningWindow = modalInit();
        meaningWindow.setTitle(word.getWord());

        Text text = new Text(word.getMeaning());

        System.out.println(text);
        VBox meaning = new VBox(20);
        meaning.setAlignment(Pos.CENTER);
        meaning.getChildren().add(text);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            meaningWindow.close();
        });
        meaning.getChildren().add(backButton);

        Scene meaningScene = new Scene(meaning, 200, 200);
        meaningWindow.setScene(meaningScene);

        meaningWindow.showAndWait();

    }

    public Scene bookToWords(Book b) {
        VBox wordsLayout = new VBox(20);
        wordsLayout.setAlignment(Pos.CENTER);
        ////
        ListView<Word> listView = new ListView<>();

        ArrayList<Button> buttons = new ArrayList<>();
//        for (Word word : b.getWords()) {
//            Button wordButton = new Button(word.getWord());
//            wordButton.setOnAction(e -> wordToMeaning(word));
//            //wordsLayout.getChildren().add(wordButton);
//            buttons.add(wordButton);
//        }
        //wordsLayout.getChildren().addAll(buttons);

        /////
        listView.getItems().addAll(b.getWords());
        wordsLayout.getChildren().add(listView);

        listView.setOnMouseClicked(e -> wordToMeaning((listView.getSelectionModel().getSelectedItems().get(0))));

        book = new Scene(wordsLayout, 200, 200);
        return book;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Book theBookThief = new Book("The Book Thief");
        theBookThief.insertWord(new Word("vehement", "MEaning herer ddue"));
        theBookThief.insertWord(new Word("immoral", "not moral"));
        books.add(theBookThief);

        Book book2 = new Book("Book II");
        books.add(book2);

        window = primaryStage;
        window.setOnCloseRequest(e -> window.close());

        VBox layout = new VBox();

        for (Book book : books) {
            Button bookButton = new Button(book.getBookTitle());

            bookButton.setOnAction(e -> {
                window.setScene(bookToWords(book));
                window.setTitle(book.getBookTitle());
            });

            layout.getChildren().add(bookButton);
        }


        // Add a button for adding new book
//        Button addBookButton = new Button("Add a new book");
//        addBookButton.setOnAction(e -> addBook());
//        layout.getChildren().add(addBookButton);

        Button addBookButton = new Button("Add a new book");
        //addBookButton.setOnAction(e -> addBook());
        //layout.getChildren().add(addBookButton);

        Button removeBookButton = new Button("Remove an existing book");
        //addBookButton.setOnAction(e -> addBook());
        //layout.getChildren().add(addBookButton);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(addBookButton.toString(), removeBookButton.toString());
        comboBox.setPromptText("Options");

        comboBox.setOnAction(e ->
        {
            String selection = comboBox.getValue();
            if (selection.equals(addBookButton.toString())) {
                addBook();
            } else if (selection.equals(addBookButton.toString())) {
                //TODO: Implement removeBook
            }
        });

        layout.getChildren().add(comboBox);


        home = new Scene(layout);
        window.setScene(home);
        window.show();


    }
}