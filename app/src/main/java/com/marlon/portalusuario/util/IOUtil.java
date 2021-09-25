package com.marlon.portalusuario.util;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.File;

public class IOUtil {


	public static File getInternalStorageAppFilesDirectoryAsFile(@Nullable Context context) {

		if (context == null) {
			return null;
		}

		File externalFilesDir = context.getExternalFilesDir(null);
		if (externalFilesDir == null) {
			return null;
		}

		// 初回は存在していないので作成しておく
		externalFilesDir.mkdirs();

		return externalFilesDir;
	}
}
