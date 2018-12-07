package br.edu.ifspsaocarlos.agenda.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;

import br.edu.ifspsaocarlos.agenda.adapter.ContatoAdapter;
import br.edu.ifspsaocarlos.agenda.model.Contato;

import java.util.ArrayList;
import java.util.List;


public class ContatoDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public ContatoDAO(Context context) {
        this.dbHelper = new SQLiteHelper(context);
    }

    public List<Contato> buscaTodosContatos() {
        database = dbHelper.getReadableDatabase();
        List<Contato> contatos = new ArrayList<>();

        Cursor cursor;

        String[] cols = new String[]{SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL,
                SQLiteHelper.KEY_FONE2, SQLiteHelper.KEY_DATA_NASC, SQLiteHelper.KEY_FAVORITO};

        cursor = database.query(SQLiteHelper.DATABASE_TABLE, cols, null, null,
                null, null, SQLiteHelper.KEY_NAME);

        while (cursor.moveToNext()) {
            Contato contato = new Contato();
            contato.setId(cursor.getInt(0));
            contato.setNome(cursor.getString(1));
            contato.setFone(cursor.getString(2));
            contato.setEmail(cursor.getString(3));
            contato.setCelular(cursor.getString(4));
            contato.setDataNascimento(cursor.getString(5));
            contato.setFavorito(cursor.getInt(6));
            contatos.add(contato);


        }
        cursor.close();


        database.close();
        return contatos;
    }

    public List<Contato> buscaContato(String nome, boolean favoritos) {
        database = dbHelper.getReadableDatabase();
        List<Contato> contatos = new ArrayList<>();

        Cursor cursor;

        String[] cols = new String[]{SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL,
                SQLiteHelper.KEY_FONE2, SQLiteHelper.KEY_DATA_NASC, SQLiteHelper.KEY_FAVORITO};
        String where = "";
        String args[] = new String[0];
        if (nome != null) {
            where = where +"("+ SQLiteHelper.KEY_NAME + " like @name OR " + SQLiteHelper.KEY_EMAIL + " like @name)";
            args = new String[]{nome + "%"};
        }
        if (favoritos && nome == null) {
            where = where + SQLiteHelper.KEY_FAVORITO + " = 1";
        } else if (favoritos && nome != null) {
            where = where + " AND " + SQLiteHelper.KEY_FAVORITO + " = ?";
            args = new String[]{nome + "%", Integer.toString(1)};
        }

        cursor = database.query(SQLiteHelper.DATABASE_TABLE, cols, where, args,
                null, null, SQLiteHelper.KEY_NAME);


        while (cursor.moveToNext()) {
            Contato contato = new Contato();
            contato.setId(cursor.getInt(0));
            contato.setNome(cursor.getString(1));
            contato.setFone(cursor.getString(2));
            contato.setEmail(cursor.getString(3));
            contato.setCelular(cursor.getString(4));
            contato.setDataNascimento(cursor.getString(5));
            contato.setFavorito(cursor.getInt(6));
            contatos.add(contato);


        }
        cursor.close();

        database.close();
        return contatos;
    }

    public void salvaContato(Contato c) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.KEY_NAME, c.getNome());
        values.put(SQLiteHelper.KEY_FONE, c.getFone());
        values.put(SQLiteHelper.KEY_EMAIL, c.getEmail());
        values.put(SQLiteHelper.KEY_FONE2, c.getCelular());
        values.put(SQLiteHelper.KEY_DATA_NASC, c.getDataNascimento());
        values.put(SQLiteHelper.KEY_FAVORITO, c.getFavorito());

        if (c.getId() > 0) {
            database.update(SQLiteHelper.DATABASE_TABLE, values, SQLiteHelper.KEY_ID + "="
                    + c.getId(), null);
        } else {
            database.insert(SQLiteHelper.DATABASE_TABLE, null, values);
        }
        database.close();

    }


    public void apagaContato(Contato c) {
        database = dbHelper.getWritableDatabase();
        database.delete(SQLiteHelper.DATABASE_TABLE, SQLiteHelper.KEY_ID + "="
                + c.getId(), null);

        database.close();
    }
}
