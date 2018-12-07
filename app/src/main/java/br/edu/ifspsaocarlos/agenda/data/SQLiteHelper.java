package br.edu.ifspsaocarlos.agenda.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "agenda.db";
    static final String DATABASE_TABLE = "contatos";
    static final String KEY_ID = "id";
    static final String KEY_NAME = "nome";
    static final String KEY_FONE = "fone";
    static final String KEY_FONE2 = "fone2";
    static final String KEY_EMAIL = "email";
    static final String KEY_FAVORITO = "favorito";
    static final String KEY_DATA_NASC = "dtNasc";
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_NAME + " TEXT NOT NULL, " +
            KEY_FONE + " TEXT, " +
            KEY_FONE2 + " TEXT, " +
            KEY_EMAIL + " TEXT," +
            KEY_DATA_NASC + " STRING," +
            KEY_FAVORITO + " INTEGER DEFAULT 0);";

    private static final String DATABASE_UPDATE_V2 = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_FAVORITO + " INTEGER DEFAULT 0;";
    private static final String DATABASE_UPDATE_V3 = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_FONE2 + " TEXT;";
    private static final String DATABASE_UPDATE_V4 = "ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_DATA_NASC + " STRING;";


    SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            database.execSQL(DATABASE_UPDATE_V2);
        }
        if (oldVersion < 3) {
            database.execSQL(DATABASE_UPDATE_V3);
        }
        if (oldVersion < 4) {
            database.execSQL(DATABASE_UPDATE_V4);
        }
    }
}

