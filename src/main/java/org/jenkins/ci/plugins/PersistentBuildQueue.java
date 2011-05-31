/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkins.ci.plugins;

import hudson.model.AbstractBuild;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A queue of {@link AbstractBuild} that is to be persisted across the Jenkins
 * lifecycle.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public final class PersistentBuildQueue {
    private static final Object LOCK = new Object();
    private static final Queue<AbstractBuild> QUEUE = new LinkedList<AbstractBuild>();

    public static void setUp(final AbstractBuild build) {
	synchronized (LOCK) {
	    QUEUE.add(build);
	    write();
	}
    }

    public static void tearDown(final AbstractBuild build) {
	synchronized (LOCK) {
	    QUEUE.remove(build);
	    write();
	}
    }

    private static void write() {
	System.out.println("QUEUE: " + QUEUE.toString());
    }

    /** Static-only access. */
    private PersistentBuildQueue() {
	// static-only access
    }
}
