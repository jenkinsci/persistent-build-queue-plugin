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

import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Project;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * A queue of {@link AbstractBuild} that is to be persisted across the Jenkins
 * lifecycle.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public final class PersistentBuildQueue {
    /**
     * Synchronization.
     */
    private static final Object LOCK = new Object();

    /**
     * The class logger.
     */
    private static final Logger LOG = Logger
	    .getLogger(PersistentBuildQueue.class.getName());

    /**
     * The {@link Queue} of {@link AbstractProject} which are in the build
     * queue.
     */
    private static final Queue<AbstractProject> QUEUE = new LinkedList<AbstractProject>();

    /**
     * Keep track of whether we've already loaded the PBQ.
     */
    private static boolean isLoaded = false;

    public static void add(final AbstractProject project) {
	synchronized (LOCK) {
	    QUEUE.add(project);
	    write();
	}
    }

    public static void load() {
	if (!isLoaded) {
	    final List<Project> projects = Hudson.getInstance().getProjects();
	    for (final Project project : projects) {
		LOG.info("Re-scheduling persisted build queue project: "
			+ project.getDisplayName());
		project.asProject().scheduleBuild(0,
			new PersistentBuildQueueCause());
	    }

	    isLoaded = true;
	}
    }

    public static void remove(final AbstractProject project) {
	synchronized (LOCK) {
	    QUEUE.remove(project);
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
