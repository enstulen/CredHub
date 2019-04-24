package com.uc3m.credhub;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Base64;

import net.sqlcipher.database.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "credhub.db";
    public static final String TABLE_NAME = "password_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "description";
    public static final String COL_3 = "username";
    public static final String COL_4 = "password";

    private static DatabaseHelper single_instance = null;
    private static final String KEY_ALIAS = "MYALIAS";

    public Context context;
    public SQLiteDatabase db;

    private Encryptor encryptor;
    private Decryptor decryptor;
    

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    /**
     * Returns singleton instance of Databasehelper. Creates an encryptor and decryptor to encrypt/decrypt password
     * The encrypted password is stored in SharedPreferences along with the IV. Creates a random password if one
     * is not already created by using the random UUID function.
     * @param context
     * @return
     */

    public static DatabaseHelper getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new DatabaseHelper(context);
            SQLiteDatabase.loadLibs(context);

            single_instance.encryptor = new Encryptor();
            try {
                single_instance.decryptor = new Decryptor();
            } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                    IOException e) {
                e.printStackTrace();
            }


            SharedPreferences prefs = context.getSharedPreferences("encryptedDBPassword", MODE_PRIVATE);
            String encryptedDBPassword = prefs.getString("encryptedDBPassword", "");

            if (encryptedDBPassword.equals("")) {
                //Create new password
                String randomString = UUID.randomUUID().toString();
                String encryptedString = single_instance.encryptText(randomString);
                SharedPreferences.Editor mEditor = prefs.edit();
                mEditor.putString("encryptedDBPassword",encryptedString);
                String iv = Base64.encodeToString(single_instance.encryptor.getIv(), Base64.DEFAULT);
                mEditor.putString("iv",iv);
                mEditor.commit();
                single_instance.db = single_instance.getWritableDatabase(single_instance.decryptText(encryptedString));

            } else {
                single_instance.db = single_instance.getWritableDatabase(single_instance.decryptText(encryptedDBPassword));
            }



        }
        return single_instance;
    }

    /**
     * Create database with static variables above.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION TEXT, USERNAME TEXT, PASSWORD TEXT) ");

    }

    /**
     * Upgrade database by dropping the table and creating it from scratch again with onCreate(db)
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert data in the database. The three paramaters corresponds to the columns in the database.
     * @param description
     * @param username
     * @param password
     * @return
     */
    public boolean insertData(String description, String username, String password) {
        if (db == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, username);
        contentValues.put(COL_4, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        }
        return true;

    }

    /**
     * Returns all data in the database
     * @return
     */
    public Cursor getAllData() {
        if (db == null) {
            return null;
        }
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    /**
     * Delete the data in the database
     * @param id
     * @return
     */
    public Integer deleteData(String id) {
        if (db == null) {
            return null;
        }
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }


    /**
     * Decrypt text using the decryptor class. If there is no IV in memory, get it from SharedPreferences.
     * @param text
     * @return
     */
    private String decryptText(String text) {
        try {
            byte[] data = Base64.decode(text, Base64.DEFAULT);

            System.out.println(encryptor.getIv());

            if (encryptor.getIv() == null) {
                SharedPreferences prefs = context.getSharedPreferences("encryptedDBPassword", MODE_PRIVATE);
                String iv = prefs.getString("iv", "");
                byte[] ivData = Base64.decode(iv, Base64.DEFAULT);
                return decryptor
                        .decryptData(KEY_ALIAS, data, ivData);
            }

            return decryptor
                    .decryptData(KEY_ALIAS, data, encryptor.getIv());
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            return null;
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt text using the encryptor class.
     * @param text
     * @return
     */

    public String encryptText(String text) {

        try {
            final byte[] encryptedText = encryptor
                    .encryptText(KEY_ALIAS, text);

            String encryptedTextString = Base64.encodeToString(encryptedText, Base64.DEFAULT);
            return encryptedTextString;

        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            return null;
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
