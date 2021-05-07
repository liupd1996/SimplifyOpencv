#include <jni.h>
#include <string>
#include "include/opencv2/core/hal/interface.h"
#include "include/opencv2/imgproc/types_c.h"
#include "include/opencv2/imgproc.hpp"
#include "include/opencv2/objdetect.hpp"
//#include "facedetect.h"
#include <android/log.h>


#define  LOG    "VR_CODE"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__) // 定义LOGD类型

using namespace std;
using namespace cv;

//facedetect _faceDetect;

extern "C" JNIEXPORT jintArray  JNICALL
Java_tech_kicky_simplifyopencv_MainActivity_doGray(
        JNIEnv *env, jobject thiz, jintArray source,
        jint width, jint height) {
    // TODO: implement convertToGray()
    jint *input;
    input = env->GetIntArrayElements(source, JNI_FALSE);

    Mat sourceMat(height, width, CV_8UC4, (unsigned char *) input);

    Mat grayMat;
    cvtColor(sourceMat, grayMat, CV_BGRA2GRAY);

    Mat resultMat;
    cvtColor(grayMat, resultMat, CV_GRAY2BGRA);
    int size = width * height;
    jintArray result = env->NewIntArray(size);
    uchar *output = resultMat.data;
    env->SetIntArrayRegion(result, 0, size, (const jint *) output);
    env->ReleaseIntArrayElements(source, input, 0);
    return result;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_test_utils_EncodeFaceUtils_detect(JNIEnv *env, jclass clazz, jintArray source,
                                                      jint width, jint height, jstring path) {
    try {
        string sdesc = env->GetStringUTFChars(path, 0);
        jint *input;
        input = env->GetIntArrayElements(source, JNI_FALSE);
        Mat sourceMat(height, width, CV_8UC4, (unsigned char *) input);
        Mat grayMat;
        cvtColor(sourceMat, grayMat, CV_BGRA2GRAY);
        CascadeClassifier _cascadeClassifier(sdesc);
        jclass list_jcls = env->FindClass("java/util/ArrayList");
        if (list_jcls == nullptr) {
            LOGD("ArrayList没找到相关类!");
            return NULL;
        }
        //获取ArrayList构造函数id
        jmethodID list_init = env->GetMethodID(list_jcls, "<init>", "()V");
        //创建一个ArrayList对象
        jobject list_obj = env->NewObject(list_jcls, list_init);
        //获取ArrayList对象的add()的methodID
        jmethodID list_add = env->GetMethodID(list_jcls, "add", "(Ljava/lang/Object;)Z");

        std::vector<Rect> objects;
        _cascadeClassifier.detectMultiScale(grayMat,objects);

        if (objects.size() > 0) {
            for (int i = 0; i < objects.size(); i++) {
                jclass rect_jcls = env->FindClass("android/graphics/Rect");
                jmethodID rect_init = env->GetMethodID(rect_jcls, "<init>", "(IIII)V");
                jobject tmprect = env->NewObject(rect_jcls, rect_init,
                                                 (int) objects[i].x,
                                                 (int) objects[i].y,
                                                 (int) (objects[i].width + objects[i].x),
                                                 (int) (objects[i].height + objects[i].y) );
                env->CallBooleanMethod(list_obj, list_add, tmprect);
                LOGD("detect1111: x:%d y:%d width:%d height:%d", objects[i].x,objects[i].y,(objects[i].width + objects[i].x),(objects[i].height + objects[i].y));
                objects[i];
            }
        }
        return list_obj;

    } catch (Exception e) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
    } catch (...) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
    }
    return NULL;
}

