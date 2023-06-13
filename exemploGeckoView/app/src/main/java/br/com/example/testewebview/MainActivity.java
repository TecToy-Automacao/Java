package br.com.example.testewebview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends AppCompatActivity {

    private GeckoView viewGecko;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewGecko = findViewById(R.id.wvNavega);
        GeckoRuntime runtime = GeckoRuntime.create(this);


        GeckoSession session = new GeckoSession();
        GeckoSessionSettings settings = session.getSettings();
        settings.setAllowJavascript(true);

        session.open(runtime);
        viewGecko.setSession(session);
        session.loadUri("https://www.tectoy.com.br/"); // Or any other URL...
    }
}