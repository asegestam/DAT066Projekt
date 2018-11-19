package dat066.dat066_projekt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartScreen extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set contentview to activity_main
        setContentView(R.layout.start_screen);
        init();
    }

    private void init() {

        //OnClickListener for the btnMap button
        Button btnMap = (Button) findViewById(R.id.button);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartScreen.this, MapsActivity.class);
                startActivity(intent);

            }
        });
    }
}
