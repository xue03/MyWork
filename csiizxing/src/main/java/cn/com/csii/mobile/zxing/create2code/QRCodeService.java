/*    */ package cn.com.csii.mobile.zxing.create2code;
/*    */ 
/*    */ import android.content.res.Resources;
/*    */ import android.graphics.Bitmap;
/*    */ import android.graphics.Bitmap.Config;
/*    */ import android.graphics.Matrix;
/*    */ import android.graphics.drawable.BitmapDrawable;
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.EncodeHintType;
/*    */ import com.google.zxing.MultiFormatWriter;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*    */ import java.util.EnumMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QRCodeService
/*    */ {
/*    */   private static final int IMAGE_WIDTH = 30;
/*    */   
/*    */   public static Bitmap cretaeBitmap(String str, Bitmap mBitmap)
/*    */     throws Exception
/*    */   {
/* 28 */     Map<EncodeHintType, Object> hints = new EnumMap(
/* 29 */       EncodeHintType.class);
/* 30 */     hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
/* 31 */     hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
/*    */     
/*    */ 
/* 34 */     BitMatrix matrix = new MultiFormatWriter().encode(str, 
/* 35 */       BarcodeFormat.QR_CODE, 300, 300, hints);
/* 36 */     int width = matrix.getWidth();
/* 37 */     int height = matrix.getHeight();
/*    */     
/* 39 */     int halfW = width / 2;
/* 40 */     int halfH = height / 2;
/* 41 */     int[] pixels = new int[width * height];
/* 42 */     for (int y = 0; y < height; y++) {
/* 43 */       for (int x = 0; x < width; x++)
/*    */       {
/* 45 */         if ((mBitmap != null) && (x > halfW - 30) && 
/* 46 */           (x < halfW + 30) && (y > halfH - 30) && 
/* 47 */           (y < halfH + 30)) {
/* 48 */           pixels[(y * width + x)] = mBitmap.getPixel(x - halfW + 
/* 49 */             30, y - halfH + 30);
/*    */         }
/* 51 */         else if (matrix.get(x, y)) {
/* 52 */           pixels[(y * width + x)] = -13547643;
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 57 */     Bitmap bitmap = Bitmap.createBitmap(width, height, 
/* 58 */       Bitmap.Config.ARGB_8888);
/*    */     
/* 60 */     bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
/* 61 */     return bitmap;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Bitmap createImageViewQRBitmap(int image, String content, Resources resources)
/*    */     throws Exception
/*    */   {
/* 75 */     Bitmap mBitmap = ((BitmapDrawable)resources.getDrawable(image))
/* 76 */       .getBitmap();
/*    */     
/* 78 */     Matrix m = new Matrix();
/* 79 */     float sx = 60.0F / mBitmap.getWidth();
/* 80 */     float sy = 60.0F / mBitmap.getHeight();
/* 81 */     m.setScale(sx, sy);
/*    */     
/* 83 */     mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), 
/* 84 */       mBitmap.getHeight(), m, true);
/* 85 */     mBitmap = cretaeBitmap(content, mBitmap);
/* 86 */     return mBitmap;
/*    */   }
/*    */ }


/* Location:              /Users/wanghan/Desktop/关于固定资产清查的通知/CSIIZxing.jar!/cn/com/csii/mobile/zxing/create2code/QRCodeService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */