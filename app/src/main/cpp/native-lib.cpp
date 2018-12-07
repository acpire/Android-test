#include <jni.h>


#include <string>


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_human_test_1opengl_MainActivity_stringFromJNI(JNIEnv *env, jobject) {

    std::string hello = "Код на С";

    return env->NewStringUTF(hello.c_str());

}
