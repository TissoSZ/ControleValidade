package com.example.controlevalidade;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Produto.class}, version = 1, exportSchema = false)
public abstract class ProdutoDatabase extends RoomDatabase {
    private static volatile ProdutoDatabase instance;
    // Uma fila para preservar a ordem entre gravações e consultas, fora da UI.
    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public abstract ProdutoDao produtoDao();

    public static ProdutoDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (ProdutoDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            ProdutoDatabase.class, "produtos.db").build();
                }
            }
        }
        return instance;
    }
}
