/*
 * Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved.
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

package javafx.application;

import com.sun.javafx.application.PlatformImpl;

/**
 * Application platform support class.
 */
public final class Platform {

    // To prevent instantiation
    private Platform() {
    }

    /**
     * Run the specified Runnable on the JavaFX Application Thread at some
     * unspecified
     * time in the future. This method, which may be called from any thread,
     * will post the Runnable to an event queue and then return immediately to
     * the caller. The Runnables are executed in the order they are posted.
     * A runnable passed into the runLater method will be
     * executed before any Runnable passed into a subsequent call to runLater.
     *
     * @param runnable the Runnable whose run method will be executed on the
     * JavaFX Application Thread
     */
    public static void runLater(Runnable runnable) {
        PlatformImpl.runLater(runnable);
    }

    // TODO: Add the following if we decide to expose it publicly
//    public static void runAndWait(Runnable runnable) {
//        PlatformImpl.runAndWait(runnable);
//    }

    /**
     * Returns true if the calling thread is the JavaFX Application Thread.
     * Use this call the ensure that a given task is being executed
     * (or not being executed) on the JavaFX Application Thread.
     *
     * @return true if running on the JavaFX Application Thread
     */
    public static boolean isFxApplicationThread() {
        return PlatformImpl.isFxApplicationThread();
    }

    /**
     * Causes the JavaFX application to terminate. If this method is called
     * after the Application start method is called, then the JavaFX launcher
     * will call the Application stop method and terminate the JavaFX
     * application thread. The launcher thread will then shutdown. If there
     * are no other non-daemon threads that are running, the Java VM will exit.
     * If this method is called from the Preloader or the Application init
     * method, then the Application stop method may not be called.
     *
     * <p>Note: if the application is embedded in a browser, then this method
     * may have no effect.
     */
    public static void exit() {
        PlatformImpl.exit();
    }

    /**
     * Queries whether a specific conditional feature is supported
     * by the platform.
     * <p>
     * For example:
     * <pre>
     * // Query whether filter effects are supported
     * if (Platform.isSupported(ConditionalFeature.EFFECT)) {
     *    // use effects
     * }
     * </pre>
     *
     * @param feature the conditional feature in question.
     */
    public static boolean isSupported(ConditionalFeature feature) {
        return PlatformImpl.isSupported(feature);
    }
}