package com.example.controlevalidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListagemProdutosActivity extends AppCompatActivity {

    private static final int REQUEST_CADASTRO_PRODUTO = 1;

    private ArrayList<Produto> produtos;
    private ProdutoAdapter produtoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_produtos);

        View layoutListagem = findViewById(R.id.layoutListagem);
        Button buttonAdicionar = findViewById(R.id.buttonAdicionar);
        Button buttonSobre = findViewById(R.id.buttonSobre);
        ListView listViewProdutos = findViewById(R.id.listViewProdutos);

        aplicarInsets(layoutListagem);

        produtos = new ArrayList<>();
        produtoAdapter = new ProdutoAdapter(this, produtos);
        listViewProdutos.setAdapter(produtoAdapter);

        buttonAdicionar.setOnClickListener(view -> abrirCadastro());
        buttonSobre.setOnClickListener(view -> {
            Intent intent = new Intent(this, AutoriaActivity.class);
            startActivity(intent);
        });

        listViewProdutos.setOnItemClickListener((parent, view, position, id) -> {
            Produto produtoSelecionado = produtos.get(position);
            String mensagem = getString(
                    R.string.produto_selecionado,
                    produtoSelecionado.getNome(),
                    produtoSelecionado.getValidade()
            );
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressWarnings("deprecation")
    private void abrirCadastro() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, REQUEST_CADASTRO_PRODUTO);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CADASTRO_PRODUTO
                || resultCode != RESULT_OK
                || data == null) {
            return;
        }

        String nome = data.getStringExtra(MainActivity.EXTRA_NOME);
        String categoria = data.getStringExtra(MainActivity.EXTRA_CATEGORIA);
        String validade = data.getStringExtra(MainActivity.EXTRA_VALIDADE);
        String local = data.getStringExtra(MainActivity.EXTRA_LOCAL);

        if (nome == null || categoria == null || validade == null || local == null) {
            return;
        }

        Produto produto = new Produto(nome, categoria, validade, local);
        produtos.add(produto);
        produtoAdapter.notifyDataSetChanged();
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
        )
                ? TypedValue.complexToDimensionPixelSize(
                        actionBarSize.data,
                        getResources().getDisplayMetrics()
                )
                : 0;

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
}
