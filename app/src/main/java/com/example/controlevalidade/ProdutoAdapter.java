package com.example.controlevalidade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
        TextView textViewStatus = itemView.findViewById(R.id.textViewStatus);
        ImageView imageViewLocal = itemView.findViewById(R.id.imageViewLocal);
        ImageView imageViewLocalPequeno = itemView.findViewById(R.id.imageViewLocalPequeno);
        MaterialCardView card = itemView.findViewById(R.id.cardProduto);

        if (produto != null) {
            textViewNome.setText(getContext().getString(
                    R.string.titulo_produto_com_quantidade,
                    produto.getNome(), produto.getQuantidade(), produto.getUnidade()
            ));
            textViewCategoria.setText(produto.getCategoria());
            textViewValidade.setText(produto.getValidade());
            textViewLocal.setText(produto.getLocalArmazenamento());
            int localIcon = iconeLocal(produto.getLocalArmazenamento());
            imageViewLocal.setImageResource(localIcon);
            imageViewLocalPequeno.setImageResource(localIcon);
            aplicarEstado(produto, card, textViewStatus, textViewValidade);
        }

        return itemView;
    }

    private int iconeLocal(String local) {
        if (getContext().getString(R.string.freezer).equals(local)) return R.drawable.ic_freezer_24;
        if (getContext().getString(R.string.armario).equals(local)) return R.drawable.ic_armario_24;
        return R.drawable.ic_kitchen_24;
    }

    private void aplicarEstado(Produto produto, MaterialCardView card, TextView chip, TextView data) {
        long dias = Long.MAX_VALUE;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formato.setLenient(false);
            Date validade = formato.parse(produto.getValidade());
            Date hoje = formato.parse(formato.format(new Date()));
            if (validade != null && hoje != null) dias = TimeUnit.MILLISECONDS.toDays(validade.getTime() - hoje.getTime());
        } catch (Exception ignored) { }
        int fundoCard = R.color.white, borda = R.color.card_stroke, fundoChip = R.color.md_surface_container;
        int texto = R.color.ok_text, corData = R.color.md_on_surface_variant;
        String status = getContext().getString(R.string.status_ok);
        if (dias < 0) { fundoCard=R.color.error_card_bg; borda=R.color.error_card_stroke; fundoChip=R.color.error_container; texto=R.color.error_text; corData=R.color.error_text; status=getContext().getString(R.string.status_vencido); }
        else if (dias <= 7) { fundoCard=R.color.warning_card_bg; borda=R.color.warning_card_stroke; fundoChip=R.color.warning_container; texto=R.color.warning; corData=R.color.warning; status = dias == 0 ? getContext().getString(R.string.status_vence_hoje) : dias == 1 ? getContext().getString(R.string.status_vence_amanha) : getContext().getString(R.string.status_vence_dias, dias); }
        card.setCardBackgroundColor(ContextCompat.getColor(getContext(), fundoCard));
        card.setStrokeColor(ContextCompat.getColor(getContext(), borda));
        chip.setText(status); chip.setTextColor(ContextCompat.getColor(getContext(), texto));
        GradientDrawable bg = new GradientDrawable(); bg.setColor(ContextCompat.getColor(getContext(), fundoChip)); bg.setCornerRadius(dp(8));
        chip.setBackground(bg); chip.setPadding(dp(8), dp(3), dp(8), dp(3));
        data.setTextColor(ContextCompat.getColor(getContext(), corData));
    }

    private int dp(int value) { return Math.round(value * getContext().getResources().getDisplayMetrics().density); }
}
