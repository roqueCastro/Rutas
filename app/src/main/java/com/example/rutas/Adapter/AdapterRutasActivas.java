package com.example.rutas.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.example.rutas.Activity.SubMainColegio;
import com.example.rutas.R;
import com.example.rutas.Utilidades.Utilidades_Request;
import com.example.rutas.entidades.Rutasa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdapterRutasActivas extends RecyclerView.Adapter<AdapterRutasActivas.RutasActivasHolder> {
    int p;

    List<Rutasa> rutasa;

    View convertView;

    public AdapterRutasActivas(List<Rutasa> rutasa) {
        this.rutasa = rutasa;
    }

    @NonNull
    @Override
    public AdapterRutasActivas.RutasActivasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vista_rutas_activas, parent,false );

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        vista.setLayoutParams(layoutParams);

        return new RutasActivasHolder(vista);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdapterRutasActivas.RutasActivasHolder holder, int position) {
        holder.txtNruta.setText(rutasa.get(position).getNombre_ruta());
        holder.txtNombre.setText(rutasa.get(position).getNombre_c());
        holder.txtapellido.setText("Conductor");
        if (rutasa.get(position).getEstado().equals(1)){
            holder.image.setVisibility(View.INVISIBLE);
        }else {
            holder.drawable.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return rutasa.size();
    }

    public class RutasActivasHolder extends RecyclerView.ViewHolder{

        TextView txtNruta,txtNombre,txtapellido;
        ImageView image, drawable;

        public RutasActivasHolder(View itemView) {
            super(itemView);
            txtNruta=itemView.findViewById(R.id.textViewNombreRuta);
            txtNombre=itemView.findViewById(R.id.textViewNombreConductor);
            txtapellido=itemView.findViewById(R.id.textViewApellido);
            image = itemView.findViewById(R.id.image_cambio);
            drawable = itemView.findViewById(R.id.image_cambios);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SubMainColegio.class);
                    intent.putExtra("id_ruta", rutasa.get(getAdapterPosition()).getId().toString());
                    v.getContext().startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    p=getAdapterPosition();
                    showAlertDeleteRuta(rutasa.get(getAdapterPosition()).getId().toString(), v);
                    return false;
                }
            });
        }

    }

    private void showAlertDeleteRuta(final String id, final View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        builder.setTitle("Eliminacion");

        View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_empty, null);
        builder.setView(viewInflated);


        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cargarWebServiceDeleteConductor(id, v);
                dialog.cancel();
            }
        });

        builder.setNegativeButton("No", null);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cargarWebServiceDeleteConductor(final String id, final View v) {
        StringRequest stringRequest;
        RequestQueue request = Volley.newRequestQueue(v.getContext());

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"_ws_delete-ruta_.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.trim().equals("registra")){
                    //mensajeAlertaTextViewError("No registro ocurrio un error vuelva a intentarlo. ", 3000);
                    Toast.makeText(v.getContext(), "Eliminado",Toast.LENGTH_SHORT).show();
                    rutasa.remove(p);
                    notifyItemRemoved(p);



                }else{
                    //mensajeAlertaTextViewVerdadero("Obra construida registrada con Exito.", 2000);
                    //cargarWebServiceActivid(id);
                    Toast.makeText(v.getContext(), "Ocurrio un error",Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mensajeAlertaTextViewError("Ocurrio un error en el servidor ", 3000);
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("id", id);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }
}
