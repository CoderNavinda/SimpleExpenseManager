package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBHandler.*;
import android.database.sqlite.SQLiteDatabase;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistantTransactionDAO implements TransactionDAO {
    private final DBHandler handler;
    private SQLiteDatabase db;

    public PersistantTransactionDAO(Context context) {

        handler = new DBHandler(context);
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expense_Type, double amount) {
        db = handler.getWritableDatabase();
        DateFormat datef = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues values = new ContentValues();
        values.put(DATE, datef.format(date));
        values.put(ACCOUNT_NO_2, accountNo);
        values.put(AMOUNT, amount);
        values.put(EXPENSE_TYPE, String.valueOf(expense_Type));
        db.insert(TABLE_NAME_2, null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transac = new ArrayList<Transaction>();
        db = handler.getReadableDatabase();
        String[] projection = {
                DATE,
                ACCOUNT_NO_2,
                EXPENSE_TYPE,
                AMOUNT
        };

        Cursor cur = db.query(
                TABLE_NAME_2,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cur.moveToNext()) {
            String date = cur.getString(cur.getColumnIndex(DATE));
            Date datef = new SimpleDateFormat("dd-MM-yyyy").parse(date); //exception handled over here
            String accountNO= cur.getString(cur.getColumnIndex(ACCOUNT_NO_2));
            String expensetype_ = cur.getString(cur.getColumnIndex(EXPENSE_TYPE));
            ExpenseType expense_Type = ExpenseType.valueOf(expensetype_);
            double amount = cur.getDouble(cur.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(datef,accountNO,expense_Type,amount);
            transac.add(transaction);
        }
        cur.close();
        return transac;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transac = new ArrayList<Transaction>();

        db = handler.getReadableDatabase();

        String[] projection = {
                DATE,
                ACCOUNT_NO_2,
                EXPENSE_TYPE,
                AMOUNT
        };

        Cursor cur = db.query(
                TABLE_NAME_2,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        int size = cur.getCount();

        while(cur.moveToNext()) {
            String date = cur.getString(cur.getColumnIndex(DATE));
            Date datef = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNO= cur.getString(cur.getColumnIndex(ACCOUNT_NO_2));
            String expensetype_ = cur.getString(cur.getColumnIndex(EXPENSE_TYPE));
            ExpenseType expense_Type = ExpenseType.valueOf(expensetype_);
            double amount = cur.getDouble(cur.getColumnIndex(AMOUNT));
            Transaction transaction = new Transaction(datef,accountNO,expense_Type,amount);

            transac.add(transaction);
        }

        if (size <= limit) {
            return transac;
        }
        return transac.subList(size - limit, size);

    }
}
