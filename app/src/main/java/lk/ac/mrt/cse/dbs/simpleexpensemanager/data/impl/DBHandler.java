package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "200452N.sqlite";
    private static final int DB_VERSION = 1;

    //accounts table
    public static final String TABLE_NAME_1 = "accounts";
    public static final String ID_COL = "id";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String NAME = "name";
    public static final String BANK_NAME = "bankName";
    public static final String BALANCE = "balance";

    //transactions table
    public static final String TABLE_NAME_2 = "transactions";
    public static final String ID_COL_2 = "id";
    public static final String ACCOUNT_NO_2 = "accountNO";
    public static final String DATE = "date";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";


    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query1 = "CREATE TABLE " + TABLE_NAME_1 + " ("
                + ACCOUNT_NO + " TEXT PRIMARY KEY,"
                + NAME + " TEXT NOT NULL,"
                + BANK_NAME + " TEXT NOT NULL,"
                + BALANCE + " REAL NOT NULL)";
        String query2 = "CREATE TABLE " + TABLE_NAME_2 + " ("
                + ID_COL_2 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE + " TEXT NOT NULL,"
                + EXPENSE_TYPE + " TEXT NOT NULL,"
                + AMOUNT + " REAL NOT NULL,"
                + ACCOUNT_NO_2 + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NO_2 + ") REFERENCES " + TABLE_NAME_1 + "(" + ACCOUNT_NO + "))";

        db.execSQL(query1);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        onCreate(db);
    }


}