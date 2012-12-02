package org.llistaCompra.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class FileProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri
			.parse("content://org.llistaCompra.files/");
	private static final HashMap<String, String> MIME_TYPES = new HashMap<String, String>();

	static {
		MIME_TYPES.put(".txt", "text/plain");
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public String getType(Uri uri) {
		String path = uri.toString();

		for (String extension : MIME_TYPES.keySet()) {
			if (path.endsWith(extension)) {
				return (MIME_TYPES.get(extension));
			}
		}

		return (null);
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		File f = new File(getContext().getFilesDir(), uri.getPath());

		if (f.exists()) {
			return (ParcelFileDescriptor.open(f,
					ParcelFileDescriptor.MODE_READ_ONLY));
		}

		throw new FileNotFoundException(uri.getPath());
	}

	@Override
	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		throw new RuntimeException("Operation not supported");
	}

	
}
