package br.com.itfast.buscapreco;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class tela2 extends BasePresentation {

    public int state;

    public tela2(Context outerContext, Display display) { super(outerContext, display); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ScreenManager.getInstance().isMinScreen()) {
            setContentView(R.layout.activity_tela2);

        }else {
            Log.i("Display", "Layout inválido. Precisa adequar");
        }

    }

    public void update(final int state) {
        this.state = state;
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void onSelect(boolean isShow) {

    }
}