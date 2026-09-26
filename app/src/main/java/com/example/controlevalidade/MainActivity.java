package com.example.controlevalidade;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NOME = "nome";
    public static final String EXTRA_QUANTIDADE = "quantidade";
    public static final String EXTRA_UNIDADE = "unidade";
    public static final String EXTRA_CATEGORIA = "categoria";
    public static final String EXTRA_VALIDADE = "validade";
    public static final String EXTRA_LOCAL = "local";
    public static final String EXTRA_ABERTO = "aberto";
    public static final String EXTRA_ID = "produto_id";

    private EditText editTextNome;
    private EditText editTextQuantidade;
    private EditText editTextDataValidade;
    private AutoCompleteTextView spinnerCategoria;
    private AutoCompleteTextView spinnerUnidade;
    private RadioGroup radioGroupArmazenamento;
    private MaterialCheckBox checkBoxAberto;
    private long produtoId = 0;

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
        spinnerUnidade = findViewById(R.id.spinnerUnidade);
        radioGroupArmazenamento = findViewById(R.id.radioGroupArmazenamento);
        checkBoxAberto = findViewById(R.id.checkBoxAberto);

        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(this,
                R.array.categorias, android.R.layout.simple_dropdown_item_1line);
        spinnerCategoria.setAdapter(categoriaAdapter);
        ArrayAdapter<CharSequence> unidadeAdapter = ArrayAdapter.createFromResource(this,
                R.array.unidades, android.R.layout.simple_dropdown_item_1line);
        spinnerUnidade.setAdapter(unidadeAdapter);
        spinnerUnidade.setText(getResources().getStringArray(R.array.unidades)[0], false);

        editTextDataValidade.setKeyListener(null);
        editTextDataValidade.setOnClickListener(view -> abrirSeletorDeData());

        produtoId = getIntent().getLongExtra(EXTRA_ID, 0);
        if (savedInstanceState == null) {
            preencherFormularioParaEdicao();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
            return true;
        }

        if (itemId == R.id.action_salvar) {
            salvarProduto();
            return true;
        }

        if (itemId == R.id.action_limpar) {
            limparFormulario();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirSeletorDeData() {
        Calendar hoje = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, ano, mes, dia) -> editTextDataValidade.setText(
                        String.format(Locale.ROOT, "%02d/%02d/%04d", dia, mes + 1, ano)
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
        spinnerCategoria.setText(getResources().getStringArray(R.array.categorias)[0], false);
        spinnerUnidade.setText(getResources().getStringArray(R.array.unidades)[0], false);
        editTextNome.requestFocus();
        Toast.makeText(this, R.string.campos_limpos, Toast.LENGTH_SHORT).show();
    }

    private void salvarProduto() {
        String nome = editTextNome.getText().toString().trim();
        String quantidade = editTextQuantidade.getText().toString().trim();
        String unidade = ProdutoValores.codigoUnidade(this, spinnerUnidade.getText().toString());
        String dataValidade = editTextDataValidade.getText().toString().trim();
        String categoria = ProdutoValores.codigoCategoria(this, spinnerCategoria.getText().toString());
        boolean aberto = checkBoxAberto.isChecked();
        int armazenamentoSelecionadoId = radioGroupArmazenamento.getCheckedRadioButtonId();

        if (nome.isEmpty()) {
            mostrarErroComFoco(R.string.informe_nome, editTextNome);
            return;
        }

        if (quantidade.isEmpty()) {
            mostrarErroComFoco(R.string.informe_quantidade, editTextQuantidade);
            return;
        }

        if (unidade.isEmpty()) {
            Snackbar.make(findViewById(R.id.scrollViewCadastro), R.string.selecione_unidade, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (dataValidade.isEmpty()) {
            mostrarErroComFoco(R.string.informe_data_validade, editTextDataValidade);
            return;
        }

        if (armazenamentoSelecionadoId == -1) {
            Snackbar.make(findViewById(R.id.scrollViewCadastro), R.string.selecione_armazenamento, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (categoria.isEmpty()) {
            Snackbar.make(findViewById(R.id.scrollViewCadastro), R.string.selecione_categoria, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String armazenamento = armazenamentoSelecionadoId == R.id.radioButtonFreezer
                ? ProdutoValores.FREEZER : armazenamentoSelecionadoId == R.id.radioButtonArmario
                ? ProdutoValores.ARMARIO : ProdutoValores.GELADEIRA;

        Intent resultado = new Intent();
        resultado.putExtra(EXTRA_NOME, nome);
        resultado.putExtra(EXTRA_QUANTIDADE, quantidade);
        resultado.putExtra(EXTRA_UNIDADE, unidade);
        resultado.putExtra(EXTRA_CATEGORIA, categoria);
        resultado.putExtra(EXTRA_VALIDADE, dataValidade);
        resultado.putExtra(EXTRA_LOCAL, armazenamento);
        resultado.putExtra(EXTRA_ABERTO, aberto);
        if (produtoId > 0) {
            resultado.putExtra(EXTRA_ID, produtoId);
        }
        setResult(RESULT_OK, resultado);
        finish();
    }

    private void preencherFormularioParaEdicao() {
        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_ID)) {
            return;
        }

        editTextNome.setText(intent.getStringExtra(EXTRA_NOME));
        editTextQuantidade.setText(intent.getStringExtra(EXTRA_QUANTIDADE));
        spinnerUnidade.setText(ProdutoValores.unidade(this, intent.getStringExtra(EXTRA_UNIDADE)), false);
        editTextDataValidade.setText(intent.getStringExtra(EXTRA_VALIDADE));
        selecionarCategoria(intent.getStringExtra(EXTRA_CATEGORIA));
        selecionarLocalArmazenamento(intent.getStringExtra(EXTRA_LOCAL));
        checkBoxAberto.setChecked(intent.getBooleanExtra(EXTRA_ABERTO, false));
    }

    private void selecionarCategoria(String categoria) {
        if (categoria == null) {
            return;
        }

        spinnerCategoria.setText(ProdutoValores.categoria(this, categoria), false);
    }

    private void selecionarLocalArmazenamento(String local) {
        if (ProdutoValores.GELADEIRA.equals(local)) {
            radioGroupArmazenamento.check(R.id.radioButtonGeladeira);
        } else if (ProdutoValores.FREEZER.equals(local)) {
            radioGroupArmazenamento.check(R.id.radioButtonFreezer);
        } else if (ProdutoValores.ARMARIO.equals(local)) {
            radioGroupArmazenamento.check(R.id.radioButtonArmario);
        }
    }

    private void mostrarErroComFoco(int mensagemId, View campo) {
        Snackbar.make(findViewById(R.id.scrollViewCadastro), mensagemId, Snackbar.LENGTH_SHORT).show();
        campo.requestFocus();
    }

    private void aplicarInsets(View view) {
        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingRight = view.getPaddingRight();
        int paddingBottom = view.getPaddingBottom();
        TypedValue actionBarSize = new TypedValue();
        int actionBarHeight = getTheme().resolveAttribute(
                androidx.appcompat.R.attr.actionBarSize,
                actionBarSize,
                true
        ) ? TypedValue.complexToDimensionPixelSize(
                actionBarSize.data,
                getResources().getDisplayMetrics()
        ) : 0;

        view.setPadding(paddingLeft, paddingTop + actionBarHeight, paddingRight, paddingBottom);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.navigationBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(
                    paddingLeft + insets.left,
                    paddingTop + actionBarHeight,
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
