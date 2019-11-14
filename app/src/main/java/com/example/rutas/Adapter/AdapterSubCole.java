package com.example.rutas.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rutas.R;
import com.example.rutas.entidades.SubCole;

import java.util.List;

public class AdapterSubCole extends RecyclerView.Adapter<AdapterSubCole.SubColeHolder> {

    List<SubCole> subColes;

    public AdapterSubCole(List<SubCole> subColes) {
        this.subColes = subColes;
    }

    @NonNull
    @Override
    public AdapterSubCole.SubColeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_rutas_activas, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        vista.setLayoutParams(layoutParams);

        return new SubColeHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSubCole.SubColeHolder holder, int position) {

        holder.txtNruta.setText(subColes.get(position).getNom_ruta());
        holder.txtNombre.setText(subColes.get(position).getName_conductor());

        if (subColes.get(position).getEstado().equals("0")){
            holder.txtapellido.setText(Html.fromHtml("<font color=#4AE300> " + "En linea" + "</font>"));
            //holder.txtapellido.setTextColor(Color.parseColor("#5DE603"));
        }else
            holder.txtapellido.setText("Finalizada");
            holder.txtapellido.setTextColor(Color.parseColor("#2234D9"));
    }

    @Override
    public int getItemCount() {
        return subColes.size();
    }


    public class SubColeHolder extends RecyclerView.ViewHolder {

        TextView txtNruta, txtNombre, txtapellido;

        public SubColeHolder(View itemView) {
            super(itemView);
            //
            txtNruta = itemView.findViewById(R.id.textViewNombreRuta);
            txtNombre = itemView.findViewById(R.id.textViewNombreConductor);
            txtapellido = itemView.findViewById(R.id.textViewApellido);

        }
    }
}
