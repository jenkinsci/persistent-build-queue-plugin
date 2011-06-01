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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

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
     * Keep track of whether we've already loaded the PBQ.
     */
    private static boolean isLoaded = false;

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
     * Add a {@link AbstractProject} to the {@link #QUEUE}.
     * 
     * @param project
     *            the project to add
     */
    public static void add(final AbstractProject project) {
	synchronized (LOCK) {
	    QUEUE.add(project);
	    write();
	}
    }

    /**
     * Get the {@link File} where the {@link PersistentBuildQueue} is stored.
     * 
     * @return the file where the PBQ is stored
     */
    private static File getPersistentBuildQueueFile() {
	return new File(Hudson.getInstance().getRootDir(),
		PersistentBuildQueue.class.getCanonicalName() + ".txt");
    }

    /**
     * Get all the entries in the persistent build queue, sorted.
     * 
     * @return the sorted entries
     */
    @SuppressWarnings("unchecked")
    private static SortedSet<String> getPersistentBuildQueueEntries() {
	final TreeSet<String> set = new TreeSet<String>();

	InputStream inputStream = null;
	try {
	    inputStream = new FileInputStream(getPersistentBuildQueueFile());
	    set.addAll(IOUtils.readLines(inputStream));
	} catch (final IOException e) {
	    LOG.log(Level.WARNING, e.getMessage(), e);
	} finally {
	    IOUtils.closeQuietly(inputStream);
	}

	return set;
    }

    /**
     * Called at Jenkins start (and probably more times, hence the isLoaded
     * check) to load up the persisted build queue and
     * {@link AbstractProject#scheduleBuild(int, hudson.model.Cause)} for all
     * entries in the set.
     */
    public static void load() {
	synchronized (LOCK) {
	    if (!isLoaded) {
		final SortedSet<String> persistedBuildQueue = getPersistentBuildQueueEntries();
		for (final Project project : Hudson.getInstance().getProjects()) {
		    if (persistedBuildQueue.contains(project.getDisplayName())) {
			LOG.info("Re-scheduling persisted build queue project: "
				+ project.getDisplayName());
			project.scheduleBuild(0,
				new PersistentBuildQueueCause());
		    }
		}

		isLoaded = true;
	    }
	}
    }

    /**
     * Convert the {@link #QUEUE} into a {@link String} suitable for persisting.
     * 
     * @return the stringified queue
     */
    private static String queueToString() {
	final StringBuffer buffer = new StringBuffer();
	for (final AbstractProject project : QUEUE) {
	    buffer.append(project.getDisplayName()).append("\n");
	}
	return buffer.toString();
    }

    /**
     * Remove a {@link AbstractProject} to the {@link #QUEUE}.
     * 
     * @param project
     *            the project to remove
     */
    public static void remove(final AbstractProject project) {
	synchronized (LOCK) {
	    QUEUE.remove(project);
	    write();
	}
    }

    /**
     * Write the {@link #QUEUE} to disk.
     */
    private static void write() {
	write(queueToString());
    }

    /**
     * Write the {@link #QUEUE}, as a {@link String}, to disk.
     * 
     * @param contents
     *            the stringified build queue
     */
    private static void write(final String contents) {
	OutputStream outputStream = null;
	try {
	    outputStream = new FileOutputStream(getPersistentBuildQueueFile());
	    IOUtils.write(contents, outputStream);
	} catch (final IOException e) {
	    LOG.log(Level.WARNING, e.getMessage(), e);
	} finally {
	    IOUtils.closeQuietly(outputStream);
	}
    }

    /**
     * Static-only access.
     */
    private PersistentBuildQueue() {
	// static-only access
    }
}
