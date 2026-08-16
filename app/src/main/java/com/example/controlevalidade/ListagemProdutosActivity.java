package com.example.controlevalidade;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListagemProdutosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_produtos);

        ListView listViewProdutos = findViewById(R.id.listViewProdutos);

        ViewCompat.setOnApplyWindowInsetsListener(listViewProdutos, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });

        String[] nomes = getResources().getStringArray(R.array.nomes_produtos);
        String[] categorias = getResources().getStringArray(R.array.categorias_produtos);
        String[] validades = getResources().getStringArray(R.array.validades_produtos);
        String[] locais = getResources().getStringArray(R.array.locais_produtos);

        ArrayList<Produto> produtos = new ArrayList<>();

        for (int i = 0; i < nomes.length; i++) {
            Produto produto = new Produto(
                    nomes[i],
                    categorias[i],
                    validades[i],
                    locais[i]
            );
            produtos.add(produto);
        }

        ProdutoAdapter adapter = new ProdutoAdapter(this, produtos);
        listViewProdutos.setAdapter(adapter);

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
}
