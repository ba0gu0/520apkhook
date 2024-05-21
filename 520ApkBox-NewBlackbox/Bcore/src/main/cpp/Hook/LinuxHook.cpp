#include "LinuxHook.h"
#import "JniHook/JniHook.h"
#include "IO.h"

HOOK_JNI(jboolean, access, JNIEnv *env, jobject obj, jstring path, jint mode) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_access(env, obj, redirect, mode);
}

HOOK_JNI(void, chmod, JNIEnv *env, jobject obj, jstring path, jint mode) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_chmod(env, obj, redirect, mode);
}

HOOK_JNI(void, chown, JNIEnv *env, jobject obj, jstring path, jint mode, jint gid) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_chown(env, obj, redirect, mode, gid);
}

HOOK_JNI(void, execv, JNIEnv *env, jobject obj, jstring filename, jobjectArray argv) {
    jstring redirect = IO::redirectPath(env, filename);
    return orig_execv(env, obj, redirect, argv);
}

HOOK_JNI(void, execve, JNIEnv *env, jobject obj, jstring filename, jobjectArray argv, jobjectArray envp) {
    jstring redirect = IO::redirectPath(env, filename);
    return orig_execve(env, obj, redirect, argv, envp);
}

HOOK_JNI(jbyteArray, getxattr, JNIEnv *env, jobject obj, jstring path, jstring name) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_getxattr(env, obj, redirect, name);
}

HOOK_JNI(void, lchown, JNIEnv *env, jobject obj, jstring path, jint uid, jint gid) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_lchown(env, obj, redirect, uid, gid);
}

HOOK_JNI(void, link, JNIEnv *env, jobject obj, jstring oldPath, jstring newPath) {
    jstring redirectOldPath = IO::redirectPath(env, oldPath);
    jstring redirectNewPath = IO::redirectPath(env, newPath);
    return orig_link(env, obj, redirectOldPath, redirectNewPath);
}

HOOK_JNI(jobjectArray, listxattr, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_listxattr(env, obj, redirect);
}

HOOK_JNI(jobject, lstat, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_lstat(env, obj, redirect);
}

HOOK_JNI(void, mkdir, JNIEnv *env, jobject obj, jstring path, jint mode) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_mkdir(env, obj, redirect, mode);
}

HOOK_JNI(void, mkfifo, JNIEnv *env, jobject obj, jstring path, jint mode) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_mkfifo(env, obj, redirect, mode);
}

HOOK_JNI(jobject, open, JNIEnv *env, jobject obj, jstring path, jint flags, jint mode) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_open(env, obj, redirect, flags, mode);
}

HOOK_JNI(jstring, readlink, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_readlink(env, obj, redirect);
}

HOOK_JNI(jstring, realpath, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_realpath(env, obj, redirect);
}

HOOK_JNI(void, remove, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_remove(env, obj, redirect);
}

HOOK_JNI(void, removexattr, JNIEnv *env, jobject obj, jstring path, jstring name) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_removexattr(env, obj, redirect, name);
}

HOOK_JNI(void, rename, JNIEnv *env, jobject obj, jstring oldPath, jstring newPath) {
    jstring redirectOldPath = IO::redirectPath(env, oldPath);
    jstring redirectNewPath = IO::redirectPath(env, newPath);
    return orig_rename(env, obj, redirectOldPath, redirectNewPath);
}

HOOK_JNI(void, setxattr, JNIEnv *env, jobject obj, jstring path, jstring name, jbyteArray value, jint flags) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_setxattr(env, obj, redirect, name, value, flags);
}

HOOK_JNI(jobject, stat, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_stat(env, obj, redirect);
}

HOOK_JNI(jobject, statvfs, JNIEnv *env, jobject obj, jstring path) {
    jstring redirect = IO::redirectPath(env, path);
    return orig_statvfs(env, obj, redirect);
}

HOOK_JNI(void, symlink, JNIEnv *env, jobject obj, jstring oldPath, jstring newPath) {
    jstring redirectOldPath = IO::redirectPath(env, oldPath);
    jstring redirectNewPath = IO::redirectPath(env, newPath);
    return orig_symlink(env, obj, redirectOldPath, redirectNewPath);
}

HOOK_JNI(void, unlink, JNIEnv *env, jobject obj, jstring pathname) {
    jstring redirect = IO::redirectPath(env, pathname);
    return orig_unlink(env, obj, redirect);
}

void LinuxHook::init(JNIEnv *env) {
    JniHook::HookJniFun(env, "libcore/io/Linux", "access", "(Ljava/lang/String;I)Z",
                        (void *) new_access, (void **) (&orig_access), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "chmod", "(Ljava/lang/String;I)V",
                        (void *) new_chmod, (void **) (&orig_chmod), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "chown", "(Ljava/lang/String;II)V",
                        (void *) new_chown, (void **) (&orig_chown), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "execv", "(Ljava/lang/String;[Ljava/lang/String;)V",
                        (void *) new_execv, (void **) (&orig_execv), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "execve", "(Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V",
                        (void *) new_execve, (void **) (&orig_execve), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "getxattr", "(Ljava/lang/String;Ljava/lang/String;)[B",
                        (void *) new_getxattr, (void **) (&orig_getxattr), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "lchown", "(Ljava/lang/String;II)V",
                        (void *) new_lchown, (void **) (&orig_lchown), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "link", "(Ljava/lang/String;Ljava/lang/String;)V",
                        (void *) new_link, (void **) (&orig_link), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "listxattr", "(Ljava/lang/String;)[Ljava/lang/String;",
                        (void *) new_listxattr, (void **) (&orig_listxattr), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "lstat", "(Ljava/lang/String;)Landroid/system/StructStat;",
                        (void *) new_lstat, (void **) (&orig_lstat), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "mkdir", "(Ljava/lang/String;I)V",
                        (void *) new_mkdir, (void **) (&orig_mkdir), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "mkfifo", "(Ljava/lang/String;I)V",
                        (void *) new_mkfifo, (void **) (&orig_mkfifo), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "open", "(Ljava/lang/String;II)Ljava/io/FileDescriptor;",
                        (void *) new_open, (void **) (&orig_open), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "readlink", "(Ljava/lang/String;)Ljava/lang/String;",
                        (void *) new_readlink, (void **) (&orig_readlink), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "realpath", "(Ljava/lang/String;)Ljava/lang/String;",
                        (void *) new_realpath, (void **) (&orig_realpath), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "remove", "(Ljava/lang/String;)V",
                        (void *) new_remove, (void **) (&orig_remove), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "removexattr", "(Ljava/lang/String;Ljava/lang/String;)V",
                        (void *) new_removexattr, (void **) (&orig_removexattr), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "rename", "(Ljava/lang/String;Ljava/lang/String;)V",
                        (void *) new_rename, (void **) (&orig_rename), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "stat", "(Ljava/lang/String;)Landroid/system/StructStat;",
                        (void *) new_stat, (void **) (&orig_stat), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "statvfs", "(Ljava/lang/String;)Landroid/system/StructStatVfs;",
                        (void *) new_statvfs, (void **) (&orig_statvfs), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "symlink", "(Ljava/lang/String;Ljava/lang/String;)V",
                        (void *) new_symlink, (void **) (&orig_symlink), false);

    JniHook::HookJniFun(env, "libcore/io/Linux", "unlink", "(Ljava/lang/String;)V",
                        (void *) new_unlink, (void **) (&orig_unlink), false);
}
