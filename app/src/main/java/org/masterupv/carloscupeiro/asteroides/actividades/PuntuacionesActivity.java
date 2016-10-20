package org.masterupv.carloscupeiro.asteroides.actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.masterupv.carloscupeiro.asteroides.R;
import org.masterupv.carloscupeiro.asteroides.adaptadores.PuntuacionAdapter;

public class PuntuacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PuntuacionAdapter adaptador;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.puntuaciones);
        recyclerView = (RecyclerView) findViewById(R.id.rv_puntuaciones);
        adaptador = new PuntuacionAdapter(this, MainActivity.almacen.listaPuntuaciones(10));
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                String s = MainActivity.almacen.listaPuntuaciones(10).get(pos);
                Toast.makeText(PuntuacionesActivity.this, "Selección: " + pos + " - " + s,
                        Toast.LENGTH_LONG).show();
            }
        });
        recyclerView.setAdapter(adaptador);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
