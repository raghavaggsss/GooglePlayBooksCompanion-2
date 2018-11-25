package ui;

import edu.mit.jwi.IDictionary;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.*;
import models.Button;
import models.exceptions.InvalidBookTitleException;
import models.exceptions.InvalidStringException;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static models.InputOutput.openDictionary;
import static models.InputOutput.readWordMeanings;

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
        authorInput.setPromptText("Optional");
        GridPane.setConstraints(authorInput, 1, 1);

        Label playBookDocLabel = new Label("Play Book");
        GridPane.setConstraints(playBookDocLabel, 0, 2);

        TextField playBookDocInput = new TextField();
        playBookDocInput.setPromptText("Optional");
        GridPane.setConstraints(playBookDocInput,1,2 );



        Button addBook = new Button("Add Book");
        // TODO: Popup for bookAdditionResult. Currently on the console.
        addBook.setOnAction(e -> {
            Label bookAdditionResult = new Label();
            try {
                Book inputBook = new Book(titleInput.getText(), authorInput.getText());
                if (!books.contains(inputBook)) {
                    books.add(inputBook);

                    String playBooksDoc = playBookDocInput.getText();
                    if (!playBooksDoc.equals("")) {
                        loadGooglePlayBookData(playBooksDoc);
                        String playBooksTxt = playBooksDoc.split("\\.")[0] + ".txt";

                        try {
                            IDictionary dict = openDictionary();
                            WordPreProcess wordPreProcess = new WordPreProcess(dict);

                            try {
                                List<String> lines = readWordMeanings("PlayBooks/"+ playBooksTxt);
                                for (String line : lines) {
                                    String[] parts = line.split(":");
                                    String word = parts[0];
                                    //String meaning = parts[1];
                                    try {
                                        String wordStem = wordPreProcess.stemmer(word);
                                        inputBook.insertWord(new Word(wordStem, "MEANING"));
                                    } catch (InvalidStringException f) {
                                        System.out.println("Invalid Word or Meaning");
                                    }
                                }
                            } catch (IOException f) {
                                System.out.println("INPUT FILE NOT FOUND");
                            }

                        }

                        catch (IOException g) {
                            System.out.println("WORDNET NOT FOUND");
                        }
                    }


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
        GridPane.setConstraints(addBook, 1, 3);

        grid.getChildren().addAll(titleLabel, titleInput, authorInput, authorLabel,
                addBook, playBookDocInput, playBookDocLabel);

        addBookStage.setScene(s);
        addBookStage.showAndWait();
        refreshHome();
    }


    public void wordToMeaning(Word word) {
        Stage meaningWindow = modalInit();
        meaningWindow.setTitle(word.getWord());

        Text text = new Text(word.getMeaning());

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

//        ArrayList<Button> buttons = new ArrayList<>();
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

        refreshHome();
        window.show();

    }

    public TreeItem<WordTree> makeBranch(WordTree book, TreeItem<WordTree> parent) {
        TreeItem<WordTree> item = new TreeItem<>(book);
        parent.getChildren().add(item);
        return item;
    }

    public static void loadGooglePlayBookData(String filename){
        try {
        Process p = Runtime.getRuntime().exec("python3 PlayBooks/googlePlayBooks.py "+ filename); }
        catch (IOException e) {
            System.out.println("Problem with loading file");
        }
    }

    public VBox homeLayoutBuilder() {
        VBox layout = new VBox();

        Menu menu = new Menu("Books");
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        MenuItem addBook = new MenuItem("Add Book");
        addBook.setOnAction(e -> addBook());
        menu.getItems().add(addBook);
        MenuItem removeBook = new MenuItem("Remove Book");
        //TODO: Implement remove book and call here
        removeBook.setOnAction(e -> {
        });
        menu.getItems().add(removeBook);

        layout.getChildren().add(menuBar);

        TreeItem<WordTree> root = new TreeItem<>();
        TreeView<WordTree> bookTree = new TreeView<>(root);
        bookTree.setShowRoot(false);


        for (Book book : books) {
            TreeItem<WordTree> bookItem = makeBranch(book, root);

            ImageView ig = new ImageView("book_small.png");
            bookItem.setGraphic(ig);

            for (Word word : book.getWords()) {
                TreeItem<WordTree> wordItem = makeBranch(word, bookItem);
            }
        }

        bookTree.getSelectionModel().selectedItemProperty().addListener(
                ((v, oldValue, newValue) ->
                {
                    WordTree wt = newValue.getValue();
                    if (wt instanceof Word) {
                        wordToMeaning((Word) wt);
                    }
                    ;
                })
        );

        layout.getChildren().add(bookTree);
        return layout;
    }

    public void refreshHome() {
        home = new Scene(homeLayoutBuilder());
        home.getStylesheets().add("Garage.css");
        window.setScene(home);
    }
}
