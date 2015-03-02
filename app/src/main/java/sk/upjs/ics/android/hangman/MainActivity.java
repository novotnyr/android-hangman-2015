package sk.upjs.ics.android.hangman;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String BUNDLE_KEY_GAME = "game";

	private HangmanGame game;

	private int[] gallowsLayouts = {
			R.drawable.gallows0,
			R.drawable.gallows1,
			R.drawable.gallows2,
			R.drawable.gallows3,
			R.drawable.gallows4,
			R.drawable.gallows5,
			R.drawable.gallows6
	};

	private ImageView gallowsImageView;

	private TextView foundLettersTextView;

	private TextView letterTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gallowsImageView = (ImageView) findViewById(R.id.gallowsImageView);
		letterTextView = (TextView) findViewById(R.id.letter);
		
		foundLettersTextView = (TextView) findViewById(R.id.foundLettersTextView);		
		
		if(savedInstanceState == null) {
			resetGame();
		} else {
			restoreGameState(savedInstanceState);
		}
	}
	
	private void restoreGameState(Bundle savedInstanceState) {
		game = (HangmanGame) savedInstanceState.get(BUNDLE_KEY_GAME);
		updateGallows();
		updateGuessedCharacters();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable(BUNDLE_KEY_GAME, game);
	}

	public void gallowsImageViewClick(View v) {
		if(game.getAttemptsLeft() == 0 || game.isWon()) {
			resetGame();
			return;
		}
		CharSequence letter = letterTextView.getText();
		if(letter == null || letter.length() == 0) {
			alertEmptyLetter();
			return;
		}
		char charLetter = Character.toLowerCase(letter.charAt(0));
		boolean isGuessed = game.guess(charLetter);

		updateGuessedCharacters();
		letterTextView.setText("");
		
		if(!isGuessed) {
			updateGallows();
			if(game.getAttemptsLeft() == 0) {
				alertFailedGame();
				return;
			} 			
		} else {
			if(game.isWon()) {
				alertSuccessfulGame();
			}
		}
	}
	
	private void updateGuessedCharacters() {
		foundLettersTextView.setText(formatLetters(game.getGuessedCharacters()));
	}

	private void resetGame() {
		game = new HangmanGame();
		gallowsImageView.setImageResource(gallowsLayouts[0]);
		gallowsImageView.setColorFilter(null);
		
		letterTextView.setText("");
		updateGuessedCharacters();
	}

	private void updateGallows() {
		int imageIndex = gallowsLayouts.length - 1 - game.getAttemptsLeft();
		gallowsImageView.setImageResource(gallowsLayouts[imageIndex]);					
	}

	private void alertEmptyLetter() {
		Toast.makeText(this, "You must enter a letter.", Toast.LENGTH_SHORT)
		.show();
	}
	
	private void alertFailedGame() {
		foundLettersTextView.setText(formatLetters(game.getChallengeWord()));
		
		ColorFilter filter = new LightingColorFilter(Color.RED, Color.BLACK); 
		gallowsImageView.setColorFilter(filter);
	}

	private void alertSuccessfulGame() {
		ColorFilter filter = new LightingColorFilter(Color.GREEN, Color.BLACK); 
		gallowsImageView.setColorFilter(filter);
	}	
	
	private CharSequence formatLetters(CharSequence string) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < string.length() - 1; i++) {
			result.append(string.charAt(i)).append(" ");
		}
		result.append(string.charAt(string.length() - 1));
		return result;
	}


}
