package br.com.itfast.example.v2propassagem

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnToqueAqui : Button = findViewById(R.id.btnToqueAqui)
        btnToqueAqui.setOnClickListener {
        finish()
        }

    }

}


