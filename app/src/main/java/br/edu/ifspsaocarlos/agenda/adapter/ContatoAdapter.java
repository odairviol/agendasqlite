package br.edu.ifspsaocarlos.agenda.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import br.edu.ifspsaocarlos.agenda.model.Contato;
import br.edu.ifspsaocarlos.agenda.R;

import java.util.List;


public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ContatoViewHolder> {

    private static List<Contato> contatos;
    private Context context;


    private static ItemClickListener clickListener;


    public ContatoAdapter(List<Contato> contatos, Context context) {
        ContatoAdapter.contatos = contatos;
        this.context = context;
    }

    @Override
    public ContatoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contato_celula, parent, false);
        return new ContatoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ContatoViewHolder holder, int position) {
        Contato c = contatos.get(position);
        holder.nome.setText(c.getNome());
        holder.favorito.setImageDrawable(ContextCompat.getDrawable(context,
                c.getFavorito() != null && c.getFavorito() == 1 ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off));
    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }


    public class ContatoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nome;
        final ImageButton favorito;

        ContatoViewHolder(View view) {
            super(view);
            nome = view.findViewById(R.id.nome);
            favorito = view.findViewById(R.id.favorite);
            view.setOnClickListener(this);
            favorito.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onItemClick(getAdapterPosition(), view);
        }
    }


    public interface ItemClickListener {
        void onItemClick(int position, View view);
    }

}


