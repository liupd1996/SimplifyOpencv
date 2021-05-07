package tech.kicky.simplifyopencv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.utils.EncodeFaceUtils
import tech.kicky.simplifyopencv.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mBinding.ivLena.setImageResource(R.drawable.lena)
        val source = BitmapFactory.decodeResource(resources, R.drawable.lena)
            .copy(Bitmap.Config.ARGB_8888, true)
        val width = source.width
        val height = source.height
        val pixel = IntArray(width * height)
        source.getPixels(pixel, 0, width, 0, 0, width, height)
        val grayPixels: IntArray? = doGray(pixel, width, height)

        val gray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        gray.setPixels(grayPixels, 0, width, 0, 0, width, height)
        mBinding.ivResult.setImageBitmap(gray)

        EncodeFaceUtils.getDetector(this, pixel, width, height)
    }

    private external fun doGray(imgData: IntArray?, width: Int, height: Int): IntArray?

//    external fun doDetect(bina: String?, path: String?): Boolean
//
//    external fun doDetect2(
//        imgData: IntArray?,
//        width: Int,
//        height: Int,
//        bina: String?,
//        path: String?
//    ): Boolean

}