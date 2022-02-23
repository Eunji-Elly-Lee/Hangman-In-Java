import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This program is that lets a user play the hangman game using GUI.
 * A user guesses a word, which is from a text file, by entering one letter at a time.
 * If the user misses seven times, a hanging man swings.
 * Once a word is finished, the user can press the Enter key to continue to guess another word.
 * Words consist only of letters without numbers or symbols.
 * Even if a user presses a key other than a letter, it will not affect the game,
 * except for pressing the enter key to continue the game.
 * @author Eunji Elly Lee
 * @version 2022-02-23
 */

public class Hangman extends Application {
	// Declare and initialize the variables
	private final File WORDS = new File("res/words.txt");
	private final int TOTAL_MISSING = 7;
	private int missingCount;
	private String quiz;
	private String notice;
	private String word;
	private String[] letterArray;
	private ArrayList<String> guesses = new ArrayList<>();
	private ArrayList<Node> drawingParts = new ArrayList<>();
	private Text quizDisplay = new Text(210, 400, "");
	private Text noticeDisplay = new Text(210, 425, "");
	private Group drawingBox = new Group();
	
	@Override
	public void start(Stage primaryStage) {	
		// Create a drawing parts list
		createDrawingParts();
		
		// Set a new game		
		drawingBox = setNewGame();
		
		// Create a pane for the game board
		Arc holder = new Arc(90, 505, 80, 35, 0, 180);
		holder.setFill(Color.TRANSPARENT);
		holder.setStroke(Color.BLACK);		
		Line verticalPole = new Line(90, 30, 90, 470);
		Line horizontalPole = new Line(90, 30, 270, 30);
		
		Pane pane = new Pane();	
		pane.getChildren().addAll(
				holder, verticalPole, horizontalPole, drawingBox, quizDisplay, noticeDisplay);		
		quizDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		noticeDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(pane, 480, 520);
		primaryStage.setTitle("Hangman");
		primaryStage.setScene(scene); 
		primaryStage.show();
		
		scene.setOnKeyPressed(e -> {
			if(e.getCode().isLetterKey() &&
					missingCount < TOTAL_MISSING && !displayWord(letterArray).equals(word)) {
				playGame(e);
			}
			else if(e.getCode() == KeyCode.ENTER &&
					(missingCount == TOTAL_MISSING || displayWord(letterArray).equals(word))) {
				continueGame(pane);				
			}
	    });
	}
	
	// Create a drawing parts list
	public void createDrawingParts() {		
		Line line = new Line(270, 30, 270, 60);
		Circle head = new Circle(270, 90, 30);
		head.setFill(Color.TRANSPARENT);
		head.setStroke(Color.BLACK);
		Line rightArm = new Line(249, 111, 189, 160);
		Line leftArm = new Line(291, 111, 351, 160);
		Line body = new Line(270, 120, 270, 210);
		Line rightLeg = new Line(270, 210, 210, 300);
		Line leftLeg = new Line(270, 210, 330, 300);			
			
		drawingParts.add(line);
		drawingParts.add(head);
		drawingParts.add(rightArm);
		drawingParts.add(leftArm);
		drawingParts.add(body);
		drawingParts.add(rightLeg);
		drawingParts.add(leftLeg);
	}
	
	// Set a new game
	public Group setNewGame() {
		pickWord();
		missingCount = 0;
		guesses.clear();
		quiz = "Guess a word: " + displayWord(letterArray);
		notice = "Missed letters: ";		
		displayTexts();
		noticeDisplay.setVisible(false);
		
		return new Group();
	}
	
	// Randomly pick a word from a file
	public void pickWord() {
		try(BufferedReader textReader = new BufferedReader(new FileReader(WORDS))) {
			ArrayList <String> words = new ArrayList<>();
			String aWord = "";
			
			while((aWord = textReader.readLine()) != null) {
				words.add(aWord);
			}
			
			word = words.get((int)(Math.random() * words.size()));
			letterArray = new String[word.length()];
				
			for(int i = 0; i < letterArray.length; i++) {
				letterArray[i] = "*";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	// Display the letter array
	public static String displayWord(String[] letterArray) {
		String onDisplay = "";
			
		for(int i = 0; i < letterArray.length; i++) {
			onDisplay += letterArray[i];
		}
				
		return onDisplay;
	}
	
	// Display the texts into the pane
	public void displayTexts() {
		quizDisplay.setText(quiz);
		noticeDisplay.setText(notice);
	}
	
	// Play a game
	public void playGame(KeyEvent e) {
		String guess = e.getText().toLowerCase();
		
		if(guesses.contains(guess)) {
    		JOptionPane.showMessageDialog(null, guess + " is already typed!");
    	}
    	else {
    		guesses.add(guess);
    	
	    	if(word.contains(guess)) {
	    		for(int i = 0; i < word.length(); i++) {
					if(word.charAt(i) == guess.charAt(0)) {
						letterArray[i] = guess;
					}
				}
	    		
	    		quiz = "Guess a word: " + displayWord(letterArray);
	    	}
	    	else {
	    		missingCount++;
	    		notice += guess + " ";
	    		drawingBox.getChildren().add(drawingParts.get(missingCount - 1));
	    	}
    	}
		
		if(missingCount > 0 || displayWord(letterArray).equals(word)) {
    		noticeDisplay.setVisible(true);
    	}
		
		if(missingCount == TOTAL_MISSING) {
    		PathTransition swing = new PathTransition();
    		swing.setPath(new Arc(270, 35, 130, 130, 240, 60));
    		swing.setNode(drawingBox);
    		swing.setDuration(Duration.millis(3000));
    		swing.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
    		swing.setCycleCount(Timeline.INDEFINITE);
    		swing.setAutoReverse(true);	    		
    		swing.play();
    		
    	    quiz = "Wrong! The word is " + word;
    		notice = "To continue the game,\npress ENTER";
	    }
		
		if(displayWord(letterArray).equals(word)) {
			quiz = "Correct! The word is " + word;
	    	notice = "To continue the game,\npress ENTER";
		}
		
		displayTexts();
	}
	
	// Continue the game
	public void continueGame(Pane pane) {
		pane.getChildren().remove(drawingBox);
		drawingBox = setNewGame();
		pane.getChildren().add(drawingBox);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}