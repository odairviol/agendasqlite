package br.edu.ifspsaocarlos.agenda.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.agenda.R;
import br.edu.ifspsaocarlos.agenda.adapter.ContatoAdapter;
import br.edu.ifspsaocarlos.agenda.data.ContatoDAO;
import br.edu.ifspsaocarlos.agenda.model.Contato;


public class MainActivity extends AppCompatActivity {

    private ContatoDAO cDAO;
    private RecyclerView recyclerView;

    private List<Contato> contatos = new ArrayList<>();
    private TextView empty;

    private ContatoAdapter adapter;
    private SearchView searchView;
    private MenuItem favoritos;
    private String query = null;

    private FloatingActionButton fab;

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            updateUI(null, favoritos.isChecked());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchView.clearFocus();
            this.query = query;
            updateUI(query, favoritos.isChecked());

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        handleIntent(intent);

        cDAO = new ContatoDAO(this);

        empty = findViewById(R.id.empty_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);

        adapter = new ContatoAdapter(contatos, this);
        recyclerView.setAdapter(adapter);

        setupRecyclerView();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DetalheActivity.class);
                startActivityForResult(i, 1);
            }
        });

        updateUI(null, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.pesqContato).getActionView();
        favoritos = menu.findItem(R.id.favoritos);
        favoritos.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (favoritos.isChecked()) {
                    favoritos.setChecked(false);
                } else {
                    favoritos.setChecked(true);
                }
                updateUI(query, favoritos.isChecked());
                return true;
            }
        });

        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.search_src_text);
                if (et.getText().toString().isEmpty())
                    searchView.onActionViewCollapsed();

                searchView.setQuery("", false);
                query = null;
                updateUI(null, favoritos.isChecked());
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);


        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                showSnackBar(getResources().getString(R.string.contato_adicionado));
                //   adapter.notifyItemInserted(adapter.getItemCount());
                updateUI(null, favoritos.isChecked());
            }


        if (requestCode == 2) {
            if (resultCode == RESULT_OK)
                showSnackBar(getResources().getString(R.string.contato_alterado));
            if (resultCode == 3)
                showSnackBar(getResources().getString(R.string.contato_apagado));


            updateUI(null, favoritos.isChecked());
        }
    }

    private void showSnackBar(String msg) {
        CoordinatorLayout coordinatorlayout = findViewById(R.id.coordlayout);
        Snackbar.make(coordinatorlayout, msg,
                Snackbar.LENGTH_LONG)
                .show();
    }


    private void updateUI(String nomeContato, boolean favoritos) {

        contatos.clear();

        if (nomeContato == null && !favoritos) {
            contatos.addAll(cDAO.buscaTodosContatos());
            empty.setText(getResources().getString(R.string.lista_vazia));
            fab.show();

        } else {
            contatos.addAll(cDAO.buscaContato(nomeContato, favoritos));
            empty.setText(getResources().getString(R.string.contato_nao_encontrado));
            fab.hide();


        }

        recyclerView.getAdapter().notifyDataSetChanged();

        if (recyclerView.getAdapter().getItemCount() == 0)
            empty.setVisibility(View.VISIBLE);
        else
            empty.setVisibility(View.GONE);

    }

    private void setupRecyclerView() {


        adapter.setClickListener(new ContatoAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (view.getId() == R.id.favorite) {
                    ImageButton ib = (ImageButton) view;
                    Contato c = contatos.get(position);
                    if (c.getFavorito() == null || c.getFavorito() == 0) {
                        c.setFavorito(1);
                        ib.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                        cDAO.salvaContato(c);

                    } else {
                        c.setFavorito(0);
                        ib.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
                        cDAO.salvaContato(c);
                    }
                } else {
                    final Contato contato = contatos.get(position);
                    Intent i = new Intent(getApplicationContext(), DetalheActivity.class);
                    i.putExtra("contato", contato);
                    startActivityForResult(i, 2);
                }
            }
        });


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    Contato contato = contatos.get(viewHolder.getAdapterPosition());
                    cDAO.apagaContato(contato);
                    contatos.remove(viewHolder.getAdapterPosition());
                    recyclerView.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
                    showSnackBar(getResources().getString(R.string.contato_apagado));

                }
            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorDelete));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_remove);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

}
