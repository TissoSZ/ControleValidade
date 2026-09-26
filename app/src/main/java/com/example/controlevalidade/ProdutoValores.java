package com.example.controlevalidade;

import android.content.Context;

// Códigos persistidos não mudam quando o idioma da interface muda.
public final class ProdutoValores {
    public static final String GELADEIRA = "refrigerator";
    public static final String FREEZER = "freezer";
    public static final String ARMARIO = "cabinet";
    private static final String[] CATEGORIAS = {"", "food", "beverage", "personal_care", "cleaning", "other"};
    private static final String[] UNIDADES = {"", "unit", "package", "box", "container", "bottle", "can", "kg", "g", "l", "ml"};

    private ProdutoValores() { }

    public static String categoria(Context context, String codigo) {
        return traduzir(context, R.array.categorias, CATEGORIAS, codigo);
    }

    public static String unidade(Context context, String codigo) {
        return traduzir(context, R.array.unidades, UNIDADES, codigo);
    }

    public static String codigoCategoria(Context context, String texto) {
        return codigo(context, R.array.categorias, CATEGORIAS, texto);
    }

    public static String codigoUnidade(Context context, String texto) {
        return codigo(context, R.array.unidades, UNIDADES, texto);
    }

    public static String local(Context context, String codigo) {
        if (FREEZER.equals(codigo)) return context.getString(R.string.freezer);
        if (ARMARIO.equals(codigo)) return context.getString(R.string.armario);
        return context.getString(R.string.geladeira);
    }

    private static String traduzir(Context context, int array, String[] codigos, String codigo) {
        String[] textos = context.getResources().getStringArray(array);
        for (int i = 0; i < codigos.length; i++) {
            if (codigos[i].equals(codigo)) return textos[i];
        }
        return textos[0];
    }

    private static String codigo(Context context, int array, String[] codigos, String texto) {
        String[] textos = context.getResources().getStringArray(array);
        for (int i = 0; i < textos.length; i++) {
            if (textos[i].equals(texto)) return codigos[i];
        }
        return "";
    }
}
