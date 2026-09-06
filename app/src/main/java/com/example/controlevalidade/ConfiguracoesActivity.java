package com.example.controlevalidade;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class ConfiguracoesActivity extends AppCompatActivity {

    private AppPreferences appPreferences;
    private RadioGroup radioGroupOrdenacao;
    private boolean carregandoPreferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        aplicarInsets();
        appPreferences = new AppPreferences(this);
        radioGroupOrdenacao = findViewById(R.id.radioGroupOrdenacao);
        Button buttonRestaurarPadrao = findViewById(R.id.buttonRestaurarPadrao);

        selecionarPreferenciaAtual();

        radioGroupOrdenacao.setOnCheckedChangeListener((group, checkedId) -> {
            if (carregandoPreferencia) {
                return;
            }
            appPreferences.setSortOrder(codigoOrdenacao(checkedId));
            Snackbar.make(findViewById(R.id.layoutConfiguracoes),
                    R.string.configuracao_salva, Snackbar.LENGTH_SHORT).show();
        });

        buttonRestaurarPadrao.setOnClickListener(view -> {
            appPreferences.restoreDefaultSortOrder();
            selecionarPreferenciaAtual();
            Snackbar.make(findViewById(R.id.layoutConfiguracoes),
                    R.string.configuracao_restaurada, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void selecionarPreferenciaAtual() {
        carregandoPreferencia = true;
        String sortOrder = appPreferences.getSortOrder();
        if (AppPreferences.SORT_NAME.equals(sortOrder)) {
            radioGroupOrdenacao.check(R.id.radioOrdenarNome);
        } else if (AppPreferences.SORT_CATEGORY.equals(sortOrder)) {
            radioGroupOrdenacao.check(R.id.radioOrdenarCategoria);
        } else {
            radioGroupOrdenacao.check(R.id.radioOrdenarValidade);
        }
        carregandoPreferencia = false;
    }

    private String codigoOrdenacao(int checkedId) {
        if (checkedId == R.id.radioOrdenarNome) {
            return AppPreferences.SORT_NAME;
        }
        if (checkedId == R.id.radioOrdenarCategoria) {
            return AppPreferences.SORT_CATEGORY;
        }
        return AppPreferences.SORT_EXPIRATION;
    }

    private void aplicarInsets() {
        android.view.View layout = findViewById(R.id.layoutConfiguracoes);
        int paddingLeft = layout.getPaddingLeft();
        int paddingTop = layout.getPaddingTop();
        int paddingRight = layout.getPaddingRight();
        int paddingBottom = layout.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(layout,
                (view, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.navigationBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                    );
                    view.setPadding(
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
