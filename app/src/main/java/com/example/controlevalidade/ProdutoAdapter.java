package com.example.controlevalidade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProdutoAdapter extends ArrayAdapter<Produto> {

    public ProdutoAdapter(@NonNull Context context, @NonNull List<Produto> produtos) {
        super(context, 0, produtos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_produto, parent, false);
        }

        Produto produto = getItem(position);

        TextView textViewNome = itemView.findViewById(R.id.textViewNomeProduto);
        TextView textViewCategoria = itemView.findViewById(R.id.textViewCategoriaProduto);
        TextView textViewValidade = itemView.findViewById(R.id.textViewValidadeProduto);
        TextView textViewLocal = itemView.findViewById(R.id.textViewLocalProduto);

        if (produto != null) {
            textViewNome.setText(produto.getNome());
            textViewCategoria.setText(getContext().getString(
                    R.string.categoria_produto,
                    produto.getCategoria()
            ));
            textViewValidade.setText(getContext().getString(
                    R.string.validade_produto,
                    produto.getValidade()
            ));
            textViewLocal.setText(getContext().getString(
                    R.string.local_produto,
                    produto.getLocalArmazenamento()
            ));
        }

        return itemView;
    }
}
