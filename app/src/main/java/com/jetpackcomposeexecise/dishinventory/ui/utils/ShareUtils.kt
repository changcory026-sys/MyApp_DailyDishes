package com.jetpackcomposeexecise.dishinventory.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

// 辅助函数：将 Bitmap 保存到缓存并触发系统分享
fun saveAndShareImage(context: Context, bitmap: Bitmap) {
    // 1. 在应用的缓存目录创建 images 文件夹
    val imagesDir = File(context.cacheDir, "images")
    if (!imagesDir.exists()) imagesDir.mkdirs()

    // 2. 创建图片文件
    val file = File(imagesDir, "daily_menu_share.png")

    try {
        // 3. 将 Bitmap 压缩并写入文件
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return // 保存失败则终止分享
    }

    // 4. 获取安全共享的 URI (这里的 authority 必须和 Manifest 里配置的保持一致)
    val authority = "${context.packageName}.fileprovider"
    val uri = FileProvider.getUriForFile(context, authority, file)

    // 5. 组装分享 Intent
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 授予目标 App 读取该图片的权限
    }

    // 6. 唤起系统底部选择弹窗
    val shareIntent = Intent.createChooser(sendIntent, "分享今日菜单到...")
    context.startActivity(shareIntent)
}