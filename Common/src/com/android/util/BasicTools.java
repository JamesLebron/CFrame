package com.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
/*
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
*/
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class BasicTools {

    /**
     * 检测网络是否通畅 *
     */
    public static boolean checkNetworkAvailable(Context ctx) {
        Logs.d("TMD", "Check Network Is Available");
        ConnectivityManager connectivity = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Logs.d("TMD", "-----Network Enable-----");
                        return true;
                    }

        }
        Logs.d("TMD", "----Network Disable----");
        return false;
    }

    public static String md5(String data) {
        String str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }

    // 普通Json数据解析
    public static String parseJson(String strResult, String attribute) {
        String result = null;
        try {
            JSONObject jsonObj = new JSONObject(strResult);
            result = jsonObj.getString(attribute);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 解析远程指令
    @SuppressWarnings("unchecked")
    public static Map parseCMD(String result) {
        try {
            Map map = new HashMap();
            String act = BasicTools.parseJson(result, "cmd");
            String cmd = BasicTools.parseJson(act, "cmd");
            map.put("cmd", cmd);
            String params = BasicTools.parseJson(act, "params");
            JSONObject json = new JSONObject(params);
            Iterator keys = json.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = json.get(key).toString();
                if (value.startsWith("{") && value.endsWith("}")) {
                    map.put(key, parseCMD(value));
                } else {
                    map.put(key, value);
                }

            }
            // Object obj = JsonUtil.jsonToObj(params);
            // HashMap<String,String> ps = (HashMap<String,String>) obj;
            // map.put("cmd", cmd);
            // Iterator<String> iterator = ps.keySet()
            // .iterator();
            // while (iterator.hasNext()) {
            // Object key = iterator.next();
            // final String val = ps.get(key);
            // map.put((String)key, val);
            // }
            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static int checkJsonStatus(String result) {
        String status = parseJson(result, "error_code");
        if (status != null) {
            return Integer.parseInt(status);
        } else {
            return -1;
        }
    }

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static InputStream getStreamFromURL(String imageURL) {
        InputStream in = null;
        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            in = connection.getInputStream();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return in;

    }

    public static boolean runCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            Logs.i("command", "The Command is : " + command);
            process.waitFor();
        } catch (Exception e) {
            Logs.w("Exception ", "Unexpected error - " + e.getMessage());
            return false;
        } finally {
            try {
                process.destroy();
            } catch (Exception e) {
                Logs.w("Exception ", "Unexpected error - " + e.getMessage());
            }
        }
        return true;
    }

    public static void saveBitmap(String bitName, Bitmap mBitmap) {
        File f = new File(Environment.getExternalStorageDirectory() + "/"
                + bitName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            String log = "在保存图片时出错：" + e.toString();
            Logs.e("TMD", log);
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 60, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Convert Unix timestamp to normal date style
    public static String TimeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString);// * 1000;
        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
        return date;
    }

    // 二维码生成工具
    /*
    public static Bitmap createQRImage(String url, int width, int height) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			// 显示到一个ImageView上面
			return bitmap;
			// sweepIV.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap createQRCode(String str, int widthAndHeight, int black)
			throws WriterException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = black;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
*/
    public static void writeVars(String fn, String var) {
        try {
            File f = new File(fn);
            FileOutputStream out = new FileOutputStream(f);
            out.write(var.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //	public static int setPowerOnOff(byte on_h, byte on_m, byte off_h,
//			byte off_m, byte enable) {
//		int fd, ret;
//
//		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
//		if (fd < 0) {
//			Logs.e("TMD", "设置开关机失败!!!");
//		}
//		ret = posix.poweronoff(off_h, off_m, on_h, on_m, enable, fd);
//		if (ret != 0) {
//			Logs.e("TMD", "设置开关机失败!!!");
//		}
//		posix.close(fd);
//		return 0;
//	}
//
//	public static int enable_watchdog() {
//		int fd, ret;
//		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
//		if (fd < 0) {
//			Logs.e("TMD", "开启看门狗失败!!!");
//		}
//		ret = posix.watchdogenable((byte) 1, fd);
//		if (ret != 0) {
//			Logs.e("TMD", "开启看门狗失败!!!");
//		}
//		posix.close(fd);
//		return 0;
//	}
//
//	public static int disable_watchdog() {
//		int fd, ret;
//		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
//		if (fd < 0) {
//			Logs.e("TMD", "关闭看门狗失败!!!");
//		}
//		ret = posix.watchdogenable((byte) 0, fd);
//		if (ret != 0) {
//			Logs.e("TMD", "关闭看门狗失败!!!");
//		}
//		posix.close(fd);
//		return 0;
//	}
//
//	public static int feed_watchdog() {
//		try {
//			int fd, ret;
//			fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
//			if (fd < 0) {
//				Logs.e("TMD", "喂狗失败!!!");
//			}
//			ret = posix.watchdogfeed(fd);
//			if (ret != 0) {
//				Logs.e("TMD", "喂狗失败!!!");
//			}
//			posix.close(fd);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Error e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//	
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是1280*720分辨率，所以高和宽我们设置为
        float hh = 1280f;//这里设置高度为1280f
        float ww = 720f;//这里设置宽度为720f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1280f;//这里设置高度为800f
        float ww = 720f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }
}
