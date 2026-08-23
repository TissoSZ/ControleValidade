package com.example.controlevalidade;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NOME = "nome";
    public static final String EXTRA_CATEGORIA = "categoria";
    public static final String EXTRA_VALIDADE = "validade";
    public static final String EXTRA_LOCAL = "local";

    private EditText editTextNome;
    private EditText editTextQuantidade;
    private EditText editTextDataValidade;
    private Spinner spinnerCategoria;
    private RadioGroup radioGroupArmazenamento;
    private CheckBox checkBoxAberto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        aplicarInsets(findViewById(R.id.scrollViewCadastro));

        editTextNome = findViewById(R.id.editTextNome);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        editTextDataValidade = findViewById(R.id.editTextDataValidade);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        radioGroupArmazenamento = findViewById(R.id.radioGroupArmazenamento);
        checkBoxAberto = findViewById(R.id.checkBoxAberto);
        Button buttonLimpar = findViewById(R.id.buttonLimpar);
        Button buttonSalvar = findViewById(R.id.buttonSalvar);

        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categorias,
                android.R.layout.simple_spinner_item
        );
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        editTextDataValidade.setKeyListener(null);
        editTextDataValidade.setOnClickListener(view -> abrirSeletorDeData());
        buttonLimpar.setOnClickListener(view -> limparFormulario());
        buttonSalvar.setOnClickListener(view -> salvarProduto());
    }

    private void abrirSeletorDeData() {
        Calendar hoje = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, ano, mes, dia) -> editTextDataValidade.setText(
                        String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, ano)
                ),
                hoje.get(Calendar.YEAR),
                hoje.get(Calendar.MONTH),
                hoje.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void limparFormulario() {
        editTextNome.setText("");
        editTextQuantidade.setText("");
        editTextDataValidade.setText("");
        radioGroupArmazenamento.clearCheck();
        checkBoxAberto.setChecked(false);
        spinnerCategoria.setSelection(0);
        editTextNome.requestFocus();
        Toast.makeText(this, R.string.formulario_limpo, Toast.LENGTH_SHORT).show();
    }

    private void salvarProduto() {
        String nome = editTextNome.getText().toString().trim();
        String quantidade = editTextQuantidade.getText().toString().trim();
        String dataValidade = editTextDataValidade.getText().toString().trim();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        int armazenamentoSelecionadoId = radioGroupArmazenamento.getCheckedRadioButtonId();

        if (nome.isEmpty()) {
            mostrarErroComFoco(R.string.informe_nome, editTextNome);
            return;
        }

        if (quantidade.isEmpty()) {
            mostrarErroComFoco(R.string.informe_quantidade, editTextQuantidade);
            return;
        }

        if (dataValidade.isEmpty()) {
            mostrarErroComFoco(R.string.informe_data_validade, editTextDataValidade);
            return;
        }

        if (armazenamentoSelecionadoId == -1) {
            Toast.makeText(this, R.string.selecione_armazenamento, Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.selecione_categoria, Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioButtonArmazenamento = findViewById(armazenamentoSelecionadoId);
        String armazenamento = radioButtonArmazenamento.getText().toString();

        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_NOME, nome);
        resultado.putExtra(EXTRA_CATEGORIA, categoria);
        resultado.putExtra(EXTRA_VALIDADE, dataValidade);
        resultado.putExtra(EXTRA_LOCAL, armazenamento);
        setResult(RESULT_OK, resultado);
        finish();
    }

    private void mostrarErroComFoco(int mensagemId, View campo) {
        Toast.makeText(this, mensagemId, Toast.LENGTH_SHORT).show();
        campo.requestFocus();
    }

    private void aplicarInsets(View view) {
        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingRight = view.getPaddingRight();
        int paddingBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.navigationBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(
                    paddingLeft + insets.left,
                    paddingTop,
                    paddingRight + insets.right,
                    paddingBottom + insets.bottom
            );
            return windowInsets;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
