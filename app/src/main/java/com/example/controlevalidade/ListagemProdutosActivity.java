package com.example.controlevalidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import java.util.List;
import androidx.appcompat.view.ActionMode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ListagemProdutosActivity extends AppCompatActivity {

    private static final int REQUEST_INCLUIR_PRODUTO = 1;
    private static final int REQUEST_EDITAR_PRODUTO = 2;

    private ArrayList<Produto> produtos;
    private ProdutoAdapter produtoAdapter;
    private AppPreferences appPreferences;
    private Produto produtoSelecionado;
    private ProdutoDao produtoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_produtos);

        View layoutListagem = findViewById(R.id.layoutListagem);
        ListView listViewProdutos = findViewById(R.id.listViewProdutos);

        aplicarInsets(layoutListagem);

        produtoDao = ProdutoDatabase.getInstance(this).produtoDao();
        produtos = new ArrayList<>();
        appPreferences = new AppPreferences(this);
        produtoAdapter = new ProdutoAdapter(this, produtos);
        listViewProdutos.setAdapter(produtoAdapter);
        atualizarEstadoVazio();

        listViewProdutos.setOnItemClickListener((parent, view, position, id) -> {
            Produto produtoSelecionado = produtos.get(position);
            String mensagem = getString(
                    R.string.produto_selecionado,
                    produtoSelecionado.getNome(),
                    produtoSelecionado.getValidade()
            );
            Snackbar.make(layoutListagem, mensagem, Snackbar.LENGTH_SHORT).show();
        });

        listViewProdutos.setOnItemLongClickListener((parent, view, position, id) -> {
            ActionMode mode = startSupportActionMode(actionModeCallback);
            produtoSelecionado = produtos.get(position);
            return mode != null;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (produtoAdapter != null) {
            carregarProdutos();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listagem_produtos, menu);
        MenuItem adicionar = menu.findItem(R.id.action_adicionar);
        View actionViewAdicionar = adicionar.getActionView();
        if (actionViewAdicionar != null) {
            actionViewAdicionar.setOnClickListener(v -> abrirCadastroParaInclusao());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_adicionar) {
            abrirCadastroParaInclusao();
            return true;
        }

        if (itemId == R.id.action_sobre) {
            startActivity(new Intent(this, AutoriaActivity.class));
            return true;
        }

        if (itemId == R.id.action_configuracoes) {
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    private void abrirCadastroParaInclusao() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, REQUEST_INCLUIR_PRODUTO);
    }

    @SuppressWarnings("deprecation")
    private void abrirCadastroParaEdicao() {
        if (produtoSelecionado == null) {
            return;
        }

        Produto produto = produtoSelecionado;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ID, produto.getId());
        intent.putExtra(MainActivity.EXTRA_NOME, produto.getNome());
        intent.putExtra(MainActivity.EXTRA_QUANTIDADE, produto.getQuantidade());
        intent.putExtra(MainActivity.EXTRA_UNIDADE, produto.getUnidade());
        intent.putExtra(MainActivity.EXTRA_CATEGORIA, produto.getCategoria());
        intent.putExtra(MainActivity.EXTRA_VALIDADE, produto.getValidade());
        intent.putExtra(MainActivity.EXTRA_LOCAL, produto.getLocalArmazenamento());
        intent.putExtra(MainActivity.EXTRA_ABERTO, produto.isAberto());
        startActivityForResult(intent, REQUEST_EDITAR_PRODUTO);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        String nome = data.getStringExtra(MainActivity.EXTRA_NOME);
        String quantidade = data.getStringExtra(MainActivity.EXTRA_QUANTIDADE);
        String unidade = data.getStringExtra(MainActivity.EXTRA_UNIDADE);
        String categoria = data.getStringExtra(MainActivity.EXTRA_CATEGORIA);
        String validade = data.getStringExtra(MainActivity.EXTRA_VALIDADE);
        String local = data.getStringExtra(MainActivity.EXTRA_LOCAL);
        boolean aberto = data.getBooleanExtra(MainActivity.EXTRA_ABERTO, false);

        if (nome == null || quantidade == null || unidade == null || categoria == null
                || validade == null || local == null) {
            return;
        }

        Produto produto = new Produto(nome, quantidade, unidade, categoria, validade, local, aberto);
        if (requestCode == REQUEST_INCLUIR_PRODUTO) {
            executarGravacao(() -> produtoDao.inserir(produto), R.string.produto_salvo);
        } else if (requestCode == REQUEST_EDITAR_PRODUTO) {
            produto.setId(data.getLongExtra(MainActivity.EXTRA_ID, 0));
            if (produto.getId() > 0) {
                executarGravacao(() -> produtoDao.atualizar(produto), R.string.produto_atualizado);
            }
        }
    }

    private void carregarProdutos() {
        ProdutoDatabase.executor.execute(() -> {
            try {
                List<Produto> resultado = produtoDao.consultarTodos();
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    produtos.clear();
                    produtos.addAll(resultado);
                    ordenarProdutos();
                    atualizarEstadoVazio();
                });
            } catch (RuntimeException exception) {
                mostrarMensagem(R.string.erro_consultar_produtos);
            }
        });
    }

    private interface Gravacao {
        long executar();
    }

    private void executarGravacao(Gravacao gravacao, int mensagemSucesso) {
        ProdutoDatabase.executor.execute(() -> {
            try {
                if (gravacao.executar() > 0) {
                    carregarProdutos();
                    mostrarMensagem(mensagemSucesso);
                } else {
                    mostrarMensagem(R.string.erro_gravar_produto);
                }
            } catch (RuntimeException exception) {
                mostrarMensagem(R.string.erro_gravar_produto);
            }
        });
    }

    private void mostrarMensagem(int mensagem) {
        runOnUiThread(() -> {
            if (isFinishing() || isDestroyed()) return;
            Snackbar.make(findViewById(R.id.layoutListagem), mensagem, Snackbar.LENGTH_LONG).show();
        });
    }

    private void confirmarExclusao(Produto produto) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_exclusao)
                .setMessage(getString(R.string.mensagem_exclusao, produto.getNome()))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.excluir, (dialog, which) ->
                        executarGravacao(() -> produtoDao.excluir(produto), R.string.produto_excluido))
                .show();
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contextual_produto, menu);
            mode.setTitle(R.string.um_produto_selecionado);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();

            if (itemId == R.id.action_editar) {
                abrirCadastroParaEdicao();
                mode.finish();
                return true;
            }

            if (itemId == R.id.action_excluir) {
                if (produtoSelecionado != null) {
                    confirmarExclusao(produtoSelecionado);
                }
                mode.finish();
                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            produtoSelecionado = null;
        }
    };

    private void atualizarEstadoVazio() {
        boolean vazio = produtos.isEmpty();
        findViewById(R.id.estadoVazio).setVisibility(vazio ? View.VISIBLE : View.GONE);
        findViewById(R.id.conteudoLista).setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    private void ordenarProdutos() {
        String sortOrder = appPreferences.getSortOrder();
        Comparator<Produto> comparator;

        if (AppPreferences.SORT_NAME.equals(sortOrder)) {
            comparator = compararTexto(Produto::getNome);
        } else if (AppPreferences.SORT_CATEGORY.equals(sortOrder)) {
            comparator = compararTexto(produto -> ProdutoValores.categoria(this, produto.getCategoria()))
                    .thenComparing(compararTexto(Produto::getNome));
        } else {
            comparator = Comparator.<Produto>comparingLong(
                    produto -> converterDataParaOrdenacao(produto.getValidade())
            ).thenComparing(compararTexto(Produto::getNome));
        }

        Collections.sort(produtos, comparator);
        produtoAdapter.notifyDataSetChanged();
    }

    private Comparator<Produto> compararTexto(java.util.function.Function<Produto, String> campo) {
        Collator collator = Collator.getInstance(getResources().getConfiguration().getLocales().get(0));
        collator.setStrength(Collator.PRIMARY);
        return (primeiro, segundo) -> collator.compare(campo.apply(primeiro), campo.apply(segundo));
    }

    private long converterDataParaOrdenacao(String validade) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        formato.setLenient(false);
        try {
            Date data = formato.parse(validade);
            return data == null ? Long.MAX_VALUE : data.getTime();
        } catch (ParseException exception) {
            return Long.MAX_VALUE;
        }
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
}
