package fr.eurecom.cardify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public void lobby(View view) {
		Intent intent = new Intent(this, Lobby.class);
		startActivity(intent);
	}

	public void startSolitaire(View view) {
		Intent intent = new Intent(this, Game.class);
		intent.putExtra("isSolitaire", true);
		this.startActivity(intent);
	}

	@Override
	public void onBackPressed() {
//		Should always exit app when back is pressed in main menu
		finish();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

    
    public void showAbout(View view) {
    	Intent intent = new Intent(this, About.class);
    	this.startActivity(intent);
    }
    
    public void twitter(View view) {
    	String url = "http://www.twitter.com/intent/tweet?text=Check%20out%20the%20new%20app%20Deck%20of%20Cards%20on%20Google%20Play%20%23DeckOfCards%0A%0Abit.ly%2F1m2TAxq";
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(url));
    	startActivity(i);
    }
    
    public void facebook(View view) {
    	System.out.println("FACEBOOK");
    	Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "The status update text http://bit.ly/1m2TAxq");
        i.putExtra(Intent.EXTRA_SUBJECT, "test");
        startActivity(i);
        //startActivity(Intent.createChooser(intent, "Dialog title text"));
    }
    
}