#import <Foundation/Foundation.h>
#import <GameController/GameController.h>
#import <CoreFoundation/CoreFoundation.h>
#include <jni.h>
#include <pthread.h>
#include <string.h>

// All framework interaction stays off Minecraft's render thread. Java only copies
// a locked snapshot, so discovery never waits on the Cocoa main event loop.
static pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;
static float values[4][23];
static NSString *names[4];
static int knownSlots;
static pthread_once_t once = PTHREAD_ONCE_INIT;

static void *monitor(void *unused) {
    @autoreleasepool {
        GCController.shouldMonitorBackgroundEvents = YES;
        dispatch_queue_t inputQueue = dispatch_queue_create("joypad.gamecontroller.events", DISPATCH_QUEUE_SERIAL);
        GCController *slots[4] = {nil, nil, nil, nil};
        NSPort *port = [NSMachPort port];
        [[NSRunLoop currentRunLoop] addPort:port forMode:NSDefaultRunLoopMode];
        while (true) {
            @autoreleasepool {
                NSArray<GCController *> *present = GCController.controllers;
                for (int i = 0; i < 4; i++) if (slots[i] && ![present containsObject:slots[i]]) slots[i] = nil;
                for (GCController *controller in present) {
                    if (!controller.extendedGamepad) continue;
                    BOOL already = NO;
                    for (int i = 0; i < 4; i++) if (slots[i] == controller) already = YES;
                    if (already) continue;
                    int target = -1;
                    for (int i = 0; i < 4; i++) {
                        if (!slots[i] && [names[i] isEqualToString:controller.vendorName]) { target = i; break; }
                    }
                    if (target < 0) for (int i = 0; i < 4; i++) if (!slots[i]) { target = i; break; }
                    if (target >= 0) {
                        slots[target] = controller;
                        controller.handlerQueue = inputQueue;
                    }
                }
                float next[4][23] = {{0}};
                for (int i = 0; i < 4; i++) {
                    next[i][22] = -1;
                    GCController *controller = slots[i];
                    if (!controller) continue;
                    GCExtendedGamepad *g = [controller capture].extendedGamepad;
                    if (!g) continue;
                    next[i][0] = 1;
                    GCControllerButtonInput *buttons[15] = {
                        g.buttonA, g.buttonB, g.buttonX, g.buttonY,
                        g.buttonOptions, g.buttonMenu, g.leftShoulder, g.rightShoulder,
                        g.leftThumbstickButton, g.rightThumbstickButton,
                        g.dpad.up, g.dpad.down, g.dpad.left, g.dpad.right, g.buttonHome
                    };
                    for (int b = 0; b < 15; b++) next[i][1 + b] = buttons[b].isPressed ? 1 : 0;
                    next[i][16] = g.leftThumbstick.xAxis.value;
                    next[i][17] = -g.leftThumbstick.yAxis.value;
                    next[i][18] = g.rightThumbstick.xAxis.value;
                    next[i][19] = -g.rightThumbstick.yAxis.value;
                    next[i][20] = g.leftTrigger.value;
                    next[i][21] = g.rightTrigger.value;
                }
                pthread_mutex_lock(&lock);
                memcpy(values, next, sizeof(values));
                for (int i = 0; i < 4; i++) if (slots[i]) {
                    names[i] = slots[i].vendorName ?: @"Gamepad";
                    if (knownSlots < i + 1) knownSlots = i + 1;
                }
                pthread_mutex_unlock(&lock);
                [[NSRunLoop currentRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.008]];
            }
        }
    }
    return NULL;
}

static void launch(void) {
    pthread_t thread;
    if (pthread_create(&thread, NULL, monitor, NULL) == 0) pthread_detach(thread);
}

JNIEXPORT void JNICALL Java_com_shiny_joypadmod_devices_MacGamepadNative_start(JNIEnv *env, jclass type) {
    pthread_once(&once, launch);
}

JNIEXPORT jint JNICALL Java_com_shiny_joypadmod_devices_MacGamepadNative_slotCount(JNIEnv *env, jclass type) {
    pthread_mutex_lock(&lock);
    int count = knownSlots;
    pthread_mutex_unlock(&lock);
    return count;
}

JNIEXPORT jstring JNICALL Java_com_shiny_joypadmod_devices_MacGamepadNative_name(JNIEnv *env, jclass type, jint slot) {
    if (slot < 0 || slot >= 4) return NULL;
    @autoreleasepool {
        pthread_mutex_lock(&lock);
        NSString *name = names[slot];
        pthread_mutex_unlock(&lock);
        if (!name) return NULL;
        NSUInteger length = name.length;
        unichar *text = malloc(sizeof(unichar) * (length + 1));
        if (!text) return NULL;
        [name getCharacters:text range:NSMakeRange(0, length)];
        jstring result = (*env)->NewString(env, text, (jsize)length);
        free(text);
        return result;
    }
}

JNIEXPORT void JNICALL Java_com_shiny_joypadmod_devices_MacGamepadNative_read(JNIEnv *env, jclass type, jint slot, jfloatArray output) {
    if (slot < 0 || slot >= 4 || !output || (*env)->GetArrayLength(env, output) < 23) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/IllegalArgumentException"), "Expected slot 0..3 and at least 23 floats");
        return;
    }
    float copy[23];
    pthread_mutex_lock(&lock);
    memcpy(copy, values[slot], sizeof(copy));
    pthread_mutex_unlock(&lock);
    (*env)->SetFloatArrayRegion(env, output, 0, 23, copy);
}
