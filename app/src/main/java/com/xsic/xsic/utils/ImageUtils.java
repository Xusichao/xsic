package com.xsic.xsic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUtils
{
	/**
	 * 获得最接近比例
	 * 
	 * @param srcScale
	 *            目标比例
	 * @param dstScaleArr
	 * @return
	 */
	public static int GetScale(float srcScale, ArrayList<Float> dstScaleArr)
	{
		int index = -1;

		int len = dstScaleArr.size();
		if(len > 0)
		{
			float min = Math.abs(srcScale - dstScaleArr.get(0));
			index = 0;
			float temp;
			for(int i = 1; i < len; i++)
			{
				temp = Math.abs(srcScale - dstScaleArr.get(i));
				if(temp < min)
				{
					min = temp;
					index = i;
				}
			}
		}

		return index;
	}

	/**
	 * 获取2点间的距离
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static float Spacing(float dx, float dy)
	{
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * 生成圆角bmp
	 * 
	 * @param bmp
	 * @param px
	 * @return
	 */
	public static Bitmap MakeRoundBmp(Bitmap bmp, float px)
	{
		Bitmap out = bmp;

		if(bmp != null && px > 0)
		{
			out = MakeRoundBmp(bmp, bmp.getWidth(), bmp.getHeight(), px);
		}

		return out;
	}

	public static Bitmap MakeRoundBmp(Bitmap bmp, int w, int h, float px)
	{
		Bitmap out = null;

		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && w > 0 && h > 0)
		{
			out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			Paint pt = new Paint();
			pt.setColor(0xFFFFFFFF);
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setStyle(Paint.Style.FILL);
			if(px > 0)
			{
				canvas.drawRoundRect(new RectF(0, 0, w, h), px, px, pt);
			}
			else
			{
				canvas.drawRect(0, 0, w, h, pt);
			}

			pt.reset();
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			Matrix m = new Matrix();
			float s;
			{
				float s1 = (float)w / (float)bmp.getWidth();
				float s2 = (float)h / (float)bmp.getHeight();
				s = s1 > s2 ? s1 : s2;
			}
			m.postTranslate((w - bmp.getWidth()) / 2f, (h - bmp.getHeight()) / 2f);
			m.postScale(s, s, w / 2f, h / 2f);
			canvas.drawBitmap(bmp, m, pt);
		}

		return out;
	}

	public static Bitmap MakeResRoundBmp(Context context, int res, int w, int h, float px)
	{
		Bitmap out = null;

		if(context != null && w > 0 && h > 0)
		{
			Bitmap temp = BitmapFactory.decodeResource(context.getResources(), res);
			if(temp != null)
			{
				out = MakeRoundBmp(temp, w, h, px);
				if(temp != out)
				{
					temp.recycle();
					temp = null;
				}
			}
		}

		return out;
	}

	public static Bitmap MakeColorRoundBmp(int color, int w, int h, float px)
	{
		Bitmap out = null;

		if(w > 0 && h > 0)
		{
			out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			Paint pt = new Paint();
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setColor(color);
			pt.setStyle(Paint.Style.FILL);
			if(px > 0)
			{
				canvas.drawRoundRect(new RectF(0, 0, w, h), px, px, pt);
			}
			else
			{
				canvas.drawRect(0, 0, w, h, pt);
			}
		}

		return out;
	}

	/**
	 * 判断是不是图片文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean IsImageFile(String path)
	{
		boolean out = false;

		if(new File(path).exists())
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			if(opts.outWidth > 0 && opts.outHeight > 0)
			{
				out = true;
			}
		}

		return out;
	}

	public static void saveBitmap(Bitmap bitmap,String path)
	{
		if(bitmap == null)
		{
			return;
		}


		FileOutputStream out = null;
		try {
			File file = new File(path);
			if(!file.exists())
			{
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap MakeDiffCornerRoundBmp(Bitmap bmp, float leftTopPx, float rightTopPx, float leftBottomPx, float rightBottomPx)
	{
		Bitmap out = null;
		int w = bmp.getWidth();
		int h = bmp.getHeight();

		if(bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0 && w > 0 && h > 0)
		{
			out = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			Paint pt = new Paint();
			pt.setColor(0xFFFFFFFF);
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setStyle(Paint.Style.FILL);
			Path path = RoundedRect(new RectF(0, 0, w, h), leftTopPx, rightTopPx, leftBottomPx, rightBottomPx);
			canvas.drawPath(path, pt);

			pt.reset();
			pt.setAntiAlias(true);
			pt.setFilterBitmap(true);
			pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			Matrix m = new Matrix();
			float s;
			{
				float s1 = (float)w / (float)bmp.getWidth();
				float s2 = (float)h / (float)bmp.getHeight();
				s = s1 > s2 ? s1 : s2;
			}
			m.postTranslate((w - bmp.getWidth()) / 2f, (h - bmp.getHeight()) / 2f);
			m.postScale(s, s, w / 2f, h / 2f);
			canvas.drawBitmap(bmp, m, pt);
		}

		return out;
	}

	static public Path RoundedRect(RectF rect, float leftTopPx, float rightTopPx, float leftBottomPx, float rightBottomPx)
	{
		float left = rect.left;
		float top = rect.top;
		float right = rect.right;
		float bottom = rect.bottom;
		Path path = new Path();
		if (leftTopPx < 0) leftTopPx = 0;
		if (rightTopPx < 0) rightTopPx = 0;
		if (leftBottomPx < 0) leftBottomPx = 0;
		if (rightBottomPx < 0) rightBottomPx = 0;
		float width = right - left;
		float height = bottom - top;
		float widthMinusCorners = (width - rightTopPx - leftTopPx);
		float heightMinusCorners = (height - leftTopPx - leftBottomPx);

		path.moveTo(right, top + rightTopPx);
		path.rQuadTo(0, -rightTopPx, -rightTopPx, -rightTopPx);//top-right corner
		path.rLineTo(-widthMinusCorners, 0);
		path.rQuadTo(-leftTopPx, 0, -leftTopPx, leftTopPx); //top-left corner
		path.rLineTo(0, heightMinusCorners);

		widthMinusCorners = (width - rightBottomPx - leftBottomPx);
		heightMinusCorners = (height - rightBottomPx - rightTopPx);
		path.rQuadTo(0, leftBottomPx, leftBottomPx, leftBottomPx);//bottom-left corner
		path.rLineTo(widthMinusCorners, 0);
		path.rQuadTo(rightBottomPx, 0, rightBottomPx, -rightBottomPx); //bottom-right corner
		path.rLineTo(0, -heightMinusCorners);

		path.close();//Given close, last lineto can be removed.

		return path;
	}
}
