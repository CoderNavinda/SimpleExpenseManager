package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBHandler.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PersistantAccountDAO implements AccountDAO {
    private final DBHandler handler;
    private SQLiteDatabase db;


    public PersistantAccountDAO(Context context) {
        handler = new DBHandler(context);
    }



    public List<String> getAccountNumbersList(){
        db = handler.getReadableDatabase();

        String[] projection = {ACCOUNT_NO};

        Cursor cursor = db.query(
                TABLE_NAME_1,
                projection,
                null,
                null,
                null,
                null,
                null
        );



        List<String> accountNo = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNum = cursor.getString(cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            accountNo.add(accountNum);
        }
        cursor.close();
        return accountNo;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        db = handler.getReadableDatabase();

        String[] projection = {
                ACCOUNT_NO,
                BANK_NAME,
                NAME,
                BALANCE
        };

        Cursor cursor = db.query(
                TABLE_NAME_1,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String accountNum = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndex(BANK_NAME));
            String accountHolderName = cursor.getString(cursor.getColumnIndex(NAME));
            double balance = cursor.getDouble(cursor.getColumnIndex(BALANCE));
            accounts.add(new Account(accountNum,bankName,accountHolderName,balance));
        }
        cursor.close();
        return accounts;

    }



    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        db = handler.getReadableDatabase();
        String[] projection = {
                ACCOUNT_NO,
                BANK_NAME,
                NAME,
                BALANCE
        };

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cur = db.query(
                TABLE_NAME_1,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cur == null){
            throw new InvalidAccountException("Account Doesn't Exist");
        }
        else {
            cur.moveToFirst();

            Account account = new Account(accountNo, cur.getString(cur.getColumnIndex(BANK_NAME)),
                    cur.getString(cur.getColumnIndex(NAME)), cur.getDouble(cur.getColumnIndex(BALANCE)));
            cur.close();
            return account;
        }

    }

    @Override
    public void addAccount(Account account) {
        db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO, account.getAccountNo());
        values.put(BANK_NAME, account.getBankName());
        values.put(NAME, account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());

        db.insert(TABLE_NAME_1, null, values);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db = handler.getWritableDatabase();
        String[] s =  { accountNo };
        db.delete(TABLE_NAME_1, ACCOUNT_NO + " = ?",s);
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        db = handler.getWritableDatabase();
        String[] projection = {
                BALANCE
        };

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = db.query(
                TABLE_NAME_1,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            throw new InvalidAccountException("Account Doesn't Exist");
        }

        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put(BALANCE, balance - amount);
                break;
            case INCOME:
                values.put(BALANCE, balance + amount);
                break;
        }


        // updating row
        db.update(TABLE_NAME_1, values, ACCOUNT_NO + " = ?",
                new String[] { accountNo });

        cursor.close();
        db.close();
    }


}
