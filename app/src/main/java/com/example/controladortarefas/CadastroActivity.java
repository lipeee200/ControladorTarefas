package com.example.controladortarefas;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class CadastroActivity extends AppCompatActivity {
    EditText editTextNome;

    EditText editTextDescriscao;
    Button botao;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        editTextNome = (EditText) findViewById(R.id.EditTextNome);
        botao = (Button) findViewById(R.id.buttonCadastrar);
        editTextDescriscao = (EditText) findViewById(R.id.EditTextDescriscao);

        SQLiteDatabase bancoDados;
        botao.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                cadastrar();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void cadastrar() {
        if (!TextUtils.isEmpty(editTextNome.getText().toString())) {
            try {
                SQLiteDatabase bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
                String sql = "INSERT INTO tarefas (nome, descrisção, data) VALUES (?,?,?)";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1,editTextNome.getText().toString());
                stmt.bindString(2, editTextDescriscao.getText().toString());
                stmt.executeInsert();
                finish();
                bancoDados.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}