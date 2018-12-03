package com.example.pang.testlive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Description: 高斯模糊工具类
 * Created by PangHaHa on 18-9-13.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class GaussinaBlurUtil {
    /**
     * 高斯模糊
     * <p>
     * context 上下文　source 原图　 radius 模糊半径 0－25
     */
    public static Bitmap GaussianBlur(Context context, Bitmap source, int radius) {
        try {
            Bitmap inputBmp = source;
            // 创建输出图片
            RenderScript renderScript = RenderScript.create(context);
            // 构建一个RenderScript对象
            // Allocate memory for Renderscript to work with
            final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
            final Allocation output = Allocation.createTyped(renderScript, input.getType());
            // 创建高斯模糊脚本
            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            scriptIntrinsicBlur.setInput(input);
            // 设置模糊半径，范围0f<radius<=25f
            scriptIntrinsicBlur.setRadius(radius);
            // Start the ScriptIntrinisicBlur
            scriptIntrinsicBlur.forEach(output);
            // Copy the output to the blurred bitmap
            output.copyTo(inputBmp);
            //关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
            renderScript.destroy();
            //颜色加深操作
            Bitmap bitmap = Bitmap.createBitmap(inputBmp.getWidth(), inputBmp.getHeight(), Bitmap.Config.ARGB_4444);
            ColorMatrix matrix = new ColorMatrix();
            //颜色加深操作
            matrix.setScale(0.4f, 0.4f, 0.4f, 1);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(matrix));
            canvas.drawBitmap(inputBmp, 0, 0, paint);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return source;
        }

    }

}
