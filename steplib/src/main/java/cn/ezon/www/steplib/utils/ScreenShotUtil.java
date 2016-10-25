package cn.ezon.www.steplib.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class ScreenShotUtil {

	public static Bitmap takeScreenShot(Activity activity) {
		// View是你�?��截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		int statusBarHeight = getStatusBarHeight(activity);

		// 获取屏幕长和�?
		int width = DeviceUtils.getScreenWidth(activity);
		// 去掉标题�?
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, b1.getHeight() - statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	public static int getStatusBarHeight(Activity activity){
		// 获取状�?栏高�?
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	private static void savePic(Bitmap b, File filePath) throws Exception {
		FileOutputStream fos = new FileOutputStream(filePath);
		if (null != fos) {
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		}
	}

	public static void shoot(Activity activity, File filePath, OnScreenShotCompleteListener l) {
		if (filePath == null) {
			return;
		}
		if (!filePath.getParentFile().exists()) {
			filePath.getParentFile().mkdirs();
		}
		try {
			Bitmap bitmap = takeScreenShot(activity);
			if (bitmap == null && l != null) {
				l.onShotComplete(false, null);
				return;
			}
			savePic(bitmap, filePath);
			if (l != null) {
				l.onShotComplete(true, bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (l != null) {
				l.onShotComplete(false, null);
			}
		}
	}

	public interface OnScreenShotCompleteListener {
		void onShotComplete(boolean isSuccess, Bitmap b);
	}
}
