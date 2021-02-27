#include <jni.h>
#include <android/log.h>
#include <pthread.h>
#include "com_aptl_jnidemo_NativeSorting.h"

#define TAG "native-log-tag" 
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))

JavaVM *g_vm;

struct thread_args {
    int* data;
    int data_size;
    jobject callback;
};

void quicksort(int *arr, int start, int end);
void background_sorting(void* args);

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_vm = vm;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_com_aptl_jnidemo_NativeSorting_nativeSortWithCallback
  (JNIEnv *env, jobject obj, jintArray data, jobject callback) {
    jint* array;
    jint length;
    jmethodID callbackMethodId;
    jclass callbackClass;
    pthread_t thread;
    struct thread_args* myThreadData = malloc(sizeof(struct thread_args));

    array = (*env)->GetIntArrayElements(env, data, 0);
    length = (*env)->GetArrayLength(env, data);
    myThreadData->data = array;
    myThreadData->data_size = length;
    myThreadData->callback = (*env)->NewGlobalRef(env, callback);

    (*env)->ReleaseIntArrayElements(env, data, array, JNI_COMMIT);

    pthread_create(&thread, NULL, (void*)background_sorting, 
                   (void*) myThreadData);
}

void background_sorting(void* arg) {
    struct thread_args *data = (struct thread_args *) arg;
    JNIEnv* env = NULL;
    jclass callbackClass;
    jmethodID callbackMethodId;
    jintArray result;

    quicksort(data->data, 0, data->data_size);

    (*g_vm)->AttachCurrentThread(g_vm, &env, NULL);

    result = (*env)->NewIntArray(env, data->data_size);
    (*env)->SetIntArrayRegion(env, result, 0, data->data_size, data->data);

    callbackClass = (*env)->GetObjectClass(env, data->callback);
    callbackMethodId = (*env)->GetMethodID(env, callbackClass, 
                                           "onSorted", "([I)V");
    (*env)->CallVoidMethod(env, data->callback, callbackMethodId, result);

    free(data->data);
    free(data);
    (*env)->DeleteGlobalRef(env, data->callback);
    (*g_vm)->DetachCurrentThread(g_vm);
}

void quicksort(int *arr, int start, int end)
{
    // Left out as an exercise for your upcoming interview at Google...
}
