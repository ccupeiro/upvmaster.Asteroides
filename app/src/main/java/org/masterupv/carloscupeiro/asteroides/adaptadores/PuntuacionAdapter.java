package org.masterupv.carloscupeiro.asteroides.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.masterupv.carloscupeiro.asteroides.R;

import java.util.List;

/**
 * Created by carlos.cupeiro on 03/10/2016.
 */

public class PuntuacionAdapter extends RecyclerView.Adapter<PuntuacionAdapter.ViewHolder> {

    private LayoutInflater inflador;
    private List<String> lista_puntuaciones;
    protected View.OnClickListener onClickListener;

    public PuntuacionAdapter(Context context, List<String> lista) {
        this.lista_puntuaciones = lista;
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elemento_puntuacion, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titulo.setText(lista_puntuaciones.get(position));
        switch (Math.round((float) Math.random() * 3)) {
            case 0:
                holder.icon.setImageResource(R.drawable.asteroide1);
                break;
            case 1:
                holder.icon.setImageResource(R.drawable.asteroide2);
                break;
            default:
                holder.icon.setImageResource(R.drawable.asteroide3);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista_puntuaciones.size();
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, subtitutlo;
        public ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
            subtitutlo = (TextView) itemView.findViewById(R.id.subtitulo);
            icon = (ImageView) itemView.findViewById(R.id.icono);
        }
    }
}
