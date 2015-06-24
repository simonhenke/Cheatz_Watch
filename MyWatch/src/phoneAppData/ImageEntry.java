package phoneAppData;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class ImageEntry implements Entry {
	private final Entry.Type type;

	private Bitmap image;

	private Bitmap getScaledBitmap(Bitmap bitmap) {
		float reqWidth;
		float reqHeight;
		// if height is bigger than width
		if (bitmap.getHeight() > bitmap.getWidth()) {
			// make height to 240
			reqHeight = 240;
			reqWidth = (reqHeight / bitmap.getHeight()) * bitmap.getWidth();
		} else {
			// make width to 240
			reqWidth = 240;
			reqHeight = (reqWidth / bitmap.getWidth()) * bitmap.getHeight();
		}

		Matrix m = new Matrix();
		m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
				new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), m, true);
	}

	public ImageEntry(Bitmap bitmap) {
		type = Entry.Type.IMAGE_ENTRY;
		if (bitmap.getHeight() > 240 || bitmap.getWidth() > 240) {
			this.image = getScaledBitmap(bitmap);
		} else {
			this.image = bitmap;
		}
	}

	@Override
	public Entry.Type getType() {
		return type;
	}
	
	public String getSerializationName(int chapterNumber, int entryNumber) {
		return "image_" + chapterNumber + "_" + entryNumber + ".jpg";
	}

	/**
	 * Returns the Bitmap represented by the ImageView
	 * 
	 * @return shown bitmap
	 */
	public Bitmap getImage() {
		return this.image;
	}

}
