package com.example.controlevalidade;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProdutoDao {
    @Query("SELECT * FROM produtos ORDER BY id")
    List<Produto> consultarTodos();

    @Query("SELECT * FROM produtos WHERE id = :id")
    Produto consultarPorId(long id);

    @Insert
    long inserir(Produto produto);

    @Update
    int atualizar(Produto produto);

    @Delete
    int excluir(Produto produto);
}
