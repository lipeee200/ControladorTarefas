package com.example.controladortarefas;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase bancoDados;
    public ListView ListViewDados;
    public Button botao;
    public ArrayList<Integer> arrayIds;
    public Integer idSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        MaterialButton button = findViewById(R.id.selecionarData);
        criarBancoDados();
        listarDados();
        TextView textView = findViewById(R.id.tv);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selecione Data")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long aLong) {
                        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(idSelecionado));
                        textView.setText(MessageFormat.format("Selecione Data: {0}", date));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });
        ListViewDados = (ListView) findViewById(R.id.Dados);
        botao = (Button) findViewById(R.id.Cadastrar);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListViewDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
                idSelecionado = arrayIds.get(i);
                confirmaExcluir();
                return true;
            }
        });

            }

    @Override
    protected void onResume(){
        super.onResume();
        listarDados();
    }
    public void criarBancoDados(){
        try {
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas( " +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT " +
                    ", nome varchar(255)," +
                    " descrisção varchar(255)," +
                    " data date)");
            bancoDados.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void listarDados(){
        try{
            arrayIds = new ArrayList<>();
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT id, nome, descrisção, data FROM tarefas", null);
            ArrayList<String> linhas = new ArrayList<String>();
            ArrayAdapter<String> meuAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            ListViewDados.setAdapter(meuAdapter);
            if (meuCursor.moveToFirst()) {
                while (meuCursor != null) {
                    int idtarefas = meuCursor.getInt(0);
                    String nome = meuCursor.getString(1);
                    String descrisção = meuCursor.getString(2);
                    String data = meuCursor.getString(3);

                    String tarefas = idtarefas + " - " + nome  + " - Nivel:" + descrisção + " - " + data + " - ";
                    linhas.add(tarefas);
                    arrayIds.add(meuCursor.getInt(0));
                    meuCursor.moveToNext();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void abrirTelaCadastro(){
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    public void confirmaExcluir() {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
        msgBox.setTitle("Excluir");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Você realmente deseja excluir esse registro?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                excluir();
                listarDados();
            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        msgBox.show();
    }
    public void excluir(){
        try {
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            String sql = "DELETE FROM tarefas WHERE id =?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1, idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            listarDados();

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    }
