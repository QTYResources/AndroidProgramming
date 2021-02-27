#include <jni.h>
#include <android/log.h>
#include "com_aptl_jnidemo_NativeSorting.h"

#define TAG "native-log-tag" 
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))

void quicksort(int *arr, int start, int end);

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_com_aptl_jnidemo_NativeSorting_nativeSort
  (JNIEnv *env, jobject obj, jintArray data) {
    jint* array = (*env)->GetIntArrayElements(env, data, 0);
    jint length = (*env)->GetArrayLength(env, data);
    quicksort(array, 0, length);
    (*env)->ReleaseIntArrayElements(env, data, array, 0);
}

void quicksort(int *arr, int start, int end)
{
    // Left out as an exercise for your upcoming interview at Google...
}
