package com.example.rutas.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rutas.Activity.NavigationActivityRoute;
import com.example.rutas.R;
import com.example.rutas.entidades.Ruuta;

import java.util.List;

public class AdaterRutasView extends RecyclerView.Adapter<AdaterRutasView.RutasViewHolder> {

    List<Ruuta> rutas;

    public AdaterRutasView(List<Ruuta> rutas) {
        this.rutas = rutas;
    }

    @NonNull
    @Override
    public AdaterRutasView.RutasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_rutas_activas, parent,false );

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        vista.setLayoutParams(layoutParams);

        return new RutasViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterRutasView.RutasViewHolder holder, int position) {
        holder.txtNruta.setText(rutas.get(position).getNombre_ruta());
        holder.txtNombre.setText(rutas.get(position).getNombre_conductor());
        holder.txtapellido.setText(rutas.get(position).getFecha_inicio());

        holder.image.setVisibility(View.INVISIBLE);
        holder.drawable.setImageResource(R.mipmap.ic_ruta_foreground);
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public class RutasViewHolder extends RecyclerView.ViewHolder{

        TextView txtNruta,txtNombre,txtapellido;
        ImageView image, drawable, imageRoud;

        public RutasViewHolder(View itemView) {
            super(itemView);
            txtNruta=itemView.findViewById(R.id.textViewNombreRuta);
            txtNombre=itemView.findViewById(R.id.textViewNombreConductor);
            txtapellido=itemView.findViewById(R.id.textViewApellido);
            image = itemView.findViewById(R.id.image_cambio);
            drawable = itemView.findViewById(R.id.image_cambios);
            imageRoud = itemView.findViewById(R.id.image_drawable);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Toast.makeText(v.getContext(), "id ruta: " +
                                    rutas.get(getAdapterPosition()).getNombre_ruta(),
                            Toast.LENGTH_SHORT).show();*/
                    Intent intent = new Intent(v.getContext(), NavigationActivityRoute.class);
                    intent.putExtra("ids", rutas.get(getAdapterPosition()).getId_resu_ruta().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }

    }

}
