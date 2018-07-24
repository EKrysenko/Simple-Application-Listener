#include "dlfcn.h"
#include "jni.h"
#include "libcob.h"
#include "adaptor.h"
#include "stdio.h"
JNIEXPORT jstring JNICALL Java_CallingCOBOL_adaptor(JNIEnv * env, jobject obj, jstring string_from_java) {
    cob_init(0, NULL);
    void * sub_cob;
    sub_cob = dlopen("./src/main/resources/libsub-cob.so", RTLD_NOW);
    if (!sub_cob) {
        puts("error no lib found");
        return;
    }
//    puts("lib loaded");
    int(*proc_cob)(char*) = (void(*)(char*))dlsym(sub_cob, "proc__cob");
    if (proc_cob) {
//        printf("proc addr is %p\n", proc_cob);
    } else {
//        puts("proc addr is null");
    }
    const char *temp = (*env)->GetStringUTFChars(env, string_from_java, 0);
//    puts("took string from java environment");
    int res =  proc_cob(temp);
    printf(res);
//    puts(res);
//    puts("cobol proc run ok");
    dlclose(sub_cob);
    return (*env)->NewStringUTF(env, res);
}
