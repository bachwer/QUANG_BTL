package com.example.btlmad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.btlmad.model.Document;
import com.example.btlmad.model.DocumentType;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DocumentManager.db";
    private static final int DATABASE_VERSION = 2;

    // Table DocumentTypes
    private static final String TABLE_TYPE = "document_types";
    private static final String COL_TYPE_ID = "id";
    private static final String COL_TYPE_NAME = "name";
    private static final String COL_TYPE_DESC = "description";

    // Table Documents
    private static final String TABLE_DOC = "documents";
    private static final String COL_DOC_ID = "id";
    private static final String COL_DOC_NAME = "name";
    private static final String COL_DOC_TYPE_ID = "typeId";
    private static final String COL_DOC_LINK = "downloadLink";
    private static final String COL_DOC_SIZE = "sizeKb"; // stored in KB

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTypeTable = "CREATE TABLE " + TABLE_TYPE + " ("
                + COL_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE_NAME + " TEXT NOT NULL, "
                + COL_TYPE_DESC + " TEXT)";

        String createDocTable = "CREATE TABLE " + TABLE_DOC + " ("
                + COL_DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DOC_NAME + " TEXT NOT NULL, "
                + COL_DOC_TYPE_ID + " INTEGER, "
                + COL_DOC_LINK + " TEXT, "
                + COL_DOC_SIZE + " REAL, "
                + "FOREIGN KEY(" + COL_DOC_TYPE_ID + ") REFERENCES " + TABLE_TYPE + "(" + COL_TYPE_ID + ") ON DELETE SET NULL)";

        db.execSQL(createTypeTable);
        db.execSQL(createDocTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    // ── DocumentType CRUD ──────────────────────────────────────────────────────

    public long addDocumentType(DocumentType type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE_NAME, type.getName());
        values.put(COL_TYPE_DESC, type.getDescription());
        return db.insert(TABLE_TYPE, null, values);
    }

    public int updateDocumentType(DocumentType type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE_NAME, type.getName());
        values.put(COL_TYPE_DESC, type.getDescription());
        return db.update(TABLE_TYPE, values, COL_TYPE_ID + " = ?", new String[]{String.valueOf(type.getId())});
    }

    public void deleteDocumentType(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TYPE, COL_TYPE_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<DocumentType> getAllDocumentTypes() {
        List<DocumentType> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TYPE + " ORDER BY " + COL_TYPE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new DocumentType(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE_DESC))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public DocumentType getDocumentTypeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TYPE,
                new String[]{COL_TYPE_ID, COL_TYPE_NAME, COL_TYPE_DESC},
                COL_TYPE_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            DocumentType type = new DocumentType(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
            cursor.close();
            return type;
        }
        return null;
    }

    // ── Document CRUD ──────────────────────────────────────────────────────────

    public long addDocument(Document doc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DOC_NAME, doc.getName());
        values.put(COL_DOC_TYPE_ID, doc.getTypeId());
        values.put(COL_DOC_LINK, doc.getDownloadLink());
        values.put(COL_DOC_SIZE, doc.getSizeKb());
        return db.insert(TABLE_DOC, null, values);
    }

    public int updateDocument(Document doc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DOC_NAME, doc.getName());
        values.put(COL_DOC_TYPE_ID, doc.getTypeId());
        values.put(COL_DOC_LINK, doc.getDownloadLink());
        values.put(COL_DOC_SIZE, doc.getSizeKb());
        return db.update(TABLE_DOC, values, COL_DOC_ID + " = ?", new String[]{String.valueOf(doc.getId())});
    }

    public void deleteDocument(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOC, COL_DOC_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Document> getAllDocuments() {
        List<Document> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DOC + " ORDER BY " + COL_DOC_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDocument(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ── Queries ────────────────────────────────────────────────────────────────

    /** Liệt kê các tài liệu theo loại */
    public List<Document> getDocumentsByType(int typeId) {
        List<Document> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_DOC + " WHERE " + COL_DOC_TYPE_ID + " = ? ORDER BY " + COL_DOC_NAME,
                new String[]{String.valueOf(typeId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDocument(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Tìm các tài liệu có kích thước trên 1MB (1024 KB) */
    public List<Document> getDocumentsLargerThan1MB() {
        List<Document> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_DOC + " WHERE " + COL_DOC_SIZE + " > 1024 ORDER BY " + COL_DOC_SIZE + " DESC",
                null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDocument(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private Document cursorToDocument(Cursor cursor) {
        return new Document(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOC_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DOC_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOC_TYPE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DOC_LINK)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DOC_SIZE))
        );
    }
}
