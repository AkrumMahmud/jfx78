/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#import "GlassAccessibleToggleProvider.h"
#import "GlassMacros.h"

//#define VERBOSE
#ifndef VERBOSE
#define LOG(MSG, ...)
#else
#define LOG(MSG, ...) GLASS_LOG(MSG, ## __VA_ARGS__);
#endif

extern NSDictionary* attributeIds;
extern NSArray* eventIds;
extern NSArray* roleIds;
extern NSMutableArray* attributes;

static jmethodID midGetToggleState=0;

@implementation GlassAccessibleToggleProvider

// attributes

- (NSArray *)accessibilityAttributeNames {
    LOG("GlassAccessibleToggleProvider:accessibilityAttributeNames");
    
    NSMutableArray *names = [[[super accessibilityAttributeNames] mutableCopy] autorelease];
    [names addObject:NSAccessibilityValueAttribute];
    //[names addObject:NSAccessibilityValueDescriptionAttribute];
    return names;
}

- (id)accessibilityAttributeValue:(NSString *)attribute {
    LOG("GlassAccessibleToggleProvider:accessibilityAttributeValue");
    LOG("  attribute: %s, self: %p", [attribute UTF8String], self);
    if ([attribute isEqualToString:NSAccessibilityValueAttribute]) {
        GET_MAIN_JENV;
        jlong toggleState = (*env)->CallIntMethod(env, jBaseProvider, midGetToggleState);
        GLASS_CHECK_EXCEPTION(env);
        LOG("  toggleState: %d", toggleState);
        return [NSNumber numberWithInt:toggleState] ;
    } else {
        LOG("  attribute: not implemented by subclass, call super");
        return [super accessibilityAttributeValue:attribute];
    }
}

@end

/*
 * Class:     com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider
 * Method:    _initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider__1initIDs(
    JNIEnv *env, jclass cls)
{
    LOG("Java_com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider__1initIDs");
    midGetToggleState  = (*env)->GetMethodID(env, cls, "getToggleState", "()J");
    GLASS_CHECK_EXCEPTION(env);
}

/*
 * Class:     com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider
 * Method:    _createAccessible
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider__1createAccessible(
    JNIEnv *env, jobject jBaseProvider)
{
    LOG("Java_com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider__1createAccessible");
    GlassAccessibleToggleProvider *acc =
    [[GlassAccessibleToggleProvider alloc] initWithEnv:env baseProvider:jBaseProvider];
    LOG("  returning: %p:", acc);
    return ptr_to_jlong(acc);
}

/*
 * Class:     com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider
 * Method:    _destroyAccessible
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider__1destroyAccessible(
    JNIEnv *env, jobject jBaseProvider, jlong acc)
{
    LOG("Java_com_sun_glass_ui_accessible_mac_MacAccessibleToggleProvider__1destroyAccessible");
    GlassAccessibleToggleProvider* accessible =
        (GlassAccessibleToggleProvider*)jlong_to_ptr(acc);
    LOG("  accessible: %p", accessible);
    if (accessible) {
        [accessible dealloc];
    }
}
