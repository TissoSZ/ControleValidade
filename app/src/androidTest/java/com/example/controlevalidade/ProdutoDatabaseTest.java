package com.example.controlevalidade;

import android.content.Context;
import android.content.res.Configuration;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ProdutoDatabaseTest {
    @Test
    public void persistenciaEdicaoPorIdEExclusao() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String nomeBanco = "produtos-teste.db";
        context.deleteDatabase(nomeBanco);
        ProdutoDatabase database = Room.databaseBuilder(context, ProdutoDatabase.class, nomeBanco).build();
        try {
            Produto primeiro = new Produto("Arroz", "1", "kg", "food", "20/12/2026", "cabinet", false);
            Produto segundo = new Produto("Leite", "2", "bottle", "beverage", "10/12/2026", "refrigerator", true);
            long id = database.produtoDao().inserir(primeiro);
            long outroId = database.produtoDao().inserir(segundo);
            assertTrue(id > 0);
            assertNotEquals(id, outroId);
            database.close();
            database = Room.databaseBuilder(context, ProdutoDatabase.class, nomeBanco).build();
            ProdutoDao dao = database.produtoDao();
            List<Produto> produtos = dao.consultarTodos();
            assertEquals(2, produtos.size());
            Collections.reverse(produtos);
            Produto editado = produtos.get(0);
            assertEquals(outroId, editado.getId());
            editado.setNome("Leite integral");
            assertEquals(1, dao.atualizar(editado));
            assertEquals("Arroz", dao.consultarPorId(id).getNome());
            assertEquals("Leite integral", dao.consultarPorId(outroId).getNome());
            assertEquals("bottle", dao.consultarPorId(outroId).getUnidade());
            assertEquals(1, dao.excluir(editado));
            assertNull(dao.consultarPorId(outroId));
            assertEquals(1, dao.excluir(dao.consultarPorId(id)));
            assertTrue(dao.consultarTodos().isEmpty());
        } finally {
            database.close();
            context.deleteDatabase(nomeBanco);
        }
    }

    @Test
    public void codigosIndependentesDoIdioma() {
        Context original = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Configuration config = new Configuration(original.getResources().getConfiguration());
        config.setLocale(Locale.forLanguageTag("pt-BR"));
        Context portugues = original.createConfigurationContext(config);
        config.setLocale(Locale.ENGLISH);
        Context ingles = original.createConfigurationContext(config);
        for (String texto : portugues.getResources().getStringArray(R.array.categorias)) {
            String codigo = ProdutoValores.codigoCategoria(portugues, texto);
            assertEquals(codigo, ProdutoValores.codigoCategoria(ingles, ProdutoValores.categoria(ingles, codigo)));
            assertEquals(texto, ProdutoValores.categoria(portugues, codigo));
        }
        for (String texto : ingles.getResources().getStringArray(R.array.unidades)) {
            String codigo = ProdutoValores.codigoUnidade(ingles, texto);
            assertEquals(codigo, ProdutoValores.codigoUnidade(portugues, ProdutoValores.unidade(portugues, codigo)));
            assertEquals(texto, ProdutoValores.unidade(ingles, codigo));
        }
        assertEquals("Geladeira", ProdutoValores.local(portugues, ProdutoValores.GELADEIRA));
        assertEquals("Refrigerator", ProdutoValores.local(ingles, ProdutoValores.GELADEIRA));
    }
}
